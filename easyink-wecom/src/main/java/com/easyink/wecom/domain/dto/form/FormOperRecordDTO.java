package com.easyink.wecom.domain.dto.form;

import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 表单操作记录DTO
 *
 * @author wx
 * 2023/1/13 15:18
 **/
@Data
@Builder
public class FormOperRecordDTO {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("智能表单id")
    private Long formId;

    @ApiModelProperty("发送智能表单的员工id")
    private String userId;

    @ApiModelProperty("员工名称")
    private String userName;

    @ApiModelProperty("员工头像地址url")
    private String userHeadImage;

    @ApiModelProperty("客户id")
    private String externalUserId;

    /**
     * {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     */
    private Integer channelType;

    @ApiModelProperty("外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。")
    private String unionId;

    @ApiModelProperty("公众号/小程序open_id")
    private String openId;

    @ApiModelProperty("填写结果,格式[{\"question\":\"\",\"type\":\"\",\"answer\":\"\"}]")
    private String formResult;

    @ApiModelProperty("创建时间/点击时间")
    private Date createTime;

    @ApiModelProperty("提交时间")
    private Date commitTime;

}
