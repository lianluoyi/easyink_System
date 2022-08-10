package com.easyink.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.LogoutReasonEnum;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * @author admin
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
        // 1.根据userKey获取登出的原因
        String userKey = SpringUtils.getBean(TokenService.class).getTokenId(ServletUtils.getRequest());
        Integer reasonCode = redisCache.getCacheObject(RedisKeyConstants.ACCOUNT_LOGOUT_REASON_KEY + userKey);
        // 2. 根据登出原因码获取应返回给前端的错误码
        int respCode = LogoutReasonEnum.getByCode(reasonCode).getResultTip().getCode();
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        // 3. 删除登出原因的缓存并返回错误码给前端
        if (reasonCode != null) {
            redisCache.deleteObject(RedisKeyConstants.ACCOUNT_LOGOUT_REASON_KEY + userKey);
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(respCode, msg)));
    }
}
