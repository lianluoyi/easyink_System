package com.easyink.wecom.factory.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.wecom.client.WeUserClient;
import com.easyink.wecom.domain.dto.WeUserDTO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeExternalUserMappingUserService;
import com.easyink.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

    @Autowired
    private WeCorpAccountService weCorpAccountService;

    @Autowired
    private WeUserClient weUserClient;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        try {
            if (StringUtils.isNotBlank(message.getSuiteId())) {
                // 代开发应用处理
                weUserService.updateWeUserDataFromWeCom(message.getUserId(), message.getAuthCorpId());
                updateForSuiteApp(message);
            } else {
                if (StringUtils.isBlank(message.getToUserName())) {
                    log.error("corpId不能为空");
                    return;
                }
                //从后台重新拉取员工信息
                WeUserDTO weUserDTO=weUserClient.getUserByUserId(message.getUserId(),message.getToUserName());
                if (Objects.isNull(weUserDTO)){
                    log.error("根据员工变更回调拉取员工信息失败,corpId:{}",message.getToUserName());
                    return;
                }
                WeUser weUser=weUserDTO.transferToWeUser();
                weUser.setCorpId(message.getToUserName());
                weUserService.updateWeUserNoToWeCom(weUser);
                weExternalUserMappingUserService.createMapping(weUser.getCorpId(), weUser.getUserId());
            }

        } catch (Exception e) {
            log.error("成员更变到数据库失败：ex{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 为代开发应用 更新有用户  (临时方案 后续删除)
     *
     * @param message
     */
    private void updateForSuiteApp(WxCpXmlMessageVO message) {
        //todo 由于代开发应用改造影响,会返回密文的corpId导致部分异常，这里先明文密文的都调用一次，后续代 代开发应用安全性改造后再去除
        // 先根据 external_corp_id 查出其明文的企业id
        WeCorpAccount corp = weCorpAccountService.getOne(new LambdaQueryWrapper<WeCorpAccount>()
                .eq(WeCorpAccount::getExternalCorpId, message.getAuthCorpId())
                .last(GenConstants.LIMIT_1)
        );
        if (corp != null && StringUtils.isNotBlank(corp.getCorpId())) {
            weUserService.updateWeUserDataFromWeCom(message.getUserId(), corp.getCorpId());

        }
    }
}
