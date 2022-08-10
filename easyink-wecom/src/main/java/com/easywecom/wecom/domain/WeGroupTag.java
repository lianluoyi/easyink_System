package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 类名：WeGroupTag
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:31
 */
@Data
@TableName("we_group_tag")
@ApiModel("群标签")
public class WeGroupTag {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "企业ID", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "群标签组ID")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "群标签名称")
    @TableField("name")
    @Size(max = 15, message = "群标签名称长度不能超过15个字符")
    private String name;
}
