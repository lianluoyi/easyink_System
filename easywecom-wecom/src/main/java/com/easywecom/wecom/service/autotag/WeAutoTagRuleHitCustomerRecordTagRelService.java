package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitCustomerRecordTagRel;

import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitCustomerRecordTagRel)表服务接口
 *
 * @author tigger
 * @since 2022-03-02 16:04:54
 */
public interface WeAutoTagRuleHitCustomerRecordTagRelService extends IService<WeAutoTagRuleHitCustomerRecordTagRel> {

    /**
     * 组装新客标签关系记录
     *
     * @param ruleId     规则id
     * @param customerId 客户id
     * @param userId     员工id
     * @param tagList    标签列表
     * @return
     */
    List<WeAutoTagRuleHitCustomerRecordTagRel> buildTagRecord(Long ruleId, String customerId, String userId, List<WeTag> tagList);
}

