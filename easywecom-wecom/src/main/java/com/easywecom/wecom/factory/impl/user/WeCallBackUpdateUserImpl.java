package com.easywecom.wecom.factory.impl.user;

import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeExternalUserMappingUserService;
import com.easywecom.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 成员更新事件
 * @date 2021/1/20 22:28
 **/
@Slf4j
@Component("update_user")
public class WeCallBackUpdateUserImpl extends WeEventStrategy {
    @Autowired
    private WeUserService weUserService;
    @Autowired
    private WeExternalUserMappingUserService weExternalUserMappingUserService;
    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        try {
            if (StringUtils.isNotBlank(message.getSuiteId())) {
                weUserService.updateWeUserDataFromWeCom(message.getUserId(), message.getAuthCorpId());
            } else {
                if (StringUtils.isBlank(message.getToUserName())){
                    log.error("corpId不能为空");
                    return;
                }
                WeUser weUser = setWeUserData(message);
                weUserService.updateWeUserNoToWeCom(weUser);
                weExternalUserMappingUserService.createMapping(weUser.getCorpId(), weUser.getUserId());
            }

        } catch (Exception e) {
            log.error("成员更变到数据库失败：ex{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
