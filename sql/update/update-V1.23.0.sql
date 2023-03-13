-- wx 2023-02-22 修改客户群成员表中join_time的数据类型 Tower 任务: 同步客户群API调用优化 ( https://tower.im/teams/636204/todos/61671 )
ALTER TABLE `we_group_member`
    MODIFY COLUMN `join_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '加群时间' AFTER `union_id`;
-- wx 2023-02-28 导出员工活码数据报表 Tower 任务: 员工活码详情数据导出 ( https://tower.im/teams/636204/todos/63010 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2318, '导出报表', 2053, 8, '', NULL, 1, 'F', '0', '0', 'wecom:codeAnalyse:export', '#', 'admin', '2023-02-28 10:09:09', 'admin', '2023-02-28 10:09:39', '');

-- 给以前的管理员、部门管理员增加导出权限
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2318
FROM sys_role
WHERE role_type = 1 OR (role_type = 2
    AND role_key = 'depart');