package com.easyink.wecom.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 类名：BatchUpdateUserInfoDTO
 *
 * @author Society my sister Li
 * @date 2021-11-16 15:21
 */
@Data
@ApiModel("批量修改员工信息")
public class BatchUpdateUserInfoDTO {

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "需要修改的员工列表", required = true)
    @Size(min = 1, message = "userIdList不能为空")
    @NotNull(message = "userIdList不能为空")
    private List<UpdateUserInfoDetailDTO> userIdList;

    @ApiModelProperty(value = "操作类型：1、角色;2、职务;3、所在部门", required = true)
    @NotNull(message = "type不能为空")
    private Integer type;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @Size(max = 64, message = "position长度不能超过64个字段")
    private String position;

    @ApiModelProperty("部门ID")
    private Long department;
}
