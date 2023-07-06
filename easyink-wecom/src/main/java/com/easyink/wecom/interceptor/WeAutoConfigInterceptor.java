package com.easyink.wecom.interceptor;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.exception.wecom.WeComException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 类名: 企业微信后台接口拦截器
 *
 * @author: 1*+
 * @date: 2021-08-23$ 11:00$
 */
@Component
@Slf4j
public class WeAutoConfigInterceptor implements Interceptor<Object> {

    private final RedisCache redisCache;
    /**
     * 短信验证
     */
    private final static String CONFIRM_CAPTCHA = "confirm_captcha";

    @Lazy
    @Autowired
    public WeAutoConfigInterceptor(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     */
    @Override
    public boolean beforeExecute(ForestRequest request) {
        if (isPreLoginUrl(request.getUrl())) {
            //登陆前
            request.addQuery("r", randomPointRString(16));
        } else if (request.getUrl().contains("/loginpage_wx")) {
            //登陆
            request.addQuery("_r", randomRString());
        } else if (isMobileConfirmUrl(request.getUrl())) {
            //短信验证
            setConfirmReq(request);
        } else if (request.getUrl().contains("/choose_corp")) {
            // 短信验证后选择企业
            setCheckReq(request);
        } else if (request.getUrl().contains("/saveIpConfig")){
            // 自动配置可信IP
            setTrustIp(request);
        } else {
            //登陆后
            setCommonReq(request);
            String d2st = getD2ct(request);
            request.addBody("_d2st", d2st);
            // 目前看好像就只有获取企业部门成员的接口需要的queryString
            request.addQuery("_d2st", d2st) ;
        }
        return true;
    }

    /**
     * 配置可信IP
     * @param request 请求头
     */
    private void setTrustIp(ForestRequest request) {
        setCommonReq(request);
        String d2st = getD2ct(request);
        request.addBody("_d2st", d2st);
        // 目前看好像就只有获取企业部门成员的接口需要的queryString
        request.addQuery("_d2st", d2st);
        // 从Header获取可信IP
        String ipList = request.getHeaderValue("ipList");
        // 重排ipList格式
        if (StringUtils.isNotBlank(ipList)) {
            for (String trustIp : ipList.split(";")) {
                request.addBody("ipList[]", trustIp);
            }
        }
    }

    /**
     * 设置check 请求的请求头
     *
     * @param request 请求头
     */
    private void setCheckReq(ForestRequest request) {
        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-site", "same-origin");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-fetch-user", "?1");
        request.addHeader("sec-fetch-dest", "document");
        request.addHeader("accept-language", "zh-CN,zh;q=0.9");
        request.addHeader("upgrade-insecure-requests", "1");
        request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        request.addHeader("accept-encoding", "gzip, deflate, br");
        request.addHeader("accept", "*/*")
               .addHeader("x-requested-with", "XMLHttpRequest")
               .addHeader("sec-fetch-mode", "cors")
               .addHeader("accept-encoding", "gzip, deflate, br");
    }


    /**
     * 判断是否是短信验证的链接
     *
     * @param url 链接 url
     * @return true 是短信验证的链接
     */
    private boolean isMobileConfirmUrl(String url) {
        return StringUtils.isNotBlank(url) && url.contains("/mobile_confirm");
    }

    /**
     * 为短信验证设置相关的请求参数
     *
     * @param request 请求体
     */
    private void setConfirmReq(ForestRequest request) {
        request.addQuery("lang", "zh_CN");
        request.addQuery("ajax", 1);
        request.addQuery("f", "json");
        // 6位随机数
        request.addQuery("random", (int) ((Math.random() * 9 + 1) * 100000));
        request.addHeader("sec-fetch-site", "same-origin");
        request.addHeader("sec-fetch-mode", "cors");
        request.addHeader("sec-fetch-dest", "empty");
        request.addHeader("accept-encoding", "gzip,deflate,br");
        request.addHeader("accept-language", "zh-CN,zh;q=0.9");
        request.addHeader("content-type", "application/json");
        request.addHeader("accept-encoding", "gzip, deflate, br");
    }


