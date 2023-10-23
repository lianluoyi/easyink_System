-- 2023-09-20 lcy 添加客户总数字段,修改原客户总数字段描述为留存客户数 Tower 任务: 增加客户总数统计数 ( https://tower.im/teams/636204/todos/72041 )
ALTER TABLE `we_user_behavior_data` ADD COLUMN `total_all_contact_cnt` INT (11) NOT NULL DEFAULT '0' COMMENT '客户总数(由每日定时任务统计，不去重，首页和数据统计共用)，【首页】：在职员工在we_flower_customer_rel表中，客户关系status != 2的客户数量 + 系统上记录的已离职的员工在we_flower_customer_rel表中，客户关系status = 3的客户数量。【数据统计】：在职员工在we_flower_customer_rel表中，客户关系status != 2的客户数量。' AFTER `negative_feedback_cnt`;
ALTER TABLE `we_user_behavior_data` MODIFY COLUMN `total_contact_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '留存客户总数(由每日定时任务统计，去重)';
-- 2023-09-21 lcy 添加员工删除客户时间字段,修改原流失时间的描述为客户删除员工时间 Tower 任务: 增加客户总数统计数 ( https://tower.im/teams/636204/todos/72041 )
ALTER TABLE `we_flower_customer_rel` ADD COLUMN `del_by_user_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '员工删除客户时间（如果没有收到删除回调，通过手动同步客户得出的主动删除客户，该字段为默认值）' AFTER `delete_time`;
-- 2023-09-26 lcy 修改客户关系表status字段描述，we_user_behavior_data表创建唯一索引 Tower 任务: 数据统计中客户总数旧数据兼容 ( https://tower.im/teams/636204/todos/75549 )
ALTER TABLE `we_flower_customer_rel` MODIFY COLUMN `status` char(2) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除流失 2员工删除用户 3待继承 4转接中）';
CREATE UNIQUE INDEX `uni_corp_user_id_stat_time_idx` ON we_user_behavior_data (`corp_id`,`user_id`,`stat_time`) COMMENT '企业ID-员工ID-时间唯一索引';
-- 2023-09-26 lcy 修改delete_time的默认值 Tower 任务: 增加客户总数统计数 ( https://tower.im/teams/636204/todos/72041 )
ALTER TABLE `we_flower_customer_rel` MODIFY COLUMN `delete_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '客户删除员工时间';