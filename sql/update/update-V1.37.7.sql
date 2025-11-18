-- tigger 2025-08-4 新增加密字段 Tower 任务: 用户信息安全保护及脱敏 ( https://tower.im/teams/636204/todos/113147 )
alter table we_user
    add column `mobile_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手机号加密' after `mobile`;
alter table we_user
    add column `address_encrypt` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '地址加密' after `address`;

alter table we_flower_customer_rel
    add column `remark_mobiles_encrypt` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此客户备注的手机号码加密' after `remark_mobiles`;
alter table we_flower_customer_rel
    add column `address_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户地址加密' after `address`;

alter table we_batch_tag_task_detail
    add column `import_mobile_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '导入的手机号加密' after import_mobile;

alter table we_map_config
    add column `api_key_encrypt` varchar(255) NOT NULL DEFAULT '' COMMENT 'API密钥加密' after api_key;
alter table we_map_config
    add column `iframe_api_key_encrypt` varchar(255) NOT NULL DEFAULT '' COMMENT '前端iframe用的apiKey加密' after iframe_api_key;

alter table we_customer_extend_property_rel
    add column `detail_address_encrypt` varchar(255) NOT NULL DEFAULT '' COMMENT '位置的详细地址加密' after detail_address;

alter table we_open_config
    add column `official_account_app_secret_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公众号secret,待开发应用为空加密' after official_account_app_secret;

alter table we_corp_account
    add column `corp_secret_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用的密钥凭证加密' after corp_secret;
alter table we_corp_account
    add column `contact_secret_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人密钥加密' after contact_secret;
alter table we_corp_account
    add column `provider_secret_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '服务商密钥加密' after provider_secret;
alter table we_corp_account
    add column `chat_secret_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会话存档密钥加密' after chat_secret;
alter table we_corp_account
    add column `agent_secret_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用密钥加密' after agent_secret;
alter table we_corp_account
    add column `encoding_aes_key_encrypt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用回调aesKey加密' after encoding_aes_key;

-- tigger 2025-08-04 添加权限 Tower 任务: 用户信息安全保护及脱敏 ( https://tower.im/teams/636204/todos/113147 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2339, '安全设置', 2305, 13, 'securityConfig', 'configCenter/securityConfig', 1, 'C', '0', '0', '', '#', 'tigger', '2023-03-20 10:35:14', 'tigger', '2023-03-20 10:39:07', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2340, '查看完整手机号', 2339, 1, '', NULL, 1, 'F', '0', '0', 'securityConfig:view:phone', '#', 'admin', '2025-08-04 17:43:33', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2341, '查看完整地址', 2339, 2, '', NULL, 1, 'F', '0', '0', 'securityConfig:view:address', '#', 'admin', '2025-08-04 17:43:33', '', NULL, '');
-- tigger 管理员默认权限
insert into sys_role_menu (role_id, menu_id)
select role_id, 2339
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2340
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2341
from sys_role
where role_type = 1;


-- tigger 活动轨迹脱敏处理 Tower 任务: 显示动态密文显示优化 ( https://tower.im/teams/636204/todos/114252 )
alter table we_customer_trajectory
    add column `detail_encrypt` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作细节（如果是文件图片则是url,如果多个选项则,隔开)加密' after detail;