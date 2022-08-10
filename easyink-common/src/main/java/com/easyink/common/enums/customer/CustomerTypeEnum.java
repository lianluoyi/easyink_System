package com.easyink.common.enums.customer;

/**
 * @author tigger
 * 2022/1/24 18:26
 **/
public enum CustomerTypeEnum {
    /**
     * 微信用户
     */
    WX_CUSTOMER(1),
    /**
     * 企业微信用户
     */
    CORP_CUSTOMER(2);


    private Integer type;

    CustomerTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
