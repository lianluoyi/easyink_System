-- 2022-02-28 silver_chariot 菜单层级调整  Tower 任务: 菜单栏样式优化 ( https://tower.im/teams/636204/todos/49586 )
-- 把流失提醒移动到客户继承
update sys_menu
set parent_id = 2179
where menu_id = 2082
  and menu_name = '流失提醒';

-- 增加客户管理,应用管理,企业风控,系统菜单,系统监控的二级目录
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2280', '客户管理', '2001', '10', 'customerCenter', NULL, '1', 'M', '0', '0', '', 'customer-manage', 'admin',
        '2022-02-28 10:32:21', 'admin', '2022-02-28 11:16:42', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2281', '应用管理', '2229', '10', 'appManage', NULL, '1', 'M', '0', '0', '', 'application-manage', 'admin',
        '2022-02-28 10:41:37', 'admin', '2022-02-28 11:19:35', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2282', '企业风控', '2079', '10', 'corpSecurity', NULL, '1', 'M', '0', '0', '', 'enterprise-risk-control', 'admin',
        '2022-02-28 10:46:49', 'admin', '2022-02-28 11:21:05', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2283', '系统设置', '1', '10', 'sysSetting', NULL, '1', 'M', '0', '0', '', 'system-hollow', 'admin',
        '2022-02-28 10:52:43', 'admin', '2022-02-28 11:24:52', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2284', '系统监控', '2', '5', 'sysMonitor', NULL, '1', 'M', '0', '0', '', 'system-monitor', 'admin',
        '2022-02-28 10:56:04', 'admin', '2022-02-28 11:27:45', '');

-- 把之前的客户功能都移到客户管理下面
update sys_menu
set parent_id = 2280
where menu_id in (2002, 2003, 2004, 2239);

-- 把之前的应用都移到应用管理二级目录下面
update sys_menu
set parent_id = 2281
where menu_id in (2230, 2232);

-- 把之前的企业风控菜单移到新的二级目录
update sys_menu
set parent_id = 2282
where menu_id in (2080, 2081);

-- 把之前的系统菜单移到新的二级目录
update sys_menu
set parent_id = 2283
where menu_id in (2201, 101, 2010, 102);

-- 把之前的系统监控菜单移到新的目录下
update sys_menu
set parent_id = 2284
where menu_id in (109, 110, 111, 112);


-- 图标修改
update sys_menu
set icon = 'huoma-manage'
where menu_id = 2052
  and menu_name = '引流获客';
update sys_menu
set icon = 'content-manage'
where menu_id = 2062
  and menu_name = '内容管理';
update sys_menu
set icon = 'guide'
where menu_id = 2083
  and menu_name = '群发管理';
update sys_menu
set icon = 'friend-circle'
where menu_id = 2272
  and menu_name = '朋友圈';
update sys_menu
set icon = 'transform-retained'
where menu_id = 2250
  and menu_name = '留存转化';

update sys_menu
set icon = 'customer-inheritance'
where menu_id = 2179
  and menu_name = '客户继承';

update sys_menu
set icon = 'log-analyse'
where menu_id = 108
  and menu_name = '日志管理';

-- 给以前的角色增加二级菜单
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2280
FROM sys_role;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2281
FROM sys_role;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2282
FROM sys_role;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2283
FROM sys_role;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2284
FROM sys_role;

