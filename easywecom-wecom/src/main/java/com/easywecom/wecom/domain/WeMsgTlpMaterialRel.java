package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 欢迎语与素材关系表
 *
 * @author tigger
 * 2022/1/4 15:52
 **/
@Data
@TableName("we_msg_tlp_material_rel")
@ApiModel("欢迎语与素材关系表")
public class WeMsgTlpMaterialRel {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "欢迎语id")
    @TableField("msg_id")
    private Long msgId;

    @ApiModelProperty(value = "特殊规则欢迎语模板id(如果不存在特殊时段欢迎语，且没有素材则该字段为0)")
    @TableField("special_msg_id")
    private Long specialMsgId;

    @ApiModelProperty(value = "欢迎语素材id")
    @TableField("msg_material_id")
    private Long msgMaterialId;

}
