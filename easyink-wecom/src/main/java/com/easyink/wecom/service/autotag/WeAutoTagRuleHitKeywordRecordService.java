package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitKeywordRecord;
import com.easyink.wecom.domain.query.autotag.TagRuleRecordKeywordDetailQuery;
import com.easyink.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easyink.wecom.domain.vo.autotag.record.CustomerCountVO;
import com.easyink.wecom.domain.vo.autotag.record.keyword.KeywordRecordDetailVO;
import com.easyink.wecom.domain.vo.autotag.record.keyword.KeywordTagRuleRecordVO;

import java.util.List;

/**
 * 客户打标签记录表(WeAutoTagRuleHitKeywordRecord)表服务接口
 *
 * @author tigger
 * @since 2022-03-02 14:51:26
 */
public interface WeAutoTagRuleHitKeywordRecordService extends IService<WeAutoTagRuleHitKeywordRecord> {

    /**
     * 关键词记录列表
     *
     * @param query
     * @return
     */
    List<KeywordTagRuleRecordVO> listKeywordRecord(TagRuleRecordQuery query);

    /**
     * 触发关键词详情列表
     *
     * @param query
     * @return
     */
    List<KeywordRecordDetailVO> listKeywordDetail(TagRuleRecordKeywordDetailQuery query);

    /**
     * 对客户进行关键词打标签
     *
     * @param elasticSearchEntities 消息列表
     * @param corpId                企业id
     */
    void makeTagToNewCustomer(List<ChatInfoVO> elasticSearchEntities, String corpId);

    /**
     * 客户统计
     *
     * @param ruleId 规则id
     * @param corpId 企业id
     * @return
     */
    CustomerCountVO keywordCustomerCount(Long ruleId, String corpId);
}

