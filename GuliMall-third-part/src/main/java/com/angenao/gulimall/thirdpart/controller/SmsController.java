package com.angenao.gulimall.thirdpart.controller;

import com.angenao.common.utils.R;
import com.angenao.gulimall.thirdpart.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: SmsController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/10 17:06
 * @Version: 1.0
 **/

@Controller
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsComponent smsComponent;

    @GetMapping("/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code,10);
        return R.ok();
    }


}
