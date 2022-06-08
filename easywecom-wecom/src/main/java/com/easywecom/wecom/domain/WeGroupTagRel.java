package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名：WeGroupTagRel
 *
 * @author Society my sister Li
 * @date 2021-11-15 14:15
 */
@Data
@TableName("we_group_tag_rel")
@ApiModel("客户群与标签关联关系")
@AllArgsConstructor
@NoArgsConstructor
public class WeGroupTagRel {

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "企业ID", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "群ID")
    @TableField("chat_id")
    private String chatId;


    @ApiModelProperty(value = "标签ID")
    @TableField("tag_id")
    private Long tagId;


    public WeGroupTagRel(String corpId, String chatId, Long tagId) {
        this.corpId = corpId;
        this.chatId = chatId;
        this.tagId = tagId;
    }


}
