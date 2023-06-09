-- 2023-05-25 lcy 为标签和标签组表创建索引 Tower 任务: 标签统计接口响应优化 ( https://tower.im/teams/636204/todos/68516 )
ALTER TABLE `we_tag` ADD INDEX `status_corpid` (`status`,`corp_id`);
ALTER TABLE `we_tag_group` ADD INDEX `status_corpid` (`status`,`corp_id`);
-- 2023-05-30 lcy 修改sys_oper_log表json_result字段类型为text
ALTER TABLE sys_oper_log MODIFY COLUMN json_result text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '返回参数';
