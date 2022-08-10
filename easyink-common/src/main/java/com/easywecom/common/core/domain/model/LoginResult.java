package com.easywecom.common.core.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果实体
 *
 * @author : silver_chariot
 * @date : 2021/8/23 14:01
 */
@Data
@ApiModel("登录结果实体")
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {

    @ApiModelProperty(value = "访问后端接口的token")
    private String token;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;
    @ApiModelProperty(value = "当前登录人", hidden = true)
    private LoginUser loginUser;

    public LoginResult(String token) {
        this.token = token;
    }

    public LoginResult(String token, LoginUser loginUser) {
        this.token = token;
        this.loginUser = loginUser;
    }
}
