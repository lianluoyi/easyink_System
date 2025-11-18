package com.easyink.wecom.publishevent;

import com.alibaba.fastjson.JSON;
import com.easyink.common.enums.WeCategoryMediaTypeEnum;
import com.easyink.wecom.domain.dto.AddWeMaterialDTO;
import com.easyink.wecom.domain.enums.form.SendSourceTypeEnum;
import com.easyink.wecom.domain.vo.EmplyCodeWelcomeMsgInfo;
import com.easyink.wecom.publishevent.welcomemsg.SendWelcomeMsgSuccessEvent;
import com.easyink.wecom.service.WeFormSendRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送欢迎语事件监听处理
 *
 * @author tigger
 * 2025/8/26 17:04
 **/
@Slf4j
@Component
@AllArgsConstructor
public class WelcomeMsgListener {

    private final WeFormSendRecordService weFormSendRecordService;

    /**
     * 处理发送app应用通知监听方法
     *
     * @param event 事件
     */
    @EventListener(SendWelcomeMsgSuccessEvent.class)
    public void SendWelcomeMsgSuccessHandle(SendWelcomeMsgSuccessEvent event) {
        log.info("[批量保存表单发送记录] event: {}", JSON.toJSONString(event));
        if (event.getEmplyCodeWelcomeMsgInfo() == null || event.getCustomerId() == null) {
            return;
        }
        EmplyCodeWelcomeMsgInfo emplyCodeWelcomeMsgInfo = event.getEmplyCodeWelcomeMsgInfo();
        List<AddWeMaterialDTO> materialList = emplyCodeWelcomeMsgInfo.getMaterialList();


        if (CollectionUtils.isEmpty(materialList)) {
            log.info("[批量保存表单发送记录] 没有媒体资源");
            return;
        }
        // 收集所有表单ID
        List<Long> formIds = new ArrayList<>();
        for (AddWeMaterialDTO addWeMaterialDTO : materialList) {
            if (WeCategoryMediaTypeEnum.FORM.getMediaType().equals(addWeMaterialDTO.getMediaType())) {
                formIds.add(addWeMaterialDTO.getExtraId());
            }
        }

        if (CollectionUtils.isEmpty(formIds)) {
            log.info("[批量保存表单发送记录] 无表单欢迎语");
            return;
        }
        // 批量保存表单发送记录（已包含推送开关判断）
        weFormSendRecordService.batchSaveFormRecord(event.getCustomerId(), formIds, SendSourceTypeEnum.WELCOME_MSG, event.getOriginState() == null? null: event.getOriginState().getState());

    }
}
