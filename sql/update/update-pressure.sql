-- 2023-12-08 lcy 为客户关系表添加索引 Tower 任务: 创建朋友圈任务失败 ( https://tower.im/teams/636204/todos/79360 )
ALTER TABLE we_flower_customer_rel DROP INDEX idx_corp_user_id, ADD INDEX idx_corp_user_id_status(corp_id, user_id, status) COMMENT '企业id-员工id-客户状态普通索引';
-- 2023-12-13 lcy 为话术库和话术附件表添加索引 Tower 任务: 话术库列表慢查询 ( https://tower.im/teams/636204/todos/79354 )
ALTER TABLE we_words_group ADD INDEX idx_corp_category_sort (corp_id, category_id, sort) COMMENT '企业id-文件夹id-排序普通索引';
ALTER TABLE we_words_detail ADD INDEX idx_corp_group_id (corp_id, group_id) COMMENT '企业id-话术id普通索引';

-- tigger 2023-12-19 Tower 任务: 老客进群慢查询异常 ( https://tower.im/teams/636204/todos/75435 )
ALTER TABLE we_group_code_actual ADD INDEX index_groupCodeId (`group_code_id`,`scan_code_times`) COMMENT '统计活码使用数量分组索引';

-- tigger 2023-12-20 Tower 任务: 群发列表慢查询异常 ( https://tower.im/teams/636204/todos/79107 )
ALTER TABLE we_customer_messageoriginal ADD INDEX index_createTime_corpId (`create_time`, `corp_id`) COMMENT '群发列表检索索引';

-- tigger 2023-12-22 Tower 任务: 会话存档客户检索慢查询 ( https://tower.im/teams/636204/todos/79437 )
ALTER TABLE we_flower_customer_rel ADD INDEX index_groupBy_external_userid(`external_userid`,`create_time`,`user_id`,`corp_id`,`status`) COMMENT '客户去重列表索引覆盖';

-- tigger 2023-12-29 Tower 任务: 客户去重统计接口慢查询 ( https://tower.im/teams/636204/todos/79364 )
ALTER TABLE we_flower_customer_rel ADD INDEX `index_corpId_remark_externalUserId`(`corp_id`, `external_userid`, `remark`) COMMENT '客户备注查询索引';
ALTER TABLE we_customer ADD INDEX `index_corpId_name_externalUserId`(`corp_id`, `name`, `external_userid`) COMMENT '客户昵称查询索引';
ALTER TABLE we_user ADD INDEX `index_corpId_mainDepartment_user_Id` (`corp_id`, `main_department`, `user_id`)  COMMENT '主部门查询索引';
ALTER TABLE we_customer ADD INDEX `index_corpId_birthday` (`corp_id`, `birthday`, `external_userid`)  COMMENT '客户生日索引';