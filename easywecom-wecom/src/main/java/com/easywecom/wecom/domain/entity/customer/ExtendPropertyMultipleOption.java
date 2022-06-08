package com.easywecom.wecom.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: 扩展属性多选项可选值
 *
 * @author : silver_chariot
 * @date : 2021/11/10 17:47
 */
@Data
public class ExtendPropertyMultipleOption {
    /**
     * 主键ID
     */
    @TableField("id")
    @TableId
    @ApiModelProperty(value = "多选值id,编辑时必传,新增时非必传")
    private Long id;
    /**
     * 扩展属性ID
     */
    @TableField("extend_property_id")
    @ApiModelProperty(value = "客户扩展属性id")
    private Long extendPropertyId;
    /**
     * 多选框.下拉框,单选框可选值
     */
    @TableField("multiple_value")
    @ApiModelProperty(value = "客户扩展属性值")
    private String multipleValue;

    @TableField("option_sort")
    @ApiModelProperty(value = "多选值的排序序号")
    private Integer optionSort;

    public ExtendPropertyMultipleOption() {
    }
}
