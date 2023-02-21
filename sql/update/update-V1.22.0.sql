-- wx 2023-02-13 数据统计相关表 Tower 任务: 数据统计 ( https://tower.im/teams/636204/todos/61609 )
CREATE TABLE `we_user_customer_message_statistics` (
                                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                                       `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                                       `user_id` varchar(128) NOT NULL DEFAULT '' COMMENT '员工id 会话存档 ES中员工',
                                                       `external_userid` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id 会话存档 ES中与user_id对话的客户',
                                                       `user_send_message_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '员工发送消息数量 会话存档 ES 中 user_id对external_userid 发送的消息数',
                                                       `external_user_send_message_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '客户发送消息数量 会话存档 ES 中 external_userid对user_id发送的消息数',
                                                       `add_time` date NOT NULL DEFAULT '0000-00-00' COMMENT '添加客户时间 user_id与exteranl_userId成为联系人的时间 we_flower_customer_rel 表中查找',
                                                       `send_time` date NOT NULL DEFAULT '0000-00-00' COMMENT '发送消息时间 统计的时间，当天',
                                                       `first_reply_time_interval_alter_receive` int(6) NOT NULL DEFAULT '0' COMMENT '当天收到客户消息到首次回复客户时间间隔（单位分钟） ES中查询并计算',
                                                       `three_rounds_dialogue_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有三个轮次对话，1：有，0：无 ES中查询并统计',
                                                       `user_active_dialogue` tinyint(1) NOT NULL DEFAULT '0' COMMENT '对话是否由员工主动发起，1：是，0：否 ES中查询并统计',
                                                       PRIMARY KEY (`id`),
                                                       KEY `idx_corp_id_send_add_time` (`corp_id`,`send_time`,`add_time`) USING BTREE COMMENT '企业id和发送和添加时间索引',
                                                       KEY `idx_corp_id_user_id` (`corp_id`,`user_id`) USING BTREE COMMENT '员工id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工客户发送消息统计数据（每天统计一次，会话存档ES中统计）';
-- 员工行为表新增字段
ALTER TABLE `we_user_behavior_data`
    ADD COLUMN `new_contact_speak_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '当天新增客户中与员工对话过的人数(此数据为每日定时任务统计 会话存档ES中查找)' AFTER `new_contact_loss_cnt`,
    ADD COLUMN `replied_within_thirty_min_customer_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '当天员工首次给客户发消息，客户在30分钟内回复的客户数(此数据为每日定时任务统计 会话存档ES中查找)' AFTER `new_contact_speak_cnt`,
    ADD COLUMN `all_chat_cnt` int(11) NOT NULL DEFAULT 0 COMMENT '当天员工会话数-不区分是否为员工主动发起(此数据为每日定时任务统计 会话存档ES中查找)' AFTER `replied_within_thirty_min_customer_cnt`,
    ADD COLUMN `new_customer_loss_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '当天员工新客流失客户数 （we_flower表中查找 每日定时任务获取' AFTER `all_chat_cnt`,
    ADD COLUMN `contact_total_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '当天员工客户总数（we_flower表中查找 每日定时任务获取' AFTER `new_customer_loss_cnt`;

-- 定时任务
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, '数据统计定时任务', 'SYSTEM', 'DataStatisticsTask.getDataStatistics()', '0 0 6 * * ?', '2', '1', '0', 'admin', '2023-02-13 16:34:13', '', NULL, '');

-- 菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2312, '数据统计', 0, 9, 'dataStatistics', NULL, 1, 'M', '0', '0', NULL, 'chart', 'admin', '2023-02-14 16:02:38', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2313, '数据统计', 2312, 1, 'dataStatistics', NULL, 1, 'M', '0', '0', NULL, 'monitor', 'admin', '2023-02-14 16:05:14', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2314, '客户联系', 2313, 1, 'customerContact', 'dataStatistics/customerContact/index', 1, 'C', '0', '0', NULL, '#', 'admin', '2023-02-14 16:05:43', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2315, '员工服务', 2313, 2, 'employeeService', 'dataStatistics/employeeService/index', 1, 'C', '0', '0', NULL, '#', 'admin', '2023-02-15 09:57:17', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2316, '导出报表', 2314, 1, '', NULL, 1, 'F', '0', '0', 'statistic:customerContact:export', '#', 'admin', '2023-02-15 16:53:30', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2317, '导出报表', 2315, 1, '', NULL, 1, 'F', '0', '0', 'statistic:employeeService:export', '#', 'admin', '2023-02-15 16:54:01', 'admin', '2023-02-15 16:54:24', '');

-- 所有角色增加数据统计客户联系员工服务的菜单权限
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2312
FROM sys_role
WHERE role_type = 1 OR role_type = 2;

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2313
FROM sys_role
WHERE role_type = 1 OR role_type = 2;

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2314
FROM sys_role
WHERE role_type = 1 OR role_type = 2;

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2315
FROM sys_role
WHERE role_type = 1 OR role_type = 2;

-- 给以前的管理员、部门管理员增加导出权限

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2316
FROM sys_role
WHERE role_type = 1 OR (role_type = 2
    AND role_key = 'depart');

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2317
FROM sys_role
WHERE role_type = 1 OR (role_type = 2
    AND role_key = 'depart');

-- wx 2023-02-17 将首页定时任务设置在每天5点30 Tower 任务: 数据概览获取今日数据异常 ( https://tower.im/teams/636204/todos/62944 )
UPDATE sys_job
SET cron_expression = '0 30 5 * * ?'
WHERE job_id = 9;

-- wx 2023-02-17 调整导航栏顺序 Tower 任务: 调整导航栏菜单顺序 ( https://tower.im/teams/636204/todos/62956 )
UPDATE sys_menu
SET order_num = 10
WHERE menu_id = 2229;