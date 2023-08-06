package com.easyink.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitCustomerRecordTagRel;
import com.easyink.wecom.mapper.autotag.WeAutoTagRuleHitCustomerRecordTagRelMapper;
import com.easyink.wecom.service.WeTagService;
import com.easyink.wecom.service.autotag.WeAutoTagRuleHitCustomerRecordTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户标签命中记录(WeAutoTagRuleHitCustomerRecordTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-03-02 16:04:54
 */
@Service("weAutoTagRuleHitCustomerRecordTagRelService")
public class WeAutoTagRuleHitCustomerRecordTagRelServiceImpl extends ServiceImpl<WeAutoTagRuleHitCustomerRecordTagRelMapper, WeAutoTagRuleHitCustomerRecordTagRel> implements WeAutoTagRuleHitCustomerRecordTagRelService {

    private final WeTagService weTagService;

    public WeAutoTagRuleHitCustomerRecordTagRelServiceImpl(WeTagService weTagService) {
        this.weTagService = weTagService;
    }

    /**
     * 组装新客标签关系记录
     *
     * @param ruleId     规则id
     * @param customerId 客户id
     * @param userId     员工id
     * @param tagList    标签列表
     * @return
     */
    @Override
    public List<WeAutoTagRuleHitCustomerRecordTagRel> buildTagRecord(Long ruleId, String customerId, String userId, List<WeTag> tagList) {
        List<WeAutoTagRuleHitCustomerRecordTagRel> tagRelList = new ArrayList<>();
        if (ruleId == null | StringUtils.isBlank(customerId) || StringUtils.isBlank(userId) || CollectionUtils.isEmpty(tagList)) {
            return tagRelList;
        }
        List<String> effectTagIds = weTagService.getTagNameByIds(tagList.stream().map(WeTag::getTagId).collect(Collectors.toList()));
        // 实际要记录的标签关系记录
        List<WeTag> realList = tagList.stream().filter(item -> effectTagIds.contains(item.getName())).collect(Collectors.toList());
        for (WeTag weTag : realList) {
            tagRelList.add(new WeAutoTagRuleHitCustomerRecordTagRel(ruleId, weTag.getTagId(), weTag.getName(), customerId, userId));
        }
        return tagRelList;
    }
}

