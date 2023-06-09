package com.easyink.wecom.domain.dto.welcomemsg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 群欢迎语修改DTO
 *
 * @author tigger
 * 2022/1/7 11:43
 **/
@Data
public class WelComeMsgUpdateGroupDTO extends WelComeMsgUpdateDTO {
    @ApiModelProperty(value = "群素材是否通知员工标识(0: 不通知(默认) 1:通知)")
    private boolean noticeFlag;
}
