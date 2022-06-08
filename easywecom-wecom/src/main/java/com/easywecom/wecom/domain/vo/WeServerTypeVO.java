package com.easywecom.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WeServerTypeVO
 *
 * @author: 1*+
 * @date: 2021-09-08 16:24
 */
@Data
@ApiModel("服务器类型实体")
@NoArgsConstructor
@AllArgsConstructor
public class WeServerTypeVO {

    @ApiModelProperty("服务器类型,internal内部应用，third三方应用")
    private String serverType;

}
