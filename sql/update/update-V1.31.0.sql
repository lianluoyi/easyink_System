-- 2023-07-18 lichaoyu 删除每小时定时任务，修改为每日活码初始化数据任务，在每天 00:00:00 执行一次 Tower 任务: 今日活码数据实时更新优化 ( https://tower.im/teams/636204/todos/71849 )
UPDATE `sys_job` SET `job_name` = '每日活码初始化数据任务',`invoke_target` = 'EmpleStatisticInitTask.getEmpleStatisticData()', `cron_expression` = '0 0 0 * * ?' WHERE `job_id` = 23;
-- 2023-07-19 lichaoyu 修改活码统计表备注 Tower 任务: 今日活码数据实时更新优化 ( https://tower.im/teams/636204/todos/71849 )
ALTER TABLE `we_emple_code_statistic` COMMENT='活码数据统计表-每天凌晨3点更新前一天的数据';
-- silver_chariot 2023-7-18  Tower 任务: #P1#开放API能力 ( https://tower.im/teams/636204/todos/70338 )
CREATE TABLE `app_id_info` (
                               `corp_id` varchar(64) NOT NULL DEFAULT '0' COMMENT '企业ID',
                               `app_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'app_id',
                               `app_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'app_secret',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='开放API配置表';
CREATE TABLE `app_callback_setting` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                        `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                        `callback_url` varchar(512) NOT NULL DEFAULT '' COMMENT '回调地址',
                                        `token` varchar(128) NOT NULL DEFAULT '' COMMENT 'token',
                                        `encoding_aes_key` varchar(255) NOT NULL DEFAULT '' COMMENT '用于加密解密的aesKey',
                                        PRIMARY KEY (`id`),
                                        KEY `uniq_corp_url` (`corp_id`,`callback_url`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='API-消息订阅配置表';
-- 2023-07-19 lichaoyu 删除多余的定时任务 Tower 任务: 首页客户总数趋势图异常 ( https://tower.im/teams/636204/todos/72158 )
DELETE FROM sys_job WHERE `job_id` IN ('25','26','27');

