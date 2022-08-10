package com.easywecom.wecom.login.service;

import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.core.domain.entity.SysUser;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.enums.UserStatus;
import com.easywecom.common.exception.BaseException;
import com.easywecom.common.service.ISysUserService;
import com.easywecom.common.token.SysPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author admin
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private RuoYiConfig ruoYiConfig;


    private static final String LOGIN_ERROR_NOTICE = "对不起，您的账号：";

    /**
     * 账密登录 : 登录校验 (只有内部应用可用)
     *
     * @param username 登录用户名
     * @return 用户详情 {@link UserDetails}
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!ruoYiConfig.isInternalServer()) {
            throw new BaseException("不支持的登录类型");
        }
        SysUser user = userService.selectUserByUserName(username);
        if (null == user) {
            log.info("登录用户：{} 不存在", username);
            throw new BaseException(LOGIN_ERROR_NOTICE + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BaseException(LOGIN_ERROR_NOTICE + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BaseException(LOGIN_ERROR_NOTICE + username + " 已停用");
        } else if (!user.isAdmin()) {
            log.info("账密登录用户:{} 非管理员", username);
            throw new BaseException("请扫码登录或使用超级管理员账号登录");
        }
        return createLoginUser(user);
    }

    /**
     * 创建登录用户
     *
     * @param user 系统用户
     * @return 登录用户详情
     */
    public UserDetails createLoginUser(SysUser user) {
        return new LoginUser(user, permissionService.getMenuPermission(user));
    }
}
