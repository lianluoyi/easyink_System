-- 2023-07-06 应用管理屏蔽青鸾智能工单系统，将启用状态设为关闭 Tower 任务: 应用管理屏蔽青鸾工单系统 ( https://tower.im/teams/636204/todos/70495 )
UPDATE we_application_center SET `enable` = FALSE WHERE name = '青鸾智能工单系统';
-- 2023-07-07 lichaoyu 增加定时任务 Tower 任务: 活码统计-定时任务统计数据处理 ( https://tower.im/teams/636204/todos/71488 )
INSERT INTO `sys_job`(`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`,
                      `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('23', '每小时活码统计数据定时任务', 'SYSTEM', 'EmpleStatisticTask.getEmpleStatisticData()', '0 3 * * * ?', '2',
        '1', '0', 'admin', '2023-07-07 15:22:28', 'admin', '2023-07-07 15:23:05', '');
INSERT INTO `sys_job`(`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`,
                      `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('24', '每日活码统计数据定时任务(传参日期格式:YYYY-MM-DD)', 'SYSTEM', 'EmpleStatisticTask.getEmpleStatisticDateData('''')', '0 0 3 * * ?', '2', '1',
        '0', 'admin', '2023-07-07 19:50:41', '', '2023-07-07 19:50:46', '');
-- 2023-07-07 新增活码统计表 Tower 任务: 活码统计-定时任务统计数据处理 ( https://tower.im/teams/636204/todos/71488 )
CREATE TABLE `we_emple_code_statistic`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`                 varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `emple_code_id`           bigint(20) NOT NULL DEFAULT '0' COMMENT '活码id',
    `user_id`                 varchar(32) NOT NULL DEFAULT '' COMMENT '员工ID',
    `accumulate_customer_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '累计客户数',
    `retain_customer_cnt`     int(11) NOT NULL DEFAULT '0' COMMENT '留存客户数',
    `new_customer_cnt`        int(11) NOT NULL DEFAULT '0' COMMENT '新增客户数',
    `loss_customer_cnt`       int(11) NOT NULL DEFAULT '0' COMMENT '流失客户数',
    `time`                    date        NOT NULL COMMENT '日期',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_emple_user_time` (`emple_code_id`,`user_id`,`time`) USING BTREE,
    KEY                       `idx_corp_user_id` (`corp_id`,`user_id`) USING BTREE,
    KEY                       `idx_emple_user_id` (`emple_code_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='活码数据统计表';
-- 2023-07-10 lcy 新增活码统计权限/菜单 Tower 任务: 角色管理 ( https://tower.im/teams/636204/todos/71553 )
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2327, '引流分析', 2312, 2, 'drainageAnalysis', NULL, 1, 'M', '0', '0', '', 'drainageAnalysis', 'admin', '2023-07-04 13:54:14', 'admin', '2023-07-05 17:42:47', '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2328, '活码统计', 2327, 1, 'codeStatistics', 'drainageAnalysis/codeStatistics/index', 1, 'C', '0', '0', NULL, '#', 'admin', '2023-07-04 13:58:05', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2329, '导出报表', 2328, 1, '', NULL, 1, 'F', '0', '0', 'stastistic:codeStatistics:export', '#', 'admin', '2023-07-10 15:24:12', 'admin', '2023-07-10 18:10:25', '');
-- 2023-07-10 lcy 给所有管理员增加菜单和权限 Tower 任务: 角色管理 ( https://tower.im/teams/636204/todos/71553 )
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2327 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2328 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2329 FROM sys_role WHERE role_type = 1;
-- 2023-07-10 lcy 给所有部门管理员增加菜单和权限 Tower 任务: 角色管理 ( https://tower.im/teams/636204/todos/71553 )
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2327 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2328 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2329 FROM sys_role WHERE role_type = 2 AND role_key = 'depart';
-- 2023-07-11 lcy 删除原有自定角色的活码统计数据菜单权限 Tower 任务: 自定义角色默认没有活码统计权限 ( https://tower.im/teams/636204/todos/71658 )
DELETE FROM sys_role_menu WHERE menu_id IN (2327, 2328) AND role_id IN (SELECT role_id FROM sys_role WHERE role_type = 3 AND role_key = '');
-- 2023-07-11 lcy 给自定义角色添加菜单权限 Tower 任务: 自定义角色默认没有活码统计权限 ( https://tower.im/teams/636204/todos/71658 )
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2327 FROM sys_role WHERE role_type = 3 AND role_key = '';
INSERT INTO sys_role_menu (role_id, menu_id) SELECT role_id, 2328 FROM sys_role WHERE role_type = 3 AND role_key = '';
-- 2023-07-11 lichaoyu 添加二次数据统计定时任务 Tower 任务: 每天定时2次统计数据 ( https://tower.im/teams/636204/todos/69513 )
INSERT INTO `sys_job`
VALUES ('25', '联系客户统计数据拉取', 'SYSTEM', 'UserBehaviorDataTak.getUserBehaviorData()', '0 0 12 * * ?', '1', '1', '0', 'admin',
        '2023-07-11 10:15:59', 'admin', '2023-07-11 10:16:37', '');
INSERT INTO `sys_job`
VALUES ('26', '群聊数据统计数据拉取', 'SYSTEM', 'GroupChatStatisticTask.getGroupChatData()', '0 30 12 * * ? ', '1', '1', '0', 'admin',
        '2023-07-11 10:16:44', '', '2023-07-11 10:16:16', '');
INSERT INTO `sys_job`
VALUES ('27', '数据统计定时任务', 'SYSTEM', 'DataStatisticsTask.getDataStatistics()', '0 30 13 * * ?', '2', '1', '0', 'admin',
        '2023-07-11 10:17:13', '', NULL, '');
-- 2023-07-11 lichaoyu 更新原来的定时任务执行时间 Tower 任务: 每天定时2次统计数据 ( https://tower.im/teams/636204/todos/69513 )
UPDATE sys_job SET cron_expression = '0 30 5 * * ?' WHERE job_id = 7;
UPDATE sys_job SET cron_expression = '0 0 6 * * ? ' WHERE job_id = 8;
UPDATE sys_job SET cron_expression = '0 30 6 * * ?' WHERE job_id = 9;
UPDATE sys_job SET cron_expression = '0 30 7 * * ?' WHERE job_id = 21;
-- 2023-07-11 lcy 修改活码详情表注释 Tower 任务: 活码详情表注释错误 ( https://tower.im/teams/636204/todos/71214 )
ALTER TABLE we_emple_code_analyse MODIFY COLUMN `time` date NOT NULL COMMENT 'type为1时是添加时间，type为0时是流失时间';
-- 2023-07-12 lcy 修改user_id字段长度 Tower 任务: 活码统计新客留存率统计异常 ( https://tower.im/teams/636204/todos/71707 )
ALTER TABLE `we_emple_code_statistic` MODIFY COLUMN `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工ID';
