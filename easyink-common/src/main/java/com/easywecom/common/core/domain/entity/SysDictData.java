package com.easywecom.common.core.domain.entity;

import com.easywecom.common.annotation.Excel;
import com.easywecom.common.annotation.Excel.ColumnType;
import com.easywecom.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 字典数据表 sys_dict_data
 *
 * @author admin
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel("字典数据表")
public class SysDictData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典编码")
    @Excel(name = "字典编码", cellType = ColumnType.NUMERIC)
    private Long dictCode;

    @ApiModelProperty("字典排序")
    @Excel(name = "字典排序", cellType = ColumnType.NUMERIC)
    private Long dictSort;

    @ApiModelProperty("字典标签")
    @Excel(name = "字典标签")
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;


    @ApiModelProperty("字典键值")
    @Excel(name = "字典键值")
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    @ApiModelProperty("字典类型")
    @Excel(name = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @ApiModelProperty("样式属性（其他样式扩展）")
    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    private String cssClass;

    @ApiModelProperty("表格字典样式")
    private String listClass;

    @ApiModelProperty("是否默认（Y是 N否）")
    @Excel(name = "是否默认", readConverterExp = "Y=是,N=否")
    private String isDefault;

    @ApiModelProperty("状态（0正常 1停用）")
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

}
