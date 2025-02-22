package com.angenao.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: MemberOrderController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/19 23:48
 * @Version: 1.0
 **/

@Controller
public class MemberOrderController {

    @RequestMapping("/memberOrder.html")
    public String memberOrder(){
        return "memberOrder";
    }

}
