-- 2022-10-08 wx 添加客户群导出按钮 Tower 任务: 客户群报表支持导出 ( https://tower.im/teams/636204/todos/55174 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2303, '导出客户群', 2003, 6, '', NULL, 1, 'F', '0', '0', 'customerManage:group:export', '#', 'admin', '2022-10-08 15:20:41', 'admin', '2022-10-08 15:22:17', '');

-- 给以前的管理员增加权限
INSERT INTO sys_role_menu
  (role_id, menu_id)
SELECT role_id,
     2303
FROM sys_role
WHERE role_type = 1;

-- 给以前的部门管理员增加权限
INSERT INTO sys_role_menu
  (role_id, menu_id)
SELECT role_id,
     2303
FROM sys_role
WHERE role_type = 2
AND role_key = 'depart';