package com.easywecom.wecom.domain.vo;

import lombok.Data;

/**
 * 外部联系人的userId和name
 *
 * @author Society my sister Li
 * @date 2021/9/14
 */
@Data
public class WeCustomerNameAndUserIdVO {

    private String externalUserId;

    private String name;

    private String remark;

    private String userId;
}
