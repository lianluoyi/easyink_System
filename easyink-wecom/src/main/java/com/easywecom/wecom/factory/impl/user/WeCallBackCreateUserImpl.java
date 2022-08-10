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
 * @description 新建外部联系人事件
 * @date 2021/1/20 22:19
 **/
@Slf4j
@Component("create_user")
public class WeCallBackCreateUserImpl extends WeEventStrategy {
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
        if (StringUtils.isAnyBlank(message.getUserId(), message.getToUserName(), message.getCreateTime().toString())) {
            log.error("企业id,员工id，添加时间，corpId：{}，userId:{},createTime：{}，事件类型:{}", message.getToUserName(), message.getUserId(), message.getCreateTime(), message.getChangeType());
            return;
        }
        try {
            WeUser weUser = setWeUserData(message);
            weUserService.insertWeUserNoToWeCom(weUser);

            weExternalUserMappingUserService.createMapping(weUser.getCorpId(), weUser.getUserId());
        } catch (Exception e) {
            log.error("新建外部联系人到本地失败：ex{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
