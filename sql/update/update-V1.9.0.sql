-- yiming 2021-12-17 企业微信标签表增加非主键自增seq_id字段  Tower 任务: 标签排序优化 ( https://tower.im/teams/636204/todos/46325 )
alter table we_tag
    add column `seq_id` bigint(20) NOT NULL;
alter table we_tag
    ADD KEY idx_seq_id (seq_id);
alter table we_tag
    modify seq_id BIGINT(20) NOT NULL auto_increment COMMENT '非主键自增序列号';

-- silver_chariot 2021-12-21 群日历 菜单权限修改 Tower 任务: 增加群日历菜单 ( https://tower.im/teams/636204/todos/47730 )
INSERT INTO `sys_menu`
VALUES ('2264', '群日历', '2250', '5', 'groupCalendarSOP', 'retainedConversion/SOP/groupCalendarSOP', '1', 'C', '0', '0',
        NULL, '#', 'admin', '2021-12-21 10:50:37', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2265', '新增日历', '2264', '2', '', NULL, '1', 'F', '0', '0', 'wecom:groupCalendar:add', '#', 'admin',
        '2021-12-21 15:27:38', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2266', '启用/关闭日历', '2264', '6', '', NULL, '1', 'F', '0', '0', 'wecom:groupCalendar:switch', '#', 'admin',
        '2021-12-21 15:28:50', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2267', '编辑日历', '2264', '10', '', NULL, '1', 'F', '0', '0', 'wecom:groupCalendar:edit', '#', 'admin',
        '2021-12-21 15:29:21', '', NULL, '');
-- silver_chariot 2021-12-21 给以前的管理员/部门管理员增加群日历权限 Tower 任务: 增加群日历菜单 ( https://tower.im/teams/636204/todos/47730 )
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2264
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2264
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2265
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2265
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2266
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2266
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2268
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2268
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
-- silver_chariot 2021-12-22 增加删除日历 菜单权限  Tower 任务: 增加群日历菜单 ( https://tower.im/teams/636204/todos/47730 )
INSERT INTO `sys_menu`
VALUES ('2268', '删除日历', '2264', '8', '', NULL, '1', 'F', '0', '0', 'wecom:groupCalendar:del', '#', 'admin',
        '2021-12-22 09:19:24', '', NULL, '');
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2267
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2267
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';

-- yiming Tower 任务: 标记删除项目中无用表和对应接口层代码 ( https://tower.im/teams/636204/todos/47762 )
DROP TABLE IF EXISTS `we_group_sop`;
DROP TABLE IF EXISTS `we_group_sop_chat`;
DROP TABLE IF EXISTS `we_group_sop_material`;
DROP TABLE IF EXISTS `we_group_sop_pic`;

DROP TABLE IF EXISTS `we_chat_item`;
DROP TABLE IF EXISTS `we_app`;

-- yiming Tower 任务: 标记删除项目中无用表和对应接口层代码 ( https://tower.im/teams/636204/todos/47762 )
DELETE
FROM
	`sys_job`
WHERE
	job_id = 5
	AND job_name = '客户列表';
DELETE
FROM
	qrtz_cron_triggers
WHERE
	trigger_name = 'TASK_CLASS_NAME5';
DELETE
FROM
	qrtz_triggers
WHERE
	trigger_name = 'TASK_CLASS_NAME5';

DROP TABLE IF EXISTS `we_allocate_customer_v2`;
DROP TABLE IF EXISTS `we_allocate_customer`;
DROP TABLE IF EXISTS `we_allocate_group`;
DROP TABLE IF EXISTS `we_allocate_group_v2`;
DROP TABLE IF EXISTS `sys_user_post`;
DROP TABLE IF EXISTS `sys_post`;
-- yiming Tower 任务: 标记删除项目中无用表和对应接口层代码 ( https://tower.im/teams/636204/todos/47762 )
DROP TABLE IF EXISTS `we_chat_collection`;
DROP TABLE IF EXISTS `sys_notice`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `we_config`;
DROP TABLE IF EXISTS `we_keyword_group`;
DROP TABLE IF EXISTS `we_keyword_group_kw`;


-- yiming Tower 任务: 链接消息报错 ( https://tower.im/teams/636204/todos/48070 )
ALTER TABLE `we_words_detail`
MODIFY COLUMN `url` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链接地址' AFTER `title`;

-- yiming Tower 任务: 企业微信域名主体限制 ( https://tower.im/teams/636204/todos/47397 )
ALTER TABLE `we_corp_account`
MODIFY COLUMN `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用 2已授权未启用)' AFTER `agent_secret`;









