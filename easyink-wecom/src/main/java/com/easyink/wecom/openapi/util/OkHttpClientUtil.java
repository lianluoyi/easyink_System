
package com.easyink.wecom.openapi.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Slf4j
public class OkHttpClientUtil {

    private static final OkHttpClient clientInstance;

    private static final OkHttpClient clientInstanceWithRetry;

    private OkHttpClientUtil() {

    }

    static {
        Integer writeTimeout = 10;
        Integer readTimeout = 30;
        Integer connectTimeout = 20;
        clientInstance = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        clientInstanceWithRetry = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(64, 60, TimeUnit.SECONDS))
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor())
                .retryOnConnectionFailure(true)
                .build();
    }

    public static OkHttpClient getClientInstance() {
        return clientInstance;
    }


    /**
     * 使用Nginx转发请求，通常用于发送HTTPS链接，防止出现证书校验的乱七八糟的问题
     */
    public static String doPostWithNginxProxy(String url, byte[] dataBytes) {
        return doPost(url, dataBytes, Boolean.TRUE);
    }

    public static String doPost(String url, byte[] dataBytes) {
        return doPost(url, dataBytes, Boolean.FALSE);
    }

    /**
     * 发起POST请求
     *
     * @param url           url
     * @param dataBytes     请求体
     * @param useNginxProxy 是否使用Nginx转发
     * @return {@link String}
     */
    public static String doPost(String url, byte[] dataBytes, Boolean useNginxProxy) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url不能为空");
        }
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        //请求体传输json格式的数据
        RequestBody requestBody = RequestBody.create(json, dataBytes);
        //创建请求
        Map<String, String> headMaps = new HashMap();
        headMaps.put("Content-Type", "application/json");
        Headers setHeaders = setHeaders(headMaps);
        Request.Builder requestBuilder = new Request.Builder()
                .post(requestBody)
                .headers(setHeaders);
        Request request = requestBuilder.url(url).build();
        Call call = clientInstanceWithRetry.newCall(request);
        Response response = null;
        String result = null;
        try {
            response = call.execute();
            result = response.body().string();
        } catch (Exception e) {
            log.error("请求失败 http request url:{},error:{}", url, ExceptionUtils.getStackTrace(e));
        }
        return result;
    }


    public static Headers setHeaders(Map<String, String> headersParams) {
        Headers headers;
        Headers.Builder headersbuilder = new Headers.Builder();
        if (headersParams != null && headersParams.size() > 0) {
            Iterator<String> iterator = headersParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                headersbuilder.add(key, headersParams.get(key));
            }
        }
        headers = headersbuilder.build();
        return headers;
    }


    /**
     * 代理URL检测
     */
    public static boolean isProxyAvailable(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            // 返回状态码为 2xx 表示代理可用
            return true;
        } catch (Exception e) {
            log.info("代理{}不可用", url);
            // 连接失败，代理不可用
            return false;
        }
    }
}
