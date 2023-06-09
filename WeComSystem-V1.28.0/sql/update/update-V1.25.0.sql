
-- 2023-03-23 lcy we_corp_account 企业表中添加客户流失标签开关 Tower 任务: 为流失客户自动打标签 ( https://tower.im/teams/636204/todos/64183 )
ALTER TABLE `we_corp_account`
	ADD COLUMN `customer_loss_tag_switch` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '客户流失标签开关 0:关闭 1:开启';

-- 2023-03-23 lcy 流失标签记录表 Tower 任务: 为流失客户自动打标签 ( https://tower.im/teams/636204/todos/64183 )
CREATE TABLE `we_customer_loss_tag` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                        `loss_tag_id` varchar(64) NOT NULL DEFAULT '' COMMENT '流失标签id',
                                        `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
                                        PRIMARY KEY (`id`),
                                        KEY `index_corpid_losstagid` (`corp_id`,`loss_tag_id`) USING BTREE COMMENT '普通索引index_corpid_losstagid'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2023-03-24 lcy 流失提醒开关权限更改为流失设置 Tower 任务: 为流失客户自动打标签 ( https://tower.im/teams/636204/todos/64183 )
UPDATE sys_menu SET menu_name = '流失设置', perms = 'wechat:corp:loss:setting' WHERE menu_id = 2086


-- 2023-04-03 silver_chariot 修改sys_short_url_mapping 表的字符集  Tower 任务: sit环境生成小程序链接失败 ( https://tower.im/teams/636204/todos/65321 )
ALTER TABLE `sys_short_url_mapping`
    MODIFY COLUMN `create_by`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人' AFTER `create_time`,
    DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci;
