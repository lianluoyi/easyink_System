package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.RootEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 素材标签关联表
 * 类名： WeMaterialTagRelEntity
 *
 * @author 佚名
 * @date 2021/10/11 15:25
 */
@Data
@ApiModel("素材标签关联实体 《WeMaterialTagRelEntity》")
@TableName("we_material_tag_rel")
public class WeMaterialTagRelEntity extends RootEntity {

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty("素材id")
    @TableField("material_id")
    private Long materialId;

    @ApiModelProperty("素材标签id")
    @TableField("material_tag_id")
    private Long materialTagId;

}
