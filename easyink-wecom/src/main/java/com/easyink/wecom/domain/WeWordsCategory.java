package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 类名：WeWordsCategory
 *
 * @author Society my sister Li
 * @date 2021-10-25 10:42
 */
@Data
@TableName("we_words_category")
@ApiModel("话术文件夹实体")
@AllArgsConstructor
@NoArgsConstructor
public class WeWordsCategory {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "授权企业ID", hidden = true)
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "上级文件夹ID")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "话术类型（0：企业话术，1：部门话术，2：我的话术）")
    @TableField("type")
    @NotNull(message = "type不能为空")
    private Integer type;

    @ApiModelProperty(value = "使用范围（企业话术：存入根部门1,部门话术：创建人的主部门，我的话术：员工id）")
    @TableField("use_range")
    private String useRange;

    @ApiModelProperty(value = "文件夹名称")
    @TableField("name")
    @NotBlank(message = "name不能为空")
    @Size(max = 12, message = "文件夹名称长度超过限制")
    private String name;

    @ApiModelProperty(value = "文件夹排序")
    @TableField("sort")
    private Integer sort;


    public WeWordsCategory(String corpId, Long parentId, Integer type, String useRange, String name, Integer sort) {
        this.corpId = corpId;
        this.parentId = parentId;
        this.type = type;
        this.useRange = useRange;
        this.name = name;
        this.sort = sort;
    }
}
