package com.easyink.common.config;

import com.easyink.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名: ThirdDefaultDomainConfig
 *
 * @author: 1*+
 * @date: 2021-09-29 14:57
 */
@Data
@ApiModel("三方应用服务默认域名配置")
public class ThirdDefaultDomainConfig {

    @ApiModelProperty("前端dashboard域名")
    private String dashboard;
    @ApiModelProperty("侧边栏sidebar域名")
    private String sidebar;
    @ApiModelProperty("后端scrm域名")
    private String scrm;

    public String getSidebar() {
        if (StringUtils.isBlank(sidebar)) {
            return StringUtils.EMPTY;
        }
        return sidebar;
    }
}
