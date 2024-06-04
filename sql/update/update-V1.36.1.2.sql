-- 2024-05-07 Tower 任务: 修改与SQL命令关键词冲突字段 ( https://tower.im/teams/636204/todos/89441 )
-- we_emple_code_statistic
ALTER TABLE we_emple_code_statistic CHANGE `time` `statistics_time` date NOT NULL COMMENT '日期';
-- we_emple_code_analyse
ALTER TABLE we_emple_code_analyse CHANGE `time` `add_code_time` date        NOT NULL COMMENT 'type为1时是添加时间，type为0时是流失时间';
-- we_customer_messageoriginal
ALTER TABLE we_customer_messageoriginal CHANGE `group` `group_id` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群组名称id';
-- we_chat_side
ALTER TABLE we_chat_side CHANGE `using` `use_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用 0 启用 1 未启用';
-- we_category
ALTER TABLE we_category CHANGE `using` `use_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用到侧边栏(0否，1是)';