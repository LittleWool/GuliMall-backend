package com.angenao.gulimall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.angenao.common.constants.SessionKeyConstant;
import com.angenao.common.exception.BizCode;
import com.angenao.common.exception.UserNameExistException;
import com.angenao.common.utils.R;
import com.angenao.common.utils.RandomCodeGenerator;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.authserver.constant.AuthConstant;
import com.angenao.gulimall.authserver.feign.MemberFeignService;
import com.angenao.gulimall.authserver.feign.ThirdPartFeignService;
import com.angenao.gulimall.authserver.vo.UserLoginVo;
import com.angenao.gulimall.authserver.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tomcat.jni.BIOCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: LoginController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/8 16:34
 * @Version: 1.0
 **/

@Slf4j
@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendcode(@RequestParam("phone") String phone) {
        String code = RandomCodeGenerator.generateRandomCode();
        String key=AuthConstant.SMS_CODE_CACHE_PREFIX+phone;

        String res = stringRedisTemplate.opsForValue().get(key);
        //判断验证码发送间隔是否超过1min
        if (!StringUtils.isEmpty(res)) {
            String[] s = res.split("_");
            long start = Long.parseLong(s[1]);
            if (System.currentTimeMillis()-start<60000){
                return R.error(BizCode.SMS_CODE_EXCEPTION.getCode(),BizCode.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //TODO 接口防刷

        //设置过期时间
        String value=code+"_"+ System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(key,value,AuthConstant.SMS_CODE_EXPIREDTIME, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }

    @PostMapping("/register")
    public String regist(@Valid UserRegisterVo vo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            //注册参数校验出错
//            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach((fieldError) -> {errors.put(fieldError.getField(), fieldError.getDefaultMessage());});
            redirectAttributes.addFlashAttribute("errors", errors);

            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //格式校验通过，进行注册

        //1.验证码检查
        String key=AuthConstant.SMS_CODE_CACHE_PREFIX+vo.getPhone();
        String s = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(s)) {
            //验证码仍然有效
            String[] s1 = s.split("_");
            if (!s1[0].equals(vo.getCode())) {
                //验证码不正确
                Map<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }else {
                //删除使用过的验证码
                stringRedisTemplate.delete(key);
                R res = memberFeignService.register(vo);

                if (res.getCode()==0){
                    //注册服务调用成功
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    HashMap<String,String> errors = new HashMap<>();

                    errors.put("msg",res.getData("msg",new TypeReference<String>(){}));

                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }
        }else {
            //验证码不存在
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }


    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R login = memberFeignService.login(vo);
        if (login.getCode()==0){
            //进入主页
            session.setAttribute(SessionKeyConstant.SESSION_USER_KEY,login.getData(new TypeReference<MemberVo>(){}));
            return "redirect:http://gulimall.com";
        }else {
            //重新登陆

            String data = login.getData("msg",new TypeReference<String>() {});
            HashMap<String,String> errors = new HashMap<>();
            errors.put("msg",data);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}
