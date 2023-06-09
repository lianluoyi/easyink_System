-- 2021-02-09 silver_chariot 取消群发详情页面的权限标识,只保留其按钮的权限标识 Tower 任务: 群发记录详情权限异常 ( https://tower.im/teams/636204/todos/48279 )
UPDATE sys_menu
SET perms = ''
WHERE menu_id = 2105
  AND menu_name = '查看群发详情'
  AND menu_type = 'P';

-- 2021-02-09 1*+ Tower 任务: 素材库链接素材支持默认信息 ( https://tower.im/teams/636204/todos/48021 )
ALTER TABLE `we_material`
  ADD COLUMN `is_defined` TINYINT(1) NOT NULL DEFAULT 0
COMMENT '链接时使用(0:默认,1:自定义)'
  AFTER `temp_flag`;

ALTER TABLE `we_customer_seedmessage`
  ADD COLUMN `is_defined` TINYINT(1) NOT NULL
COMMENT '链接消息：图文消息数据来源(0:默认,1:自定义)'
  AFTER `lin_desc`;

-- 2021-02-15 tigger Tower 任务: 自动拉群 ( https://tower.im/teams/636204/todos/48672 )
ALTER TABLE `we_group_code`
    ADD COLUMN `create_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '创建类型 1:群二维码 2: 企微活码'
    AFTER `seq`;

ALTER TABLE `we_group_code_actual`
    ADD COLUMN `chat_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '群ids，逗号分割'
    AFTER `create_time`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `scene` tinyint(2) NOT NULL DEFAULT '2' COMMENT '场景。1 - 群的小程序插件 2 - 群的二维码插件'
    AFTER `chat_ids`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `remark` varchar(30) NOT NULL DEFAULT '' COMMENT '备注信息'
    AFTER `scene`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `room_base_id` int(11) NOT NULL DEFAULT '1' COMMENT '起始序号'
    AFTER `remark`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `room_base_name` varchar(40) NOT NULL DEFAULT '' COMMENT '群名前缀'
    AFTER `room_base_id`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `auto_create_room` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否自动新建群。0-否；1-是。 默认为1'
    AFTER `room_base_name`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `state` varchar(30) NOT NULL DEFAULT '' COMMENT '企业自定义的state参数，用于区分不同的入群渠道'
    AFTER `auto_create_room`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `config_id` varchar(64) NOT NULL DEFAULT '' COMMENT '加群配置id'
    AFTER `state`;
ALTER TABLE `we_group_code_actual`
    ADD COLUMN `sort_no` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序字段'
    AFTER `config_id`;
update `we_group_code`
set create_type = 1
where create_type = 0;

