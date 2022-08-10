package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.entity.SysRole;
import com.easyink.wecom.domain.WeUserRole;

/**
 * 企微用户-角色关系 业务接口
 *
 * @author : silver_chariot
 * @date : 2021/8/18 15:34
 */
public interface WeUserRoleService extends IService<WeUserRole> {
    /**
     * 为企业初始化默认角色
     *
     * @param corpId   公司ID
     * @param createBy 创建人 (不传则默认admin)
     * @return true 初始化成功 false 初始化失败或不完全
     */
    Boolean initDefaultRole(String corpId, String createBy);

    /**
     * 插入不存在的默认角色
     *
     * @param role 角色
     * @return true 已存在或插入成功 false 插入失败
     */
    Boolean insertRoleIfNotExist(SysRole role);

    /**
     * 插入默认角色 并初始化菜单
     *
     * @param role 角色实体
     * @return 插入成功角色数
     */
    Integer insertDefaultRoleAndInitMenus(SysRole role);

    /**
     * 根据corpId和roleKey查询角色ID
     *
     * @param corpId  公司id
     * @param roleKey 角色Key
     * @return 角色ID
     */
    Long selectRoleIdByCorpIdAndRoleKey(String corpId, String roleKey);
}
