package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.core.domain.RootEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 素材标签表
 * 类名： WeMaterialTagEntity
 *
 * @author 佚名
 * @date 2021/10/11 15:11
 */
@Data
@ApiModel("素材标签实体《WeMaterialTagEntity》")
@TableName("we_material_tag")
public class WeMaterialTagEntity extends RootEntity {

    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty("企业id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("标签名称")
    @TableField("tag_name")
    private String tagName;

}
