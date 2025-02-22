package com.angenao.gulimallcart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.angenao.common.utils.R;
import com.angenao.gulimallcart.exception.CartExceptionHandler;
import com.angenao.gulimallcart.feign.ProductFeignService;
import com.angenao.gulimallcart.interceptor.CartInterceptor;
import com.angenao.gulimallcart.service.CartService;
import com.angenao.gulimallcart.to.UserInfoTo;
import com.angenao.gulimallcart.vo.CartItemVo;
import com.angenao.gulimallcart.vo.CartVo;
import com.angenao.gulimallcart.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.angenao.gulimallcart.constant.CartConstant.CART_PREFIX;

/**
 * @ClassName: CartServiceimpl
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/14 12:06
 * @Version: 1.0
 **/
@Service
public class CartServiceimpl implements CartService {

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public CartItemVo addCartItem(Long skuId, Integer num) {
        //1.把sku加入到购物车中
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemJson = (String) cartOps.get(skuId.toString());
        if (!StringUtils.isEmpty(cartItemJson)) {
            //购物车已有该商品数量+1重新放回
            CartItemVo cartItemVo = JSON.parseObject(cartItemJson, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));
            return cartItemVo;

        }

        //2如果购物车没此件商品则查询加入
        CartItemVo cartItemVo = new CartItemVo();
        //2，1查询sku基本信息
        CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
            R info = productFeignService.info(skuId);
            if (info.getCode() == 0) {
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItemVo.setSkuId(skuId);
                cartItemVo.setCount(num);
                cartItemVo.setPrice(skuInfo.getPrice());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                //默认选中
            }
        }, threadPoolExecutor);

        //2.2查询sku销售属性
        //查询sku属性
        CompletableFuture<Void> skuAttrFuture = CompletableFuture.runAsync(() -> {
            List<String> skuSaleAttrValuesAsString = productFeignService.getSkuSaleAttrValuesAsString(skuId);
            cartItemVo.setSkuAttrValues(skuSaleAttrValuesAsString);
        }, threadPoolExecutor);


        //放入redis
        try {
            //阻塞等待上面两个异步线程完成
            CompletableFuture.allOf(skuInfoFuture,skuAttrFuture).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));
        return cartItemVo;

    }

    @Override
    public CartVo getCart() {
        ThreadLocal threadLocal = CartInterceptor.threadLocal;
        UserInfoTo userInfo = (UserInfoTo) threadLocal.get();
        CartVo cartVo = new CartVo();
        if (userInfo.getUserId()==null) {
            //未登录
            String key=userInfo.getUserKey();
            List<CartItemVo> cartByKey = getCartByKey(key);
            cartVo.setItems(cartByKey);
            return cartVo;
        }else {
            //已经登录
            //将临时的cart合并到用户cart
            List<CartItemVo> tempCart= getCartByKey(userInfo.getUserKey());
            if (tempCart.size()>0) {
                for (CartItemVo cartItemVo : tempCart) {
                    addCartItem(cartItemVo.getSkuId(),cartItemVo.getCount());
                }
                //清除临时购物车
                clearCart(userInfo.getUserKey());
            }
            List<CartItemVo> userCart = getCartByKey(userInfo.getUserId().toString());
            cartVo.setItems(userCart);
            return cartVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        String s = (String)getCartOps().get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(s, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public List<CartItemVo> getCartByKey(String key) {
        List<CartItemVo> cartItemVos = new ArrayList<>();
        List<Object> values = stringRedisTemplate.boundHashOps(CART_PREFIX + key).values();
        if (values != null && values.size() > 0) {
            for (Object value : values) {
                CartItemVo cartItemVo = JSON.parseObject((String) value, CartItemVo.class);
                cartItemVos.add(cartItemVo);
            }
        }
        return cartItemVos;
    }

    @Override
    public void clearCart(String key) {
        stringRedisTemplate.delete(CART_PREFIX+key);
    }


    //获取临时/正式用户的购物车
    private BoundHashOperations<String, Object, Object> getCartOps() {
        ThreadLocal threadLocal = CartInterceptor.threadLocal;
        String key = "";
        UserInfoTo userInfoTo = (UserInfoTo) threadLocal.get();
        if (userInfoTo.getTempUser()) {
            //临时用户
            key = CART_PREFIX + userInfoTo.getUserKey();
        } else {
            key = CART_PREFIX + userInfoTo.getUserId();
        }
        return stringRedisTemplate.boundHashOps(key);
    }


    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        String cartJson = (String) ops.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
        cartItemVo.setCheck(isChecked==1);
        ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        String cartJson = (String) ops.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
        cartItemVo.setCount(num);
        ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        ops.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getCheckedItems() {
        UserInfoTo userInfoTo = (UserInfoTo) CartInterceptor.threadLocal.get();
        List<CartItemVo> cartByKey = getCartByKey(userInfoTo.getUserId().toString());
        List<CartItemVo> collect = cartByKey.stream().filter(CartItemVo::getCheck).collect(Collectors.toList());
        List<Long> ids = collect.stream().map(e -> e.getSkuId()).collect(Collectors.toList());
        Map<Long,BigDecimal> price= productFeignService.getCurrentPrice(ids);
        for (CartItemVo cartItemVo : collect) {
            cartItemVo.setPrice(price.get(cartItemVo.getSkuId()));
        }
        return collect;
    }




}
