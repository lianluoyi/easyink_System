package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名： 话术库主表实体
 *
 * @author 佚名
 * @date 2021/10/25 17:34
 */
@Data
@TableName("we_words_group")
@ApiModel("话术库主表实体")
@NoArgsConstructor
public class WeWordsGroupEntity {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID")
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;
    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID")
    @TableField("category_id")
    private Long categoryId;
    /**
     * 话术标题
     */
    @ApiModelProperty(value = "话术标题")
    @TableField("title")
    private String title;
    /**
     * 附件ID用逗号隔开，从左往右表示先后顺序
     */
    @ApiModelProperty(value = "附件ID用逗号隔开，从左往右表示先后顺序")
    @TableField("seq")
    private String[] seq;
    /**
     * 是否推送到应用（0：不推送，1推送）
     */
    @ApiModelProperty(value = "是否推送到应用（0：不推送，1推送）")
    @TableField("is_push")
    private Boolean isPush;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Long sort;

    public WeWordsGroupEntity(String title, String corpId, Long categoryId) {
        this.title = title;
        this.corpId = corpId;
        this.categoryId = categoryId;
        this.seq = new String[]{};
        this.isPush = Boolean.TRUE;
    }
}
