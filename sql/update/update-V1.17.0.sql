-- silver_chariot 2022-07-19 Tower 任务: 雷达链接 ( https://tower.im/teams/636204/todos/53347 )
-- ----------------------------
-- Table structure for we_radar 雷达表
-- ----------------------------
CREATE TABLE `we_radar`
(
    `id`                     bigint(20) NOT NULL,
    `corp_id`                varchar(64)   NOT NULL DEFAULT '' COMMENT '企业ID',
    `radar_title`            varchar(255)  NOT NULL DEFAULT '' COMMENT '雷达标题',
    `url`                    varchar(3000) NOT NULL DEFAULT '' COMMENT '雷达原始路径url',
    `cover_url`              varchar(1200) NOT NULL DEFAULT '' COMMENT '雷达链接封面图',
    `title`                  varchar(255)  NOT NULL DEFAULT '' COMMENT '链接标题',
    `content`                varchar(255)  NOT NULL DEFAULT '' COMMENT '雷达链接摘要',
    `enable_click_notice`    tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启行为通知（1[true]是0[false]否）',
    `enable_behavior_record` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许轨迹记录（1[true]是 0[false]否) ',
    `enable_customer_tag`    tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许打上客户标签（ 1[true]是 0[false]否) ',
    `enable_update_notice`   tinyint(1) NOT NULL DEFAULT '1' COMMENT '更新后是否通知员工（true[1]是 false[0]否) ',
    `create_time`            datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`              varchar(64)   NOT NULL DEFAULT '' COMMENT '创建人',
    `update_time`            datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`              varchar(64)   NOT NULL DEFAULT '' COMMENT '更新人',
    `is_defined`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用自定义链接（1[true]是,0[false]否)',
    PRIMARY KEY (`id`),
    KEY                      `idx_corp_id_type` (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达表';
-- ----------------------------
-- Table structure for we_radar_tag_rel 雷达-标签关系表
-- ----------------------------
CREATE TABLE `we_radar_tag_rel`
(
    `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达ID',
    `tag_id`   varchar(64) NOT NULL DEFAULT '' COMMENT '标签ID',
    PRIMARY KEY (`radar_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达-标签关系表';

-- ----------------------------
-- Table structure for we_radar_channel  渠道表
-- ----------------------------
CREATE TABLE `we_radar_channel`
(
    `id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道id',
    `radar_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id',
    `name`        varchar(32)  NOT NULL DEFAULT '' COMMENT '渠道名称',
    `short_url`   varchar(255) NOT NULL DEFAULT '' COMMENT '渠道的短链url',
    `create_time` datetime     NOT NULL,
    `create_by`   varchar(64)  NOT NULL,
    UNIQUE KEY `uniq_redar_name` (`radar_id`,`name`) USING BTREE,
    KEY           `idx_radar_id_channel_name` (`radar_id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达-渠道表';

-- ----------------------------
-- Table structure for we_radar_click_record  雷达点击记录表
-- ----------------------------
CREATE TABLE `we_radar_click_record`
(
    `id`                 bigint(20) NOT NULL COMMENT '雷达点击记录表ID',
    `corp_id`            varchar(64)  NOT NULL DEFAULT '' COMMENT '公司Id',
    `user_id`            varchar(64)  NOT NULL DEFAULT '' COMMENT '发送活码用户id',
    `user_name`          varchar(200) NOT NULL DEFAULT '' COMMENT '发送雷达链接的用户名称',
    `external_user_id`   varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `external_user_name` varchar(128) NOT NULL DEFAULT '' COMMENT '客户名称',
    `channel_id`         bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道id（（0未知渠道,1员工活码，2朋友圈，3群发，4侧边栏,5欢迎语,6 客户SOP,7群SOP，8新客进群，9群日历）',
    `channel_name`       varchar(32)  NOT NULL DEFAULT '未知渠道' COMMENT '渠道名',
    `union_id`           varchar(32)  NOT NULL DEFAULT '' COMMENT '外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。',
    `open_id`            varchar(32)  NOT NULL DEFAULT '' COMMENT '公众号/小程序open_id',
    `create_time`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_date`        varchar(32)  NOT NULL DEFAULT '0000-00-00' COMMENT '创建日期（格式yyyy-mm-dd)',
    PRIMARY KEY (`id`),
    KEY                  `idx_corp_date_external` (`corp_id`,`create_date`,`external_user_id`,`channel_name`) USING BTREE,
    KEY                  `idx_corp_channel` (`corp_id`,`channel_name`) USING BTREE,
    KEY                  `idx_corp_customer` (`corp_id`,`external_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达点击记录表';


-- silver_chariot 2022-7-19 活码表保存对应的小程序短链 Tower 任务: 活码小程序 ( https://tower.im/teams/636204/todos/53069 )
ALTER TABLE `we_emple_code`
    ADD COLUMN `app_link` varchar(128) NOT NULL DEFAULT '' COMMENT '活码小程序链接' AFTER `tag_flag`;


-- silver_chariot 2022-7-21 短链长链映射表
CREATE TABLE `sys_short_url_mapping`
(
    `id`          bigint(20) NOT NULL COMMENT 'id,短链',
    `short_code`  varchar(32)   NOT NULL DEFAULT '' COMMENT '短链后面的唯一字符串（用于和域名拼接成短链）',
    `long_url`    varchar(1024) NOT NULL COMMENT '原链接（长链接）',
    `append_info` varchar(512)  NOT NULL DEFAULT '' COMMENT '附加信息Json(user_id,radar_id,channel_id,detail)',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   varchar(64)   NOT NULL DEFAULT '' COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_short_code` (`short_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='长链-短链映射表';


-- silver_chariot   2022-07-19 Tower 任务: 新客户的客户画像显示失败 ( https://tower.im/teams/636204/todos/54394 )
ALTER TABLE `we_flower_customer_rel`
    MODIFY COLUMN `oper_userid` varchar (64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid' AFTER `corp_id`;

-- silver_chariot   2022-07-19 记录报表增加客户头像
ALTER TABLE `we_radar_click_record`
    ADD COLUMN `external_user_head_image` varchar(512) NOT NULL DEFAULT '' COMMENT '客户头像url' AFTER `external_user_name`;

-- wx Tower we_radar表 任务: 新增企业雷达显示异常 ( https://tower.im/teams/636204/todos/54473 )
ALTER TABLE `we_radar`
    ADD COLUMN `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '雷达类型（1个人雷达，2部门雷达，3企业雷达）';

-- silver_chariot   2022-07-22 增加 客户表索引
ALTER TABLE `we_customer`
    ADD INDEX `idx_union_id` (`unionid`) USING BTREE ;

-- silver_chariot  2022-07-23 雷達菜單相關
-- 增加菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2299', '雷达库', '2062', '3', 'radarManage', 'radarLibrary/radarManage', '1', 'C', '0', '0', '', '#', 'admin', '2022-07-15 15:14:42', 'admin', '2022-07-15 15:27:08', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2300', '查看雷达链接详情', '2062', '1', 'radarDetail', 'radarLibrary/radarDetail', '1', 'P', '1', '0', NULL, '#', 'admin', '2022-07-19 09:12:38', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2301', '管理企业雷达', '10011', '5', '', NULL, '1', 'F', '0', '0', 'radar:corp:manage', '#', 'admin', '2022-07-23 21:52:48', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2302', '管理部门雷达', '10011', '10', '', NULL, '1', 'F', '0', '0', 'radar:department:manage', '#', 'admin', '2022-07-23 21:53:42', '', NULL, '');

-- 权限设置
-- 如果需要给所有管理员增加该菜单
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2299
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2301
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2302
FROM sys_role
WHERE role_type = 1;
-- 如果需要给所有部门管理员增加该菜单 ， 2264 用新增菜单id替换
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2299
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2302
FROM sys_role
WHERE role_type = 2
  AND role_key = 'depart';
-- 如果需要给所有员工增加该菜单 ， 2264 用新增菜单id替换
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2299
FROM sys_role
WHERE role_type = 2
  AND role_key = 'employee';
-- wx we_radar_click_record表 修改字段 Tower 任务: 查看个人雷达报错 ( https://tower.im/teams/636204/todos/54486 )
ALTER TABLE `we_radar_click_record` change `corp_id` `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id';

-- wx we_radar_click_record表 修改字段 Tower 任务: 查看个人雷达报错 ( https://tower.im/teams/636204/todos/54486 )
ALTER TABLE `we_radar_click_record` change `channel_id` `channel_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '渠道type（0未知渠道,1员工活码，2朋友圈，3群发，4侧边栏,5欢迎语,6 客户SOP,7群SOP，8新客进群，9群日历,10自定义渠道)';


-- wx we_material 添加radar_id字段 Tower 任务: 选择素材调整（工时0.6） ( https://tower.im/teams/636204/todos/54349 )
ALTER TABLE `we_material`
    ADD COLUMN `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用';

-- wx we_material 添加enable_convert_radar字段 Tower 任务: 选择素材调整（工时0.6） ( https://tower.im/teams/636204/todos/54349 )
ALTER TABLE `we_material`
    ADD COLUMN `enable_convert_radar` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接时使用(0,不转化为雷达，1：转化为雷达)';

-- wx we_msg_tlp_material 添加radar_id字段 Tower 任务: 选择素材调整（工时0.6） ( https://tower.im/teams/636204/todos/54349 )
ALTER TABLE `we_msg_tlp_material`
    ADD COLUMN `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用';

-- wx we_words_detail 添加radar_id字段 Tower 任务: 选择素材调整（工时0.6） ( https://tower.im/teams/636204/todos/54349 )
ALTER TABLE `we_words_detail`
    ADD COLUMN `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用';

-- wx we_customer_seedmessage 添加radar_id字段 Tower 任务: 选择素材调整（工时0.6） ( https://tower.im/teams/636204/todos/54349 )
ALTER TABLE `we_customer_seedmessage`
    ADD COLUMN `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用' AFTER `update_time`;

-- wx we_category 添加雷达链接类别数据 Tower 任务: 选择素材调整（工时0.6） ( https://tower.im/teams/636204/todos/54349 )
INSERT INTO `we_category`(`id`, `corp_id`, `media_type`, `using`, `name`, create_by, create_time, update_by,
                          update_time, del_flag)
SELECT 14423047601513984 + CEILING(RAND() * 999999),
       a.corp_id,
       7,
       1,
       '雷达链接',
       'admin',
       '2022-07-24 00:00:00',
       '',
       '2022-07-24 00:00:00',
       0
FROM we_corp_account a
WHERE a.del_flag = 0
GROUP BY a.corp_id;

-- silver_chariot 2022-07-25 Tower 任务: 雷达权限功能点补充 ( https://tower.im/teams/636204/todos/54492 )
update sys_menu
set parent_id = 2299
where menu_id = 2301
  and menu_name = '管理企业雷达';
update sys_menu
set parent_id = 2299
where menu_id = 2302
  and menu_name = '管理部门雷达';
update sys_menu set parent_id = 2299 where menu_id = 2301 and menu_name = '管理企业雷达';
update sys_menu set parent_id = 2299 where menu_id = 2302 and menu_name = '管理部门雷达';


-- silver_chariot 2022-07-25  Tower 任务: 增加提供配置公众号参数入口 ( https://tower.im/teams/636204/todos/54556 )
CREATE TABLE `we_open_config`
(
    `corp_id`                     varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `official_account_app_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '公众号appid',
    `official_account_app_secret` varchar(128) NOT NULL DEFAULT '' COMMENT '公众号secret',
    `official_account_domain`     varchar(255) NOT NULL DEFAULT '' COMMENT '公众号域名',
    `create_by`                   varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`                 datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                   varchar(64)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`                 datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='企业公众号配置表';

-- wx we_radar_click_record Tower 任务: 查看客户点击记录报错 ( https://tower.im/teams/636204/todos/54607 )
ALTER TABLE `we_radar_click_record`
    ADD COLUMN `detail` varchar(255) NOT NULL DEFAULT '' COMMENT '详情(如果是员工活码,则为员工活码使用场景，如果是新客进群则为新客进群的活码名称,如果是SOP则为SOP名称，如果是群日历，则为日历名称)' AFTER `channel_name`;