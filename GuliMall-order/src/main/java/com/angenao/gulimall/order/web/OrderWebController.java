package com.angenao.gulimall.order.web;

import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.order.exception.NoStockException;
import com.angenao.gulimall.order.service.OrderService;
import com.angenao.gulimall.order.vo.OrderConfirmVo;
import com.angenao.gulimall.order.vo.OrderSubmitVo;
import com.angenao.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName: OrderWebController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/16 23:33
 * @Version: 1.0
 **/
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    //创建订单
    @RequestMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo =orderService.confirmOrder() ;
        model.addAttribute("confirmOrder",confirmVo);
        return "confirm";
    }

    //提交订单
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        try {
            SubmitOrderResponseVo res=orderService.submitOrder(vo);
            model.addAttribute("order", res.getOrder());
            System.out.println("订单提交数据"+vo);
            if (res.getCode()==0){
                //下单成功来到支付页
                return "pay";
            }else {
                String msg="下单失败";
                switch (res.getCode()){
                    case 1:msg+="订单信息过期请刷新后提交" ;break;
                    case 2:msg+="商品价格发生变化请刷新后提交";break;
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        }catch (Exception e){
            if (e instanceof NoStockException){
                String msg = "下单失败，商品无库存";
                redirectAttributes.addFlashAttribute("msg", msg);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }


    }

    /**
     * 获取当前用户的所有订单
     * @return
     */
    @RequestMapping("/memberOrder.html")
    public String memberOrder(@RequestParam(value = "pageNum",required = false,defaultValue = "0") Integer pageNum, Model model){
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        PageUtils page = orderService.getMemberOrderPage(params);
        model.addAttribute("pageUtil", page);
        return "list";
    }

}
