package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 类名：WeMaterialConfig
 *
 * @author Society my sister Li
 * @date 2021-10-11 10:44
 */
@Data
@TableName("we_material_config")
@ApiModel("企业素材配置表")
@AllArgsConstructor
@NoArgsConstructor
public class WeMaterialConfig {

    @ApiModelProperty(value = "企业id", hidden = true)
    @TableId
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "过期后是否自动删除（0否，1是）", required = true)
    @TableField("is_del")
    @NotNull(message = "isDel不能为空")
    private Boolean isDel;

    @ApiModelProperty(value = "删除时间", required = true)
    @TableField("del_days")
    @NotNull(message = "delDays不能为空")
    @Min(value = 0, message = "delDays范围值填写错误")
    @Max(value = 999, message = "delDays范围值填写错误")
    private Integer delDays;

}
