-- tigger 2024-12-11 会话统计索引修改 Tower 任务: mysql的CPU异常占用 ( https://tower.im/teams/636204/todos/101137 )
ALTER TABLE we_user_customer_message_statistics DROP INDEX `idx_corp_id_user_id`;
ALTER TABLE we_user_customer_message_statistics ADD INDEX  `idx_corp_id_user_id` (`corp_id`,`user_id`, `send_time`) USING BTREE COMMENT '员工id索引';