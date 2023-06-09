package com.easyink.wecom.domain.vo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 表单客户操作记录VO
 *
 * @author wx
 * 2023/1/29 13:45
 **/
@NoArgsConstructor
@Data
public class FormCustomerOperRecordVO {
    
    @ApiModelProperty("记录id")
    private Long recordId;

    @ApiModelProperty("客户id")
    private String externalUserId;

    @ApiModelProperty("客户昵称")
    private String externalUserName;

    @ApiModelProperty("客户头像url")
    private String externalUserHeadImage;

    @ApiModelProperty("发送智能表单的员工id")
    private String userId;

    @ApiModelProperty("员工名称")
    private String userName;

    @ApiModelProperty("员工所属部门")
    private String departmentName;

    /**
     * {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     */
    @ApiModelProperty("点击渠道")
    private Integer channelType;

    @ApiModelProperty("创建时间/点击时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commitTime;

}
