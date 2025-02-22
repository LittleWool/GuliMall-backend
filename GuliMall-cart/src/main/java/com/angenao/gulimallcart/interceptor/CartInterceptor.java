package com.angenao.gulimallcart.interceptor;

import com.angenao.common.constants.SessionKeyConstant;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimallcart.constant.CartConstant;
import com.angenao.gulimallcart.to.UserInfoTo;
import org.aspectj.weaver.ast.Instanceof;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @ClassName: CartInterceptor
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/14 9:38
 * @Version: 1.0
 **/

@Component
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal threadLocal = new ThreadLocal();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否登陆
        UserInfoTo userInfo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberVo member = (MemberVo)session.getAttribute(SessionKeyConstant.SESSION_USER_KEY);
        if (member != null) {
            //若已登录则存入id
            userInfo.setUserId(member.getId());
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(CartConstant.CART_USER_KEY)){
                userInfo.setUserKey(cookie.getValue());
                if (userInfo.getUserId() == null) {
                    //若id为空则说明未登陆，则为未登录用户
                    userInfo.setTempUser(true);
                }
            }
        }

        if (StringUtils.isEmpty(userInfo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfo.setUserKey(uuid);
            Cookie cookie=new Cookie(CartConstant.CART_USER_KEY,userInfo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.CART_ITEM_KEY_TIMEOUT);
            response.addCookie(cookie);
        }


        threadLocal.set(userInfo);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfo = (UserInfoTo) threadLocal.get();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);

    }


}
