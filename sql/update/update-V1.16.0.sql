-- 2022-07-04 wx 兑换码活动表 Tower 任务: 表结构设计 ( https://tower.im/teams/636204/todos/53806 )

CREATE TABLE `we_redeem_code_activity`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '兑换码活动主键',
    `corp_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `name`            varchar(32) NOT NULL DEFAULT '' COMMENT '活动名称',
    `start_time`      date        NOT NULL DEFAULT '0000-00-00' COMMENT '活动开始时间',
    `end_time`        date        NOT NULL DEFAULT '0000-00-00' COMMENT '活动结束时间',
    `create_by`       varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `enable_limited`  tinyint(1) NOT NULL DEFAULT '1' COMMENT '客户参与限制，0：可以参与多次，1：只可参与一次',
    `enable_alarm`    tinyint(1) NOT NULL DEFAULT '0' COMMENT '库存告警开关，0：不开启，1：开启',
    `alarm_threshold` int(10) NOT NULL DEFAULT '0' COMMENT '库存告警阈值，告警开启时，库存低于阈值通知员工',
    `del_flag`        tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志，0：未删除，1：已删除',
    PRIMARY KEY (`id`),
    KEY               `index_corpid` (`corp_id`) USING BTREE,
    KEY               `create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换码活动表';

-- 2022-07-04 wx 兑换码表 Tower 任务: 表结构设计 ( https://tower.im/teams/636204/todos/53806 )
CREATE TABLE `we_redeem_code`
(
    `code`            varchar(20) NOT NULL COMMENT '兑换码',
    `activity_id`     bigint(20) NOT NULL COMMENT '兑换码活动id',
    `status`          tinyint(1) NOT NULL DEFAULT '0' COMMENT '领取状态，0：未领取，1：已领取',
    `effective_time`  date        NOT NULL DEFAULT '0000-00-00' COMMENT '有效期，在该天24点之前可以发送给客户',
    `redeem_time`     datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '兑换码发送给客户的时间',
    `receive_user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '领取人id',
    PRIMARY KEY (`activity_id`, `code`),
    KEY               `status` (`status`) USING BTREE,
    KEY               `redeem_time` (`redeem_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换码库存表';
-- 2022-07-04 wx 告警员工表 Tower 任务: 表结构设计 ( https://tower.im/teams/636204/todos/53806 )
CREATE TABLE `we_redeem_code_alarm_employee_rel`
(
    `activity_id` bigint(20) NOT NULL COMMENT '活动id',
    `target_id`   varchar(64) NOT NULL COMMENT '员工id,部门id',
    `type`        tinyint(1) NOT NULL DEFAULT '2' COMMENT 'type, 1：存部门，2：存员工',
    PRIMARY KEY (`activity_id`, `target_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换码活动，告警员工表';

-- 2020-07-09 wx 员工活码表 添加兑换码活动相关字段 Tower 任务: 表结构设计 ( https://tower.im/teams/636204/todos/53806 )

ALTER TABLE we_emple_code
    ADD welcome_msg_type tinyint(1) NOT NULL DEFAULT '0' COMMENT '欢迎语类型，0：普通欢迎语，1：活动欢迎语';

ALTER TABLE we_emple_code
    ADD `code_activity_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '兑换码活动id';

ALTER TABLE we_emple_code
    ADD `code_success_material_sort` varchar(255) NOT NULL DEFAULT '' COMMENT '有可使用兑换码，发送该附件';

ALTER TABLE we_emple_code
    ADD `code_success_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '有可使用兑换码，发送该欢迎语';

ALTER TABLE we_emple_code
    ADD `code_fail_material_sort` varchar(255) NOT NULL DEFAULT '' COMMENT '没有可用的兑换码，或者兑换码已被删除，发送该附件';

ALTER TABLE we_emple_code
    ADD `code_fail_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '没有可用的兑换码，或者兑换活动已被删除，发送该欢迎语';

ALTER TABLE we_emple_code
    ADD `code_repeat_material_sort` varchar(255) NOT NULL DEFAULT '' COMMENT '客户再次触发，若活动开启参与限制，发送该附件';

ALTER TABLE we_emple_code
    ADD `code_repeat_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '客户再次触发，若活动开启参与限制，发送该欢迎语';



-- 2022-7-11  silver_chariot 兑换码活动 权限菜单 相关SQL Tower 任务: 增加兑换券自动发送营销功能 ( https://tower.im/teams/636204/todos/53063 )
-- 插入菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2292', '营销活动', '2188', '3', 'conversionCode', NULL, '1', 'M', '0', '0', '', 'conversion-code', 'admin',
        '2022-07-08 09:15:22', 'admin', '2022-07-08 09:15:32', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2293', '兑换码', '2292', '10', 'list', 'marketingActivities/conversionCode/list', '1', 'C', '0', '0', '', '#',
        'admin', '2022-07-08 09:17:54', 'admin', '2022-07-08 09:54:33', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2294', '新建兑换活动', '2292', '11', 'conversionCodeAdd', 'marketingActivities/conversionCode/add', '1', 'P', '1',
        '0', '', '#', 'admin', '2022-07-08 09:19:54', 'admin', '2022-07-08 09:52:25', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2295', '兑换码详情', '2292', '1', 'conversionCodeDetail', 'marketingActivities/conversionCode/detail', '1', 'P',
        '1', '0', '', '#', 'admin', '2022-07-08 09:21:59', 'admin', '2022-07-08 09:52:17', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2296', '新增兑换活动', '2293', '5', '', NULL, '1', 'F', '0', '0', 'redeeomCode:activity:add', '#', 'admin',
        '2022-07-11 09:52:22', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2297', '删除兑换活动', '2293', '10', '', NULL, '1', 'F', '0', '0', 'redeeomCode:activity:del', '#', 'admin',
        '2022-07-11 09:53:02', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2298', '编辑兑换活动', '2293', '15', '', NULL, '1', 'F', '0', '0', 'redeeomCode:activity:edit', '#', 'admin',
        '2022-07-11 09:53:33', '', NULL, '');

-- 给以前的管理员增加权限
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2292
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2293
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2294
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2295
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2296
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2297
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2298
FROM sys_role
WHERE role_type = 1;
-- 给以前的部门管理员增加权限
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2292
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2293
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2294
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2295
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2296
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2297
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
    (role_id, menu_id)
SELECT role_id,
       2298
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';

