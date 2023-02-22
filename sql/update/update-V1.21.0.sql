-- 2023-01-12 wx 公众号配置表 Tower 任务: 公众号配置改为通用配置 ( https://tower.im/teams/636204/todos/54975 )
ALTER TABLE `we_open_config`
    ADD COLUMN `service_type_info` tinyint(1) NOT NULL DEFAULT '-1' COMMENT '授权方公众号类型,(0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号),自建应用为空' AFTER `official_account_domain`,
    ADD COLUMN `nick_name` varchar(64) NOT NULL DEFAULT '' COMMENT '授权方昵称' AFTER `service_type_info`,
    ADD COLUMN `principal_name` varchar(64) NOT NULL DEFAULT '' COMMENT '公众号的主体名称，自建应用为空' AFTER `nick_name`,
    ADD COLUMN `head_img` varchar(255) NOT NULL DEFAULT '' COMMENT '授权方头像，自建应用为空' AFTER `principal_name`,
    ADD COLUMN `authorizer_access_token` varchar(255) NOT NULL DEFAULT '' COMMENT '授权方接口调用凭据' AFTER `head_img`,
    ADD COLUMN `authorizer_refresh_token` varchar(255) NOT NULL DEFAULT '' COMMENT '接口调用凭据刷新令牌(上面令牌过期，需用此令牌刷新)' AFTER `authorizer_access_token`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`corp_id`, `official_account_app_id`) USING BTREE;

