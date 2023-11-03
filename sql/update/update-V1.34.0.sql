-- 2023-10-12 lcy 修改we_emple_code的source来源字段描述 Tower 任务: 活码表关于source属性的描述错误 ( https://tower.im/teams/636204/todos/76047 )
ALTER TABLE we_emple_code MODIFY COLUMN `source` tinyint(4) NOT NULL DEFAULT '0' COMMENT '来源类型：0：活码创建，1：新客建群创建，2：获客链接创建';
-- 2023-10-10 lcy 为we_emple_code表添加删除状态-来源联合索引 Tower 任务: #P1#活码列表加载缓慢 ( https://tower.im/teams/636204/todos/74016 )
ALTER TABLE we_emple_code ADD INDEX `idx_del_flag_source` (`del_flag`,`source`) COMMENT '删除状态-来源联合索引';
-- 2023-10-24 lcy 为we_flower_customer_rel表添加企业ID-添加时间索引 Tower 任务: 后端支持分页 ( https://tower.im/teams/636204/todos/76545 )
ALTER TABLE we_flower_customer_rel ADD INDEX `idx_corp_create_time` (`corp_id`, `create_time`) COMMENT '企业ID-添加时间普通索引';
-- 2023-10-24 lcy 为we_user表添加user_id索引 Tower 任务: 后端支持分页 ( https://tower.im/teams/636204/todos/76545 )
ALTER TABLE we_user ADD INDEX `idx_user_id` (`user_id`) COMMENT '员工ID普通索引';
-- 2023-10-26 lcy 新增we_msg_tlp_filter_rule欢迎语筛选条件表 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/76687 )
DROP TABLE IF EXISTS `we_msg_tlp_filter_rule`;
CREATE TABLE `we_msg_tlp_filter_rule`
(
    `id`               bigint(20) NOT NULL COMMENT '主键id',
    `msg_tlp_id`       bigint(20) NOT NULL DEFAULT '0' COMMENT '欢迎语模板id',
    `filter_type`      tinyint(2) NOT NULL DEFAULT '0' COMMENT '筛选类型，0：来源；1：性别',
    `filter_condition` tinyint(2) NOT NULL DEFAULT '0' COMMENT '筛选条件，0：不是；1：是',
    `filter_value`     varchar(50)CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '筛选值，来源见：AddWayEnum类，性别见：性别见：WeConstans.corpUserEnum，为空表示所有',
    PRIMARY KEY (`id`),
    KEY                `idx_tlp_id` (`msg_tlp_id`) COMMENT '欢迎语id普通索引'
) ENGINE=InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT='欢迎语筛选条件表';
-- 2023-10-26 lcy 欢迎语模板表新增exist_filter_flag过滤条件存在标识字段，multi_filter_association多个筛选条件间的关联字段 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/76687 )
ALTER TABLE `we_msg_tlp` ADD COLUMN `exist_filter_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否存在有过滤条件（存在则关联we_msg_tlp_filter表）0：不存在，1：存在' AFTER `exist_special_flag`;
ALTER TABLE `we_msg_tlp` ADD COLUMN `multi_filter_association` tinyint(2) NOT NULL DEFAULT '0' COMMENT '多个筛选条件间的关联，0：或；1：且（仅存在过滤条件有效）' AFTER `exist_filter_flag`;
-- 2023-10-26 lcy 更新原来指定为utf8字符集的表字符集为utf8mb4，排序规则为utf8mb4_general_ci Tower 任务: 雷达点击记录缺失部分客户记录 ( https://tower.im/teams/636204/todos/74307 )
ALTER TABLE we_customer_extend_property_rel CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE we_auto_tag_user_rel CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE we_radar CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE we_radar_tag_rel CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE we_radar_channel CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE we_radar_click_record CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE we_open_config CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE app_id_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE app_callback_setting CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- 2023-10-30 lcy 为we_user_customer_message_statistics表添加索引 Tower 任务: 数据统计日期维度接口优化 ( https://tower.im/teams/636204/todos/77121 )
ALTER TABLE we_user_customer_message_statistics ADD INDEX `idx_corp_external_id_send_time` (`corp_id`,`external_userid`,`user_id`,`send_time`) COMMENT '企业ID、客户ID、发送时间普通索引';