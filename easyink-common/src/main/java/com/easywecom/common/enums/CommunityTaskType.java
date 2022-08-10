package com.easywecom.common.enums;

import lombok.Getter;

/**
 * 社群运营H5列表页数据类型
 *
 * @author admin
 * @Date 2021/3/24 11:02
 */
@Getter
public enum CommunityTaskType {

    TAG(1, "老客标签建群"),

    SOP(2, "（旧）群sop"),

    GROUP_SOP(3,"改造后的群sop"),

    CUSTOMER_SOP(4,"客户SOP")

    ;


    private final String name;

    private final Integer type;

    CommunityTaskType(Integer type, String name) {
        this.name = name;
        this.type = type;
    }
}
