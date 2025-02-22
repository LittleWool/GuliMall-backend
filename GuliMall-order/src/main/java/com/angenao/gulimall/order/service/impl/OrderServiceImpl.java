package com.angenao.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.angenao.common.mq.OrderTo;
import com.angenao.common.mq.SeckillOrderTo;
import com.angenao.common.to.SkuHasStockTo;
import com.angenao.common.utils.R;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.order.constant.OrderConstant;
import com.angenao.gulimall.order.constant.PayConstant;
import com.angenao.gulimall.order.dao.OrderItemDao;
import com.angenao.gulimall.order.entity.OrderItemEntity;
import com.angenao.gulimall.order.entity.PaymentInfoEntity;
import com.angenao.gulimall.order.enums.OrderStatusEnum;
import com.angenao.gulimall.order.feign.CartFeignService;
import com.angenao.gulimall.order.feign.MemberFeignService;
import com.angenao.gulimall.order.feign.ProductFeignService;
import com.angenao.gulimall.order.feign.WareFeignService;
import com.angenao.gulimall.order.interceptor.LoginInterceptor;
import com.angenao.gulimall.order.service.OrderItemService;
import com.angenao.gulimall.order.to.OrderCreateTo;
import com.angenao.gulimall.order.to.SpuInfoTo;
import com.angenao.gulimall.order.vo.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.order.dao.OrderDao;
import com.angenao.gulimall.order.entity.OrderEntity;
import com.angenao.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoServiceImpl paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }



    /**
     * 提交订单
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
//    @GlobalTransactional
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        //验证防重令牌
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);
        //1. 验证防重令牌
        MemberVo memberResponseVo = LoginInterceptor.threadLocal.get();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.ORDER_UNIQUE_TOKEN_KEY_PREFIX + memberResponseVo.getId()), vo.getOrderToken());
        if (execute == 0L) {
            //验证防未通过
            //1.1 防重令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        } else {
            //防重验证通过
            //创建订单,订单项
            OrderCreateTo order = createOrderTo(memberResponseVo, vo);
            //验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //验价通过保存订单
                saveOrder(order);
                rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                //锁定库存
                List<OrderItemVo> vos = order.getOrderItems().stream().map(e -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(e.getSkuId());
                    orderItemVo.setCount(e.getSkuQuantity());
                    return orderItemVo;
                }).collect(Collectors.toList());
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                lockVo.setLocks(vos);
                R r = wareFeignService.orderLock(lockVo);
                if (r.getCode() == 0) {
                    //锁定库存成功
                    responseVo.setOrder(order.getOrder());
                    return responseVo;
                } else {
                    // 锁定库存失败
                    responseVo.setCode(3);
                    return responseVo;
                }
            } else {
                responseVo.setCode(2);
                return responseVo;
            }


        }


    }

    @Override
    public OrderEntity getByOrderSn(String orderSn) {
        OrderEntity orderEntity = getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        OrderEntity order = getById(orderEntity.getId());
        if (order.getStatus()==OrderStatusEnum.CREATE_NEW.getCode()) {
            //仍旧未付款
            OrderEntity orderNew=new OrderEntity();
            orderNew.setId(orderEntity.getId());
            orderNew.setStatus(OrderStatusEnum.CANCLED.getCode());

            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //解锁库存
            rabbitTemplate.convertAndSend("order-event-exchange","oder.release.other.#",orderTo);

            this.updateById(orderNew);
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        BigDecimal payAmount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(payAmount.toString());

        List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        return payVo;
    }

    @Override
    public PageUtils getMemberOrderPage(Map<String, Object> params) {
        MemberVo memberResponseVo = LoginInterceptor.threadLocal.get();
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<OrderEntity>().eq("member_id", memberResponseVo.getId()).orderByDesc("create_time");
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),queryWrapper
        );
        List<OrderEntity> entities = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItems(orderItemEntities);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(entities);
        return new PageUtils(page);
    }

    @Override
    public String handlePayResult(PayAsyncVo vo) {
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(vo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(vo.getTrade_no());
        paymentInfo.setPaymentStatus(vo.getTrade_status());
        paymentInfo.setCallbackTime(vo.getNotify_time());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setSubject(vo.getSubject());
        paymentInfoService.save(paymentInfo);
        String trade_status = vo.getTrade_status();

        if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")) {
            baseMapper.updateOrderStatus(vo.getOut_trade_no(), OrderStatusEnum.PAYED.getCode(), PayConstant.ALIPAY);
        }
        return "success";
    }

    /**
     * 秒杀订单创建
     * @param orderTo
     */
    @Transactional
    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        MemberVo memberResponseVo = LoginInterceptor.threadLocal.get();
        //1. 创建订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        if (memberResponseVo!=null){
            orderEntity.setMemberUsername(memberResponseVo.getUsername());
        }
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setCreateTime(new Date());
        orderEntity.setPayAmount(orderTo.getSeckillPrice().multiply(new BigDecimal(orderTo.getNum())));
        this.save(orderEntity);
        //2. 创建订单项
        R r = productFeignService.info(orderTo.getSkuId());
        if (r.getCode() == 0) {
            SeckillSkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SeckillSkuInfoVo>() {
            });
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderSn(orderTo.getOrderSn());
            orderItemEntity.setSpuId(skuInfo.getSpuId());
            orderItemEntity.setCategoryId(skuInfo.getCatalogId());
            orderItemEntity.setSkuId(skuInfo.getSkuId());
            orderItemEntity.setSkuName(skuInfo.getSkuName());
            orderItemEntity.setSkuPic(skuInfo.getSkuDefaultImg());
            orderItemEntity.setSkuPrice(skuInfo.getPrice());
            orderItemEntity.setSkuQuantity(orderTo.getNum());
            orderItemService.save(orderItemEntity);
        }
    }


    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        MemberVo memberVo = LoginInterceptor.threadLocal.get();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        CompletableFuture<Void> addressesFuture = CompletableFuture.runAsync(() -> {
            //给远程请求放置请求头，也可以使用ali的threadloaclthransmit解决
            RequestContextHolder.setRequestAttributes(requestAttributes);
            /** 会员收获地址列表 **/
            List<MemberAddressVo> addressVos = memberFeignService.getReceiveAddresses(memberVo.getId());
            confirmVo.setMemberAddressVos(addressVos);
        }, executor);

        //查询购物车选中后商品最新价格
        CompletableFuture<Void> itemsFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            /** 所有选中的购物项价格**/
            List<OrderItemVo> orderItemVos = cartFeignService.getCheckedItems();
            confirmVo.setItems(orderItemVos);
        }, executor).thenRunAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            /** 所有选中的购物项库存 **/
            List<Long> collect = confirmVo.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.hasStock(collect);
            if (r.getCode() == 0) {
                List<SkuHasStockTo> data = r.getData(new TypeReference<List<SkuHasStockTo>>() {});
                if (data != null && data.size() > 0) {
                    Map<Long, Boolean> stocks = data.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
                    confirmVo.setStocks(stocks);
                }
            }
        }, executor);


        /** 发票记录 **/

        /** 优惠券（会员积分） **/
        CompletableFuture<Void> integrationFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            confirmVo.setIntegration(memberVo.getIntegration());
        }, executor);


        /** 防止重复提交的令牌 **/
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.ORDER_UNIQUE_TOKEN_KEY_PREFIX + memberVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(addressesFuture, itemsFuture, integrationFuture).get();
        return confirmVo;

    }

    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity order = orderCreateTo.getOrder();
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        this.save(order);
//        for (OrderItemEntity orderItem : orderCreateTo.getOrderItems()) {
//            orderItemService.save(orderItem);
//        }

        orderItemService.saveBatch(orderCreateTo.getOrderItems());
    }

    /**
     * 创建订单订单项
     *
     * @param memberVo 登录用户信息
     * @param vo       订单页面提交的信息
     * @return
     */
    private OrderCreateTo createOrderTo(MemberVo memberVo, OrderSubmitVo vo) {
        //生成订单id
        String orderSn = IdWorker.getTimeId();
        //构建订单
        OrderEntity orderEntity = buildOrder(memberVo, orderSn, vo);
        //构建订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        //计算价格
        compute(orderEntity, orderItemEntities);
        OrderCreateTo createTo = new OrderCreateTo();
        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);
        return createTo;

    }

    private void compute(OrderEntity entity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = BigDecimal.ZERO;
        //优惠价格
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        //积分
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total = total.add(orderItemEntity.getRealAmount());
            promotion = promotion.add(orderItemEntity.getPromotionAmount());
            integration = integration.add(orderItemEntity.getIntegrationAmount());
            coupon = coupon.add(orderItemEntity.getCouponAmount());
            integrationTotal += orderItemEntity.getGiftIntegration();
            growthTotal += orderItemEntity.getGiftGrowth();
        }

        entity.setTotalAmount(total);
        entity.setPromotionAmount(promotion);
        entity.setIntegrationAmount(integration);
        entity.setCouponAmount(coupon);
        entity.setIntegration(integrationTotal);
        entity.setGrowth(growthTotal);

        //付款价格=商品价格+运费
        entity.setPayAmount(entity.getFreightAmount().add(total));
        //设置删除状态(0-未删除，1-已删除)
        entity.setDeleteStatus(0);
    }

    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> checkedItems = cartFeignService.getCheckedItems();
        //查出这些sku所属的spu信息。
        Set<Long> collect = checkedItems.stream().map(OrderItemVo::getSkuId).collect(Collectors.toSet());
        List<Long> skuIds = new ArrayList<>(collect);

        R spuBySkuIds = productFeignService.getSpuBySkuIds(skuIds);
        Map<Long, SpuInfoTo> skuMapSpu = spuBySkuIds.getData(new TypeReference<Map<Long, SpuInfoTo>>() {
        });

        //构造订单项
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for (OrderItemVo checkedItem : checkedItems) {
            orderItemEntities.add(buildOrderItem(orderSn,checkedItem, skuMapSpu));
        }
        return orderItemEntities;
    }

    private OrderItemEntity buildOrderItem(String sn, OrderItemVo item, Map<Long, SpuInfoTo> spuInfoMap) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setSkuId(item.getSkuId());
        //sku相关属性
        orderItemEntity.setSkuId(item.getSkuId());
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttrValues(), ";"));
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());

        //spu相关属性
        SpuInfoTo spuInfo = spuInfoMap.get(item.getSkuId());
        orderItemEntity.setSpuId(spuInfo.getId());
        orderItemEntity.setSpuName(spuInfo.getSpuName());
        orderItemEntity.setSpuBrand(spuInfo.getBrandName());
        orderItemEntity.setCategoryId(spuInfo.getCatalogId());

        //商品的优惠信息(不做)

        //商品的积分成长，为价格x数量
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());

        //订单项订单价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //实际价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);

        orderItemEntity.setOrderSn(sn);

        return orderItemEntity;

    }

    private OrderEntity buildOrder(MemberVo memberVo, String orderSn, OrderSubmitVo vo) {
        //设置订单号
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);

        //设置个人信息
        orderEntity.setMemberId(memberVo.getId());
        orderEntity.setMemberUsername(memberVo.getUsername());

        //设置收货部分信息
        FareVo fareVo = wareFeignService.getFare(vo.getAddrId());
        orderEntity.setFreightAmount(fareVo.getFare());
        MemberAddressVo address = fareVo.getAddress();
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        orderEntity.setReceiverRegion(address.getRegion());


        //设置状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(0);
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

    @Data
    class SkuLockVo {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }

}