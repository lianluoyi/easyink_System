package com.easyink.wecom.domain;

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
 * 类名：WeGroupTagCategory
 *
 * @author Society my sister Li
 * @date 2021-11-12 14:31
 */
@Data
@TableName("we_group_tag_category")
@ApiModel("群标签组")
public class WeGroupTagCategory {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "企业ID", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "群标签组名称")
    @TableField("name")
    @Size(max = 15, message = "群标签组名称长度不能超过15个字符")
    private String name;
}
