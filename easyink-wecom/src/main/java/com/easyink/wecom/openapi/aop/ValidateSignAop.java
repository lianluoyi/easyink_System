package com.easyink.wecom.openapi.aop;


import com.easyink.common.utils.ServletUtils;
import com.easyink.wecom.openapi.constant.AppInfoConst;
import com.easyink.wecom.openapi.domain.entity.AppIdInfo;
import com.easyink.wecom.openapi.exception.SignValidateException;
import com.easyink.wecom.openapi.util.AppGenUtil;
import com.easyink.wecom.openapi.util.AppIdCache;
import com.easyink.wecom.openapi.util.AppInfoRedisClient;
import com.easyink.wecom.openapi.util.SignGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * 类名: 校验签名切面
 *
 * @author : silver_chariot
 * @date : 2022/3/14 17:55
 */
@Slf4j
@Aspect
@Component
public class ValidateSignAop {
    private final AppInfoRedisClient appInfoRedisClient;

    public ValidateSignAop(@NotNull AppInfoRedisClient appInfoRedisClient) {
        this.appInfoRedisClient = appInfoRedisClient;
    }

    @Pointcut("@annotation(com.easyink.wecom.openapi.aop.ValidateSign)")
    public void validateSignPointCut() {
    }

    @Before("validateSignPointCut()")
    public void before(JoinPoint joinPoint) {
        // 获取当前请求
        HttpServletRequest request = ServletUtils.getRequest();
        String appId = request.getHeader(AppInfoConst.HEADER_APP_ID);
        String sign = request.getHeader(AppInfoConst.HEADER_SIGN);
        String nonce = request.getHeader(AppInfoConst.HEADER_NONCE);
        String timestamp = request.getHeader(AppInfoConst.HEADER_TIME_STAMP);
        String ticket = request.getHeader(AppInfoConst.HEADER_TICKET);
        log.info("[signValidate]请求头信息,appId:{},nonce:{},timestamp:{},ticket:{},sign:{}", appId, nonce, timestamp, ticket, sign);
        // 分别校验appId,timestamp,nonce,ticket
        String secret = validateAppId(appId);
        validateTimestamp(timestamp);
        validateNonce(appId, nonce);
        validateTicket(appId, ticket);
        // 根据请求头参数,生成签名
        String generateSign = new SignGenerator()
                .add(AppInfoConst.HEADER_APP_ID, appId)
                .add(AppInfoConst.HEADER_NONCE, nonce)
                .add(AppInfoConst.HEADER_TIME_STAMP, timestamp)
                .add(AppInfoConst.HEADER_TICKET, ticket)
                .doGenerate();
        log.info("[open api请求校验]服务端生成的签名：{}", generateSign);
        // 校验签名
        validateSign(sign, generateSign);
    }

    /**
     * 校验签名
     *
     * @param sign         请求头中的签名
     * @param generateSign 根据请求头生成的签名
     */
    private void validateSign(String sign, String generateSign) {
        if (StringUtils.isAnyBlank(sign)) {
            throw new SignValidateException("sign cannot be null");
        }
        if (StringUtils.isBlank(generateSign) || !generateSign.equals(sign)) {
            throw new SignValidateException("invalid sign");
        }
    }

    /**
     * 校验票据
     *
     * @param appId  appId
     * @param ticket 票据
     */
    private void validateTicket(String appId, String ticket) {
        if (StringUtils.isAnyBlank(appId, ticket)) {
            throw new SignValidateException("ticket cannot be null");
        }
        // 根据ticket获取appId
        String cacheAppId;
        try {
            cacheAppId = AppGenUtil.getAppIdByTicket(ticket);
        } catch (ExpiredJwtException e) {
            throw new SignValidateException("ticket expires");
        } catch (Exception e) {
            throw new SignValidateException("invalid ticket");
        }
        if (!appId.equals(cacheAppId)) {
            throw new SignValidateException("error ticket");
        }
    }

    /**
     * 校验随机字符
     *
     * @param appId appId
     * @param nonce 随机字符
     */
    private void validateNonce(String appId, String nonce) {
        if (StringUtils.isBlank(nonce)) {
            throw new SignValidateException("nonce cannot be null");
        }
        if (nonce.length() != AppInfoConst.NONCE_LENGTH) {
            throw new SignValidateException("invalid nonceStr");
        }
        try {
            if (appInfoRedisClient.existNonce(appId, nonce)) {
                throw new SignValidateException("nonce should be random");
            }
        } finally {
            // 缓存本次的随机字符串
            appInfoRedisClient.setNonce(appId, nonce);
        }
    }

    /**
     * 校验timestamp
     *
     * @param timestampStr 时间戳
     */
    private void validateTimestamp(String timestampStr) {
        if (StringUtils.isBlank(timestampStr)) {
            throw new SignValidateException("timestamp cannot be null");
        }
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            throw new SignValidateException("invalid timestamp");
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - timestamp > AppInfoConst.REQ_EXPIRE_TIME) {
            throw new SignValidateException("invalid timestamp");
        }
    }

    /**
     * 校验appId
     *
     * @param appId APP_ID
     * @return 系统缓存的对应appId的秘钥
     */
    private String validateAppId(String appId) {
        if (StringUtils.isBlank(appId)) {
            throw new SignValidateException("appId cannot be null");
        }
        AppIdInfo appIdInfo = AppIdCache.INSTANCE.get(appId);
        if (appIdInfo == null || StringUtils.isBlank(appIdInfo.getAppSecret())) {
            throw new SignValidateException("invalid appId");
        }
        return appIdInfo.getAppSecret();
    }


}
