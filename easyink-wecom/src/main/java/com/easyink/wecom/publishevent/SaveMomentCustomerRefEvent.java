package com.easyink.wecom.publishevent;

import com.easyink.wecom.domain.model.customer.UserIdAndExternalUserIdModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 保存朋友圈客户员工关系事件
 * @author tigger
 * 2024/6/12 16:59
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMomentCustomerRefEvent {

    /**
     * 员工id
     */
    private List<String> candidateUserIdList;

    /**
     * 标签id, 逗号分隔
     */
    private String tags;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 朋友圈id
     */
    private Long momentId;
}
