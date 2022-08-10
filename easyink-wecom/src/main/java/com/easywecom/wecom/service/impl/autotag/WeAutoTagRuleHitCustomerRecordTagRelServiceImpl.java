package com.easywecom.wecom.service.impl.autotag;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitCustomerRecordTagRel;
import com.easywecom.wecom.mapper.autotag.WeAutoTagRuleHitCustomerRecordTagRelMapper;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitCustomerRecordTagRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitCustomerRecordTagRel)表服务实现类
 *
 * @author tigger
 * @since 2022-03-02 16:04:54
 */
@Service("weAutoTagRuleHitCustomerRecordTagRelService")
public class WeAutoTagRuleHitCustomerRecordTagRelServiceImpl extends ServiceImpl<WeAutoTagRuleHitCustomerRecordTagRelMapper, WeAutoTagRuleHitCustomerRecordTagRel> implements WeAutoTagRuleHitCustomerRecordTagRelService {

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
        for (WeTag weTag : tagList) {
            tagRelList.add(new WeAutoTagRuleHitCustomerRecordTagRel(ruleId, weTag.getTagId(), weTag.getName(), customerId, userId));
        }
        return tagRelList;
    }
}

