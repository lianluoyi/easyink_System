package com.easywecom.framework.interceptor.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 清除当前线程的pageHelper参数
 *
 * @author : silver_chariot
 * @date : 2021/10/15 15:47
 */
@Component
public class ClearPageHelperParamInterceptor extends HandlerInterceptorAdapter {

    /**
     * 拦截所有请求 ,判断当前线程是否存在未清除的PageHelper变量, 如果存在则清除这些变量,
     * 防止对后续的数据库查询造成异常
     *
     * @param request  servlet请求
     * @param response servlet响应
     * @param handler  处理器
     * @return always true
     * @throws Exception e
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            if (PageHelper.getLocalPage() != null) {
                PageHelper.clearPage();
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }
}
