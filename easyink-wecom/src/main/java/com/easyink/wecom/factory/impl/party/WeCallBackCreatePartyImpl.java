package com.easyink.wecom.factory.impl.party;

import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.wecom.client.WeDepartMentClient;
import com.easyink.wecom.domain.dto.WeDepartMentDTO;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

    private final WeDepartMentClient weDepartMentClient;

    public WeCallBackCreatePartyImpl(WeDepartMentClient weDepartMentClient) {
        this.weDepartMentClient = weDepartMentClient;
    }

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }
        if (StringUtils.isBlank(message.getToUserName()) || message.getTagId() == null) {
            log.error("企业id,部门id，corpId：{}，部门id:{}，事件类型:{}", message.getToUserName(), message.getTagId(), message.getChangeType());
            return;
        }
        try {
            // 由于企微官方限制，对于2022年8月15号后通讯录助手新配置或修改的回调url，部门属性只回调Id/ParentId两个字段，故需要根据返回的部门id，获取部门详细信息
            WeDepartMentDTO weDepartMentDTO = weDepartMentClient.weDepartMents(Long.valueOf(message.getTagId()), message.getToUserName());
            if (weDepartMentDTO == null || CollectionUtils.isEmpty(weDepartMentDTO.getDepartment())) {
                log.error("[创建部门事件] 未从企微获取到回调对应的部门信息，部门id：{}，企业id：{}，事件类型：{}", message.getTagId(), message.getToUserName(), message.getChangeType());
                return;
            }
            WeDepartment weDepartment = new WeDepartment();
            // 从返回的部门信息中获取回调中部门的信息。
            for (WeDepartMentDTO.DeartMentDto deartMentDto : weDepartMentDTO.getDepartment()) {
                if (deartMentDto.getId().toString().equals(message.getTagId())) {
                    weDepartment.setId(deartMentDto.getId());
                    weDepartment.setCorpId(message.getToUserName());
                    weDepartment.setName(deartMentDto.getName());
                    weDepartment.setParentId(Long.valueOf(message.getParentId()));
                }
            }
            weDepartmentService.insertWeDepartmentNoToWeCom(weDepartment);
        } catch (Exception e) {
            log.error("创建部门添加数据库失败：ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }


}
