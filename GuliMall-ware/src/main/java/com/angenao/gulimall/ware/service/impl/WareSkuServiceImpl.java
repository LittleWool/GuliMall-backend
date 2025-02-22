package com.angenao.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.angenao.common.mq.OrderTo;
import com.angenao.common.mq.StockDetailTo;
import com.angenao.common.mq.StockLockedTo;
import com.angenao.common.to.SkuHasStockTo;
import com.angenao.common.utils.R;
import com.angenao.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.angenao.gulimall.ware.entity.WareOrderTaskEntity;
import com.angenao.gulimall.ware.enums.OrderStatusEnum;
import com.angenao.gulimall.ware.enums.WareTaskStatusEnum;
import com.angenao.gulimall.ware.exception.NoStockException;
import com.angenao.gulimall.ware.feign.OrderFeignService;
import com.angenao.gulimall.ware.feign.ProductServiceFein;
import com.angenao.gulimall.ware.vo.OrderItemVo;
import com.angenao.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.ware.dao.WareSkuDao;
import com.angenao.gulimall.ware.entity.WareSkuEntity;
import com.angenao.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductServiceFein productServiceFein;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final WareSkuDao wareSkuDao;
    @Autowired
    private WareOrderTaskDetailServiceImpl wareOrderTaskDetailService;
    @Autowired
    private WareOrderTaskServiceImpl wareOrderTaskService;
    @Autowired
    private OrderFeignService orderFeignService;


    public WareSkuServiceImpl(WareSkuDao wareSkuDao) {
        this.wareSkuDao = wareSkuDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //若是没库存则是新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            R info;
            info = productServiceFein.info(skuId);
            Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
            if (info.getCode() == 0) {
                wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
            }

            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

    @Override
    public List<SkuHasStockTo> getHastStock(List<Long> ids) {
        List<SkuHasStockTo> hasStock = this.baseMapper.getHasStock(ids);

        return hasStock;
    }

    /**
     * 锁定库存
     * 运行时异常都会回滚
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public boolean lockOrderStock(WareSkuLockVo vo) throws NoStockException {
        List<OrderItemVo> locks = vo.getLocks();

        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        taskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(taskEntity);

        //查库存
        List<SkuLockVo> skuLockVos = locks.stream().map(item -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(item.getSkuId());
            skuLockVo.setNum(item.getCount());
            //找出所有库存大于商品数的仓库
            List<Long> wareIds = baseMapper.listWareIdsHasStock(item.getSkuId(), item.getCount());
            skuLockVo.setWareIds(wareIds);
            return skuLockVo;
        }).collect(Collectors.toList());

        //锁定库存
        boolean allLocked = true;
        skuLockVos.forEach(skuLockVo -> {
            int need = skuLockVo.getNum();
            List<Long> wareIds = skuLockVo.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuLockVo.getSkuId().toString());
            } else {
                boolean lock = true;
                for (Long wareId : wareIds) {
                    int count = baseMapper.tryLockStock(skuLockVo.getSkuId(), skuLockVo.getNum(), wareId);
                    if (count == 0) {
                        lock = false;
                    } else {
                        //锁定库存成功
                        //锁定成功，发送消息，保存库存单
                        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                        wareOrderTaskDetailEntity.setSkuId(skuLockVo.getSkuId());
                        wareOrderTaskDetailEntity.setSkuName("");
                        wareOrderTaskDetailEntity.setWareId(wareId);
                        wareOrderTaskDetailEntity.setLockStatus(1);
                        wareOrderTaskDetailEntity.setSkuNum(skuLockVo.getNum());
                        wareOrderTaskDetailEntity.setTaskId(taskEntity.getId());
                        wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);

                        StockDetailTo stockDetailTo = new StockDetailTo();
                        BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);

                        StockLockedTo stockLockedTo = new StockLockedTo();
                        stockLockedTo.setId(taskEntity.getId());
                        stockLockedTo.setDetailTo(stockDetailTo);

                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);

                        lock = true;

                        break;
                    }
                }
                if (lock == false) {
                    //所有的仓库库存都不足
                    throw new NoStockException(skuLockVo.getSkuId().toString());
                }

            }
        });
        return allLocked;
    }

    @Override
    public void unLock(StockLockedTo stockLockedTo) {
        Long taskId = stockLockedTo.getId();
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(stockLockedTo.getId());
        WareOrderTaskDetailEntity taskDetail = wareOrderTaskDetailService.getById(stockLockedTo.getDetailTo().getId());

        if (taskDetail != null) {
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getByOrderSn(orderSn);
            if (r.getCode() == 0) {
                //查询订单成功
                OrderTo data = r.getData(new TypeReference<OrderTo>() {
                });
                if (data == null || data.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                    //判断该库存单是否已经解锁
                    if (taskDetail.getLockStatus() == WareTaskStatusEnum.Locked.getCode()) {
                        unlockStock(taskDetail.getSkuId(), taskDetail.getSkuNum(), taskDetail.getWareId(), taskDetail.getId());
                    }
                }
            } else {
                //抛出异常重新入队，等待其他人解锁
                throw new RuntimeException("远程调用订单服务失败");
            }
        } else {
            //如果为空，说明创建任务单时已经出现问题，会自动回滚
        }
    }

    @Override
    public void unLock(OrderTo orderTo) {

        String orderSn = orderTo.getOrderSn();
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_Sn", orderTo.getOrderSn()));
        List<WareOrderTaskDetailEntity> detailEntities=wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskEntity.getId()));
        for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
            unlockStock(detailEntity.getSkuId(), detailEntity.getSkuNum(), detailEntity.getWareId(), detailEntity.getId());
        }
    }


    private void unlockStock(Long skuId, Integer skuNum, Long wareId, Long taskDetailId) {
        baseMapper.unlockStock(skuId, skuNum, wareId);
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        taskDetailEntity.setLockStatus(WareTaskStatusEnum.hasUnLocked.getCode());
        //更新任务单状态
        wareOrderTaskDetailService.updateById(taskDetailEntity);

    }

    @Data
    class SkuLockVo {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }
}