-- silver_chariot 2022-3-2 修改页面路由 Tower 任务: 部分页面路由未配置 ( https://tower.im/teams/636204/todos/49914 )
-- 客户群聊详情和客户详情 页面 路由修改
update sys_menu
set path = 'customerCenter/customerDetail'
where menu_id = 2006
  and menu_name = '客户详情';

update sys_menu
set path = 'customerCenter/groupDetail'
where menu_id = 2007
  and menu_name = '客户群聊详情';
-- 应用中心的页面路由修改
update sys_menu
set path = 'appManage/appConfig'
where menu_id = 2234
  and menu_name = '应用设置页面';

update sys_menu
set path = 'appManage/appDetails'
where menu_id = 2235
  and menu_name = '应用详情页面';

update sys_menu
set path = 'sysMonitor/job/log'
where menu_id = 2224
  and menu_name = '定时任务日志查看';


-- tigger Tower 任务: 自动标签 ( https://tower.im/teams/636204/todos/49537 ) 标签规则设置
DROP TABLE IF EXISTS `we_auto_tag_customer_rule_effect_time`;
CREATE TABLE `we_auto_tag_customer_rule_effect_time`
(
    `rule_id`           bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `effect_begin_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '生效开始时间',
    `effect_end_time`   datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '生效结束时间',
    UNIQUE KEY `idx_rule_id` (`rule_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新客规则生效时间表';

DROP TABLE IF EXISTS `we_auto_tag_customer_scene`;
CREATE TABLE `we_auto_tag_customer_scene`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_id`         bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `loop_point`      tinyint(2) NOT NULL DEFAULT '0' COMMENT '指定循环节点 周: 1-7 月: 1-月末',
    `loop_begin_time` time        NOT NULL DEFAULT '00:00:00' COMMENT '循环指定开始时间',
    `loop_end_time`   time        NOT NULL DEFAULT '00:00:00' COMMENT '循环指定结束时间',
    `scene_type`      tinyint(2) NOT NULL DEFAULT '0' COMMENT '场景类型 1:天 2:周 3:月',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新客标签场景表';

DROP TABLE IF EXISTS `we_auto_tag_customer_scene_tag_rel`;
CREATE TABLE `we_auto_tag_customer_scene_tag_rel`
(
    `rule_id`           bigint(20) NOT NULL COMMENT '所属规则id',
    `customer_scene_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户场景id',
    `tag_id`            varchar(64) NOT NULL DEFAULT '' COMMENT '标签id',
    PRIMARY KEY (`rule_id`, `customer_scene_id`, `tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新客标签场景与标签关系表';

DROP TABLE IF EXISTS `we_auto_tag_group_scene`;
CREATE TABLE `we_auto_tag_group_scene`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群标签场景表';

DROP TABLE IF EXISTS `we_auto_tag_group_scene_group_rel`;
CREATE TABLE `we_auto_tag_group_scene_group_rel`
(
    `rule_id`        bigint(20) NOT NULL COMMENT '所属规则id',
    `group_scene_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键id',
    `group_id`       varchar(32) NOT NULL DEFAULT '' COMMENT '群id',
    PRIMARY KEY (`rule_id`, `group_scene_id`, `group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群标签场景与群关系表';
DROP TABLE IF EXISTS `we_auto_tag_group_scene_tag_rel`;
CREATE TABLE `we_auto_tag_group_scene_tag_rel`
(
    `rule_id`        bigint(20) NOT NULL COMMENT '所属规则id',
    `group_scene_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '群场景id',
    `tag_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '标签id',
    PRIMARY KEY (`rule_id`, `group_scene_id`, `tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群标签场景与标签关系表';
DROP TABLE IF EXISTS `we_auto_tag_keyword`;
CREATE TABLE `we_auto_tag_keyword`
(
    `rule_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `match_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '匹配规则 1:模糊匹配 2:精确匹配',
    `keyword`    varchar(32) NOT NULL DEFAULT '' COMMENT '关键词',
    PRIMARY KEY (`rule_id`, `match_type`, `keyword`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关键词规则表';
DROP TABLE IF EXISTS `we_auto_tag_keyword_tag_rel`;
CREATE TABLE `we_auto_tag_keyword_tag_rel`
(
    `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `tag_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '规则名称',
    PRIMARY KEY (`rule_id`, `tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关键词与标签关系表';
DROP TABLE IF EXISTS `we_auto_tag_rule`;
CREATE TABLE `we_auto_tag_rule`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_name`   varchar(32) NOT NULL DEFAULT '' COMMENT '规则名称',
    `label_type`  tinyint(2) NOT NULL DEFAULT '0' COMMENT '规则类型 1:关键词 2:入群 3:新客',
    `status`      tinyint(2) NOT NULL DEFAULT '1' COMMENT '启用禁用状态 0:禁用1:启用',
    `create_time` datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='标签规则表';

-- tigger 自动标签菜单权限 Tower 任务: 自动标签 ( https://tower.im/teams/636204/todos/49537 )
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2285, '自动标签', 2280, 25, 'customerManage/autoLabel', 'customerManage/autoLabel', 1, 'C', '0', '0', 'customerManage:autoLabel:list', '#', 'admin', '2022-03-04 13:47:45', 'admin', '2022-03-04 15:53:10', '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2286, '新增规则', 2285, 10, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:add', '#', 'admin', '2022-03-04 13:53:47', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2287, '启用禁用', 2285, 20, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:enable', '#', 'admin', '2022-03-04 13:56:42', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2288, '删除规则', 2285, 30, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:del', '#', 'admin', '2022-03-04 13:57:12', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2289, '编辑规则', 2285, 40, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:edit', '#', 'admin', '2022-03-04 13:57:36', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2290, '新增规则页面', 2001, 1, 'customerCenter/addRule', 'customerManage/autoLabel/addRule', 1, 'P', '1', '0', '', '#', 'admin', '2022-03-04 16:22:16', 'admin', '2022-03-04 17:20:27', '');

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2285
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2286
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2287
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2288
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2289
FROM sys_role
WHERE role_type = 1;


INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2285
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2286
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2287
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2288
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2289
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2290
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2290
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
UPDATE `sys_menu` SET `menu_name` = '自动标签', `parent_id` = 2280, `order_num` = 25, `path` = 'autoLabel', `component` = 'customerManage/autoLabel', `is_frame` = 1, `menu_type` = 'C', `visible` = '0', `status` = '0', `perms` = 'customerManage:autoLabel:list', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 13:47:45', `update_by` = 'admin', `update_time` = '2022-03-04 17:37:12', `remark` = '' WHERE `menu_id` = 2285;
UPDATE `sys_menu` SET `menu_name` = '新增规则', `parent_id` = 2285, `order_num` = 10, `path` = '', `component` = NULL, `is_frame` = 1, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'wecom:autotag:add', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 13:53:47', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2286;
UPDATE `sys_menu` SET `menu_name` = '启用禁用', `parent_id` = 2285, `order_num` = 20, `path` = '', `component` = NULL, `is_frame` = 1, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'wecom:autotag:enable', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 13:56:42', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2287;
UPDATE `sys_menu` SET `menu_name` = '删除规则', `parent_id` = 2285, `order_num` = 30, `path` = '', `component` = NULL, `is_frame` = 1, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'wecom:autotag:del', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 13:57:12', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2288;
UPDATE `sys_menu` SET `menu_name` = '编辑规则', `parent_id` = 2285, `order_num` = 40, `path` = '', `component` = NULL, `is_frame` = 1, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'wecom:autotag:edit', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 13:57:36', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2289;
UPDATE `sys_menu` SET `menu_name` = '新增规则', `parent_id` = 2280, `order_num` = 1, `path` = '', `component` = NULL, `is_frame` = 1, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = NULL, `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 17:51:50', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2290;
UPDATE `sys_menu` SET `menu_name` = '规则详情', `parent_id` = 2280, `order_num` = 33, `path` = 'labelDetail', `component` = 'customerManage/autoLabel/labelDetail', `is_frame` = 1, `menu_type` = 'P', `visible` = '1', `status` = '0', `perms` = NULL, `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 18:10:05', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2291;
UPDATE `sys_menu` SET `menu_name` = '新增规则页面', `parent_id` = 2280, `order_num` = 22, `path` = 'addRule', `component` = 'customerManage/autoLabel/addRule', `is_frame` = 1, `menu_type` = 'P', `visible` = '1', `status` = '0', `perms` = '', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 16:22:16', `update_by` = 'admin', `update_time` = '2022-03-04 18:08:09', `remark` = '' WHERE `menu_id` = 2292;
UPDATE `sys_menu` SET `menu_name` = '启用/关闭规则', `parent_id` = 2285, `order_num` = 20, `path` = '', `component` = NULL, `is_frame` = 1, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'wecom:autotag:enable', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 13:56:42', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2287;
UPDATE `sys_menu` SET `menu_name` = '规则详情', `parent_id` = 2280, `order_num` = 33, `path` = 'labelDetail', `component` = 'customerManage/autoLabel/labelDetail', `is_frame` = 1, `menu_type` = 'P', `visible` = '1', `status` = '0', `perms` = NULL, `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 18:10:05', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 2290;
UPDATE `sys_menu` SET `menu_name` = '新增规则页面', `parent_id` = 2280, `order_num` = 22, `path` = 'addRule', `component` = 'customerManage/autoLabel/addRule', `is_frame` = 1, `menu_type` = 'P', `visible` = '1', `status` = '0', `perms` = '', `icon` = '#', `create_by` = 'admin', `create_time` = '2022-03-04 16:22:16', `update_by` = 'admin', `update_time` = '2022-03-04 18:08:09', `remark` = '' WHERE `menu_id` = 2291;
DELETE
FROM
    sys_role_menu
WHERE
        role_id IN ( SELECT role_id FROM sys_role WHERE role_type = 1 )
  AND menu_id = 2290;

DELETE
FROM
    sys_role_menu
WHERE
        role_id IN ( SELECT role_id FROM sys_role WHERE role_type = 2 AND role_key = 'depart' )
  AND menu_id = 2290;

DROP TABLE IF EXISTS `we_auto_tag_user_rel`;
CREATE TABLE `we_auto_tag_user_rel` (
                                        `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                        `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
                                        PRIMARY KEY (`rule_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='标签与员工使用范围表';
-- 记录相关
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_customer_record`;
CREATE TABLE `we_auto_tag_rule_hit_customer_record` (
                                                        `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                                        `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                                        `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                        `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
                                                        `add_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '添加时间',
                                                        PRIMARY KEY (`rule_id`,`corp_id`,`customer_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户打标签记录表';
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_customer_record_tag_rel`;
CREATE TABLE `we_auto_tag_rule_hit_customer_record_tag_rel` (
                                                                `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                                                `tag_id` varchar(64) NOT NULL DEFAULT '' COMMENT '标签id,去重用',
                                                                `tag_name` varchar(100) NOT NULL DEFAULT '' COMMENT '标签名',
                                                                `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                                `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户标签命中记录';
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_group_record`;
CREATE TABLE `we_auto_tag_rule_hit_group_record` (
                                                     `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                                     `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                                     `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                     `group_id` varchar(32) NOT NULL DEFAULT '' COMMENT '群id',
                                                     `group_name` varchar(128) NOT NULL DEFAULT '' COMMENT '群名',
                                                     `join_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '进群时间',
                                                     PRIMARY KEY (`rule_id`,`corp_id`,`customer_id`,`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户打标签记录表';
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_group_record_tag_rel`;
CREATE TABLE `we_auto_tag_rule_hit_group_record_tag_rel` (
                                                             `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                                             `tag_id` varchar(64) NOT NULL DEFAULT '' COMMENT '标签id,去重用',
                                                             `tag_name` varchar(100) NOT NULL DEFAULT '' COMMENT '标签名',
                                                             `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                             `group_id` varchar(32) NOT NULL DEFAULT '' COMMENT '群id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户标签命中记录';
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_keyword_record`;
CREATE TABLE `we_auto_tag_rule_hit_keyword_record` (
                                                       `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                                       `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                                       `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                       `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
                                                       `keyword` varchar(32) NOT NULL DEFAULT '' COMMENT '触发的关键词',
                                                       `from_text` varchar(255) NOT NULL DEFAULT '' COMMENT '触发文本',
                                                       `hit_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '命中时间',
                                                       `match_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '匹配类型 1:模糊匹配 2:精确匹配'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户打标签记录表';
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_keyword_record_tag_rel`;
CREATE TABLE `we_auto_tag_rule_hit_keyword_record_tag_rel` (
                                                               `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
                                                               `tag_id` varchar(64) NOT NULL COMMENT '标签id,去重用',
                                                               `tag_name` varchar(100) NOT NULL COMMENT '标签名',
                                                               `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                               `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户标签命中记录';

-- tigger 2022-03-07 16:58   Tower 任务: 查看客户记录异常 ( https://tower.im/teams/636204/todos/50048 )  缺少分号创建表失败
DROP TABLE IF EXISTS `we_auto_tag_rule_hit_customer_record`;
CREATE TABLE `we_auto_tag_rule_hit_customer_record` (
                                                        `rule_id`     bigint(20)  NOT NULL DEFAULT '0' COMMENT '规则id',
                                                        `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                                        `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
                                                        `user_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
                                                        `add_time`    datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '添加时间',
                                                        PRIMARY KEY (`rule_id`, `corp_id`, `customer_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='客户打标签记录表';

-- tigger 2022-03-08 10:29 Tower 任务: 关闭标签后创建日期显示异常 ( https://tower.im/teams/636204/todos/50103 )
ALTER TABLE `we_auto_tag_rule`
    MODIFY COLUMN `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间';
ALTER TABLE `we_auto_tag_rule_hit_customer_record`
    MODIFY COLUMN `add_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间';
ALTER TABLE `we_auto_tag_rule_hit_group_record`
    MODIFY COLUMN `join_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '进群时间';
ALTER TABLE `we_auto_tag_rule_hit_keyword_record`
    MODIFY COLUMN `hit_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '命中时间';

-- silver_chariot 2022-03-08 去除编辑老客进群的权限  Tower 任务: 老客进群内容编辑功能异常 ( https://tower.im/teams/636204/todos/50082 )
DELETE
FROM sys_menu
WHERE menu_id = 2211
  AND menu_name = '编辑任务';

-- tigger 20022-03-08 修改触发关键词标签记录表 涉及的操作 触发逻辑,记录列表查询,触发关键词详情
ALTER TABLE we_auto_tag_rule_hit_keyword_record DROP match_type;
ALTER TABLE `we_auto_tag_rule_hit_keyword_record` MODIFY COLUMN `keyword` varchar(255) NOT NULL DEFAULT '' COMMENT '触发的关键词';

-- tigger 2022-03-09 添加规则页面 2291的菜单
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2291, '新增规则页面', 2280, 22, 'addRule', 'customerManage/autoLabel/addRule', 1, 'P', '1', '0', '', '#', 'admin', '2022-03-04 16:22:16', 'admin', '2022-03-04 18:08:09', '');

