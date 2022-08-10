package com.easyink.common.enums;

import lombok.Getter;

/**
 * 外部群 群成员类型定义
 *
 * @author Society my sister Li
 * @date 2021/9/9
 */
public enum ExternalGroupMemberTypeEnum {

    //内部成员
    INTERNAL(1),
    //外部成员
    EXTERNAL(2),
    ;

    @Getter
    private final Integer type;

    ExternalGroupMemberTypeEnum(Integer type) {
        this.type = type;
    }
}
