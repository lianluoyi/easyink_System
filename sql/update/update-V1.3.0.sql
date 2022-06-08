-- 1*+ 2021.09.09 兼容三方应用增加服务商配置表 Tower 任务: 兼容第三方应用的改造 ( https://tower.im/teams/636204/todos/42274 )
DROP TABLE IF EXISTS `we_provider_account`;
CREATE TABLE `we_provider_account` (
  `corp_id`          VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '服务商企业ID',
  `provider_secret`  VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '服务商企业Secret',
  `suite_id`         VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '三方应用ID',
  `suite_secret`     VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '三方应用Secret',
  `token`            VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '三方应用token',
  `encoding_aes_key` VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '三方应用aeskey',
  PRIMARY KEY (`corp_id`, `suite_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='企业微信服务商配置表';


-- 1*+ 2021.09.09 兼容三方应用增加服务商配置表 Tower 任务: 兼容第三方应用的改造 ( https://tower.im/teams/636204/todos/42274 )
DROP TABLE IF EXISTS `we_auth_corp_info`;
CREATE TABLE `we_auth_corp_info` (
  `corp_id`              VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '授权企业ID',
  `suite_id`             VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '第三方应用的SuiteId',
  `permanent_code`       VARCHAR(512) NOT NULL DEFAULT ''
  COMMENT '企业微信永久授权码,最长为512字节',
  `corp_name`            VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权方企业名称，即企业简称',
  `corp_type`            VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '授权方企业类型，认证号：verified, 注册号：unverified',
  `corp_square_logo_url` VARCHAR(512) NOT NULL DEFAULT ''
  COMMENT '授权方企业方形头像',
  `corp_user_max`        INT(11)      NOT NULL DEFAULT '0'
  COMMENT '授权方企业用户规模',
  `corp_agent_max`       INT(11)      NOT NULL DEFAULT '0'
  COMMENT '授权方企业应用数上限',
  `corp_full_name`       VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权方企业的主体名称(仅认证或验证过的企业有)，即企业全称。',
  `subject_type`         TINYINT(2)   NOT NULL DEFAULT '0'
  COMMENT '企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号',
  `verified_end_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '认证到期时间',
  `corp_wxqrcode`        VARCHAR(512) NOT NULL DEFAULT ''
  COMMENT '授权企业在微工作台（原企业号）的二维码，可用于关注微工作台',
  `corp_scale`           VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '企业规模。当企业未设置该属性时，值为空',
  `corp_industry`        VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '企业所属行业。当企业未设置该属性时，值为空',
  `corp_sub_industry`    VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '企业所属子行业。当企业未设置该属性时，值为空',
  `cancel_auth`          TINYINT(1)   NOT NULL DEFAULT '0'
  COMMENT '取消授权(0N1Y)',
  PRIMARY KEY (`corp_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='授权企业信息表';

-- 1*+ 2021.09.09 兼容三方应用增加服务商配置表 Tower 任务: 兼容第三方应用的改造 ( https://tower.im/teams/636204/todos/42274 )
DROP TABLE IF EXISTS `we_auth_corp_info_extend`;
CREATE TABLE `we_auth_corp_info_extend` (
  `corp_id`                    VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '授权企业ID',
  `suite_id`                   VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '第三方应用的SuiteId',
  `agentid`                    VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '授权方应用id',
  `name`                       VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权方应用名字',
  `square_logo_url`            VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权方应用方形头像',
  `round_logo_url`             VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权方应用圆形头像',
  `auth_mode`                  TINYINT(2)   NOT NULL DEFAULT '-1'
  COMMENT '授权模式，0为管理员授权；1为成员授权',
  `is_customized_app`          TINYINT(1)   NOT NULL DEFAULT '0'
  COMMENT '是否为代开发自建应用(0N1Y)',
  `auth_user_info_userid`      VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '授权管理员的userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）',
  `auth_user_info_open_userid` VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '授权管理员的open_userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）',
  `auth_user_info_name`        VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权管理员的name，可能为空',
  `auth_user_info_avatar`            VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '授权管理员的头像url，可能为空',
  `dealer_corp_info_corpid`          VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '代理服务商企业微信id',
  `dealer_corp_info_corp_name`       VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '代理服务商企业微信名称',
  `register_code_info_register_code` VARCHAR(512) NOT NULL DEFAULT ''
  COMMENT '注册码 , 最长为512个字节',
  `register_code_info_template_id`   VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '推广包ID，最长为128个字节',
  `register_code_info_state`         VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '用户自定义的状态值。只支持英文字母和数字，最长为128字节。',
  PRIMARY KEY (`corp_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='授权企业信息扩展表';

-- honghai 2021.09.24 修改敏感行为记录表操作人/被操作人信息字段长度 Tower 任务: 消息审计/敏感行为/敏感行为记录缺失字段 ( https://tower.im/teams/636204/todos/43101 )
ALTER TABLE `we_sensitive_act_hit`
  MODIFY COLUMN `operator` VARCHAR(512) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '敏感行为操作人信息'
  AFTER `operator_id`;

ALTER TABLE `we_sensitive_act_hit`
  MODIFY COLUMN `operate_target` VARCHAR(512) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '敏感行为操作对象信息'
  AFTER `operate_target_id`;

-- honghai 2021.09.24  Tower 任务: 敏感行为管理类型名称修改 ( https://tower.im/teams/636204/todos/43182 )
UPDATE we_sensitive_act
SET act_name = '拉黑/删除客户'
WHERE act_name = '拉黑/删除好友';

UPDATE we_sensitive_act
SET act_name = '互发红包'
WHERE act_name = '发红包';

UPDATE we_sensitive_act
SET act_name = '员工发送名片'
WHERE act_name = '发名片';

-- yiming 2021.9.18 多租户改造 Tower 任务: 表字段增加 ( https://tower.im/teams/636204/todos/42742 )
-- 修改客户表
ALTER TABLE `we_customer`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业ID' AFTER `external_userid`;
-- 修改客户轨迹表
ALTER TABLE `we_customer_trajectory`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `agent_id`;
-- 修改客户关系表
ALTER TABLE `we_flower_customer_rel`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `external_userid`;
-- 修改离职员工表
ALTER TABLE `we_leave_user`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `user_id`;
-- 修改标签组表
ALTER TABLE `we_tag_group`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `group_id`;
-- 修改群聊表
ALTER TABLE `we_group`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `owner`;

-- honghai 2021.9.18 会话存档模块多租户改造 Tower 任务: 表字段增加 ( https://tower.im/teams/636204/todos/42714/ )
ALTER TABLE `we_chat_contact_mapping`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `is_custom`;

ALTER TABLE `we_sensitive`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `update_time`;

ALTER TABLE `we_sensitive_act`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `update_time`;

ALTER TABLE `we_sensitive_act_hit`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `update_time`;

ALTER TABLE `we_sensitive_audit_scope`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `audit_scope_name`;
-- yytofo 2021.9.18 Tower 任务: 首页统计 ( https://tower.im/teams/636204/todos/42843 )
-- 修改联系客户统计数据
ALTER TABLE `we_user_behavior_data`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业ID' AFTER `id`;

-- 修改群聊数据统计表
ALTER TABLE `we_group_statistic`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '授权企业ID' AFTER `id`;

-- My society sisterLi 2021.09.18 欢迎语增加corpId字段 Tower 任务: 兼容第三方应用改造 ( https://tower.im/teams/636204/todos/42810/ )
ALTER TABLE `we_msg_tlp`
    ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID' AFTER `id`;

-- My society sisterLi 2021.9.18 员工活码增加corp_id字段 Tower 任务: 表结构字段长度修改 ( https://tower.im/teams/636204/todos/42722/ )
ALTER TABLE `we_emple_code`
    ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID' AFTER `id`;
-- yiming 2021.9.17 Tower 任务: 运营中心 ( https://tower.im/teams/636204/todos/42710 )
ALTER TABLE `we_customer_messageoriginal`
ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID' AFTER `message_original_Id`;

-- yiming 2021.9.17 多租户修改  Tower 任务: 群活码 ( https://tower.im/teams/636204/todos/42723 )
ALTER TABLE `we_group_code`
ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业Id' AFTER `id`;
-- yiming 2021.9.18 多租户 Tower 任务: 新客建群 ( https://tower.im/teams/636204/todos/42727 )
ALTER TABLE `we_community_new_group`
ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业Id' AFTER `id`;
-- yiming 2021.9.22 增加索引 Tower 任务: 群发 ( https://tower.im/teams/636204/todos/42725 )
ALTER TABLE `we_customer_messgaeresult`
ADD INDEX `idx_message_id`(`message_id`) USING BTREE COMMENT '消息表id';


-- silver_chariot 2021.09.18 成员表增加corp_id字段 Tower 任务: 多租户模式改造 ( https://tower.im/teams/636204/todos/42289 )
ALTER TABLE `we_user`
    ADD COLUMN `corp_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公司ID' FIRST ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`corp_id`, `user_id`),
    ADD INDEX `idx_is_activate` (`is_activate`) USING BTREE ;


-- silver_chariot 2021.09.18 部门表增加corp_id字段并与原id构成主键  Tower 任务: 多租户模式改造 ( https://tower.im/teams/636204/todos/42289 )
ALTER TABLE `we_department`
    MODIFY COLUMN `id`  bigint(20) NOT NULL ,
    ADD COLUMN `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '公司ID' FIRST ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`corp_id`, `id`);

-- yiming 2021.9.18 多租户改造 Tower 任务: 表字段增加 ( https://tower.im/teams/636204/todos/42742 )
ALTER TABLE `we_allocate_customer_v2`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `handover_userid`;
ALTER TABLE `we_allocate_group_v2`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `chat_id`;


-- silver_chariot 2021.9.22 多租户：角色表改造 Tower 任务: 角色列表和导出角色 ( https://tower.im/teams/636204/todos/42789 )
ALTER TABLE `sys_role`
    ADD COLUMN `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '公司id' AFTER `role_id`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`role_id`);
-- silver_chariot 2021.9.22 多租户：角色表增加corp_id的索引
ALTER TABLE `sys_role`
    ADD INDEX `idx_corp_id` (`corp_id`) USING BTREE ;

-- silver_chariot 2021.09.22 角色表增加角色类型字段
ALTER TABLE `sys_role`
    ADD COLUMN `role_type`  tinyint(4) NOT NULL DEFAULT 3 COMMENT '角色类型（1：系统默认超级管理员角色, 2:系统默认角色,3:自定义角色)' AFTER `remark`;

-- silver_chariot 2021.09.22 多租户：给以前的角色数据初始化corp_id
UPDATE sys_role
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account  WHERE status = 0 AND del_flag = 0  LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;
-- silver_chariot 2021.09.22 多租户：给以前的部门数据初始化corp_id
UPDATE we_department
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account  WHERE status = 0 AND del_flag = 0  LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;
-- silver_chariot 2021.09.22 多租户：给以前的员工数据初始化corp_id
UPDATE we_user
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account  WHERE status = 0 AND del_flag = 0   LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;

-- silver_chariot 2021.09.22 角色类型 默认值改为3 自定义
ALTER TABLE `sys_role`
    MODIFY COLUMN `role_type`  tinyint(4) NOT NULL DEFAULT 3 COMMENT '角色类型（1：系统默认超级管理员角色, 2:系统默认角色,3:自定义角色)' AFTER `remark`;
-- silver_chariot 2021.09.22 修改默认角色的角色类型
update sys_role set role_type = 1 where role_key = 'admin';
update sys_role set role_type = 2 where role_key = 'depart' or role_key = 'employee';


-- yiming 2021.9.23 多租户：群聊表改造 Tower 任务: 群聊模块修改 ( https://tower.im/teams/636204/todos/42745 )
ALTER TABLE `we_group_member`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id' AFTER `chat_id`;


-- My society sisterLi 2021.09.22 标签建群增加corpId字段 Tower 任务: 兼容第三方应用改造 (https://tower.im/teams/636204/todos/42726/)
ALTER TABLE `we_pres_tag_group`
    ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID' AFTER `task_id`;

-- My society sisterLi 2021.09.18 素材库增加corpId字段 Tower 任务: 兼容第三方应用改造 ( https://tower.im/teams/636204/todos/42724/ )
ALTER TABLE `we_category`
    ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID' AFTER `id`;
ALTER TABLE `we_chat_item`
    ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID' AFTER `item_id`;
ALTER TABLE `we_chat_side`
    ADD COLUMN `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID' AFTER `side_id`;

-- silver_chariot 2021.9.23 登录记录表增加corp_id Tower 任务: 登录记录 ( https://tower.im/teams/636204/todos/42795 )
ALTER TABLE `sys_logininfor`
    ADD COLUMN `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID' AFTER `info_id`,
    ADD INDEX `idx_corp_id` (`corp_id`) USING BTREE ;

-- silver_chariot 2021.9.23 给登录记录表初始化corp_id Tower 任务: 登录记录 ( https://tower.im/teams/636204/todos/42795 )
UPDATE sys_logininfor
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;

-- silver_chariot 2021.9.24 给操作记录表增加 corp_id Tower 任务: 系统操作记录 ( https://tower.im/teams/636204/todos/42796 )
ALTER TABLE `sys_oper_log`
    ADD COLUMN `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '公司ID' AFTER `oper_id`,
    ADD INDEX `idx_corp_id` (`corp_id`) USING BTREE ;

-- silver_chariot 2021.9.24 给操作记录表初始化corp_id  Tower 任务: 系统操作记录 ( https://tower.im/teams/636204/todos/42796 )
UPDATE sys_oper_log
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;

-- silver_chariot 2021.9.24 多租户改造：给we_user_role表增加corp_id Tower 任务: 企业成员的增删改查 ( https://tower.im/teams/636204/todos/42800 )
ALTER TABLE `we_user_role`
    ADD COLUMN `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '公司ID' FIRST ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`corp_id`, `user_id`, `role_id`),
    DROP INDEX `un_user_id` ,
    ADD UNIQUE INDEX `un_corp_user_id` (`corp_id`, `user_id`) USING BTREE ;

-- silver_chariot 2021.9.24 给we_user_role之前数据初始化corp_id的值  Tower 任务: 企业成员的增删改查 ( https://tower.im/teams/636204/todos/42800 )
UPDATE we_user_role
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;

-- My society sisterLi 2021.9.26 欢迎语we_msg_tlp增加数据初始化 Tower 任务: 运营中心-数据初始化 (https://tower.im/teams/636204/todos/43271/)
UPDATE we_msg_tlp
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;


-- yiming 2021.9.23 多租户改造标签 Tower 任务: 标签模块修改 ( https://tower.im/teams/636204/todos/42744 )
-- 修改客户表
ALTER TABLE `we_tag`
    ADD COLUMN `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业ID' AFTER `group_id`;

-- yiming 2021.9.26 多租户改造修改
ALTER TABLE `we_customer`
DROP PRIMARY KEY,
ADD PRIMARY KEY (`external_userid`, `corp_id`) USING BTREE;

ALTER TABLE `we_flower_customer_rel`
DROP INDEX `un_user_external_userid`,
ADD UNIQUE INDEX `un_user_external_userid_corpid`(`external_userid`, `user_id`, `corp_id`) USING BTREE;

ALTER TABLE `we_leave_user`
DROP INDEX `user_id`,
ADD UNIQUE INDEX `user_id`(`user_id`, `dimission_time`, `corp_id`) USING BTREE;

ALTER TABLE `we_group_member`
DROP INDEX `un_member` ,
ADD UNIQUE INDEX `un_member` (`user_id`, `chat_id`, `corp_id`) USING BTREE ;

-- 客户表
UPDATE we_customer
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 客户轨迹表
UPDATE we_customer_trajectory
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 客户关系表 we_flower_customer_rel
UPDATE we_flower_customer_rel
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 群聊表 we_group
UPDATE we_group
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 群成员表
UPDATE we_group_member
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 离职员工表
UPDATE we_leave_user
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 离职员工客户表
UPDATE we_allocate_customer_v2
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 离职员工群聊表
UPDATE we_allocate_group_v2
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;
-- 标签组表
UPDATE we_tag_group
SET corp_id =
        IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
        corp_id = '' OR corp_id IS NULL;

-- yiming 2021.9.26 初始化群发消息 原始数据信息表 corpId Tower 任务: 群发 ( https://tower.im/teams/636204/todos/43267 )
UPDATE we_customer_messageoriginal
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;

-- yiming 2021.9.26 初始化群活码CorpId Tower 任务: 群活码 ( https://tower.im/teams/636204/todos/43266 )
UPDATE we_group_code
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;

-- yiming 2021.9.26 初始化新客建群
UPDATE we_community_new_group
SET corp_id =
IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE
corp_id = '' OR corp_id IS NULL;


-- My society sisterLi 2021.09.26 员工活码增加初始化数据 Tower 任务: 运营中心-员工活码-初始化 (https://tower.im/teams/636204/todos/43265/)

UPDATE we_emple_code
SET corp_id = IFNULL((SELECT corp_id FROM we_corp_account WHERE STATUS = 0 AND del_flag = 0 LIMIT 1),'')
WHERE corp_id = '' OR corp_id IS NULL;


-- My society sisterLi 2021.09.26 给以前的素材库相关数据初始化corp_id. Tower 任务: 运营中心-素材库-数据初始化 (https://tower.im/teams/636204/todos/43270/)
UPDATE we_category
SET corp_id = IFNULL((SELECT corp_id FROM we_corp_account WHERE STATUS = 0 AND del_flag = 0 LIMIT 1),'')
WHERE corp_id = '' OR corp_id IS NULL;

UPDATE we_chat_item
SET corp_id =  IFNULL((SELECT corp_id FROM we_corp_account WHERE STATUS = 0 AND del_flag = 0 LIMIT 1),'')
WHERE corp_id = '' OR corp_id IS NULL;

UPDATE we_chat_side
SET corp_id = IFNULL((SELECT corp_id FROM we_corp_account WHERE STATUS = 0 AND del_flag = 0 LIMIT 1),'')
WHERE corp_id = '' OR corp_id IS NULL;

-- My society sisterLi 2021.09.26  给以前的标签建群数据初始化corp_id. Tower 任务: 运营中心-标签建群-数据初始化 (https://tower.im/teams/636204/todos/43269/)
UPDATE we_pres_tag_group
SET corp_id = IFNULL( ((SELECT  corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1 )),'')
WHERE corp_id = '' OR corp_id IS NULL;

-- silver_chariot 2021.09.28 自定义角色关联部门表增加corp_id Tower 任务: 第三方应用数据权限异常 ( https://tower.im/teams/636204/todos/43515 )
ALTER TABLE `sys_role_dept`
    ADD COLUMN `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '公司Id' AFTER `role_id`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`role_id`, `corp_id`, `dept_id`);
-- silver_chariot  2021.09.28  给以前的 sys_role_dept初始化corp_id
UPDATE sys_role_dept
SET corp_id =  IFNULL((SELECT corp_id FROM we_corp_account WHERE status = 0 AND del_flag = 0 LIMIT 1),'')
WHERE corp_id = '' OR corp_id IS NULL;

-- silver_chariot 2021-09.29  去除不必要的菜单项 Tower 任务: 图片取消下载和复制菜单权限 ( https://tower.im/teams/636204/todos/43649 )
delete from sys_menu where menu_id in (2145,2203,2149,2204,2209);
delete from sys_role_menu where menu_id in (2145,2203,2149,2204,2209);
