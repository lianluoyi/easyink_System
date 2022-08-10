package com.easyink.wecom.domain.dto.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 类名: 分配离职员工请求实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:54
 */
@Data
public class TransferResignedUserDTO {

    @ApiModelProperty(value = "原跟进人列表" ,required = true)
    @NotEmpty(message = "原跟进人列表不能为空")
    private List<LeaveUserDetail> handoverUserList;

    @ApiModelProperty(value = "接替者id", required = true)
    @NotBlank(message = "接替者id不能为空")
    private String takeoverUserid;

    @ApiModelProperty(value = "企业ID,不必填")
    private String corpId;

    @ApiModelProperty(value = "指定分配的群聊id")
    private String chatId;

    @ApiModelProperty(value = "指定分配的外部联系人id")
    private String externalUserid;

    @Data
    public static class LeaveUserDetail {

        @ApiModelProperty(value = "成员userId")
        @NotBlank(message = "成员id不能为空")
        private String userId;

        @ApiModelProperty("员工离职时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotBlank(message = "离职时间不能为空")
        private Date dimissionTime;
    }
}
