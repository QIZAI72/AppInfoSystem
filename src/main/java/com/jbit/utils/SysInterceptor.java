package com.jbit.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 */
public class SysInterceptor implements HandlerInterceptor {
    /**
     * 进入请求方法之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 目前 session dev (userdev)  backend (backuser)
        Object devuser = request.getSession().getAttribute("devuser");
        Object backuser = request.getSession().getAttribute("backuser");
        if (devuser!=null||backuser!=null){
            return true;
        }
        response.sendRedirect("/index.jsp");
//        if (request.getRequestURI().startsWith("/dev")){
//            // 获取session (dev)
//            Object devuser = request.getSession().getAttribute("devuser");
//            if (devuser!=null){
//                return true;
//            }
//            response.sendRedirect("/jsp/devlogin.jsp");
//        }else if (request.getRequestURI().startsWith("/backuser")){
//            // 获取session (backuser)
//            Object backuser = request.getSession().getAttribute("backuser");
//            if (backuser!=null){
//                return true;
//            }
//            response.sendRedirect("/jsp/backendlogin.jsp");
//        }else {
//            Object devuser = request.getSession().getAttribute("devuser");
//            Object backuser = request.getSession().getAttribute("backuser");
//            if (devuser!=null||backuser!=null){
//                return true;
//            }
//        }
        return false;
    }
}
