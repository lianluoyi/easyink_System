package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名：BaseWeUserVO
 *
 * @author Society my sister Li
 * @date 2021-12-09 17:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("群SOP详情-群主信息")
public class BaseGroupSopWeUserVO {

    @ApiModelProperty("员工userId")
    private String userId;

    @ApiModelProperty("员工姓名")
    private String name;
}
