package com.angenao.gulimall.member.interceptor;

import com.angenao.common.constants.SessionKeyConstant;
import com.angenao.common.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: LoginInterceptor
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/16 23:42
 * @Version: 1.0
 **/

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberVo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean match1 = matcher.match("/order/order/info/**", requestURI);
        boolean match2 = matcher.match("/member/**", requestURI);
        if (match1||match2) return true;

        HttpSession session = request.getSession();

        MemberVo memberVo = (MemberVo) session.getAttribute(SessionKeyConstant.SESSION_USER_KEY);
        if (memberVo != null) {
            threadLocal.set(memberVo);
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }else {
            request.getSession().setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
