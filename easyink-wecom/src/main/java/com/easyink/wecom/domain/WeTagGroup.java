package com.easyink.wecom.domain;

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
import java.util.Date;
import java.util.List;

/**
 * 标签组对象 we_tag_group
 *
 * @author 佚名
 * @date 2021-7-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_tag_group")
public class WeTagGroup {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "分组id")
    @TableId
    @TableField("group_id")
    private String groupId;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "分组名")
    @TableField("group_name")
    @NotBlank(message = "标签组名称不能为空")
    @Size(max = 15, message = "标签组名称长度已超出限制")
    private String groupName;

    @ApiModelProperty(value = "创建者")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();


    @ApiModelProperty(value = "帐号状态（0正常 2删除）")
    @TableField("status")
    private String status = new String("0");

    /**
     * 标签
     */
    @TableField(exist = false)
    private List<WeTag> weTags;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询标签组/标签时的参数:标签组名或标签名")
    private String searchName;
}
