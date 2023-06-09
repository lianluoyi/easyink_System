package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 类名：
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Data
@TableName("we_operations_center_sop_material")
@ApiModel("实体")
public class WeOperationsCenterSopMaterialEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    private String corpId;
    /**
     * sop的主键id
     */
    @ApiModelProperty(value = "sop的主键id")
    @TableField("sop_id")
    private Long sopId;
    /**
     * 规则id
     */
    @ApiModelProperty(value = "规则id")
    @TableField("rule_id")
    private Long ruleId;
    /**
     * 素材id
     */
    @ApiModelProperty(value = "素材id")
    @TableField("material_id")
    private Long materialId;
    /**
     * 素材排序
     */
    @ApiModelProperty(value = "素材排序")
    @TableField("sort")
    private Integer sort = 0;

}
