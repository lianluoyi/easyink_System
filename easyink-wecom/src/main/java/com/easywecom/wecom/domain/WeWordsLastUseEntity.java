package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


/**
 * 类名： 最近使用话术表
 *
 * @author 佚名
 * @date 2021-11-02 10:32:00
 */
@Data
@TableName("we_words_last_use")
@ApiModel("最近使用话术表实体")
public class WeWordsLastUseEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    @JsonIgnore
    private Long id;
    /**
     * 企业id
     */
    @ApiModelProperty(value = "企业id")
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;
    /**
     * 员工id
     */
    @ApiModelProperty(value = "员工id admin传‘admin’")
    @TableField("user_id")
    private String userId;
    /**
     * 话术类型（0：企业话术，1：部门话术，2：我的话术）
     */
    @ApiModelProperty(value = "话术类型（0：企业话术，1：部门话术，2：我的话术）", required = true)
    @TableField("type")
    private Integer type;
    /**
     * 话术id
     */
    @ApiModelProperty(value = "话术id数组顺序就是话术顺序", required = true)
    @TableField("words_ids")
    @NotEmpty(message = "话术id")
    private String[] wordsIds;

}
