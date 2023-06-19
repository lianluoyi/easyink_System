--  2023-6-5  silver_chariot   Tower 任务: saas后端服务异常 ( https://tower.im/teams/636204/todos/69234 )
ALTER TABLE `we_flower_customer_rel`
    ADD INDEX `idx_corp_status` (`corp_id`, `status`, `create_time`) USING BTREE ;


-- 2023-06-13 lcy 新增account_original_id字段，存储小程序原始ID Tower 任务: 好友欢迎语发送小程序异常 ( https://tower.im/teams/636204/todos/69882 )
ALTER TABLE `we_msg_tlp_material` ADD COLUMN `account_original_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '小程序账号原始id，小程序专用' AFTER `description`;