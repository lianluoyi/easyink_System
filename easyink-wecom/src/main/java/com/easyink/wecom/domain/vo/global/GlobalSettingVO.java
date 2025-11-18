package com.easyink.wecom.domain.vo.global;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局设置VO
 *
 * @author tigger
 * 2025/7/15 15:06
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSettingVO {

    /**
     *
     */
    @ApiModelProperty(value = "查询跨月份限制数")
    private Integer selectMonthLimit;

}
