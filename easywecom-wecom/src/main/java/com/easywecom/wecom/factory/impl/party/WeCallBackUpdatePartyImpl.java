package com.easywecom.wecom.factory.impl.party;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import com.easywecom.common.exception.CallBackNullPointerException;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 修改部门事件
 * @date 2021/1/20 23:00
 **/
@Slf4j
@Component("update_party")
public class WeCallBackUpdatePartyImpl extends WeEventStrategy {
    @Autowired
    private WeDepartmentService weDepartmentService;

    @Autowired
    private WeCorpAccountService weCorpAccountService;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        if (StringUtils.isBlank(message.getToUserName()) || message.getId() == null) {
            log.error("企业id,部门id，corpId：{}，部门id:{}，事件类型:{}", message.getToUserName(), message.getId(), message.getChangeType());
            return;
        }
        try {
            weDepartmentService.update(setWeDepartMent(message), new LambdaQueryWrapper<WeDepartment>()
                    .eq(WeDepartment::getCorpId, message.getToUserName())
                    .eq(WeDepartment::getId, message.getId()));
            updateForSuiteApp(message);
        } catch (Exception e) {
            log.error("修改部门到本地数据库失败：ex:{}", ExceptionUtils.getStackTrace(e));
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
            message.setToUserName(corp.getCorpId());
            weDepartmentService.update(setWeDepartMent(message), new LambdaQueryWrapper<WeDepartment>()
                    .eq(WeDepartment::getCorpId, message.getToUserName())
                    .eq(WeDepartment::getId, message.getId()));
        }
    }

}
