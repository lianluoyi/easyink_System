package com.easyink.wecom.domain.dto;

import com.easyink.common.core.domain.BaseEntity;
import com.easyink.wecom.domain.entity.customer.ExtendPropertyMultipleOption;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 类名: 保存自定义属性关系 参数
 *
 * @author : silver_chariot
 * @date : 2021/11/10 17:45
 */
@Data
@ApiModel("保存客户自定义属性参数")
public class SaveCustomerExtendPropertyDTO extends BaseEntity {

    @ApiModelProperty(value = "扩展字段id,编辑时必传,新增时无需传")
    private Long id;

    @ApiModelProperty(value = "扩展字段名称", required = true)
    @Size(max = 12, message = "字段名称不能超过12位")
    @NotBlank(message = "名称不能为空")
    private String name;

    @ApiModelProperty(value = "字段类型（1系统默认字段,2单行文本，3多行文本，4单选框，5多选框，6下拉框，7日期，8图片，9文件）", required = true)
    @Max(value = 9, message = "字段类型错误")
    @Min(value = 2, message = "字段类型错误")
    private Integer type;

    @ApiModelProperty(value = "是否必填（1必填0非必填）")
    private Boolean required;

    @ApiModelProperty(value = "字段排序")
    private Integer propertySort;

    @ApiModelProperty(value = "状态（0停用1启用）")
    private Boolean status;

    @ApiModelProperty(value = "多选框选项,当type=4,5,6时必传")
    private List<ExtendPropertyMultipleOption> optionList;
    /**
     * 企业ID
     */
    private String corpId;
}
