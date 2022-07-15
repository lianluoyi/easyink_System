package com.easywecom.wecom.factory.impl.party;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.constant.GenConstants;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
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
 * @description 创建部门事件
 * @date 2021/1/20 22:54
 **/
@Slf4j
@Component("create_party")
public class WeCallBackCreatePartyImpl extends WeEventStrategy {
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
            weDepartmentService.insertWeDepartmentNoToWeCom(setWeDepartMent(message));

        } catch (Exception e) {
            log.error("创建部门添加数据库失败：ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }


}
