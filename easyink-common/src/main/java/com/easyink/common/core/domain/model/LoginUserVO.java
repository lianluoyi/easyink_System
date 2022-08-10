package com.easyink.common.core.domain.model;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 返回给前端的登录对象
 *
 * @author : silver_chariot
 * @date : 2021/8/23 20:38
 */
@ApiModel
@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "用户ID")
    private String userId;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "是否是超级管理员")
    private Boolean isSuperAdmin;
    @ApiModelProperty(value = "角色集合")
    private HashSet<String> roles;
    @ApiModelProperty(value = "权限集合")
    private HashSet<String> permissions;
    @ApiModelProperty(value = "主题颜色")
    private String uiColor;

    /**
     * 构建返回给前端的登录用户对象
     *
     * @param loginUser   登录用户实体
     * @param roles
     * @param permissions
     */
    public LoginUserVO(LoginUser loginUser, HashSet<String> roles, HashSet<String> permissions) {
        this.roles = roles;
        this.permissions = permissions;
        this.uiColor = loginUser.getUiColor();
        if (loginUser.isSuperAdmin() && ObjectUtil.isNotNull(loginUser.getUser())) {
            //构建账密登录管理员用户实体
            this.isSuperAdmin = true;
            this.userName = loginUser.getUser().getUserName();
            this.avatar = loginUser.getUser().getAvatar();
            this.userId = String.valueOf(loginUser.getUser().getUserId());
        } else if (!loginUser.isSuperAdmin() && ObjectUtil.isNotNull(loginUser.getWeUser())) {
            //构建 扫码登录用户实体
            this.isSuperAdmin = false;
            this.userName = loginUser.getWeUser().getName();
            this.avatar = loginUser.getWeUser().getAvatarMediaid();
            this.userId = loginUser.getWeUser().getUserId();
        }
    }
}
