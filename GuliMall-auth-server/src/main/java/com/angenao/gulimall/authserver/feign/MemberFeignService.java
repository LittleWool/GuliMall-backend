package com.angenao.gulimall.authserver.feign;

import com.angenao.common.utils.R;
import com.angenao.gulimall.authserver.to.SocialUserTo;
import com.angenao.gulimall.authserver.vo.SocialUser;
import com.angenao.gulimall.authserver.vo.UserLoginVo;
import com.angenao.gulimall.authserver.vo.UserRegisterVo;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: MemberFeignService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/11 12:33
 * @Version: 1.0
 **/

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    public R register(@RequestBody UserRegisterVo vo);


    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oath2/login")
    public R login(@RequestBody SocialUserTo socialUserTo);

}
