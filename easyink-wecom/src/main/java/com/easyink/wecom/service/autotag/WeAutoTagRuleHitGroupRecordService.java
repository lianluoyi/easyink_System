package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.autotag.WeAutoTagRuleHitGroupRecord;
import com.easyink.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easyink.wecom.domain.vo.autotag.record.CustomerCountVO;
import com.easyink.wecom.domain.vo.autotag.record.group.GroupTagRuleRecordVO;

import java.util.List;

/**
 * 客户打标签记录表(WeAutoTagRuleHitGroupRecord)表服务接口
 *
 * @author tigger
 * @since 2022-03-02 15:42:27
 */
public interface WeAutoTagRuleHitGroupRecordService extends IService<WeAutoTagRuleHitGroupRecord> {

    /**
     * 群记录列表
     *
     * @param query
     * @return
     */
    List<GroupTagRuleRecordVO> listGroupRecord(TagRuleRecordQuery query);

    /**
     * 新客进群打标签
     *
     * @param chatId                群id
     * @param newJoinCustomerIdList 客户id列表
     * @param corpId                企业id
     */
    void makeTagToNewCustomer(String chatId, List<String> newJoinCustomerIdList, String corpId);

    /**
     * 组装记录数据
     *
     * @param ruleId                规则id
     * @param newJoinCustomerIdList 涉及的客户id列表
     * @param chatId                群id
     * @param groupName             群名称
     * @param corpId                企业id
     * @return
     */
    List<WeAutoTagRuleHitGroupRecord> buildRecord(Long ruleId, List<String> newJoinCustomerIdList, String chatId, String groupName, String corpId);


    /**
     * 群客户统计
     *
     *
     * @param ruleId
     * @param corpId 企业id
     * @return
     */
    CustomerCountVO groupCustomerCount(Long ruleId, String corpId);
}

