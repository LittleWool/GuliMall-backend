package com.angenao.gulimall.authserver.feign;

import com.angenao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ThirdPartFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/10 17:42
 * @Version: 1.0
 **/

@FeignClient("gulimall-third-part")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
