package com.easywecom.common.enums;

import lombok.Getter;

/**
 * 类名：WeTempMaterialEnum 员工活码临时素材枚举值说明
 *
 * @author Society my sister Li
 * @date 2021-11-08 13:50
 */
public enum WeTempMaterialEnum {

    //临时素材
    TEMP(1),
    //素材
    MATERIAL(0);
    @Getter
    private Integer tempFlag;

    WeTempMaterialEnum(Integer tempFlag) {
        this.tempFlag = tempFlag;
    }

}
