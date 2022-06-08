package com.easywecom.wecom.domain.dto.welcomemsg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 欢迎语删除DTO
 *
 * @author tigger
 * 2022/1/13 16:46
 **/
@Data
public class WelComeMsgDeleteDTO {

    @NotEmpty(message = "至少选择一个要删除的欢迎语")
    @ApiModelProperty(value = "删除的欢迎语ids")
    private List<Long> ids;
}