-- 雷达公众号配置表
CREATE TABLE `we_radar_official_account_config`
(
    `corp_id`     varchar(64) NOT NULL COMMENT '企业id',
    `app_id`      varchar(64) NOT NULL COMMENT '公众号appid',
    `create_by`   varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`corp_id`, `app_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='雷达公众号配置';

-- 公众号菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2305, '配置中心', 1, 2, 'configCentre', NULL, 1, 'M', '0', '0', '', 'system', 'admin', '2023-01-09 13:50:10', 'admin', '2023-01-09 13:59:24', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2306, '公众号管理', 2305, 1, 'offAccount', 'configCenter/offAccount', 1, 'C', '0', '0', 'system:offAccount:list', '#', 'admin', '2023-01-09 13:52:03', 'admin', '2023-01-09 13:59:44', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2307, '设置公众号', 2306, 1, '', NULL, 1, 'F', '0', '0', 'officialAccountsManager:set', '#', 'admin', '2023-01-12 17:43:33', '', NULL, '');

-- 给以前的管理员、部门管理员增加权限

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2305
FROM sys_role
WHERE role_type = 1 OR (role_type = 2
    AND role_key = 'depart');

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2306
FROM sys_role
WHERE role_type = 1 OR (role_type = 2
    AND role_key = 'depart');

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2307
FROM sys_role
WHERE role_type = 1 OR (role_type = 2
    AND role_key = 'depart');

-- 2023-01-13 wx 智能表单操作记录表 Tower 任务: 点击表单和提交表单相关 ( https://tower.im/teams/636204/todos/61401 )
CREATE TABLE `we_form_oper_record`
(
    `id`               bigint(20)    NOT NULL COMMENT '主键id',
    `form_id`          bigint(20)    NOT NULL DEFAULT '0' COMMENT '智能表单id',
    `user_id`          varchar(64)   NOT NULL DEFAULT '' COMMENT '发送智能表单的员工id',
    `user_name`        varchar(200)  NOT NULL DEFAULT '' COMMENT '员工名称',
    `user_head_image`  varchar(255)  NOT NULL DEFAULT '' COMMENT '员工头像地址url',
    `external_user_id` varchar(32)   NOT NULL DEFAULT '' COMMENT '客户id',
    `channel_type`     tinyint(1)    NOT NULL DEFAULT '0' COMMENT '渠道（0未知渠道,1员工活码，2朋友圈，3群发，4侧边栏,5欢迎语,6 客户SOP,7群SOP，8新客进群，9群日历,10自定义渠道,11:推广)',
    `union_id`         varchar(32)   NOT NULL DEFAULT '' COMMENT '外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。',
    `open_id`          varchar(32)   NOT NULL DEFAULT '' COMMENT '公众号/小程序open_id',
    `form_result`      varchar(4096) NOT NULL DEFAULT '' COMMENT '填写结果,格式[{"question":"","type":"","answer":""}]',
    `create_time`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间/点击时间',
    `commit_time`      datetime      NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '提交时间',
    `commit_flag`      tinyint(1)    NOT NULL DEFAULT '0' COMMENT '是否已提交，0：未提交，1：已提交',
    PRIMARY KEY (`id`),
    KEY `idx_form_date_external_channel` (`form_id`,`external_user_id`,`create_time`,`commit_time`,`channel_type`) USING BTREE COMMENT '客户操作记录查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能表单操作记录表';

-- 2023-01-16 wx sys_short_url_mapping 添加type 判断链接是雷达还是表单
ALTER TABLE `sys_short_url_mapping`
    MODIFY COLUMN `append_info` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '附加信息雷达：Json(user_id,radar_id,channel_id,detail)\r\n表单:(formId,channelType,appId,userId,corpId)' AFTER `long_url`,
    ADD COLUMN `type` tinyint(1) NOT NULL DEFAULT -1 COMMENT '链接类型，-1：未知，1：雷达，2：表单' AFTER `create_by`;

-- 2023-01-16 wx 表单短链code关联表
CREATE TABLE `we_form_short_code_rel`
(
    `form_id` bigint(11) NOT NULL COMMENT '表单id',
    `user_id` varchar(64) NOT NULL COMMENT '生成短链的员工id',
    `short_code` varchar(32) NOT NULL COMMENT '短链后面的唯一字符串（用于和域名拼接成短链）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`form_id`,`user_id`,`short_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单-短链关联表';

-- 2023-01-16 wx 智能表单权限设置 Tower 任务: 权限和菜单 ( https://tower.im/teams/636204/todos/61416 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2304, '智能表单', 2062, 4, 'intelligentForm', 'intelligentForm/index', 1, 'C', '0', '0', NULL, '#', 'admin', '2023-01-09 10:09:31', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2308, '编辑表单', 2062, 5, 'editForm', 'smartForm/addFormFiled', 1, 'P', '1', '0', NULL, '#', 'admin', '2023-01-13 13:58:18', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2309, '查看表单详情', 2062, 6, 'formDetail', 'smartForm/formDetail', 1, 'P', '1', '0', NULL, '#', 'admin', '2023-01-15 09:58:27', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2310, '管理企业表单', 2304, 1, '', NULL, 1, 'F', '0', '0', 'intelligentForm:manage:corp', '#', 'admin', '2023-01-16 18:00:23', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2311, '管理部门表单', 2304, 2, '', NULL, 1, 'F', '0', '0', 'intelligentForm:manage:dept', '#', 'admin', '2023-01-16 18:01:04', '', NULL, '');

-- 所有角色添加菜单路由
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2304
FROM sys_role;

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2308
FROM sys_role;

INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2309
FROM sys_role;

-- 给以前的管理员增加权限
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2310
FROM sys_role
WHERE role_type = 1;

-- 给以前的部门管理员增加权限
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2311
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';


-- tigger 2023-01-16 17:56  表单表 Tower 任务: 智能表单 ( https://tower.im/teams/636204/todos/61364 )
CREATE TABLE `we_form`
(
    `id`                   int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `head_image_url`       varchar(128) NOT NULL DEFAULT '' COMMENT '表单头图',
    `form_name`            varchar(32)  NOT NULL DEFAULT '' COMMENT '表单名称',
    `description`          varchar(255) NOT NULL DEFAULT '' COMMENT '表单说明',
    `submit_text`          varchar(16)  NOT NULL DEFAULT '' COMMENT '提交按钮文本描述',
    `submit_color`         varchar(16)  NOT NULL DEFAULT '#6BB4AB' COMMENT '提交按钮颜色rgb值',
    `form_field_list_json` text         NOT NULL COMMENT '表单字段列表json',
    `head_image_open_flag` tinyint(1)   NOT NULL DEFAULT '1' COMMENT '头图开关(false:关闭 true:开启)',
    `show_sort_flag`       tinyint(1)   NOT NULL DEFAULT '1' COMMENT '显示排序开关(false:关闭 true:开启)',
    `description_flag`     tinyint(1)   NOT NULL DEFAULT '1' COMMENT '表单说明开关(false:关闭 true:开启)',
    `group_id`             int(11)      NOT NULL DEFAULT '0' COMMENT '表单分组id',
    `enable_flag`          tinyint(1)   NOT NULL DEFAULT '1' COMMENT '启用标识(0: 未启用 1:启用)',
    `del_flag`             tinyint(1)   NOT NULL DEFAULT '0' COMMENT '删除标识 0: 未删除 1:删除',
    `delete_id`            int(11)      NOT NULL DEFAULT '0' COMMENT '唯一键删除id(删除的时候给deleteId设置为主键id(不重复))',
    `corp_id`              varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `create_by`            varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            varchar(64)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `corp_id_name_delete_flag_unique` (`corp_id`, `form_name`, `del_flag`, `delete_id`) USING BTREE COMMENT '企业下名称逻辑删除唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表单表';
-- tigger 2023-01-16 17:56  表单设置表 Tower 任务: 智能表单 ( https://tower.im/teams/636204/todos/61364 )
CREATE TABLE `we_form_advance_setting`
(
    `form_id`                 int(11)       NOT NULL DEFAULT '0' COMMENT '关联表单id',
    `dead_line_type`          tinyint(1)    NOT NULL COMMENT '截止时间类型(1: 永久有效 2:自定义日期)',
    `custom_date`             datetime      NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '截止时间的自定义时间(deadLineType == 1时才有)',
    `we_chat_public_platform` varchar(255)  NOT NULL DEFAULT '' COMMENT '公众号设置',
    `submit_cnt_type`         tinyint(1)             DEFAULT '0' COMMENT '提交次数类型(1: 不限 2:每个客户限提交1次)',
    `action_info_param_json`  varchar(255)           DEFAULT '' COMMENT '提交结果行为详情参数(当 submitActionType不为1的时候有用)',
    `action_note_flag`        tinyint(1)    NOT NULL DEFAULT '0' COMMENT '行为通知开关(false:关闭 true:开启)',
    `submit_action_type`      tinyint(1)    NOT NULL DEFAULT '0' COMMENT '提交结果行为类型(1:不跳转 2:跳转结果页面 3:跳转连接)',
    `tract_record_flag`       tinyint(1)    NOT NULL DEFAULT '0' COMMENT '轨迹记录开关(false:关闭 true:开启)',
    `customer_label_flag`     tinyint(1)    NOT NULL DEFAULT '0' COMMENT '客户标签开关(false:关闭 true:开启)',
    `label_setting_json`      varchar(1024) NOT NULL DEFAULT '' COMMENT '客户标签开关设置详情json 格式: {"clickLabelIdList":[""],"submitLabelIdList":[""]}',
    UNIQUE KEY `form_id_unique` (`form_id`) USING BTREE COMMENT '表单id唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表单设置表';
-- tigger 2023-01-16 17:56  客户评价 Tower 任务: 智能表单 ( https://tower.im/teams/636204/todos/61364 )
CREATE TABLE `we_form_customer_feedback`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `form_id`     int(11)     NOT NULL DEFAULT '0' COMMENT '表单id',
    `customer_id` varchar(64) NOT NULL DEFAULT '' COMMENT '客户id',
    `user_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
    `score`       tinyint(2)  NOT NULL DEFAULT '0' COMMENT '评分值(1-10)',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(提交时间)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客户好评评价表';
-- tigger 2023-01-16 17:56  表单分组 Tower 任务: 智能表单 ( https://tower.im/teams/636204/todos/61364 )
CREATE TABLE `we_form_group`
(
    `id`            int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `p_id`          int(11)     NOT NULL DEFAULT '0' COMMENT '父分组id',
    `name`          varchar(32) NOT NULL DEFAULT '' COMMENT '分组名称',
    `source_type`   tinyint(1)  NOT NULL DEFAULT '0' COMMENT '分组所属类别(1:企业 2: 部门 3:个人)',
    `department_id` bigint(20)  NOT NULL DEFAULT '0' COMMENT '所属部门id',
    `del_flag`      tinyint(1)  NOT NULL DEFAULT '0' COMMENT '删除标识 0: 未删除 1:删除',
    `corp_id`       varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `delete_id`     int(11)     NOT NULL DEFAULT '0' COMMENT '唯一键删除id(删除的时候给deleteId设置为主键id(不重复))',
    `sort`          int(11)     NOT NULL DEFAULT '0' COMMENT '排序号',
    `create_by`     varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `corp_id_name_delete_flag_unique` (`corp_id`, `name`, `del_flag`, `delete_id`) USING BTREE COMMENT '企业下名称逻辑删除唯一索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表单分组表';
-- tigger 2023-01-16 17:56  表单使用记录 Tower 任务: 智能表单 ( https://tower.im/teams/636204/todos/61364 )
CREATE TABLE `we_form_use_record`
(
    `id`               int(11)     NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `form_id`          int(11)     NOT NULL DEFAULT '0' COMMENT '表单id',
    `user_id`          varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
    `external_user_id` varchar(64) NOT NULL COMMENT '客户id',
    `use_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间(相同唯一索引则更新使用时间为新的)',
    `corp_id`          varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `form_id_user_id_corp_id_index` (`form_id`, `user_id`, `corp_id`) USING BTREE COMMENT '表单员工企业查询index'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表单使用记录表';

-- tigger 2023-01-16 17:56  为旧数据添加企业默认分组 Tower 任务: 智能表单 ( https://tower.im/teams/636204/todos/61364 )
INSERT IGNORE INTO `we_form_group`(`name`, `source_type`, `corp_id`,
                                   `create_by`, `update_by`)
    (SELECT '默认分组',1,corp_id,'admin','admin' FROM we_corp_account);


-- silver_chariot 2023-01-16 增加客戶總數統計字段 Tower 任务: 首页增加统计数据 ( https://tower.im/teams/636204/todos/61517 )
ALTER TABLE `we_user_behavior_data`
    ADD COLUMN `total_contact_cnt`  int(11) NOT NULL DEFAULT 0 COMMENT '总客户数(此总数由于官方统计接口不统计,所以每日定时任务进行统计)' AFTER `negative_feedback_cnt`;
-- silver_chariot 2023-01-16 增加新增客户流失数字段 Tower 任务: 首页增加统计数据 ( https://tower.im/teams/636204/todos/61517 )
ALTER TABLE `we_user_behavior_data`
    ADD COLUMN `new_contact_loss_cnt`  int(11) NOT NULL DEFAULT 0 COMMENT '当天加入的新客流失数(与negative_feedback_cnt不同，这是只统计当天加的流失的客户,由于当天新增客户的流失数官方统计没有提供，此数据也是由系统自行定时任务计算保存)' AFTER `total_contact_cnt`;

-- wx 2023-01-31 将企微配置菜单移动到配置中心 Tower 任务: 系统设置功能菜单布局优化 ( https://tower.im/teams/636204/todos/61802 )
UPDATE sys_menu
SET parent_id = 2305
WHERE menu_id IN (102,2010);

-- wx 2023-02-03 修改二级目录名称及调整顺序 Tower 任务: 系统设置功能菜单布局优化 ( https://tower.im/teams/636204/todos/61802 )
UPDATE sys_menu
SET menu_name = '企业管理', order_num = 1
WHERE menu_id = 2283;


-- tigger 2023-02-03 删除表单名称唯一索引  Tower 任务: 表单名称校验异常 ( https://tower.im/teams/636204/todos/62193 )
DROP INDEX `corp_id_name_delete_flag_unique` ON we_form;

-- wx 2023-02-08 添加一字段作为客户所属员工字段 Tower 任务: 客户使用推广方式填写表单数据异常 ( https://tower.im/teams/636204/todos/62376 )
ALTER TABLE `we_form_oper_record`
    MODIFY COLUMN `user_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送智能表单的员工名称' AFTER `user_id`,
    ADD COLUMN `employees` varchar(64) NOT NULL DEFAULT '' COMMENT '客户所属员工user_id' AFTER `external_user_id`;

-- wx 2023-02-08 企业管理和配置中心的图标 Tower 任务: 修改菜单图标 ( https://tower.im/teams/636204/todos/62400 )
UPDATE sys_menu
SET icon = 'tree'
WHERE menu_id = 2283;

UPDATE sys_menu
SET icon = 'system-hollow'
WHERE menu_id = 2305;

-- wx 2023-02-09 修改配置中心path名称 Tower 任务: 将configCentre改为 configCenter ( https://tower.im/teams/636204/todos/62458 )
UPDATE sys_menu
SET path = 'configCenter'
WHERE menu_id = 2305;