package com.angenao.gulimallcart.service;

import com.angenao.gulimallcart.vo.CartItemVo;
import com.angenao.gulimallcart.vo.CartVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: CartService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/14 12:05
 * @Version: 1.0
 **/
@Service
public interface CartService {
    //向购物车加入商品
    CartItemVo addCartItem(Long skuId, Integer num);

    //获取购物车
    CartVo getCart();

    //获取购物车中某个商品
    CartItemVo getCartItem(Long skuId);

    //获取key购物车中所有商品
    List<CartItemVo> getCartByKey(String key);

    //清空购物车
    void clearCart(String key);


    void checkCart(Long skuId, Integer isChecked);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItemVo> getCheckedItems();


}
