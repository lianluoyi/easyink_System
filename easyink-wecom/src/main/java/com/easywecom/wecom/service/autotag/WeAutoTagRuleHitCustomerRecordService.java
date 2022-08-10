package com.easywecom.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.entity.autotag.WeAutoTagRuleHitCustomerRecord;
import com.easywecom.wecom.domain.query.autotag.CustomerTagRuleRecordQuery;
import com.easywecom.wecom.domain.vo.autotag.record.CustomerCountVO;
import com.easywecom.wecom.domain.vo.autotag.record.customer.CustomerTagRuleRecordVO;

import java.util.List;

/**
 * 客户打标签记录表(WeAutoTagRuleHitCustomerRecord)表服务接口
 *
 * @author tigger
 * @since 2022-03-02 16:04:52
 */
public interface WeAutoTagRuleHitCustomerRecordService extends IService<WeAutoTagRuleHitCustomerRecord> {

    /**
     * 新客规则记录列表
     *
     * @param query
     * @return
     */
    List<CustomerTagRuleRecordVO> listCustomerRecord(CustomerTagRuleRecordQuery query);

    /**
     * 新客户打标签
     *
     * @param customerId 客户id
     * @param userId     员工id
     * @param corpId     企业id
     */
    void makeTagToNewCustomer(String customerId, String userId, String corpId);

    /**
     * 新客客户统计
     *
     * @param ruleId 规则id
     * @param corpId 企业id
     * @return
     */
    CustomerCountVO customerCustomerCount(Long ruleId, String corpId);
}

