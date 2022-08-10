package com.easyink.wecom.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.domain.entity.SysRole;
import com.easyink.common.enums.DataScopeEnum;
import com.easyink.common.enums.RoleTypeEnum;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeUserRole;
import com.easyink.wecom.mapper.WeUserRoleMapper;
import com.easyink.wecom.service.WeUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * 企微用户-角色关系业务接口 实现类
 *
 * @author : silver_chariot
 * @date : 2021/8/18 15:36
 */
@Service
@Slf4j
public class WeUserRoleServiceImpl extends ServiceImpl<WeUserRoleMapper, WeUserRole> implements WeUserRoleService {

    private final WeUserRoleMapper weUserRoleMapper;

    @Autowired
    public WeUserRoleServiceImpl(@NotNull WeUserRoleMapper weUserRoleMapper) {
        this.weUserRoleMapper = weUserRoleMapper;
    }

    @Override
    public Boolean initDefaultRole(String corpId, String createBy) {
        if (StringUtils.isBlank(corpId)) {
            return false;
        }
        // 不传创建人则默认admin
        if (StringUtils.isBlank(createBy)) {
            createBy = Constants.SUPER_ADMIN;
        }
        // 初始化管理员角色实体
        SysRole admin = new SysRole(corpId, UserConstants.INIT_ADMIN_ROLE_NAME, UserConstants.INIT_ADMIN_ROLE_KEY,
                createBy, DataScopeEnum.ALL.getCode(), RoleTypeEnum.SYS_ADMIN.getType());

        //初始化部门管理员角色实体
        SysRole departAdmin = new SysRole(corpId, UserConstants.INIT_DEPARTMENT_ADMIN_ROLE_NAME, UserConstants.INIT_DEPARTMENT_ADMIN_ROLE_KEY,
                createBy, DataScopeEnum.SELF_DEPT.getCode(), RoleTypeEnum.SYS_DEFAULT.getType());

        //初始化普通员工实体
        SysRole employee = new SysRole(corpId, UserConstants.INIT_EMPLOYEE_ROLE_NAME, UserConstants.INIT_EMPLOYEE_ROLE_KEY,
                createBy, DataScopeEnum.SELF.getCode(), RoleTypeEnum.SYS_DEFAULT.getType());

        try {
            // 分别判断3个默认角色是否存在 不存在则插入
            return insertRoleIfNotExist(admin) && insertRoleIfNotExist(departAdmin) && insertRoleIfNotExist(employee);
        } catch (Exception e) {
            log.error("初始化企业系统默认角色失败,corpId:{},e:{}", corpId, ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public Boolean insertRoleIfNotExist(SysRole role) {
        //根据role_key 判断该默认角色是否存在 ,不存在则插入
        return weUserRoleMapper.selectRoleByCorpIdAndRoleKey(role) != null || insertDefaultRoleAndInitMenus(role) > 0;
    }

    @Override
    public Integer insertDefaultRoleAndInitMenus(SysRole role) {
        Integer res = weUserRoleMapper.insertRole(role);
        String menuIds = null;
        if (UserConstants.INIT_ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
            menuIds = UserConstants.ADMIN_DEFAULT_MENU_IDS;
        } else if (UserConstants.INIT_DEPARTMENT_ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
            menuIds = UserConstants.DEPARTMENT_ADMIN_DEFAULT_IDS;
        } else if (UserConstants.INIT_EMPLOYEE_ROLE_KEY.equals(role.getRoleKey())) {
            menuIds = UserConstants.EMPLOYEE_DEFAULT_IDS;
        }
        if (StringUtils.isNotBlank(menuIds)) {
            String[] menuArr = menuIds.split(",");
            weUserRoleMapper.insertRoleMenu(role.getRoleId(), menuArr);
        }
        return res;
    }

    @Override
    public Long selectRoleIdByCorpIdAndRoleKey(String corpId, String roleKey) {
        SysRole role = weUserRoleMapper.selectRoleByCorpIdAndRoleKey(
                SysRole.builder()
                        .corpId(corpId)
                        .roleKey(roleKey)
                        .build()
        );
        if (ObjectUtil.isNotNull(role) && ObjectUtil.isNotNull(role.getRoleId())) {
            return role.getRoleId();
        }
        return Constants.DEFAULT_ID;
    }


}
