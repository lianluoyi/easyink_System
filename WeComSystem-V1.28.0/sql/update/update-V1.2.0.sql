-- yiming 2021.9.6 修改群名称字段长度 Tower 任务: 表结构字段长度修改 ( https://tower.im/teams/636204/todos/42292 )
ALTER TABLE `we_group`
    MODIFY COLUMN `group_name` varchar (128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '群聊' COMMENT '群名' AFTER `owner`;

-- yiming 2021.9.6 修改客户地址、邮件字段长度 Tower 任务: 表结构字段长度修改 ( https://tower.im/teams/636204/todos/42292 )
ALTER TABLE `we_flower_customer_rel`
    MODIFY COLUMN `address` varchar (255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户地址' AFTER `qq`,
    MODIFY COLUMN `email` varchar (128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮件' AFTER `address`;

-- yiming 2021.9.6 修改群聊名称长度 Tower 任务: 表结构字段长度修改 ( https://tower.im/teams/636204/todos/42292 )
ALTER TABLE `we_group_code_actual`
    MODIFY COLUMN `chat_group_name` varchar (128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群聊名称' AFTER `scan_code_times_limit`;

-- yiming 20219.6 修改音频时长字段长度 Tower 任务: 表结构字段长度修改 ( https://tower.im/teams/636204/todos/42292 )
ALTER TABLE `we_material`
    MODIFY COLUMN `audio_time` varchar (32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '音频时长' AFTER `del_flag`;

-- yiming 20219.9 修改错误标签名 Tower 任务: 数据表内容拼写错误 ( https://tower.im/teams/636204/todos/42369 )
ALTER TABLE `we_tag_group`
    CHANGE COLUMN `gourp_name` `group_name` varchar (50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签组名' AFTER `group_id`;

-- silver_chariot 2021.9.8 we_user增加 主部门main_department字段 (https://tower.im/teams/636204/todos/42304/)
ALTER TABLE `we_user`
    ADD COLUMN `main_department`  bigint(20) NOT NULL DEFAULT 0 COMMENT '主部门ID' AFTER `wx_account`;

--  yiming 2021.9.9 离职继承重构需要新建表 Tower 任务: 离职继承优化 ( https://tower.im/teams/636204/todos/41989 )
-- ----------------------------
-- Table structure for we_leave_user
-- ----------------------------
DROP TABLE IF EXISTS `we_leave_user`;
CREATE TABLE `we_leave_user`
(
    `id`                   bigint(20) NOT NULL,
    `user_id`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户id',
    `head_image_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '头像地址',
    `user_name`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名称',
    `alias`                varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
    `main_department_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户所属主部门名字',
    `is_allocate`          tinyint(1) NOT NULL DEFAULT 0 COMMENT '离职是否分配(1:已分配;0:未分配;)',
    `dimission_time`       datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '离职时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `user_id`(`user_id`, `dimission_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业员工离职表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_allocate_customer_v2
-- ----------------------------
DROP TABLE IF EXISTS `we_allocate_customer_v2`;
CREATE TABLE `we_allocate_customer_v2`
(
    `id`              bigint(20) NOT NULL,
    `takeover_userid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接替成员的userid',
    `external_userid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '被分配的客户id',
    `handover_userid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原跟进成员的userid',
    `allocate_time`   datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    `customer_name`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人名称',
    `status`          tinyint(4) NOT NULL DEFAULT 2 COMMENT '接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录',
    `dimission_time`  datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '员工离职时间',
    `takeover_time`   datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接替客户的时间，如果是等待接替状态，则为未来的自动接替时间',
    `fail_reason`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '失败原因',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '离职分配的客户列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_allocate_group_v2
-- ----------------------------
DROP TABLE IF EXISTS `we_allocate_group_v2`;
CREATE TABLE `we_allocate_group_v2`
(
    `id`             bigint(20) NOT NULL,
    `chat_id`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分配的群id',
    `new_owner`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '新群主',
    `old_owner`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原群主',
    `status`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '接替状态， 0-未交接 1-接替完毕 2-交接失败',
    `dimission_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '员工离职时间',
    `allocate_time`  datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分配的群组表' ROW_FORMAT = Dynamic;


-- Society my sister Li 2021.09.13 将tip_msg原本数据中超过10字符的数据截取前1-10 Tower任务: https://tower.im/teams/636204/todos/42433/
UPDATE we_group_code SET tip_msg = SUBSTRING( tip_msg, 1, 10 ) WHERE LENGTH( tip_msg ) > 10;

-- Society my sister Li 2021.09.13 修改客户群活码字段长度 Tower任务: https://tower.im/teams/636204/todos/42433/
ALTER TABLE `we_group_code`
    MODIFY COLUMN `activity_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活码名称' AFTER `activity_head_url`,
    MODIFY COLUMN `activity_desc` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活码描述' AFTER `activity_name`,
    MODIFY COLUMN `guide` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '引导语' AFTER `activity_scene`,
    MODIFY COLUMN `tip_msg` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '进群提示语' AFTER `join_group_is_tip`;

-- Society my sister Li 2021.09.13 修改欢迎语welcome_msg字段长度255->4000 Tower 任务: (https://tower.im/teams/636204/todos/42438/)
ALTER TABLE `we_msg_tlp`
    MODIFY COLUMN `welcome_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '欢迎语' AFTER `id`;

-- yiming 2021.9.13 Tower 任务: 后端修改 ( https://tower.im/teams/636204/todos/42452 )
ALTER TABLE `we_emple_code`
    MODIFY COLUMN `welcome_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '欢迎语' AFTER `scenario`;

-- yiming 2021.9.13 Tower 任务: 系统设置——员工 ( https://tower.im/teams/636204/todos/42430 )
ALTER TABLE `we_user`
    MODIFY COLUMN `id_card` char (30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '身份证号' AFTER `enable`;
-- yiming 2021.9.13 Tower 任务: 后端修改 ( https://tower.im/teams/636204/todos/42451 )
ALTER TABLE `we_customer_seedmessage`
    MODIFY COLUMN `link_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链接消息：图文消息标题' AFTER `pic_url`;

-- Society my sister Li 2021.9.14 Tower 任务: 素材库分组名称修改长度限制,超出部分截取 (https://tower.im/teams/636204/todos/42437/)
UPDATE `we_category` SET `name` = SUBSTRING( `name`, 1, 16 ) WHERE LENGTH( `name` ) > 16;
ALTER TABLE `we_category`
    MODIFY COLUMN `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类名称' AFTER `media_type`;

-- Society my sister Li 2021.9.14 Tower 任务: 素材库名称、描述修改长度限制,超出部分截取 (https://tower.im/teams/636204/todos/42437/)
UPDATE `we_material` SET `material_name` = SUBSTRING( `material_name`, 1, 32 ) WHERE LENGTH( `material_name` ) > 32;
UPDATE `we_material` SET `digest` = SUBSTRING( `digest`, 1, 64 ) WHERE LENGTH( `digest` ) > 64;
ALTER TABLE `we_material`
    MODIFY COLUMN `material_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片名称' AFTER `content`,
    MODIFY COLUMN `digest` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '摘要' AFTER `material_name`;

--  yiming 2021.9.13 离职继承数据权限问题 Tower 任务: 部门权限下离职继承数据库异常 ( https://tower.im/teams/636204/todos/42475 )
ALTER TABLE `we_leave_user`
    ADD COLUMN `main_department` bigint(20) NOT NULL DEFAULT '0' COMMENT '主部门id，用于数据权限效验' AFTER `alias`;

-- yiming 2021.9.10 Tower 任务: 群发记录异常 ( https://tower.im/teams/636204/todos/42406 )
ALTER TABLE `we_customer_messagetimetask`
ADD COLUMN `customers_map` longtext NOT NULL COMMENT '客户员工对应map' AFTER `customers_info`;


-- Society my sister Li 2021.9.14 Tower 任务:新客建群创建成功后显示在员工活码中 (https://tower.im/teams/636204/todos/42250/)
ALTER TABLE `we_emple_code`
ADD COLUMN `source` tinyint(4) NOT NULL DEFAULT 0 COMMENT '来源类型：0：活码创建，1：新客建群创建';
--  Society my sister Li 2021.09.14 标签建群.任务名称长度>32字符的,截取1-32 Tower 任务:( https://tower.im/teams/636204/todos/42436/ )
UPDATE we_pres_tag_group SET task_name = SUBSTRING( task_name, 1, 32 ) WHERE LENGTH( task_name ) > 32;

--  Society my sister Li 2021.09.14 标签建群字段长度修改 Tower 任务:( https://tower.im/teams/636204/todos/42436/ )
ALTER TABLE `we_pres_tag_group`
    MODIFY COLUMN `task_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称' AFTER `msgid`,
    MODIFY COLUMN `welcome_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '加群引导语' AFTER `cus_end_time`;

-- yiming 2021.9.15 Tower 任务: 群发记录异常 ( https://tower.im/teams/636204/todos/42406 )
ALTER TABLE `we_customer_messagetimetask`
DROP COLUMN `customers_map`;

-- Tower 任务: 后端 ( https://tower.im/teams/636204/todos/42609 )
ALTER TABLE `we_user`
    MODIFY COLUMN `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮箱' AFTER `mobile`;

-- yiming 2021.9.16 Tower 任务: 会话存档定时任务初始化异常 ( https://tower.im/teams/636204/todos/42686 )
UPDATE sys_job SET cron_expression = '0 */2 * * * ?' WHERE invoke_target = 'ryTask.FinanceTask';
UPDATE qrtz_cron_triggers SET cron_expression = '0 */2 * * * ?' WHERE trigger_name = 'TASK_CLASS_NAME4';


