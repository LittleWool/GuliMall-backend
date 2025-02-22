package com.angenao.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.angenao.gulimall.order.config.AlipayTemplate;
import com.angenao.gulimall.order.service.OrderService;
import com.angenao.gulimall.order.vo.PayAsyncVo;
import com.angenao.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/aliPayOrder",produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        System.out.println("接收到订单信息orderSn："+orderSn);
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

    @RequestMapping("/payed/notify")
    public String handlePayedNotify(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
        System.out.println(vo);
        String res=orderService.handlePayResult(vo);
        return res;
    }


}
