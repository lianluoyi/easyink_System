package com.easyink.wecom.domain.entity.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 智能表单操作记录表(WeFormOperRecord)实体类
 *
 * @author wx
 * @since 2023-01-13 12:23:42
 */
@NoArgsConstructor
@Data
@TableName("we_form_oper_record")
@AllArgsConstructor
@Builder
public class WeFormOperRecord implements Serializable {
    private static final long serialVersionUID = -74077762856351920L;

    @ApiModelProperty("主键id")
    @TableId
    private Long id;

    @ApiModelProperty("智能表单id")
    @TableField("form_id")
    private Long formId;

    @ApiModelProperty("发送智能表单的员工id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("员工名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("员工头像地址url")
    @TableField("user_head_image")
    private String userHeadImage;

    @ApiModelProperty("客户id")
    @TableField("external_user_id")
    private String externalUserId;

    @ApiModelProperty("客户所属员工user_id")
    @TableField("employees")
    private String employees;

    /**
     * {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     */
    @TableField("channel_type")
    private Integer channelType;

    @ApiModelProperty("外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。")
    @TableField("union_id")
    private String unionId;

    @ApiModelProperty("公众号/小程序open_id")
    @TableField("open_id")
    private String openId;

    @ApiModelProperty("填写结果,格式[{\"question\":\"\",\"type\":\"\",\"answer\":\"\"}]")
    @TableField("form_result")
    private String formResult;

    @ApiModelProperty("创建时间/点击时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("提交时间")
    @TableField("commit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commitTime;

    @ApiModelProperty("是否已提交，0：未提交，1：已提交")
    @TableField("commit_flag")
    private Boolean commitFlag;

}

