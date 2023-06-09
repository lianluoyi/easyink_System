package com.easyink.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.LoginTypeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.manager.AsyncManager;
import com.easyink.common.manager.factory.AsyncFactory;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义退出处理类 返回成功
 *
 * @author admin
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getCorpId(), userName, Constants.LOGOUT, "退出成功", LoginTypeEnum.getByUser(loginUser).getType()));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(ResultTip.TIP_GENERAL_SUCCESS.getCode(), "退出成功")));
    }
}
