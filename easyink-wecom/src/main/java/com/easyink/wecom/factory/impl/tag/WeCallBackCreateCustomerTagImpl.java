package com.easyink.wecom.factory.impl.tag;

import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeTagGroupService;
import com.easyink.wecom.service.WeTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 创建标签事件
 * @date 2021/1/21 1:18
 **/
@Slf4j
@Component("createCustomerTag")
public class WeCallBackCreateCustomerTagImpl extends WeEventStrategy {
    @Autowired
    private WeTagGroupService weTagGroupService;
    @Autowired
    private WeTagService weTagService;

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null) {
            log.error("message不能为空");
            return;
        }

        if ( StringUtils.isAnyBlank(message.getToUserName(), message.getTagId())) {
            log.error("创建标签事件失败，corpId：{}，tagId：{}", message.getToUserName(), message.getTagId());
            return;
        }

        try {
            switch (message.getTagType()) {
                case tagGroup:
                    weTagGroupService.createTagGroup(message.getTagId(), message.getToUserName());
                    break;
                case tag:
                    weTagService.creatTag(message.getTagId(), message.getToUserName());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("createCustomerTag>>>>>>>>>param:{},ex:{}", message.getId(), ExceptionUtils.getStackTrace(e));
        }
    }
}
