package com.easyink.wecom.domain.vo.form;

import com.easyink.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户提交表单详情VO
 *
 * @author wx
 * 2023/1/29 17:53
 **/
@NoArgsConstructor
@Data
public class FormOperRecordDetailVO {

    @ApiModelProperty("记录id")
    private String recordId;

    @ApiModelProperty("表单名称")
    private String formName;

    @ApiModelProperty("客户id")
    private String externalUserId;

    @ApiModelProperty("客户昵称")
    @Excel(name = "客户", sort = 1)
    private String externalUserName;

    @ApiModelProperty("客户头像url")
    private String externalUserHeadImage;

    @ApiModelProperty("提交表单结果")
    private String formResult;

    /**
     * {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     */
    @ApiModelProperty("点击渠道")
    @Excel(name = "点击渠道", sort = 3, readConverterExp="4=侧边栏,11=推广")
    private Integer channelType;

    @ApiModelProperty("提交时间")
    @Excel(name = "提交时间", sort = 5, dateFormat ="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private Date commitTime;

}
