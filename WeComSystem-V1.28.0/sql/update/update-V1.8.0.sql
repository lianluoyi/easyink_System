-- 1*+ 46885

CREATE TABLE `order_user_to_order_account` (
  `id`              INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT '主键',
  `user_id`         VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '企业员工ID',
  `corp_id`         VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '企业ID',
  `network_id`      VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '网点ID',
  `order_user_id`   VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '工单帐号ID',
  `order_user_name` VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '工单帐号名',
  `bind_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user` (`user_id`, `corp_id`) USING BTREE,
  UNIQUE KEY `uniq_order_user` (`network_id`, `order_user_id`) USING BTREE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='企业员工与工单帐号绑定关系表';

CREATE TABLE `we_my_application_use_scope` (
  `my_application_id` INT(11)      NOT NULL
  COMMENT 'we_my_application表的ID',
  `type`              TINYINT(1)   NOT NULL DEFAULT '0'
  COMMENT '使用类型(1指定员工，2指定角色)',
  `val`               VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '指定员工存userId,指定角色存角色ID',
  PRIMARY KEY (`my_application_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='我的应用使用范围';

ALTER TABLE `we_application_center`
  ADD COLUMN `development_type` TINYINT(1) NOT NULL DEFAULT 1
COMMENT '开发类型（1自研，2三方）'
  AFTER `create_time`,
  ADD COLUMN `sidebar_redirect_url` VARCHAR(512) NOT NULL DEFAULT ''
COMMENT '侧边栏url:自研存储相对路径，三方开发存储完整url'
  AFTER `development_type`,
  ADD COLUMN `application_entrance_url` VARCHAR(512) NOT NULL DEFAULT ''
COMMENT '应用入口url'
  AFTER `sidebar_redirect_url`;

CREATE TABLE `order_group_to_order_customer` (
  `id`                  INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT '主键',
  `chat_id`             VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '外部群ID',
  `corp_id`             VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '企业ID',
  `network_id`          VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '网点ID',
  `order_customer_id`   VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '工单客户ID',
  `order_customer_name` VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '工单客户名',
  `bind_time`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '绑定时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_chat` (`chat_id`, `corp_id`) USING BTREE,
  UNIQUE KEY `uniq_customer` (`network_id`, `order_customer_id`) USING BTREE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='企业客户群与工单客户绑定关系';

INSERT INTO `we_application_center` VALUES (2, '壹鸽快递工单助手应用', '',
                                               'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/11/03/应用中心-企微Plus.jpeg',
                                               1, '', '', '', 1, '2021-12-20 09:59:03', 1, '', '');

ALTER TABLE `we_my_application_use_scope`
  CHANGE COLUMN `my_application_id` `id` INT(11) NOT NULL AUTO_INCREMENT
COMMENT 'ID'
  FIRST,
  ADD COLUMN `corp_id` VARCHAR(64) NOT NULL DEFAULT ''
COMMENT '企业ID'
  AFTER `id`,
  ADD COLUMN `app_id` INT(0) NOT NULL DEFAULT -1
COMMENT '应用ID'
  AFTER `corp_id`,
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`id`) USING BTREE;

ALTER TABLE `we_my_application_use_scope`
  CHANGE COLUMN `app_id` `appid` INT(11) NOT NULL DEFAULT -1
COMMENT '应用ID'
  AFTER `corp_id`;

-- 1*+ 46885 2021-12-16 补充壹鸽快递工单功能介绍、使用说明、咨询服务
UPDATE `we_application_center`
SET `description`      = '企微群聊自动分析反馈内容形成工单。自动根据工单状态变更回复工单内容。',
  `introduction`       = '群聊关联客户：企微群聊关联工单系统客户关系对应；\r\n自动获取工单信息：根据NLP自动获取信息，形成工单；\r\n自动回复工单信息：工单状态变更，自动群聊@工单反馈人员，自动告知状态；\r\n工单报表：自动发送当日工单报表；',
  `instructions`       = '使用该应用需进行初始化操作，进行工单系统注册、客服人员账号注册、客户资料维护；若您有疑问请扫描以下二维码，添加客服，咨询初始化操作事宜',
  `consulting_service` = 'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/12/16/壹鸽咨询服务.png'
WHERE `appid` = 2 AND `name` = '壹鸽快递工单助手应用';


-- yiming 46885
ALTER TABLE `order_group_to_order_customer`
DROP INDEX `uniq_customer`,
ADD UNIQUE INDEX `uniq_customer`(`network_id`, `order_customer_id`, `chat_id`) USING BTREE;

UPDATE `we_application_center`
SET `application_entrance_url` = 'http://121.37.253.126'
WHERE `appid` = 2 AND `name` = '壹鸽快递工单助手应用';

UPDATE `we_application_center`
SET `sidebar_redirect_url` = 'workSheetAssistant'
WHERE `appid` = 2 AND `name` = '壹鸽快递工单助手应用';

UPDATE `we_application_center`
SET `logo_url` = ''
WHERE `appid` = 2 AND `name` = '壹鸽快递工单助手应用';

UPDATE `we_application_center`
SET `name` = '壹鸽快递工单助手'
WHERE `appid` = 2 AND `name` = '壹鸽快递工单助手应用';

-- yiming Tower 任务: 多企业绑定工单账号异常 ( https://tower.im/teams/636204/todos/47532 )
ALTER TABLE `order_user_to_order_account`
DROP INDEX `uniq_order_user`,
ADD UNIQUE INDEX `uniq_order_user`(`network_id`, `order_user_id`, `corp_id`, `user_id`) USING BTREE;

-- yiming Tower 任务: 企微plus功能说明存储到数据库 ( https://tower.im/teams/636204/todos/47538 )
UPDATE `we_application_center`
SET
  `introduction`       = '企微号聚合管理：多个企微号同时管理，无需多个客户端切换，提高服务能效，降低人工成本。\r\n辅助营销：一键批量定时群发，突破限制，促活拉新，优化营销效果。\r\n智能AI：智能机器人，自动通过好友、关键词自动回复、固定时间段自动回复、问答库，快速响应，给客户最佳的服务体验。\r\n企业风控：历史消息留存，设置敏感词监管，保证服务标准化、专业化，避免客户流失及不当营销',
  `instructions`       = '使用该应用需要安装系统服务，若您未安装，请联系客服咨询开通事宜。'
WHERE `appid` = 1 AND `name` = '企微Plus';

-- yiming Tower 任务: 工单助手应用信息修改 ( https://tower.im/teams/636204/todos/47533 )
UPDATE `we_application_center`
SET `description`      = '企微群聊自动分析反馈内容形成工单，自动根据工单状态变更回复工单内容。'
WHERE `appid` = 2 AND `name` = '壹鸽快递工单助手';
