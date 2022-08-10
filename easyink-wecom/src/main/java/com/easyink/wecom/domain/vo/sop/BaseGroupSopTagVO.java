package com.easyink.wecom.domain.vo.sop;

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
@ApiModel("群SOP详情-标签信息")
public class BaseGroupSopTagVO {

    @ApiModelProperty("标签ID")
    private Long tagId;

    @ApiModelProperty("标签名称")
    private String name;
}
