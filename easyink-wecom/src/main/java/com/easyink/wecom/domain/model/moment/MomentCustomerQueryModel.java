package com.easyink.wecom.domain.model.moment;

import com.easyink.common.core.domain.BaseEntity;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 朋友圈查询model
 * @author tigger
 * 2024/2/7 11:37
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentCustomerQueryModel extends BaseEntity {




    /**
     * 企业id
     */
    private String corpId;


    /**
     * 员工id列表
     */
    private List<String> userIdList;

}
