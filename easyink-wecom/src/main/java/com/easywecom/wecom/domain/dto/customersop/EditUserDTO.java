package com.easywecom.wecom.domain.dto.customersop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 类名： EditUserDTO
 *
 * @author 佚名
 * @date 2021/12/8 16:46
 */
@Data
@ApiModel("修改员工dto EditUserDTO")
public class EditUserDTO {
    @ApiModelProperty(value = "sopId")
    private Long id;
    @ApiModelProperty("指定员工id")
    private List<String> userIdList;

    @ApiModelProperty("指定部门id")
    private List<String> departmentIdList;

    @ApiModelProperty(value = "企业ID", hidden = true)
    @JsonIgnore
    private String corpId;
}
