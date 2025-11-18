package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 满意度表单发送记录表 we_form_send_record
 *
 * @author easyink
 */
@ApiModel("满意度表单发送记录")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_form_send_record")
public class WeFormSendRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 企业ID */
    @ApiModelProperty(value = "企业ID", required = true)
    @TableField("corp_id")
    @NotBlank(message = "企业ID不能为空")
    private String corpId;

    /** 客户外部联系人ID */
    @ApiModelProperty(value = "客户外部联系人ID", required = true)
    @TableField("external_userid")
    @NotBlank(message = "客户外部联系人ID不能为空")
    private String externalUserid;

    /** 员工用户ID */
    @ApiModelProperty(value = "员工用户ID", required = true)
    @TableField("user_id")
    @NotBlank(message = "员工用户ID不能为空")
    private String userId;

    /** 表单ID */
    @ApiModelProperty(value = "表单ID", required = true)
    @TableField("form_id")
    @NotNull(message = "表单ID不能为空")
    private Long formId;

    @ApiModelProperty(value = "表单发送渠道", required = true)
    @TableField("source")
    private Integer source;

    /** 发送时间 */
    @ApiModelProperty(value = "发送时间", required = true)
    @TableField("sent_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "发送时间不能为空")
    private Date sentTime;

    /** 提交时间 */
    @ApiModelProperty(value = "提交时间")
    @TableField("submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 提交状态(0:未提交 1:已提交) */
    @ApiModelProperty(value = "提交状态")
    @TableField("submit_status")
    private Integer submitStatus;

    /** 表单结果数据(JSON格式) */
    @ApiModelProperty(value = "表单结果数据")
    @TableField("form_result_data")
    private String formResultData;

    /** 推送状态(0:未推送 1:已推送 2:推送失败) */
    @ApiModelProperty(value = "推送状态")
    @TableField("push_status")
    private Integer pushStatus;

    /** 推送时间 */
    @ApiModelProperty(value = "推送时间")
    @TableField("push_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pushTime;

    /** 超时推送状态(0:未推送 1:已推送 2:推送失败) */
    @ApiModelProperty(value = "超时推送状态")
    @TableField("timeout_push_status")
    private Integer timeoutPushStatus;

    /** 超时推送时间 */
    @ApiModelProperty(value = "超时推送时间")
    @TableField("timeout_push_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeoutPushTime;

    /** 客户专属活码state */
    @ApiModelProperty(value = "客户专属活码state")
    @TableField("state")
    private String state;

}
