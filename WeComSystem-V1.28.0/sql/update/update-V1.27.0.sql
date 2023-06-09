-- 2023-05-04 zhaorui 修改删除时间默认值 Tower 任务: 新增客户流失时间异常 ( https://tower.im/teams/636204/todos/67481 )
ALTER TABLE `we_flower_customer_rel` CHANGE `delete_time` `delete_time` DATETIME DEFAULT '0000-00-00 00:00:00' NOT NULL COMMENT '删除时间';
-- 2023-05-04 lcy 更新数据库中的数据单位，由分钟改为秒：(原数据 * 60)，注意只可执行一次 Tower 任务: 首次回复时长统计精确到s ( https://tower.im/teams/636204/todos/67465 )
UPDATE we_user_customer_message_statistics SET first_reply_time_interval_alter_receive = first_reply_time_interval_alter_receive * 60;
-- 2023-05-04 lcy 修改数据库字段备注内容 Tower 任务: 首次回复时长统计精确到s ( https://tower.im/teams/636204/todos/67465 )
ALTER TABLE `we_user_customer_message_statistics` CHANGE `first_reply_time_interval_alter_receive` `first_reply_time_interval_alter_receive` int(6) NOT NULL DEFAULT '0' COMMENT '当天收到客户消息到首次回复客户时间间隔（单位：秒） ES中查询并计算';
-- 2023-05-09 lcy 为we_flower_customer_tag_rel表添加索引 Tower 任务: 标签统计 ( https://tower.im/teams/636204/todos/63992 )
ALTER TABLE `we_flower_customer_tag_rel` ADD INDEX tagid_externaluserid (`tag_id`,`external_userid`);
-- 2023-05-09 lcy 添加标签统计菜单和标签统计-导出报表功能 Tower 任务: 后端-菜单权限 ( https://tower.im/teams/636204/todos/67832 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2320, '标签统计', 2313, 3, 'labelStatistics', 'dataStatistics/labelStatistics/index', 1, 'C', '0', '0', NULL, '#', 'admin', '2023-05-09 13:48:26', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2321, '导出报表', 2320, 1, '', NULL, 1, 'F', '0', '0', 'statistic:labelStatistics:export', '#', 'admin', '2023-05-09 13:48:26', 'admin', '2023-05-09 13:48:46', '');
-- 2023-05-09 给所有管理员增加菜单 Tower 任务: 后端-菜单权限 ( https://tower.im/teams/636204/todos/67832 )
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2320 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2321 FROM sys_role WHERE role_type = 1;
-- 2023-05-09 给所有部门管理员增加菜单 Tower 任务: 后端-菜单权限 ( https://tower.im/teams/636204/todos/67832 )
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2320 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2321 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
-- 2023-05-09 给所有员工增加该菜单 Tower 任务: 后端-菜单权限 ( https://tower.im/teams/636204/todos/67832 )
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2320 FROM sys_role WHERE role_type = 2 AND role_key = 'employee';
-- 2023-05-10 we_group_tag 缺少标签创建时间，新增标签创建时间字段 Tower 任务: 后端-群标签 ( https://tower.im/teams/636204/todos/67742 )
ALTER TABLE `we_group_tag` ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '群标签创建时间' AFTER `name`;
-- 2023-05-10 we_group_tag_category缺少标签组创建时间，新增标签组创建时间字段 Tower 任务: 后端-群标签 ( https://tower.im/teams/636204/todos/67742 )
ALTER TABLE `we_group_tag_category` ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '群标签组创建时间' AFTER `name`;
-- 2023-05-15 lcy 删除原有自定角色的标签统计数据菜单权限 Tower 任务: 自定义角色数据统计权限异常 ( https://tower.im/teams/636204/todos/68180 )
DELETE FROM sys_role_menu WHERE menu_id IN (2312, 2313, 2314, 2315, 2320) AND role_id IN (SELECT role_id FROM sys_role WHERE role_type = 3 AND role_key = '');
-- 2023-05-15 lcv 为原有创建的自定角色添加标签统计数据菜单权限，不包括导出报表 Tower 任务: 自定义角色数据统计权限异常 ( https://tower.im/teams/636204/todos/68180 )
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2312 FROM sys_role WHERE role_type = 3 AND role_key = '';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2313 FROM sys_role WHERE role_type = 3 AND role_key = '';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2314 FROM sys_role WHERE role_type = 3 AND role_key = '';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2315 FROM sys_role WHERE role_type = 3 AND role_key = '';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2320 FROM sys_role WHERE role_type = 3 AND role_key = '';