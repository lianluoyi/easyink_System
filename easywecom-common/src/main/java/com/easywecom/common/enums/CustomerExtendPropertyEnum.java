package com.easywecom.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名: 客户扩展属性类型枚举
 *
 * @author : silver_chariot
 * @date : 2021/11/10 17:49
 */
@AllArgsConstructor
public enum CustomerExtendPropertyEnum {

    SYS_DEFAULT(1, "系统默认字段", CustomerTrajectoryEnums.SubType.EDIT_CHOICE),

    SINGLE_ROW(2, "单行文本", CustomerTrajectoryEnums.SubType.EDIT_CHOICE),

    MULTI_ROWS(3, "多行文本", CustomerTrajectoryEnums.SubType.EDIT_CHOICE),

    RADIO_BOX(4, "单选框", CustomerTrajectoryEnums.SubType.EDIT_MULTI),

    CHECK_BOX(5, "多选框", CustomerTrajectoryEnums.SubType.EDIT_MULTI),

    COMBO_BOX(6, "下拉框", CustomerTrajectoryEnums.SubType.EDIT_MULTI),

    DATE(7, "日期", CustomerTrajectoryEnums.SubType.EDIT_CHOICE),

    PIC(8, "图片", CustomerTrajectoryEnums.SubType.EDIT_PIC),

    FILE(9, "文件", CustomerTrajectoryEnums.SubType.EDIT_FILE);
    /**
     * 类型
     */
    @Getter
    private final Integer type;
    /**
     * 描述
     */
    @Getter
    private final String desc;
    /**
     * 编辑该类型属性对应的操作子类型枚举
     */
    @Getter
    private final CustomerTrajectoryEnums.SubType oprSubType;

    /**
     * 判断是否是多选属性
     *
     * @param type 属性类型值
     * @return true or false
     */
    public static boolean isMultiple(Integer type) {
        if (type == null) {
            return false;
        }
        return type.equals(RADIO_BOX.getType())
                || type.equals(CHECK_BOX.getType())
                || type.equals(COMBO_BOX.getType());

    }

    /**
     * 根据type获取扩展属性类型
     *
     * @param type 类型
     * @return 对应的枚举
     */
    public static CustomerExtendPropertyEnum getByType(Integer type) {
        return Arrays.stream(values()).filter(a -> a.type.equals(type)).findFirst().orElse(null);
    }

    /**
     * 获取所有多选选项类型的集合
     *
     * @return 多选字段类型ID集合
     */
    public static List<Integer> multipleTypeList() {
        return Arrays.stream(values()).filter(a -> isMultiple(a.getType())).map(CustomerExtendPropertyEnum::getType).collect(Collectors.toList());
    }

    /**
     * 根据type判断是否是文件或者图片
     *
     * @param type 类型
     * @return true or false
     */
    public static boolean isFileOrPic(Integer type) {
        if (type == null) {
            return false;
        }
        return type.equals(PIC.getType()) || type.equals(FILE.getType());
    }
}
