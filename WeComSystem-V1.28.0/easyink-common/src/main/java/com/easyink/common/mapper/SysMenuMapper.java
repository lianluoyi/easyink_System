package com.easyink.common.mapper;

import com.easyink.common.core.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单表 数据层
 *
 * @author admin
 */

@Repository
public interface SysMenuMapper {
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);


    /**
     * 通过菜单ids查询系统菜单列表
     *
     * @param menuIds 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByMenuIds(@Param("menuIds") Long [] menuIds);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);


    /**
     * 根据企微用户ID 查询权限
     *
     * @param corpId   公司ID
     * @param weUserId 企微用户ID
     * @return
     */
    List<String> selectMenuPermsByWeUserId(@Param("corpId") String corpId, @Param("weUserId") String weUserId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Integer> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int hasChildByMenuId(Long menuId);

    /**
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    /**
     * 根据企微userID 获取其所属角色下的所有菜单树信息
     *
     * @param corpId   企业id
     * @param weUserId 企微userId
     * @return 菜单树
     */
    List<SysMenu> selectMenuTreeByWeUserId(@Param("corpId") String corpId, @Param("userId") String weUserId);

    /**
     * 获取所传菜单的所有父菜单
     *
     * @param menuIds 菜单 ，隔开
     * @return
     */
    List<SysMenu> selectParentMenuList(@Param("array") Long[] menuIds);

    /**
     * 查询默认菜单
     *
     * @return
     */
    List<SysMenu> selectDefaultPage();
}
