package com.easyink.wecom.openapi.util;

import com.easyink.common.exception.CustomException;
import jodd.util.ThreadUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * RetryInterceptor
 *
 * @author 1*+
 * @date 2023/7/11 10:07 AM
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class RetryInterceptor implements Interceptor {

    /**
     * 最大重试次数
     */
    private Integer MAX_RETRIES = 1;
    /**
     * 重试间隔
     */
    private Long RETRY_INTERVAL = 1000L;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                response = chain.proceed(request);
                // 判断响应码是否是非 200
                if (!response.isSuccessful()) {
                    //检测rsp code，需不需要继续重试
                    if (checkRspCode(response.code())) {
                        return response;
                    }
                    //等待设置请求间隔时间
                    ThreadUtil.sleep(RETRY_INTERVAL);
                    // 增加重试次数
                    retryCount++;
                    //读取body
                    String body = response.body() != null ? response.body().string() : "";
                    response.close();
                    //输出重试日志
                    log.info("请求开始重试{}次(Max={},Interval={}ms),rspCode={},rspMsg={},rspBody={}", retryCount, MAX_RETRIES, RETRY_INTERVAL, response.code(), response.message(),body);
                } else {
                    // 响应码为 200，直接返回响应
                    return response;
                }
            } catch (IOException e) {
                //等待设置请求间隔时间
                ThreadUtil.sleep(RETRY_INTERVAL);
                // 增加重试次数
                retryCount++;
                if (response != null){
                    response.close();
                }
                //输出重试日志
                log.info("请求开始重试{}次(Max={},Interval={}ms),IOException={}", retryCount, MAX_RETRIES, RETRY_INTERVAL, e.getMessage());
            }
        }
        // 重试次数超过最大限制，抛出服务不可用业务异常
        throw new CustomException("请求服务不可用");
    }

    /**
     * 用于检测非20X的code，如果遇到已记录的code则不重试直接抛出异常
     * @param code rsp code
     */
    private boolean checkRspCode(Integer code) {
        if (code == null) {
            return false;
        }
        //40X错误不需要再次重试
        if (code >= 400 && code <= 499){
            return true;
        }
        if (code.equals(590)) {
            //590下载较早之前的资源会响应，直接抛出资源过期业务异常，不进行重试
            throw new CustomException("资源过期，不再重试");
        }
        return false;
    }

}
