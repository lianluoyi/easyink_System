-- wx 2023-03-06 修改素材表中radar_id字段，改为通用字段存储存储雷达或表单id Tower 任务: 在群发、SOP等应用表单 ( https://tower.im/teams/636204/todos/62452 )
ALTER TABLE `we_material`
    CHANGE COLUMN `radar_id` `extra_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '其他id, 素材类型为雷达时存储雷达id，为智能表单时为存储表单id' AFTER `enable_convert_radar`;

ALTER TABLE `we_msg_tlp_material`
    CHANGE COLUMN `radar_id` `extra_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '其他id, 素材类型为雷达时存储雷达id，为智能表单时为存储表单id' AFTER `sort_no`;

ALTER TABLE `we_words_detail`
    CHANGE COLUMN `radar_id` `extra_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '其他id, 素材类型为雷达时存储雷达id，为智能表单时为存储表单id' AFTER `size`;

ALTER TABLE `we_customer_seedmessage`
    CHANGE COLUMN `radar_id` `extra_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '其他id, 素材类型为雷达时存储雷达id，为智能表单时为存储表单id' AFTER `update_time`;
-- wx 2023-03-06  we_category表添加智能表单链接类别数据 Tower 任务: 在群发、SOP等应用表单 ( https://tower.im/teams/636204/todos/62452 )
INSERT INTO `we_category`(`id`, `corp_id`, `media_type`, `using`, `name`, create_by, create_time, update_by,
                          update_time, del_flag)
SELECT 14423047601513984 + CEILING(RAND() * 999999),
       a.corp_id,
       8,
       1,
       '智能表单',
       'admin',
       '2022-03-06 00:00:00',
       '',
       '2022-03-06 00:00:00',
       0
FROM we_corp_account a
WHERE a.del_flag = 0
GROUP BY a.corp_id;

-- wx 2023-03-09 we_material 素材表中添加两个字段兼容小程序 Tower 任务: 后端配合调整 ( https://tower.im/teams/636204/todos/63671 )
ALTER TABLE `we_material`
    ADD COLUMN `account_original_id` varchar(64) NOT NULL DEFAULT '' COMMENT '小程序账号原始id，小程序专用' AFTER `digest`,
    ADD COLUMN `appid` varchar(64) NOT NULL DEFAULT '' COMMENT '小程序appId，小程序专用' AFTER `account_original_id`;

-- 话术库兼容小程序字段
ALTER TABLE `we_words_detail`
    ADD COLUMN `account_original_id` varchar(64) NOT NULL DEFAULT '' COMMENT '小程序账号原始id，小程序专用' AFTER `cover_url`,
    ADD COLUMN `appid` varchar(64) NOT NULL DEFAULT '' COMMENT '小程序appId，小程序专用' AFTER `account_original_id`;

ALTER TABLE `we_customer_seedmessage`
    ADD COLUMN `account_original_id` varchar(64) NOT NULL DEFAULT '' COMMENT '小程序账号原始id，必须是关联到企业的小程序应用' AFTER `appid`;

-- 2023-03-14 wx 络客侧边栏配置 Tower 任务: 对接络客侧边栏 ( https://tower.im/teams/636204/todos/63301 )
CREATE TABLE `we_lock_sidebar_config` (
                                          `app_id` varchar(32) NOT NULL COMMENT '络客app_id',
                                          `corp_id` varchar(64) NOT NULL COMMENT '企业Id',
                                          `app_secret` varchar(64) NOT NULL DEFAULT '' COMMENT '络客app_secret',
                                          PRIMARY KEY (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='络客侧边栏配置';

-- 2023-03-14 wx 外部联系人externalUserId明密文映射表（代开发应用使用）Tower 任务: 对接络客侧边栏 ( https://tower.im/teams/636204/todos/63301 )
CREATE TABLE `we_external_userid_mapping` (
                                              `corp_id` varchar(64) NOT NULL COMMENT '密文corpId',
                                              `external_userid` varchar(64) NOT NULL COMMENT '明文externalUserId',
                                              `open_external_userid` varchar(64) NOT NULL DEFAULT '' COMMENT '密文externalUserId',
                                              PRIMARY KEY (`corp_id`,`external_userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外部联系人externalUserId明密文映射表';

-- 2023-03-14 wx 员工userId明密文映射表（代开发应用使用） Tower 任务: 对接络客侧边栏 ( https://tower.im/teams/636204/todos/63301 )
CREATE TABLE `we_user_id_mapping` (
                                      `corp_id` varchar(64) NOT NULL COMMENT '密文企业id',
                                      `user_id` varchar(64) NOT NULL COMMENT '明文userId',
                                      `open_user_id` varchar(64) DEFAULT '' COMMENT '密文userId',
                                      PRIMARY KEY (`corp_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工userId明密文映射表';

-- 2023-03-14 wx Tower 任务: 去除络客关联配置表中的corpId字段 ( https://tower.im/teams/636204/todos/64258 )
ALTER TABLE `we_lock_sidebar_config`
DROP COLUMN `corp_id`;


-- 2023-3-16 silver_chariot 客户群活码增加短链字段 Tower 任务: 活码小程序 ( https://tower.im/teams/636204/todos/53069 )
ALTER TABLE `we_group_code`
    ADD COLUMN `app_link`  varchar(128) NOT NULL DEFAULT '' COMMENT '活码短链' AFTER `create_type`;

-- 2023-3-17 wx Tower 任务: easyink配置络客侧边栏的appId与appSecret ( https://tower.im/teams/636204/todos/64397 )
ALTER TABLE `we_lock_sidebar_config`
    ADD `corp_id` varchar(64) NOT NULL COMMENT '企业Id' AFTER app_id,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`app_id`, `corp_id`) USING BTREE;

    -- 2023-3-20 wx 增加api配置菜单 Tower 任务: 络客侧边栏配置可视化 ( https://tower.im/teams/636204/todos/64397 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2319, 'API配置', 2305, 12, 'apiConfig', 'configCenter/apiConfig', 1, 'C', '0', '0', '', '#', '王孙', '2023-03-20 10:35:14', '王孙', '2023-03-20 10:39:07', '');

-- 给以前的管理员操作API配置的权限
INSERT INTO sys_role_menu
(role_id, menu_id)
SELECT role_id,
       2319
FROM sys_role
WHERE role_type = 1;

-- 2023-03-21 wx Tower 任务: 群活码引导语初始化sql异常 ( https://tower.im/teams/636204/todos/64566 )
ALTER TABLE `we_group_code`
    MODIFY COLUMN `guide` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '引导语' AFTER `activity_scene`;