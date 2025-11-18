package com.easyink.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp工具类 - 用于发送HTTP POST请求
 * 
 * @author easyink
 */
@Slf4j
public class OkHttpUtil {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int DEFAULT_TIMEOUT = 30; // 默认超时时间：30秒

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP响应结果封装类
     */
    @Data
    public static class HttpResponse {
        /** 响应状态码 */
        private int code;
        /** 响应内容 */
        private String body;
        /** 是否成功（2xx状态码） */
        private boolean success;
        /** 错误信息 */
        private String errorMessage;

        public HttpResponse(int code, String body, boolean success) {
            this.code = code;
            this.body = body;
            this.success = success;
        }

        public HttpResponse(int code, String body, boolean success, String errorMessage) {
            this.code = code;
            this.body = body;
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * 发送POST请求（JSON格式）
     *
     * @param url 请求地址
     * @param jsonData JSON字符串数据
     * @return HttpResponse 响应结果
     */
    public static HttpResponse postJson(String url, String jsonData) {
        return postJson(url, jsonData, null);
    }

    /**
     * 发送POST请求（JSON格式）
     *
     * @param url 请求地址
     * @param jsonData JSON字符串数据
     * @param headers 请求头
     * @return HttpResponse 响应结果
     */
    public static HttpResponse postJson(String url, String jsonData, Map<String, String> headers) {
        if (StringUtils.isEmpty(url)) {
            return new HttpResponse(0, null, false, "请求URL不能为空");
        }

        RequestBody body = RequestBody.create(JSON, jsonData == null ? "{}" : jsonData);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        // 添加请求头
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(requestBuilder::addHeader);
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            String responseBody = response.body() != null ? response.body().string() : "";
            boolean success = response.isSuccessful();

            log.info("POST请求: {}, 响应码: {}, 成功: {}", url, code, success);

            if (success) {
                return new HttpResponse(code, responseBody, true);
            } else {
                String errorMsg = String.format("HTTP请求失败，状态码: %d", code);
                log.error("POST请求失败: {}, 响应: {}", url, responseBody);
                return new HttpResponse(code, responseBody, false, errorMsg);
            }
        } catch (IOException e) {
            log.error("POST请求异常: {}", url, e);
            return new HttpResponse(0, null, false, "网络请求异常: " + e.getMessage());
        }
    }

    /**
     * 发送POST请求（对象转JSON）
     *
     * @param url 请求地址
     * @param data 要发送的对象数据
     * @return HttpResponse 响应结果
     */
    public static HttpResponse postObject(String url, Object data) {
        return postObject(url, data, null);
    }

    /**
     * 发送POST请求（对象转JSON）
     *
     * @param url 请求地址
     * @param data 要发送的对象数据
     * @param headers 请求头
     * @return HttpResponse 响应结果
     */
    public static HttpResponse postObject(String url, Object data, Map<String, String> headers) {
        try {
            String jsonData = data == null ? "{}" : objectMapper.writeValueAsString(data);
            return postJson(url, jsonData, headers);
        } catch (Exception e) {
            log.error("对象转JSON失败", e);
            return new HttpResponse(0, null, false, "对象序列化失败: " + e.getMessage());
        }
    }

    /**
     * 发送POST请求（表单格式）
     *
     * @param url 请求地址
     * @param formParams 表单参数
     * @return HttpResponse 响应结果
     */
    public static HttpResponse postForm(String url, Map<String, String> formParams) {
        return postForm(url, formParams, null);
    }

    /**
     * 发送POST请求（表单格式）
     *
     * @param url 请求地址
     * @param formParams 表单参数
     * @param headers 请求头
     * @return HttpResponse 响应结果
     */
    public static HttpResponse postForm(String url, Map<String, String> formParams, Map<String, String> headers) {
        if (StringUtils.isEmpty(url)) {
            return new HttpResponse(0, null, false, "请求URL不能为空");
        }

        FormBody.Builder formBuilder = new FormBody.Builder();
        if (formParams != null && !formParams.isEmpty()) {
            formParams.forEach(formBuilder::add);
        }

        RequestBody body = formBuilder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        // 添加请求头
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(requestBuilder::addHeader);
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            String responseBody = response.body() != null ? response.body().string() : "";
            boolean success = response.isSuccessful();

            log.info("POST表单请求: {}, 响应码: {}, 成功: {}", url, code, success);

            if (success) {
                return new HttpResponse(code, responseBody, true);
            } else {
                String errorMsg = String.format("HTTP请求失败，状态码: %d", code);
                log.error("POST表单请求失败: {}, 响应: {}", url, responseBody);
                return new HttpResponse(code, responseBody, false, errorMsg);
            }
        } catch (IOException e) {
            log.error("POST表单请求异常: {}", url, e);
            return new HttpResponse(0, null, false, "网络请求异常: " + e.getMessage());
        }
    }
}
