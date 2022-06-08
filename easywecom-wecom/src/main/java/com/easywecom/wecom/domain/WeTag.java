package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 企业微信标签对象 we_tag
 *
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_tag")
public class WeTag implements Serializable {

    @ApiModelProperty(value = "标签组id")
    @TableField("group_id")
    private String groupId;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "标签名")
    @TableField("name")
    @NotBlank(message = "标签名不能为空")
    @Size(max = 15, message = "标签名长度已超出限制")
    private String name;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    @ApiModelProperty(value = "状态（0正常 1删除）")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "微信端返回的id")
    @TableId
    @TableField("tag_id")
    private String tagId;

    @ApiModelProperty(value = "非主键自增序列号")
    @TableField("seq_id")
    private Long seqId;


}
