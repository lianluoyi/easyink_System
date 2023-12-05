package com.easyink.wecom.factory.impl.tag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.Constants;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 企业客户标签删除事件
 * @date 2021/1/21 1:18
 **/
@Slf4j
@Component("deleteCustomerTag")
public class WeCallBackDeleteCustomerTagImpl extends WeEventStrategy {
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
            // 最终要删除的标签ID列表
            List<String> tagIdList = new ArrayList<>();
            switch (message.getTagType()) {
                case tagGroup:
                    weTagGroupService.deleteTagGroup(message.getTagId(), message.getToUserName());
                    // 获取标签组下的所有状态正常的标签ID
                    List<WeTag> weTags = weTagService.list(new LambdaQueryWrapper<WeTag>().select(WeTag::getTagId)
                                                                                          .eq(WeTag::getCorpId, message.getToUserName())
                                                                                          .eq(WeTag::getGroupId, message.getTagId())
                                                                                          .eq(WeTag::getStatus, Constants.NORMAL_CODE));
                    if (CollectionUtils.isEmpty(weTags)) {
                        return;
                    }
                    tagIdList = weTags.stream().map(WeTag::getTagId).collect(Collectors.toList());
                    // 删除标签-员工-客户关系
                    weTagGroupService.delFlowerTagRel(tagIdList, message.getToUserName());
                    break;
                case tag:
                    weTagService.deleteTag(message.getTagId(), message.getToUserName());
                    tagIdList.add(message.getTagId());
                    // 删除标签-员工-客户关系
                    weTagGroupService.delFlowerTagRel(tagIdList, message.getToUserName());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("deleteCustomerTag>>>>>>>>>param:{},ex:{}", message.getId(), ExceptionUtils.getStackTrace(e));
        }
    }
}
