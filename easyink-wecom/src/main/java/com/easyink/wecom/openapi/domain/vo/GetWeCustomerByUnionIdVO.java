package com.easyink.wecom.openapi.domain.vo;

import com.easyink.wecom.domain.WeCustomer;
import lombok.Data;

/**
 * open_api根据unionId获取externalUserId的响应
 *
 * @author : limeizi
 * @date : 2024/2/4 14:26
 */
@Data
public class GetWeCustomerByUnionIdVO {

    /**
     * 外部联系人id
     */
    private String externalUserid;

    /**
     * 客户昵称
     */
    private String name;

    /**
     * 客户头像
     */
    private String avatar;

    /**
     * 客户性别
     */
    private Integer gender;

    public void initByWeCustomer(WeCustomer weCustomer) {
        this.setExternalUserid(weCustomer.getExternalUserid());
        this.setAvatar(weCustomer.getAvatar());
        this.setName(weCustomer.getName());
        this.setGender(Integer.valueOf(weCustomer.getGender()));
    }
}
