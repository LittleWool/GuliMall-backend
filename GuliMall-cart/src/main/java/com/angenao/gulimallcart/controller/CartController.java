package com.angenao.gulimallcart.controller;

import com.angenao.gulimallcart.interceptor.CartInterceptor;
import com.angenao.gulimallcart.service.CartService;
import com.angenao.gulimallcart.to.UserInfoTo;
import com.angenao.gulimallcart.vo.CartItemVo;
import com.angenao.gulimallcart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * @ClassName: test
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/13 21:13
 * @Version: 1.0
 **/

@Controller
public class CartController {

    @Autowired
    CartService cartService;


    @RequestMapping("/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes attributes) {
        //返回的结果暂时没用
        CartItemVo cartItemVo=  cartService.addCartItem(skuId, num);
        //如果不重定向直接返回，刷新success界面就会重新发送请求
        //如果使用重定向存放数据的话的话，多次刷新该属性就会消失
        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addCartItemSuccess";
    }

    @RequestMapping("/cart.html")
    public String getCartList(Model model) {
        CartVo cartVo=cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }


    @RequestMapping("/addCartItemSuccess")
    public String addCartItemSuccess(@RequestParam("skuId") Long skuId,Model model) {
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "success";
    }


    @RequestMapping("/checkCart")
    public String checkCart(@RequestParam("isChecked") Integer isChecked,@RequestParam("skuId")Long skuId) {
        cartService.checkCart(skuId, isChecked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @RequestMapping("/countItem")
    public String changeItemCount(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @RequestMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @ResponseBody
    @RequestMapping("/getCheckedItems")
    public List<CartItemVo> getCheckedItems() {
        return cartService.getCheckedItems();
    }



}
