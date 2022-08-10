package com.easyink.wecom.factory.impl;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.service.WeDepartmentService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 类名: WeInfoTypeChangeAuthImpl
 *
 * @author: 1*+
 * @date: 2021-09-09 14:23
 */
@Slf4j
@Service("change_auth")
public class WeInfoTypeChangeAuthImpl implements WeCallBackEventFactory {

    private final WeDepartmentService weDepartmentService;
    private final WeUserService weUserService;

    public WeInfoTypeChangeAuthImpl(WeDepartmentService weDepartmentService, WeUserService weUserService) {
        this.weDepartmentService = weDepartmentService;
        this.weUserService = weUserService;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (ObjectUtils.isEmpty(message)) {
            log.error("message为空");
            return;
        }
        String corpId = message.getToUserName();
        // 更新可见范围的部门和成员
        refreshDepartmentAndUser(corpId);
        log.info("企业变更授权,suiteId:{},authCorpId:{}", message.getSuiteId(), message.getAuthCorpId());
    }

    /**
     * 刷新企业的可见部门和员工
     *
     * @param corpId 企业ID
     */
    private void refreshDepartmentAndUser(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            log.info("[change_auth] 缺少corpId,不处理");
            return;
        }
        weDepartmentService.synchWeDepartment(corpId);
        weUserService.syncWeUser(corpId);
        log.info("[change_auth] 授权信息更新,更新可见的部门和员工完成,corpId:{}", corpId);

    }
}
