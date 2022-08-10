package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecordTagRel;

import java.util.List;

/**
 * 客户标签命中记录(WeAutoTagRuleHitGroupRecordTagRel)表服务接口
 *
 * @author tigger
 * @since 2022-03-02 15:42:28
 */
public interface WeAutoTagRuleHitGroupRecordTagRelService extends IService<WeAutoTagRuleHitGroupRecordTagRel> {
    /**
     * 组装群标签关系记录
     *
     * @param ruleId                规则id
     * @param newJoinCustomerIdList 涉及的客户id列表
     * @param chatId                群id
     * @param tagList               标签列表
     * @return
     */
    List<WeAutoTagRuleHitGroupRecordTagRel> buildTagRel(Long ruleId, List<String> newJoinCustomerIdList, String chatId, List<WeTag> tagList);
}

