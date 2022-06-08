package com.easywecom.wecom.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类名： AllocateWeCustomerV2DTO
 *
 * @author 佚名
 * @date 2021/9/6 16:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllocateWeCustomerV2DTO {
    /**
     * 外部联系人的userid，注意不是企业成员的帐号
     */
    private String handover_userid;

    /**
     * 原跟进成员的userid集合
     */
    private List<String> external_userid;

    /**
     * 接替成员的userid
     */
    private String takeover_userid;
}
