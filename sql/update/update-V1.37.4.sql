-- tigger 2025-04-29 Tower 任务: 专属活码支持设置位置 ( https://tower.im/teams/636204/todos/107420 )
CREATE TABLE `we_map_config` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID，空字符串表示系统默认配置',
                                 `api_key` varchar(255) NOT NULL DEFAULT '' COMMENT 'API密钥',
                                 `iframe_api_key` varchar(255) NOT NULL DEFAULT '' COMMENT '前端iframe用的apiKey',
                                 `map_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '地图类型：1-腾讯地图, 2-高德地图, 3-百度地图',
                                 `daily_limit` varchar(255) NOT NULL DEFAULT '[]' COMMENT '每日调用限制次数，小于0 ,则表示无限制',
                                 `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0：停用 1：启用',
                                 `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                                 `create_time` datetime NOT NULL COMMENT '创建时间',
                                 `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
                                 `update_time` datetime NOT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `idx_unique` (`corp_id`,`map_type`) COMMENT '唯一键',
                                 KEY `idx_corp_id` (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图API配置表';

ALTER TABLE we_customer_extend_property ADD COLUMN `extra` varchar(255) NOT NULL DEFAULT '' COMMENT '额外扩展字段, 根据type来存储json的格式';

ALTER TABLE we_customer_extend_property_rel ADD COLUMN `province` varchar(32) NOT NULL DEFAULT '' COMMENT '第一级行政区, 省' after property_value;
ALTER TABLE we_customer_extend_property_rel ADD COLUMN `city` varchar(32) NOT NULL DEFAULT '' COMMENT '第二级行政区, 市' after province;
ALTER TABLE we_customer_extend_property_rel ADD COLUMN `area` varchar(32) NOT NULL DEFAULT '' COMMENT '第三级行政区, 区/县' after city;
ALTER TABLE we_customer_extend_property_rel ADD COLUMN `town` varchar(32) NOT NULL DEFAULT '' COMMENT '第四级行政区, 街道/镇' after area;
ALTER TABLE we_customer_extend_property_rel ADD COLUMN `detail_address` varchar(128) NOT NULL DEFAULT '' COMMENT '位置的详细地址' after town;


-- tigger Tower 任务: 后端 ( https://tower.im/teams/636204/todos/108946 )
CREATE TABLE `we_customer_temp_emple_code_select_tag_scope` (
                                                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                                `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID',
                                                                `origin_emple_code_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '原员工活码id',
                                                                `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '选择标签详情json',
                                                                `value` varchar(255) NOT NULL DEFAULT '',
                                                                `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人(后续员工专属活码存储员工id)',
                                                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                                PRIMARY KEY (`id`) USING BTREE,
                                                                KEY `idx_corpId_state` (`corp_id`),
                                                                KEY `idx_emple_code_id` (`origin_emple_code_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户专属员工活码选择标签范围表';