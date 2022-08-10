package com.easyink.wecom.domain.dto;

import com.easyink.common.core.domain.RootEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询员工信息DTO
 *
 * @author 佚名
 * @date 2021/8/24 11:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUserDTO extends RootEntity {
    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 是否激活
     */
    private Integer isActivate;
    /**
     * 员工姓名
     */
    private String userName;
    /**
     * 部门id 用逗号分割
     */
    private String departments;
    /**
     * 企业ID
     */
    private String corpId;
    /**
     * 是否被分配
     */
    private Integer isAllocate;
}
