package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName： UserVO
 *
 * @author wx
 * @date 2022/8/2 11:41
 */
@Data
@ApiModel("员工实体VO")
@NoArgsConstructor
public class UserVO {
    @ApiModelProperty(value = "员工id")
    private String userId;

    @ApiModelProperty(value = "员工名称")
    private String name;
}
