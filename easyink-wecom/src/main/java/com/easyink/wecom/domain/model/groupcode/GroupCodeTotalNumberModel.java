package com.easyink.wecom.domain.model.groupcode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 群活码统计model
 * @author tigger
 * 2023/12/19 15:27
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCodeTotalNumberModel {

    /**
     * 群活码id
     */
    private String groupCodeId;
    /**
     * 使用总数
     */
    private Integer totalNumber;
}
