package com.easyink.common.core.domain.model;

import cn.hutool.core.util.ObjectUtil;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.entity.SysRole;
import com.easyink.common.core.domain.entity.SysUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.DataScopeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.user.UserNoCorpException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 登录用户身份权限
 *
 * @author：admin
 */
public class LoginUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 权限列表
     */
    private Set<String> permissions;
    /**
     * 是否是超级管理员
     */
    private boolean isSuperAdmin;

    /**
     * 登录用户是否为个人数据权限
     *
     * @return
     */
    public boolean isSelfDataScope() {
        return !isSuperAdmin && DataScopeEnum.SELF.getCode().equals(getRole().getDataScope());
    }

    /**
     * 用户信息
     */
    private SysUser user;
    /**
     * 企微用户信息
     */
    private WeUser weUser;
    /**
     * 所属的系统角色
     */
    private SysRole role;
    /**
     * 可见的部门数据范围（部门ID ,隔开）
     */
    private String departmentDataScope;
    /**
     * ui界面颜色
     */
    private String uiColor;

    /**
     * 企业名
     */
    @Getter
    @Setter
    private String corpName;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginUser() {
    }

    /**
     * 根据账密登录的超级管理员用户 构建登录用户实体
     *
     * @param user        sysUser
     * @param permissions 权限set
     */
    public LoginUser(SysUser user, Set<String> permissions) {
        this.user = user;
        this.permissions = permissions;
        this.isSuperAdmin = true;
    }

    /**
     * 根据 扫码登录的企微用户构建 登录用户实体
     *
     * @param weUser      企微用户实体
     * @param permissions 权限set
     */
    public LoginUser(WeUser weUser, Set<String> permissions) {
        this.weUser = weUser;
        this.permissions = permissions;
        this.isSuperAdmin = false;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        if(user != null) {
            return user.getPassword();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getUsername() {
        if(isSuperAdmin){
            return user.getUserName();
        }else {
            return weUser.getName();
        }
    }

    /**
     * 获取部门名字
     *
     * @return
     */
    public String getDepartmentName() {
        if(isSuperAdmin && ObjectUtil.isNotNull(user) && ObjectUtil.isNotNull(user.getDept())) {
            return  user.getDept().getDeptName();
        }else if(!isSuperAdmin && ObjectUtil.isNotNull(weUser)) {
            return weUser.getDepartmentName();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * 是否可用 ,禁用的用户不能身份验证
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin && this.user != null;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public WeUser getWeUser() {
        return weUser;
    }

    public void setWeUser(WeUser weUser) {
        this.weUser = weUser;
    }

    public String getDepartmentDataScope() {
        return departmentDataScope;
    }

    public void setDepartmentDataScope(String departmentDataScope) {
        this.departmentDataScope = departmentDataScope;
    }

    public SysRole getRole() {
        return role;
    }

    public void setRole(SysRole role) {
        this.role = role;
    }

    /**
     * 获取当前登录用户的公司ID
     *
     * @return 公司ID
     * @throws UserNoCorpException 获取用户企业异常
     */
    public String getCorpId() {
        if (isSuperAdmin && null != user) {
            return user.getCorpId();
        } else if (null != weUser && StringUtils.isNotBlank(weUser.getCorpId())) {
            return weUser.getCorpId();
        } else {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
    }

    /**
     * 给当前登录设置corpId
     *
     * @param corpId 需要设置的corpId
     */
    public void setCorpId(String corpId) {
        if (isSuperAdmin()) {
            user.setCorpId(corpId);
        } else if (weUser != null) {
            weUser.setCorpId(corpId);
        }
    }

    /**
     * 重写eq方法，若两个user corpId和userId相同
     *
     * @param obj weUser
     * @return corpId和userId相等则返回true
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LoginUser)) {
            return false;
        }
        LoginUser loginUser = (LoginUser) obj;
        if (isSuperAdmin() && loginUser.isSuperAdmin()) {
            // 账密登录用户 比较corpId 和 userId
            return this.user.equals(loginUser.user);
        } else if (!isSuperAdmin() && !loginUser.isSuperAdmin()) {
            if (this.weUser == null || loginUser.weUser == null) {
                return false;
            }
            // 扫码登录用户比较corpId 和 userId
            return this.weUser.equals(loginUser.getWeUser());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(isSuperAdmin()){
            return Objects.hash(user.getUserId(), user.getCorpId());
        }
        return Objects.hash(weUser.getUserId());
    }

    /**
     * 获取当前登录用户所设置的主题颜色
     *
     * @return 当前登录用户所设置的主题颜色
     */
    public String getUiColor() {
        if (isSuperAdmin && null != user && StringUtils.isNotBlank(user.getUiColor())) {
            return user.getUiColor();
        } else if (!isSuperAdmin && null != weUser && StringUtils.isNotBlank(weUser.getUiColor())) {
            return weUser.getUiColor();
        }
        // 返回默认颜色
        return Constants.DEFAULT_UI_COLOR;
    }

    public void setUiColor(String uiColor) {
        this.uiColor = uiColor;
    }

    /**
     * 获取登录人的userId admin用户返回admin
     * @return userId
     */
    public String getUserId(){
        if (this.isSuperAdmin()){
            return "admin";
        }else {
           return this.getWeUser().getUserId();
        }
    }


}
