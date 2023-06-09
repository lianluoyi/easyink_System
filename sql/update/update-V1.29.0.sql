-- 2023-06-07 lcy 新增批量打标签菜单和权限 Tower 任务: 权限/菜单 ( https://tower.im/teams/636204/todos/69422 )
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2322, '任务详情', 2001, 34, 'CustomerCenter/BatchTagTaskDetail', 'customerManage/autoLabel/BatchTagTaskDetail', 1, 'P', '1', '0', '', '#', 'admin', '2023-06-06 13:42:09', 'admin', '2023-06-07 09:28:58', '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2323, '批量打标签', 2285, 50, '', NULL, 1, 'F', '0', '0', 'wecom:batchtag:list', '#', 'admin', '2023-06-07 09:45:05', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2324, '新增打标任务', 2285, 51, '', NULL, 1, 'F', '0', '0', 'wecom:batchtag:add', '#', 'admin', '2023-06-07 09:49:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2325, '删除打标任务', 2285, 52, '', NULL, 1, 'F', '0', '0', 'wecom:batchtag:del', '#', 'admin', '2023-06-07 09:49:32', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2326, '导出打标任务报表', 2285, 53, '', NULL, 1, 'F', '0', '0', 'wecom:batchtag:export', '#', 'admin', '2023-06-07 09:49:51', '', NULL, '');
-- 2023-06-07 lcy 给所有管理员增加菜单和权限 Tower 任务: 权限/菜单 ( https://tower.im/teams/636204/todos/69422 )
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2323 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2324 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2325 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2326 FROM sys_role WHERE role_type = 1;
-- 2023-06-07 lcy 给所有部门管理员增加菜单和权限 Tower 任务: 权限/菜单 ( https://tower.im/teams/636204/todos/69422 )
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2323 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2324 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2325 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2326 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';


-- 2023-6-6 silver_chariot 批量打标签 Tower 任务: 批量打标签 ( https://tower.im/teams/636204/todos/69305 )

-- 2023-6-6 silver_chariot 批量打标签任务表
CREATE TABLE `we_batch_tag_task` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                     `corp_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
                                     `name` varchar(32)NOT NULL DEFAULT '' COMMENT '任务名称',
                                     `execute_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '执行状态（0 [false]执行中，1 [true]已执行)',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `create_by` varchar(255)  NOT NULL DEFAULT '' COMMENT '创建人',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `tag_name` varchar(512)  NOT NULL DEFAULT '' COMMENT '需要打上的标签（作冗余,同时因为后续可能存在标签被删除的情况，所以这里直接存打标签时的名称)',
                                     `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除(0 否， 1 被删除）',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_corp_id` (`corp_id`,`del_flag`,`execute_flag`) USING BTREE
) ENGINE=InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='批量打标签任务表';


--  2023-6-6 silver_chariot 批量打标签任务详情表

CREATE TABLE `we_batch_tag_task_detail` (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                            `task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '批量群发任务id',
                                            `import_external_userid` varchar(32)  NOT NULL DEFAULT '' COMMENT '导入的表格中的外部联系人id',
                                            `import_union_id` varchar(32)NOT NULL DEFAULT '' COMMENT '导入的表格中的unionId,外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。',
                                            `import_mobile` varchar(64) NOT NULL DEFAULT '' COMMENT '导入的手机号',
                                            `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '打标签状态（0 待执行， 1 成功 ， 2 失败）',
                                            `tag_user_id` varchar(512)  NOT NULL DEFAULT '' COMMENT '实际打标签的user_id',
                                            `tag_external_userid` varchar(64)  NOT NULL DEFAULT '' COMMENT '实际打标签的客户id',
                                            `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='批量打标签任务详情表';



--  2023-6-6 silver_chariot 批量打标签任务- 标签关联表
CREATE TABLE `we_batch_tag_task_rel` (
                                         `task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '任务id',
                                         `tag_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '此次任务需要打上的标签id',
                                         PRIMARY KEY (`task_id`,`tag_id`)
) ENGINE=InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='批量打标签任务-标签关联表';

-- 2023--6-8 silver_chariot 增加定时任务  Tower 任务: 执行批量打标签导致系统无法使用 ( https://tower.im/teams/636204/todos/69816 )
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('22', '同步回调变更客户定时任务', 'SYSTEM', 'syncCustomerChangeTask.execute()', '0/30 * * * * ? ', '2', '1', '0', 'admin', '2023-06-08 19:31:58', 'admin', '2023-06-08 19:32:36', '');
