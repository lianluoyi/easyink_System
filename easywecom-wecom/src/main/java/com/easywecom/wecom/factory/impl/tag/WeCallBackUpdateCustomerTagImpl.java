package com.easywecom.wecom.factory.impl.tag;

import com.easywecom.common.exception.BaseException;
import com.easywecom.wecom.domain.vo.WxCpXmlMessageVO;
import com.easywecom.wecom.factory.WeEventStrategy;
import com.easywecom.wecom.service.WeTagGroupService;
import com.easywecom.wecom.service.WeTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author admin
 * @description 企业客户标签变更事件
 * @date 2021/1/21 1:21
 **/
@Slf4j
@Component("updateCustomerTag")
public class WeCallBackUpdateCustomerTagImpl extends WeEventStrategy {
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
        if (StringUtils.isAnyBlank(message.getTagId(), message.getToUserName())) {
            log.error("企业客户标签变更事件,tagId:{},corpID:{}", message.getTagId(), message.getToUserName());
            return;
        }

        try {
            switch (message.getTagType()) {
                case tagGroup:
                    weTagGroupService.updateTagGroup(message.getTagId(), message.getToUserName());
                    break;
                case tag:
                    weTagService.updateTag(message.getTagId(), message.getToUserName());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("updateCustomerTag>>>>>>>>>param:{},ex:{}", message.getId(), ExceptionUtils.getStackTrace(e));
        }
    }
}