    /**
     * 请求发送失败时被调用
     *
     * @param e              失败异常
     * @param forestRequest  请求结构体
     * @param forestResponse 返回结构体体
     */
    @Override
    public void onError(ForestRuntimeException e, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.error("请求失败url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
        throw new WeComException("官方接口请求过于频繁，稍后再试");
    }

    public void onRedirection(ForestRequest<?> redirectReq, ForestRequest<?> prevReq, ForestResponse<?> prevRes) {
        log.info("请求重定向,url:{} ,重定向到:{}" , prevReq.getUrl(), redirectReq.getUrl());
        if(prevReq.getUrl().contains("/choose_corp")  || prevReq.getUrl().contains("loginpage_wx")) {
            String qrcodeKey = prevReq.getHeader("qrcodeKey") != null? String.valueOf(prevReq.getHeader("qrcodeKey").getValue()) : (String) prevReq.getQuery("qrcode_key");
            redirectReq.addHeader("qrcodeKey",qrcodeKey);
        }
    }

    /**
     * 请求成功调用(微信端错误异常统一处理)
     *
     * @param o              方法实际返回对象,对应每个方法的Result
     * @param forestRequest  请求结构体
     * @param forestResponse 返回结构体体
     */
    @Override
    public void onSuccess(Object o, ForestRequest forestRequest, ForestResponse forestResponse) {
        log.info("url:【{}】,result:【{}】", forestRequest.getUrl(), forestResponse.getContent());
    }

    /**
     * 保存Cookie
     *
     * @param request 请求结构体
     * @param cookies Cookie
     */
    @Override
    public void onSaveCookie(ForestRequest request, ForestCookies cookies) {
        if (isPreLoginUrl(request.getUrl())) {
            return;
        }
        String redisKey = getQrcodeKey(request);
        List<ForestCookie> cookieList = cookies.allCookies();
        redisCache.setCacheObject(redisKey, JSON.toJSONString(cookieList), 30, TimeUnit.MINUTES);
        // 如果是登录页则需要
    }

    /**
     * 加载Cookie
     *
     * @param request 请求结构体
     * @param cookies Cookie
     */
    @Override
    public void onLoadCookie(ForestRequest request, ForestCookies cookies) {
        if (isPreLoginUrl(request.getUrl())) {
            return;
        }
        String redisKey = getQrcodeKey(request);
        String val = redisCache.getCacheObject(redisKey);

        List<ForestCookie> cookieList = new ArrayList<>();
        if (StringUtils.isNotBlank(val)){
            cookieList = JSON.parseArray(val,ForestCookie.class);
        }

        if (!CollectionUtils.isEmpty(cookieList)) {
            cookies.addAllCookies(cookieList);
        }
    }

    /**
     * 生成请求中的r参数
     *
     * @return 随机字符串
     */
    private String randomPointRString(int numCount) {
        StringBuilder uid = new StringBuilder();
        uid.append("0.");
        //产生16位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < numCount; i++) {
            uid.append(rd.nextInt(10));
        }
        return uid.toString();
    }

    /**
     * 生成请求中的r参数
     *
     * @return 随机字符串
     */
    private String randomRString() {
        StringBuilder uid = new StringBuilder();
        int numCount = 3;
        //产生16位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < numCount; i++) {
            uid.append(rd.nextInt(10));
        }
        return uid.toString();
    }

    /**
     * 从Cookie中获取D2ct的值
     *
     * @param request 请求结构体
     * @return D2ct的值
     */
    private String getD2ct(ForestRequest request) {
        String redisKey = getQrcodeKey(request);
        String val = redisCache.getCacheObject(redisKey);

        List<ForestCookie> cookieList = new ArrayList<>();
        if (StringUtils.isNotBlank(val)){
            cookieList = JSON.parseArray(val,ForestCookie.class);
        }
        if (CollectionUtils.isEmpty(cookieList)) {
            return "";
        }
        for (ForestCookie cookie : cookieList) {
            if ("wwrtx.d2st".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }

    /**
     * 设置通用请求头
     *
     * @param request 请求结构体
     */
    private void setCommonReq(ForestRequest request) {
        request.addQuery("lang", "zh_CN");
        request.addQuery("f", "json");
        request.addQuery("ajax", 1);
        request.addQuery("timeZoneInfo%5Bzone_offset%5D", -8);
        request.addQuery("random", randomPointRString(17));
        request.addHeader("referer", "https://work.weixin.qq.com/wework_admin/frame");
    }

    /**
     * 是否登陆前请求的URL
     *
     * @param url url
     * @return {@link Boolean}
     */
    private boolean isPreLoginUrl(String url) {
        if (url.contains("/wwqrlogin/mng/get_key") || url.contains("/wwqrlogin/mng/check")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 获取后台二维码对应的Key
     *
     * @param request {@link ForestRequest}
     * @return {@link String}
     */
    private String getQrcodeKey(ForestRequest request) {
        String key;
        if (request.getUrl().contains("/loginpage_wx")) {
            key = (String) request.getQuery("qrcode_key");
        } else {
            key = request.getHeaderValue("qrcodeKey");
        }
        return "Admin_Cookie:" + key;
    }


}
