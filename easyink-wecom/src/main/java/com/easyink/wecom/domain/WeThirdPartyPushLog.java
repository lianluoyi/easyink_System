package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 第三方推送日志对象 we_third_party_push_log
 *
 * @author easyink
 * @date 2024-01-01
 */
@ApiModel("第三方推送日志")
@EqualsAndHashCode(callSuper = true)
@TableName("we_third_party_push_log")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeThirdPartyPushLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("关联记录ID(satisfaction_form_record表ID)")
    @TableField("record_id")
    private Long recordId;

    @ApiModelProperty("推送类型(1:表单提交推送 2:超时未提交推送)")
    @TableField("push_type")
    private Integer pushType;

    @ApiModelProperty("推送URL")
    @TableField("push_url")
    private String pushUrl;

    @ApiModelProperty("推送数据(JSON格式)")
    @TableField("push_data")
    private String pushData;

    @ApiModelProperty("推送结果")
    @TableField("push_result")
    private String pushResult;

    @ApiModelProperty("HTTP响应码")
    @TableField("http_code")
    private Integer httpCode;

    @ApiModelProperty("推送状态(0:推送中 1:成功 2:失败)")
    @TableField("push_status")
    private Integer pushStatus;

    @ApiModelProperty("错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @ApiModelProperty("推送时间")
    @TableField("push_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pushTime;

    @ApiModelProperty("响应时间")
    @TableField("response_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date responseTime;

    @ApiModelProperty("耗时(毫秒)")
    @TableField("cost_time")
    private Long costTime;
}
