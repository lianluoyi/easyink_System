-- 2022-04-07 silver_chariot 客户关系表 增加来源的视频号字段wecaht_channel Tower 任务: 客户来源是视频号时展示视频号名称 ( https://tower.im/teams/636204/todos/50410 )
ALTER TABLE `we_flower_customer_rel`
    ADD COLUMN `wechat_channel` varchar(64) NOT NULL DEFAULT '' COMMENT '该成员添加此客户的来源add_way为10时，对应的视频号信息' AFTER `delete_time`;



