package com.easywecom.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名：UpdateUserInfoDetailDTO
 *
 * @author Society my sister Li
 * @date 2021-11-16 18:27
 */
@Data
@ApiModel("批量操作的员工信息")
public class UpdateUserInfoDetailDTO {

    @ApiModelProperty(value = "员工ID", required = true)
    private String userId;

    @ApiModelProperty(value = "员工姓名", required = true)
    private String userName;
}
