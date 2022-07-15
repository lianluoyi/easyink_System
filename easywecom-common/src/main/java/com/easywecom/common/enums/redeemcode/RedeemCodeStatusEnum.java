package com.easywecom.common.enums.redeemcode;

/**
 * ClassName： RedeemCodeStatusEnum
 *
 * @author wx
 * @date 2022/7/12 9:54
 */
public enum RedeemCodeStatusEnum {

    /**
     * 已分配
     */
    ASSIGNED(1),
    /**
     * 未分配
     */
    NOT_ASSIGN(0),
    ;


    private Integer type;


    RedeemCodeStatusEnum(Integer type) {
        this.type = type;
    }


    public Integer getType() {
        return type;
    }

}
