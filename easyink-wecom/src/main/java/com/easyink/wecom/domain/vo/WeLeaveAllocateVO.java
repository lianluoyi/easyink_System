package com.easyink.wecom.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 类名： 离职继承单一分配实体类
 *
 * @author 佚名
 * @date 2021/10/13 16:12
 */
@Data
@ApiModel("离职继承单一分配实体类")
public class WeLeaveAllocateVO {
    @ApiModelProperty(value = "群聊id")
    private String chatId;

    @ApiModelProperty(value = "客户id")
    private String externalUserid;

    @ApiModelProperty(value = "原跟进人id")
    @NotBlank(message = "原跟进人id不能为空")
    private String handoverUserid;

    @ApiModelProperty(value = "原跟进人员离职时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "原跟进人员离职时间不能为空")
    private Date dimissionTime;

    @ApiModelProperty(value = "接替者id")
    @NotBlank(message = "接替者id不能为空")
    private String takeoverUserid;
    /**
     * 公司id
     */
    @NotBlank(message = "公司id不能为空")
    private String corpId;
}
