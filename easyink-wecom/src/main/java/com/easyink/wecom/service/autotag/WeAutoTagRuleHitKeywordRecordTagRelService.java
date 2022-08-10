package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecordTagRel;

import java.util.List;
import java.util.Set;

/**
 * 客户标签命中记录(WeAutoTagRuleHitKeywordRecordTagRel)表服务接口
 *
 * @author tigger
 * @since 2022-03-02 14:51:30
 */
public interface WeAutoTagRuleHitKeywordRecordTagRelService extends IService<WeAutoTagRuleHitKeywordRecordTagRel> {

    /**
     * 组装标签记录
     *
     * @param corpId           企业id
     * @param userId           员工id
     * @param customerId       客户id
     * @param matchedRuleIdSet 匹配到的规则id
     * @return
     */
    List<WeAutoTagRuleHitKeywordRecordTagRel> buildTagRecord(String corpId, String userId, String customerId, Set<Long> matchedRuleIdSet);
}

