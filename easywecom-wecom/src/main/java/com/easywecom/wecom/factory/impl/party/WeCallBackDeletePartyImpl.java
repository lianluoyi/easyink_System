package com.easywecom.wecom.factory.impl.party;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import com.easywecom.common.exception.CallBackNullPointerException;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 删除部门事件
 * @date 2021/1/20 23:04
 **/
@Slf4j
@Component("delete_party")
public class WeCallBackDeletePartyImpl extends WeEventStrategy {
    @Autowired
    private WeDepartmentService weDepartmentService;

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
            weDepartmentService.remove(new LambdaQueryWrapper<WeDepartment>()
                    .eq(WeDepartment::getCorpId,message.getToUserName())
                    .eq(WeDepartment::getId,message.getId()));
        } catch (Exception e) {
            log.error("删除部门添加到数据库失败：ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
