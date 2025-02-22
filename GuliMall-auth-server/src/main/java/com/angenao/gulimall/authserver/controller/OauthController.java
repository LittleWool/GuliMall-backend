package com.angenao.gulimall.authserver.controller;




import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.angenao.common.constants.SessionKeyConstant;
import com.angenao.common.utils.R;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.authserver.feign.MemberFeignService;
import com.angenao.gulimall.authserver.to.SocialUserTo;
import com.angenao.gulimall.authserver.vo.utils.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.HashMap;


/**
 * @ClassName: OauthController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/12 7:52
 * @Version: 1.0
 **/

@Controller
public class OauthController {



    @Autowired
    MemberFeignService  memberFeignService;


    @GetMapping("/oauth2.0/gitee/success")
    public String oauth2Login(@RequestParam("code") String code, HttpSession session) throws Exception {

        String redirectUrl = "http://auth.gulimall.com/oauth2.0/gitee/success";
        String host="https://gitee.com";
        String path="/oauth/token";
        HashMap<String,String> map=new HashMap<>();
        map.put("code",code);
        map.put("redirect_uri",redirectUrl);
        map.put("grant_type","authorization_code");
        map.put("client_id","cf91e78c3c1648eb02b58fbfb85428ae91d2090300ce6a4eabbb78aeec7efb5a");
        map.put("client_secret","ddd9161b3d667b59ea05b2cba4834c2be57689479ea6895de2551514118d9c79");
        HttpResponse response = HttpUtils.doPost(host, path, "POST", new HashMap<>(), new HashMap<>(), map);

        if (response.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(response.getEntity());
            SocialUserTo socialUserTo = JSON.parseObject(json, SocialUserTo.class);
            System.out.println("oauthGiteeTokenEntity"+socialUserTo);
            String infoUrl="/api/v5/user";
            HashMap<String,String> map2=new HashMap<>();
            map2.put("access_token",socialUserTo.getAccessToken());
            HttpResponse res = HttpUtils.doGet(host, infoUrl, "GET", new HashMap<>(), map2);
            if (res.getStatusLine().getStatusCode() == 200) {
                //查询授权用户详细信息
                json = EntityUtils.toString(res.getEntity());
                SocialUserTo userInfo = JSON.parseObject(json, SocialUserTo.class);
                userInfo.setAccessToken(socialUserTo.getAccessToken());
                userInfo.setScope(socialUserTo.getScope());
                userInfo.setExpiresIn(socialUserTo.getExpiresIn());
                userInfo.setRefreshToken(socialUserTo.getRefreshToken());
                userInfo.setTokenType(socialUserTo.getTokenType());
                R r= memberFeignService.login(userInfo);
                if (r.getCode()==0){
                    //登陆成功
                    MemberVo memberVo=r.getData("data",new TypeReference<MemberVo>(){});
                    System.out.println("登陆成功:"+memberVo);
                    session.setAttribute(SessionKeyConstant.SESSION_USER_KEY,memberVo);
                    return "redirect:http://gulimall.com";
                }
            }

        }


        return "redirect:http://auth.gulimall.com/login.html";
    }

}
