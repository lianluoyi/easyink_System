-- silver_chariot 2021-11-29 菜单调整 Tower 任务: 菜单调整 ( https://tower.im/teams/636204/todos/44877 )
-- 大菜单调整顺序 ：运营中心放置第一位，应用管理第二顺位，
update sys_menu set order_num = 3
where menu_id = 2001  and menu_name = '客户中心';
update sys_menu set order_num = 1
where menu_id = 2188  and menu_name = '运营中心';
update sys_menu set order_num = 5
where menu_id = 2229  and menu_name = '应用管理';
update sys_menu set order_num = 8
where menu_id = 2079  and menu_name = '会话存档';
-- 会话存档改名'企业风控'
update sys_menu set menu_name = '企业风控'
where menu_id = 2079  and menu_name = '会话存档';

-- ‘活码管理’改名‘引流获客’
update sys_menu set menu_name = '引流获客'
where menu_id = 2052  and menu_name = '活码管理';
-- 新客进群，老客进群移到引流获客
update sys_menu set parent_id = 2052
where menu_id = 2101 and menu_name = '新客进群' ;
update sys_menu set parent_id = 2052
where menu_id = 2106 and menu_name = '新客拉群增改查' ;
update sys_menu set parent_id = 2052
where menu_id = 2102 and menu_name = '老客进群' ;
update sys_menu set parent_id = 2052
where menu_id = 2107 and menu_name = '编辑老客进群' ;
-- 修改引流获客内部顺序
update sys_menu set order_num = 15
where menu_id = 2101 and menu_name = '新客进群' ;
update sys_menu set order_num = 12
where menu_id = 2056
  and menu_name = '客户群活码';
-- 停用社群管理
update sys_menu
set status = 1
where menu_id = 2100
  and menu_name = '社群管理';
-- 内容管理顺序调整
update sys_menu
set order_num = 2
where menu_id = 2052
  and menu_name = '引流获客';
update sys_menu
set order_num = 5
where menu_id = 2062
  and menu_name = '内容管理';

-- silver_chariot 2021-12-01 Tower 任务: 在职继承 ( https://tower.im/teams/636204/todos/44243 )
CREATE TABLE `we_customer_transfer_config`
(
    `corp_id`              varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `enable_transfer_info` tinyint(1)  NOT NULL DEFAULT '1' COMMENT '继承客户信息开关（1：开启，0：关闭）',
    `enable_side_bar`      tinyint(1)  NOT NULL DEFAULT '0' COMMENT '侧边栏转接客户开关（1:开启，0:关闭）',
    PRIMARY KEY (`corp_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='继承设置表';

-- silver_chariot 2021-12-01  Tower 任务: 在职继承 ( https://tower.im/teams/636204/todos/44243 )
CREATE TABLE `we_customer_transfer_record`
(
    `id`                       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`                  varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `handover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '原跟进成员userid',
    `external_userid`          varchar(32)  NOT NULL DEFAULT '' COMMENT '待分配的外部联系人userid',
    `takeover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '接替成员的userid',
    `hanover_username`         varchar(200) NOT NULL DEFAULT '' COMMENT '原跟进成员名称',
    `takeover_username`        varchar(200) NOT NULL DEFAULT '' COMMENT '跟进成员名称',
    `handover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '原跟进人部门名称',
    `takeover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '接替人部门名称',
    `transfer_time`            datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    `status`                   tinyint(2)   NOT NULL DEFAULT '2' COMMENT '接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录',
    `takeover_time`            datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '接替时间',
    `remark`                   varchar(64)  NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_corp_id` (`corp_id`) USING BTREE,
    KEY `idx_transfer_time` (`transfer_time`) USING BTREE,
    KEY `idx_external_userid` (`external_userid`, `handover_userid`, `takeover_userid`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='在职继承分配记录表';


-- My society sister li 2021-11-30 Tower 任务: 客户SOP、客户群SOP表结构创建 (https://tower.im/teams/636204/todos/46661/)

-- SOP基本信息
CREATE TABLE `we_operations_center_sop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT 'SOP名称',
  `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人.员工userId',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `sop_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'sop类型 0：定时sop，1：循环sop，2：新客sop，3：活动sop，4：生日sop，5：群日历',
  `filter_type` tinyint(2) NOT NULL COMMENT '使用群聊类型 0：指定群聊 ,1：筛选群聊 ',
  `is_open` tinyint(2) NOT NULL DEFAULT '1' COMMENT '启用状态 0：关闭，1：启用',
  PRIMARY KEY (`id`),
  KEY `index_corpid` (`corp_id`) USING BTREE COMMENT '普通索引(index_corpid)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOP基本信息';

-- SOP作用范围
CREATE TABLE `we_operations_center_sop_scope` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `sop_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'we_operations_center_sop 主键ID',
  `target_id` varchar(64) NOT NULL DEFAULT '' COMMENT '当为群sop时，为chatId;当为客户sop时，为userId',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `index_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '普通索引（index_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOP作用范围';

-- 群SOP筛选群聊条件
CREATE TABLE `we_operations_center_group_sop_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `sop_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'we_operations_center_sop主键ID',
  `owner` text NOT NULL COMMENT '群主( 多个逗号隔开)',
  `tag_id` text NOT NULL COMMENT '群标签ID（多个逗号隔开）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '群创建时间范围',
  `end_time` datetime NOT NULL COMMENT '群创建时间',
  PRIMARY KEY (`id`),
  KEY `index_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '普通索引（index_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群SOP筛选群聊条件';

-- 客户SOP筛选条件
CREATE TABLE `we_operations_center_customer_sop_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `sop_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'we_operations_center_sop主键ID',
  `users` text NOT NULL COMMENT '所属员工（多个逗号隔开 ）',
  `tag_id` text NOT NULL COMMENT '标签ID（多个逗号隔开 ）',
  `cloumn_info` text NOT NULL COMMENT '客户属性名和值，json存储',
  `filter_cloumn_info` text NOT NULL COMMENT '过滤客户属性名和值，json存储',
  `filter_tag_id` text NOT NULL COMMENT '标签ID(多个逗号隔开) ',
  PRIMARY KEY (`id`),
  KEY `index_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '普通索引（index_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户SOP筛选条件';

-- sop规则表
CREATE TABLE `we_operations_center_sop_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `sop_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '群sop的主键id',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '规则名称',
  `alert_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '提醒类型\r\n0：xx小时xx分钟提醒，1：xx天xx:xx提醒，2：每天xx:xx提醒，3：每周周x的xx:xx提醒，4：每月x日xx:xx提醒',
  `alert_data1` int(2) NOT NULL DEFAULT '0' COMMENT '提醒时间内容1',
  `alert_data2` varchar(20) NOT NULL DEFAULT '' COMMENT '提醒时间内容2',
  PRIMARY KEY (`id`),
  KEY `idx_sop` (`sop_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sop规则表';

-- sop素材表
CREATE TABLE `we_operations_center_sop_material`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `sop_id` bigint(20) NOT NULL DEFAULT 0 COMMENT 'sop的主键id',
  `rule_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '规则id',
  `material_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '素材id',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '素材排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_corp_sop`(`corp_id`, `sop_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- sop任务详情表
CREATE TABLE `we_operations_center_sop_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
  `sop_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'sop的主键id',
  `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
  `user_id` varchar(64) NOT NULL COMMENT '操作人/群主',
  `target_id` varchar(32) NOT NULL DEFAULT '' COMMENT '消息接收者(当为客户时，填写客户userId；当为群时，填写群chatId)',
  `is_finish` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已执行 0：未执行，1：已执行',
  `alert_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '提醒时间',
  `finish_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_corp_sop` (`corp_id`,`sop_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 循环sop的起止时间表
CREATE TABLE `we_operations_center_group_sop_filter_cycle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `sop_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'sopId',
  `cycle_start` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `cycle_end` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
  PRIMARY KEY (`id`),
  KEY `index_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '普通索引（index_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户群SOP-循环SOP的起止时间设置表';


-- My society sister li 2021-12-02 Tower 任务: 修改SOP (https://tower.im/teams/636204/todos/46699/)
ALTER TABLE `we_operations_center_sop_scope`
ADD UNIQUE INDEX `unique_corpid_sopid_targetid`(`corp_id`, `sop_id`, `target_id`) USING BTREE COMMENT '唯一索引（unique_corpid_sopid_targetid）';

ALTER TABLE `we_operations_center_group_sop_filter`
DROP INDEX `index_corpid_sopid`,
ADD UNIQUE INDEX `unique_corpid_sopid`(`corp_id`, `sop_id`) USING BTREE COMMENT '唯一索引（unique_corpid_sopid）';

ALTER TABLE `we_operations_center_group_sop_filter_cycle`
DROP INDEX `index_corpid_sopid`,
ADD UNIQUE INDEX `unique_corpid_sopid`(`corp_id`, `sop_id`) USING BTREE COMMENT '唯一索引（unique_corpid_sopid）';

-- yiming Tower 任务: 保存客户sop接口 ( https://tower.im/teams/636204/todos/46733 )
ALTER TABLE `we_operations_center_customer_sop_filter`
ADD COLUMN `start_time` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '客户添加开始时间' AFTER `filter_tag_id`,
ADD COLUMN `end_time` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '客户添加截止时间' AFTER `start_time`;

ALTER TABLE `we_operations_center_sop`
MODIFY COLUMN `filter_type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '使用群聊类型 0：指定群聊 ,1：筛选群聊 ' AFTER `sop_type`;


-- My society sister li 2021-12-03 Tower 任务: 运营中心SOP定时任务(https://tower.im/teams/636204/todos/46701/)
INSERT INTO `sys_job`
VALUES (15, '运营中心SOP任务', 'SYSTEM', 'weOperationsCenterSopTask.execute', '0 * * * * ?', '1', '1', '0', 'admin',
        '2021-12-03 00:00:00', 'admin', '2021-12-03 00:00:00', '');

-- silver_chariot 增加定期查询客户分配情况任务 Tower 任务: 在职继承 ( https://tower.im/teams/636204/todos/44243 )
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`,
                       `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('定期查询客户分配情况任务', 'SYSTEM', 'transferResultTask.execute', '0 */5 * * * ?', '2', '1', '0', 'admin',
        '2021-12-01 18:25:03', 'admin', '2021-12-01 18:25:51', '');

-- silver_chariot 2021-12-03 把description 从tinytext类型改成varchar 并设置默认值
ALTER TABLE `we_flower_customer_rel`
    MODIFY COLUMN `description` varchar(258) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此外部联系人的描述' AFTER `remark`;

-- silver_chariot 2021-12-03 在职继承:菜单权限  Tower 任务: 在职继承 ( https://tower.im/teams/636204/todos/44243 )

INSERT INTO `sys_menu`
VALUES ('2245', '在职继承', '2179', '45', 'inherit', 'customerManage/inherit/index', '1', 'C', '0', '0', '', '#', 'admin',
        '2021-11-29 14:35:48', 'admin', '2021-12-01 13:48:49', '');
INSERT INTO `sys_menu`
VALUES ('2246', '分配记录', '2179', '46', 'inheritRecord', 'customerManage/inherit/record', '1', 'P', '1', '0', '', '#',
        'admin', '2021-12-01 13:50:48', 'admin', '2021-12-01 14:21:25', '');
INSERT INTO `sys_menu`
VALUES ('2247', '分配客户', '2245', '2', '', NULL, '1', 'F', '0', '0', 'customerMange:active:transfer', '#', 'admin',
        '2021-12-03 10:30:36', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2248', '查看分配记录', '2245', '6', '', NULL, '1', 'F', '0', '0', 'customerMange:transfer:record', '#', 'admin',
        '2021-12-03 10:31:18', 'admin', '2021-12-03 10:32:38', '');
INSERT INTO `sys_menu`
VALUES ('2249', '继承设置', '2245', '12', '', NULL, '1', 'F', '0', '0', ' customerManage:transfer:config', '#', 'admin',
        '2021-12-03 10:31:48', '', NULL, '');

-- silver_chariot 2021-12-03 给之前的角色初始化 在职继承 菜单权限   Tower 任务: 在职继承 ( https://tower.im/teams/636204/todos/44243 )
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2245
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2247
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2248
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2249
FROM sys_role
WHERE role_type = 1;

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2245
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2247
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2248
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';

-- silver_chariot 给以前的企业初始化继承设置 Tower 任务: 在职继承 ( https://tower.im/teams/636204/todos/44243 )
INSERT ignore INTO we_customer_transfer_config
    (corp_id, enable_transfer_info, enable_side_bar)
SELECT corp_id, 1, 0
FROM we_corp_account
WHERE del_flag = '0'
  AND status = '0';

-- yiming 增加性别字段
ALTER TABLE `we_operations_center_customer_sop_filter`
ADD COLUMN `gender` tinyint(4) NOT NULL DEFAULT 0 COMMENT '外部联系人性别 0-未知 1-男性 2-女性' AFTER `sop_id`;

-- silver_chariot 离职继承改造  Tower 任务: 离职继承改造 ( https://tower.im/teams/636204/todos/46981 )
-- 离职分配记录总表
CREATE TABLE `we_resigned_transfer_record`
(
    `id`                       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`                  varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `handover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '原跟进离职员工id',
    `takeover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '接替员工id',
    `dimission_time`           datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '离职时间',
    `handover_username`        varchar(200) NOT NULL DEFAULT '' COMMENT '原跟进人用户名',
    `takeover_username`        varchar(200) NOT NULL DEFAULT '' COMMENT '接替人名称',
    `handover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '原跟进人部门名称',
    `takeover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '接替人部门名称',
    `transfer_time`            datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_corp_handover_takeover_id` (`corp_id`, `handover_userid`, `takeover_userid`, `dimission_time`) USING BTREE,
    KEY `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
-- 离职分配记录客户详情表
CREATE TABLE `we_resigned_customer_transfer_record`
(
    `record_id`       bigint(20)   NOT NULL DEFAULT '0' COMMENT '分配记录id',
    `external_userid` varchar(32)  NOT NULL DEFAULT '' COMMENT '外部联系人userId',
    `status`          tinyint(2)   NOT NULL DEFAULT '2' COMMENT '接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限',
    `takeover_time`   datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '接替时间',
    `remark`          varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`record_id`, `external_userid`),
    KEY `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='离职客户继承记录表';
-- 离职分配记录客户群详情表
CREATE TABLE `we_resigned_group_transfer_record`
(
    `record_id`     bigint(20)   NOT NULL DEFAULT '0' COMMENT '分配记录id',
    `chat_id`       varchar(32)  NOT NULL DEFAULT '' COMMENT '群聊id',
    `status`        tinyint(1)   NOT NULL DEFAULT '1' COMMENT '接替状态,只有继承成功才会有值（1成功0失败)',
    `takeover_time` datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '接替时间',
    `remark`        varchar(100) NOT NULL DEFAULT '' COMMENT '失败原因',
    PRIMARY KEY (`record_id`, `chat_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='离职客户群继承记录表';

-- 2021-12-08 silver_chariot SOP菜单权限修改  Tower 任务: 菜单权限 ( https://tower.im/teams/636204/todos/46948 )
INSERT INTO `sys_menu`
VALUES ('2250', '留存转换', '2188', '31', 'retainedConversion', NULL, '1', 'M', '0', '0', NULL, '#', 'admin',
        '2021-11-29 15:36:04', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2251', '客户SOP', '2250', '3', 'customerSOP', 'retainedConversion/SOP/customerSOP', '1', 'C', '0', '0', '', '#',
        'admin', '2021-11-29 15:41:36', 'admin', '2021-11-30 13:49:09', '');
INSERT INTO `sys_menu`
VALUES ('2252', '群SOP', '2250', '4', 'groupSOP', 'retainedConversion/SOP/groupSOP', '1', 'C', '0', '0', '', '#',
        'admin', '2021-11-29 15:43:48', 'admin', '2021-11-30 13:46:18', '');
INSERT INTO `sys_menu`
VALUES ('2254', '新增SOP页面', '2250', '8', 'addSOP', 'retainedConversion/SOP/addSOP', '1', 'P', '1', '0', '', '#', 'admin',
        '2021-11-30 11:35:36', 'admin', '2021-12-07 10:08:28', '');
INSERT INTO `sys_menu`
VALUES ('2255', 'SOP详情', '2250', '9', 'SOPDetail', 'retainedConversion/SOP/SOPDetail', '1', 'P', '1', '0', NULL, '#',
        'admin', '2021-12-01 19:19:47', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2256', '新增SOP', '2251', '1', '', NULL, '1', 'F', '0', '0', 'wecom:customerSop:add', '#', 'admin',
        '2021-12-08 10:26:25', 'admin', '2021-12-08 10:27:10', '');
INSERT INTO `sys_menu`
VALUES ('2257', '启用/关闭SOP', '2251', '5', '', NULL, '1', 'F', '0', '0', 'wecom:customerSop:switch', '#', 'admin',
        '2021-12-08 10:27:02', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2258', '删除SOP', '2251', '10', '', NULL, '1', 'F', '0', '0', 'wecom:customerSop:del', '#', 'admin',
        '2021-12-08 10:27:36', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2259', '编辑SOP', '2251', '20', '', NULL, '1', 'F', '0', '0', 'wecom:customerSop:edit', '#', 'admin',
        '2021-12-08 10:28:00', 'admin', '2021-12-08 10:28:15', '');
INSERT INTO `sys_menu`
VALUES ('2260', '新增SOP', '2252', '1', '', NULL, '1', 'F', '0', '0', 'wecom:groupSop:add', '#', 'admin',
        '2021-12-08 10:28:46', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2261', '启用/关闭SOP', '2252', '5', '', NULL, '1', 'F', '0', '0', 'wecom:groupSop:switch', '#', 'admin',
        '2021-12-08 10:29:16', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2262', '删除SOP', '2252', '10', '', NULL, '1', 'F', '0', '0', 'wecom:groupSop:del', '#', 'admin',
        '2021-12-08 10:29:50', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2263', '编辑SOP', '2252', '15', '', NULL, '1', 'F', '0', '0', 'wecom:groupSop:edit', '#', 'admin',
        '2021-12-08 10:30:19', '', NULL, '');


-- 2021-12-08 silver_chariot 给以前管理员角色增加sop权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2250
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2251
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2252
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2256
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2257
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2258
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2259
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2260
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2261
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2262
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2263
FROM sys_role
WHERE role_type = 1;

-- silver_chariot 给以前的部门管理员增加sop权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2250
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2251
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2252
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2256
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2257
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2258
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2259
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2260
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2261
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2262
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2263
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';


-- 修改权限标识   Tower 任务: 在职继承部分功能按钮不显示 ( https://tower.im/teams/636204/todos/46976 )
update sys_menu
set perms = 'customerManage:active:transfer'
where menu_id = 2247
  and menu_name = '分配客户';

update sys_menu
set perms = 'customerManage:transfer:record'
where menu_id = 2248
  and menu_name = '查看分配记录';

update sys_menu
set perms = 'customerManage:transfer:record'
where menu_id = 2249
  and menu_name = '继承设置';



-- 2021-12-08 增加更新离职分配记录状态的定时任务 Tower 任务: 继承状态获取任务 ( https://tower.im/teams/636204/todos/46986 )
INSERT INTO `sys_job` VALUES (16, '更新离职员工客户接替结果任务', 'SYSTEM', 'transferResignedResultTask.execute', '0 /10 * * * ? *', '1', '1', '0', 'admin', '2021-12-08 14:43:11', '', '2021-12-08 14:43:17', '');


-- yiming 增加任务详情 待办任务id Tower 任务: 定时任务推送提醒到应用后，增加保存到待办事项 ( https://tower.im/teams/636204/todos/46987 )
ALTER TABLE `we_customer_trajectory`
ADD COLUMN `detail_id` bigint(20) NOT NULL COMMENT 'sop任务详情id' AFTER `corp_id`,
ADD COLUMN `sop_task_ids` varchar(1000) NOT NULL DEFAULT '' COMMENT 'sop任务待办id 逗号隔开' AFTER `detail_id`;

CREATE TABLE `we_operations_center_sop_task` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
  `media_type` int(1) NOT NULL DEFAULT '0' COMMENT '0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序',
  `content` varchar(1500) NOT NULL DEFAULT '' COMMENT '内容详情',
  `title` varchar(128) NOT NULL DEFAULT '' COMMENT '标题',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '链接地址',
  `cover_url` varchar(255) NOT NULL DEFAULT '' COMMENT '封面',
  `is_defined` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接时使用：0 默认，1 自定义',
  PRIMARY KEY (`id`),
  KEY `idx_corp` (`corp_id`) USING BTREE COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sop待办任务素材表';
-- 2021.12.09 继承设置权限标识修改
update sys_menu
set perms = 'customerManage:transfer:config'
where menu_id = 2249
  and menu_name = '继承设置';

-- 2021.12.09 silver_chariot 把【聊天记录】菜单 改名为 【会话存档】 Tower 任务: 菜单调整 ( https://tower.im/teams/636204/todos/44877 )
UPDATE sys_menu
SET menu_name = '会话存档'
WHERE menu_id = 2080
  AND menu_name = '聊天记录';

-- 2021.12.09 silver_chariot 把【留存转换】菜单 改名为 【留存转化】 Tower 任务: 菜单调整 ( https://tower.im/teams/636204/todos/44877 )
UPDATE sys_menu
SET menu_name = '留存转化'
WHERE menu_id = 2250
  AND menu_name = '留存转换';

-- yiming 2021-12-9 删除没用的字段
ALTER TABLE `we_operations_center_customer_sop_filter`
DROP COLUMN `filter_cloumn_info`;
-- 为sop筛选条件补充corpId
UPDATE we_operations_center_customer_sop_filter woccsf
INNER JOIN we_operations_center_sop wocs ON woccsf.sop_id = wocs.id
SET woccsf.corp_id = wocs.corp_id
WHERE woccsf.corp_id ='';


-- 2021.12.08  silver_chariot 把原来的部门和is_leader字段改成varchar，原因：tinytext不能设置默认值导致一些SQL会报cannot be null 错误
ALTER TABLE `we_user`
    MODIFY COLUMN `department` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '用户所属部门,使用逗号隔开,字符串格式存储' AFTER `main_department`;

ALTER TABLE `we_user`
    MODIFY COLUMN `is_leader_in_dept` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '1表示为上级,0表示普通成员(非上级)。' AFTER `position`;

-- yiming Tower 任务: 群待办事项异常 ( https://tower.im/teams/636204/todos/47262 )
ALTER TABLE `we_customer_trajectory`
MODIFY COLUMN `detail_id` bigint(20) NOT NULL DEFAULT -1 COMMENT 'sop任务详情id' AFTER `corp_id`;

UPDATE we_customer_trajectory wct
INNER JOIN we_operations_center_sop_detail wocsd ON wct.detail_id = wocsd.id
AND wct.detail_id != - 1 AND wct.detail_id != 0
SET wct.external_userid = wocsd.target_id
WHERE
wct.trajectory_type = 4;

UPDATE we_customer_trajectory wct
INNER JOIN we_operations_center_sop_detail wocsd ON wct.detail_id = wocsd.id
AND wct.detail_id != - 1 AND wct.detail_id != 0
SET wct.start_time = DATE_FORMAT(wocsd.alert_time,'%T')
WHERE
wct.trajectory_type = 4 and wct.start_time = '00:00:00';



