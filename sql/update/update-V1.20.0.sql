-- 2023-01-06 silver_chariot  修改群员表的索引 Tower 任务: 线上环境部分接口响应过慢 ( https://tower.im/teams/636204/todos/55908 )
ALTER TABLE `we_group_member`
ADD INDEX `idx_chat_id` (`chat_id`) USING BTREE ;

-- 2023-01-06 silver_chariot  修改客户员工关系表的索引 Tower 任务: 线上环境部分接口响应过慢 ( https://tower.im/teams/636204/todos/55908 )
ALTER TABLE `we_flower_customer_rel`
ADD INDEX `idx_corp_user_id` (`corp_id`, `user_id`) USING BTREE ;