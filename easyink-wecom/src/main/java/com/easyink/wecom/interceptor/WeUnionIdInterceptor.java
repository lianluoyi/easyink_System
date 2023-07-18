package com.easyink.wecom.interceptor;


import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.client.WeAccessTokenClient;
import com.easyink.wecom.domain.dto.WeAccessTokenDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.easyink.common.constant.WeConstans.WE_COMMON_ACCESS_TOKEN;

/**
 * 类名: 获取unionId的拦截器
 *
 * @author : silver_chariot
 * @date : 2023/1/5 14:04
 **/
@Slf4j
@Component
@AllArgsConstructor
public class WeUnionIdInterceptor implements Interceptor<Object> {
    private final WeAccessTokenClient accessTokenClient;
    private final RedisCache redisCache;
    /**
     * 请求头上的corpId 和  corpSecret
     */
    private static final String HEADER_CORP_ID = "corpId";
    private static final String HEADER_CORP_SECRET = "corpSecret";
    /**
     * 请求参数的access_token
     */
    private static final String ACCESS_TOKEN = "access_token";

    @Override
    public boolean beforeExecute(ForestRequest request) {
        // 获取corpId
        String corpId = request.getHeaderValue(HEADER_CORP_ID);
        if (corpId == null) {
            log.error("[getUnionId]请求头的corpId为空,停止请求");
            return false;
        }
        // 先获取缓存中的access_token
        String accessToken = redisCache.getCacheObject(getAccessTokenKey(corpId));
        if (StringUtils.isBlank(accessToken)) {
            // 若缓存不存在,  调用企微接口获取access_token
            String corpSecret = request.getHeaderValue(HEADER_CORP_SECRET);
            accessToken = getAndCacheToken(corpId, corpSecret);
        }
        if (StringUtils.isNotBlank(accessToken)) {
            // 添加access_token 参数
            request.addQuery(ACCESS_TOKEN, accessToken);
        }
        return Interceptor.super.beforeExecute(request);
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("请求成功url:【{}】,result:【{}】", request.getUrl(), response.getContent());
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.error("请求失败url:【{}】,result:【{}】", request.getUrl(), response.getContent());

    }

    /**
     * 获取access_token 并缓存
     *
     * @param corpId     企业id
     * @param corpSecret 企业秘钥
     * @return access_token
     */
    private String getAndCacheToken(String corpId, String corpSecret) {
        if (StringUtils.isAnyBlank(corpId, corpSecret)) {
            return StringUtils.EMPTY;
        }
        WeAccessTokenDTO accessTokenDTO = accessTokenClient.getToken(corpId, corpSecret);
        if (accessTokenDTO == null || accessTokenDTO.getExpires_in() == null
                || StringUtils.isBlank(accessTokenDTO.getAccess_token())) {
            return StringUtils.EMPTY;
        }
        // 缓存token
        redisCache.setCacheObject(getAccessTokenKey(corpId), accessTokenDTO.getAccess_token(), accessTokenDTO.getExpires_in()
                                                                                                             .intValue(), TimeUnit.SECONDS);
        return accessTokenDTO.getAccess_token();
    }

    /**
     * 获取access_token缓存的redis key
     *
     * @param corpId 企业id
     * @return redis key
     */
    private String getAccessTokenKey(String corpId) {
        return WE_COMMON_ACCESS_TOKEN + ":" + corpId;
    }
}
