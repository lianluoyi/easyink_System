-- 2023-08-22 lcy 创建获客链接-渠道表 Tower 任务: 新增、编辑、删除获客链接 ( https://tower.im/teams/636204/todos/73572 )
DROP TABLE IF EXISTS we_emple_code_channel;
CREATE TABLE `we_emple_code_channel`
(
    `id`            bigint(20) NOT NULL COMMENT '获客自定义渠道id',
    `emple_code_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '获客链接id',
    `name`          varchar(16)  NOT NULL DEFAULT '' COMMENT '自定义渠道名称（不超过16个字符）',
    `channel_url`   varchar(255) NOT NULL DEFAULT '' COMMENT '自定义渠道的url（主渠道url?customer_channel= "hk_" + {渠道id}）',
    `create_time`   datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人id',
    `update_time`   datetime     NOT NULL COMMENT '更新时间',
    `update_by`     varchar(64)  NOT NULL DEFAULT '' COMMENT '更新人id',
    `del_flag`      tinyint(1) NOT NULL COMMENT '删除状态 0：正常，1：删除',
    PRIMARY KEY (`id`),
    KEY `idx_emple_code_id_name` (`emple_code_id`,`name`) USING BTREE COMMENT '获客链接id-自定义渠道名称普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='获客链接-渠道表';
-- 2023-08-22 lcy 新增获客链接场景描述和获客链接id字段，修改表字段描述 Tower 任务: 新增、编辑、删除获客链接 ( https://tower.im/teams/636204/todos/73572 )
ALTER TABLE we_emple_code ADD description VARCHAR(64) NOT NULL DEFAULT '' COMMENT '获客链接场景描述，最大64个字符';
ALTER TABLE we_emple_code ADD link_id VARCHAR(50) NOT NULL DEFAULT '' COMMENT '获客链接id，由企微返回，用于编辑，删除获客链接';
ALTER TABLE we_emple_code MODIFY COLUMN `scenario` varchar(300) NOT NULL DEFAULT '' COMMENT '活动场景/获客链接名称';
ALTER TABLE we_emple_code MODIFY COLUMN `source` tinyint(4) NOT NULL DEFAULT '0' COMMENT '来源类型：0：活码创建，1：新客建群创建，3：获客链接创建';
ALTER TABLE we_emple_code MODIFY COLUMN `qr_code` varchar(100) NOT NULL DEFAULT '' COMMENT '二维码链接/获客链接';
-- 2023-08-23 lcy 新增获客链接告警配置表 Tower 任务: 告警设置、新增、编辑、删除、查询自定义渠道接口 ( https://tower.im/teams/636204/todos/73966 )
DROP TABLE IF EXISTS we_emple_code_warn_config;
CREATE TABLE we_emple_code_warn_config
(
    `corp_id`                  varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `link_unavailable_switch`  tinyint(1) NOT NULL COMMENT '链接不可用通知开关 True：开启，False：关闭',
    `link_unavailable_users`   text        NOT NULL COMMENT '链接不可用通知员工，多个用逗号隔开',
    `alarm_creater`            tinyint(1) NOT NULL COMMENT '链接不可用是否通知链接创建人，True：通知创建人，False：不通知创建人',
    `alarm_other_user`         tinyint(1) NOT NULL COMMENT '链接不可用是否通知其他员工，True：通知其他员工，False：不通知其他员工',
    `balance_low_switch`       tinyint(1) NOT NULL COMMENT '额度即将耗尽通知开关 True：开启，False：关闭',
    `balance_low_users`        text        NOT NULL COMMENT '额度即将耗尽通知员工，多个用逗号隔开',
    `balance_exhausted_switch` tinyint(1) NOT NULL COMMENT '额度已耗尽通知开关 True：开启，False：关闭',
    `balance_exhausted_users`  text        NOT NULL COMMENT '额度已耗尽通知员工，多个用逗号隔开',
    `type`                     tinyint(1) NOT NULL COMMENT '告警类型 True：每次都告警，False：每天只告警一次',
    `update_time`              datetime    NOT NULL COMMENT '变更时间',
    `update_by`                varchar(64) NOT NULL COMMENT '变更人id',
    PRIMARY KEY (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='获客链接告警配置表';
-- 2023-08-24 lcy 新增获客链接主页获客情况表 Tower 任务: 详情-数据总览，客户、渠道、日期维度统计，导出报表 ( https://tower.im/teams/636204/todos/73967 )
CREATE TABLE `we_emple_code_situation`
(
    `corp_id`          varchar(64) NOT NULL COMMENT '企业ID',
    `new_customer_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '今日新增客户数',
    `total`            int(11) NOT NULL DEFAULT '0' COMMENT '累计新增客户数（历史累计使用量），企微官方获取',
    `balance`          int(11) DEFAULT '0' COMMENT '剩余使用量，企微官方获取',
    PRIMARY KEY (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='获客助手-主页获客情况统计表';
-- 2023-08-25 lcy 分析表新增渠道id属性 Tower 任务: 详情-数据总览，客户、渠道、日期维度统计，导出报表 ( https://tower.im/teams/636204/todos/73967 )
ALTER TABLE we_emple_code_analyse ADD `channel_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '获客链接渠道id';
-- 2023-08-27 lcy 新增获客助手菜单和权限 Tower 任务: 回调处理，应用通知、角色权限 ( https://tower.im/teams/636204/todos/73968 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2330, '获客助手', 2052, 50, 'customerAssistant', 'drainageCode/customerAssistant', 1, 'C', '0', '0', NULL, '#', 'admin', '2023-08-14 10:41:34', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2331, '获客情况', 2330, 1, '', NULL, 1, 'F', '0', '0', 'customer:assistant:situation', '#', 'admin', '2023-08-27 21:54:26', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2332, '新增链接', 2330, 2, '', NULL, 1, 'F', '0', '0', 'customer:assistant:add', '#', 'admin', '2023-08-27 21:55:58', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2333, '编辑链接', 2330, 3, '', NULL, 1, 'F', '0', '0', 'customer:assistant:edit', '#', 'admin', '2023-08-27 21:56:27', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2334, '删除链接', 2330, 4, '', NULL, 1, 'F', '0', '0', 'customer:assistant:delete', '#', 'admin', '2023-08-27 21:57:02', 'admin', '2023-08-27 21:57:30', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2335, '导出报表', 2330, 5, '', NULL, 1, 'F', '0', '0', 'customer:assistant:export', '#', 'admin', '2023-08-27 21:57:58', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2336, '获客链接详情', 2052, 13, 'customerAssistantDetail', 'drainageCode/customerAssistant/detail', 1, 'P', '1', '0', '', '#', 'admin', '2023-08-17 15:42:45', 'admin', '2023-08-27 21:55:38', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2337, '新增获客链接', 2052, 14, 'customerAssistantAdd', 'drainageCode/customerAssistant/add', 1, 'P', '1', '0', '', '#', 'admin', '2023-08-18 14:58:11', 'admin', '2023-08-27 21:55:33', '');
-- 2023-08-30 lcy 前端新增路由菜单 Tower 任务: 回调处理，应用通知、角色权限 ( https://tower.im/teams/636204/todos/73968 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2338, '编辑获客链接', 2052, 15, 'customerAssistantEdit', 'drainageCode/customerAssistant/add', 1, 'P', '1', '0', '', '#', 'admin', '2023-08-30 09:19:32', 'admin', '2023-08-30 09:20:07', '');
-- 2023-08-27 lcy 给所有管理员增加菜单和权限 Tower 任务: 回调处理，应用通知、角色权限 ( https://tower.im/teams/636204/todos/73968 )
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2330 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2331 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2332 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2333 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2334 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2335 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2336 FROM sys_role WHERE role_type = 1;
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2337 FROM sys_role WHERE role_type = 1;
-- 2023-08-30 lcy 前端新增路由 Tower 任务: 回调处理，应用通知、角色权限 ( https://tower.im/teams/636204/todos/73968 )
INSERT INTO sys_role_menu(role_id, menu_id) SELECT role_id, 2338 FROM sys_role WHERE role_type = 1;
-- 2023-08-28 lcy 添加更新获客链接消耗情况定时任务 Tower 任务: 回调处理，应用通知、角色权限 ( https://tower.im/teams/636204/todos/73968 )
INSERT INTO `sys_job`(`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`,
                      `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('25', '更新获客链接消耗情况定时任务', 'SYSTEM', 'customerAssistantSituationTask.getCustomerAssistantSituationData()', '0 0 0 * * ?', '2', '1',
        '0', 'admin', '2023-08-28 11:09:36', '', '2023-08-28 11:09:48', '');
-- 2023-08-28 lcy 修改we_emple_code_analyse表唯一索引 Tower 任务: 回调处理，应用通知、角色权限 ( https://tower.im/teams/636204/todos/73968 )
ALTER TABLE we_emple_code_analyse
DROP INDEX `uniq_corpid_codeid_userid_extid_type_time`,
ADD UNIQUE KEY `uniq_corpid_codeid_channelid_userid_extid_type_time` (`corp_id`,`emple_code_id`,`channel_id`,`user_id`,`external_userid`,`type`,`time`) USING BTREE COMMENT '唯一索引';
-- 2023-08-30 lcy 修改联系客户统计数据拉取、群聊数据统计数据拉取、首页数据统计、数据统计定时任务执行时间 Tower 任务: 新增客户数统计异常 ( https://tower.im/teams/636204/todos/73829 )
UPDATE sys_job SET cron_expression = '0 30 6 * * ?' WHERE job_id = 7;
UPDATE sys_job SET cron_expression = '0 0 7 * * ?' WHERE job_id = 8;
UPDATE sys_job SET cron_expression = '0 30 7 * * ?' WHERE job_id = 9;
UPDATE sys_job SET cron_expression = '0 0 8 * * ?' WHERE job_id = 21;
-- 2023-09-01 lcy 获客链接告警添加获客额度即将过期通知开关、通知员工 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/74479 )
ALTER TABLE we_emple_code_warn_config ADD COLUMN `quota_expire_soon_users` text NOT NULL COMMENT '获客额度即将过期通知员工，多个用逗号隔开' AFTER `balance_exhausted_users`;
ALTER TABLE we_emple_code_warn_config ADD COLUMN `quota_expire_soon_switch` tinyint(1) NOT NULL  COMMENT '获客额度即将过期通知开关' AFTER `balance_exhausted_users`;
-- 2023-09-07 lcy 修改活码统计菜单名称为渠道统计，修改组件、权限、路径等名称 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/74736 )
UPDATE sys_menu SET `menu_name` = '渠道统计', `path` = 'channelStatistics', component = 'drainageAnalysis/channelStatistics/index' WHERE `menu_id` = 2328;
UPDATE sys_menu SET `perms` = 'stastistic:channelStatistics:export' WHERE `menu_id` = 2329;
