package com.easyink.wecom.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： 素材标签VO
 *
 * @author 佚名
 * @date 2021/10/12 15:27
 */
@Data
@ApiModel("素材标签VO《WeMaterialTagVO》")
@TableName("we_material_tag")
public class WeMaterialTagVO {
    @ApiModelProperty("主键")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty("标签名称")
    @TableField("tag_name")
    private String tagName;
}
