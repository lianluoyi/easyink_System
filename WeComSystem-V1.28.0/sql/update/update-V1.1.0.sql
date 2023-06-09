DROP TABLE IF EXISTS `we_poster`;
DROP TABLE IF EXISTS `we_poster_font`;
DROP TABLE IF EXISTS `we_poster_subassembly`;
DROP TABLE IF EXISTS `we_task_fission`;
DROP TABLE IF EXISTS `we_task_fission_complete_record`;
DROP TABLE IF EXISTS `we_task_fission_record`;
DROP TABLE IF EXISTS `we_task_fission_reward`;
DROP TABLE IF EXISTS `we_task_fission_staff`;


-- 新增企微账号-角色关系表
DROP TABLE IF EXISTS `we_user_role`;
CREATE TABLE `we_user_role`
(
`user_id` VARCHAR (64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企微用户ID',
`role_id` BIGINT (20) NOT NULL COMMENT '角色ID',
 PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;


-- 登录记录表新增 登录方式字段
ALTER TABLE `sys_logininfor` ADD COLUMN `login_type`  tinyint(2) NOT NULL DEFAULT 1 COMMENT '登录方式(1账号密码登录,2企微扫码登录)' ;

ALTER TABLE `we_group_member` ADD COLUMN `name` varchar(32) NOT NULL DEFAULT '' COMMENT '成员名称';

-- 添加一个删除时间，得到实时的流失数据
ALTER TABLE `we_flower_customer_rel` ADD COLUMN `delete_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间';

-- 添加注释
ALTER TABLE `we_flower_customer_rel` MODIFY COLUMN `status` char(2) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除流失 2员工删除用户）';

--  修改sys_role 增加部分字段的默认值
ALTER TABLE `sys_role`
    MODIFY COLUMN `role_key`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色权限字符串' AFTER `role_name`,
    MODIFY COLUMN `role_sort`  int(11) NOT NULL DEFAULT 0 COMMENT '显示顺序' AFTER `role_key`,
    MODIFY COLUMN `status`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '角色状态（0正常 1停用）' AFTER `data_scope`;

-- we_user_role增加user_id唯一键
ALTER TABLE `we_user_role` ADD UNIQUE INDEX `un_user_id` (`user_id`) USING BTREE ;

-- 修改素材库名称长度
ALTER TABLE `we_material`
MODIFY COLUMN `material_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片名称' AFTER `content`;
-- 企业微信表增加应用回调url字段
ALTER TABLE `we_corp_account`
  ADD COLUMN `callback_uri` VARCHAR(255) NOT NULL DEFAULT ''
COMMENT '应用回调url'
  AFTER `cert_file_path`;

-- silver_chariot 2021-9-1 修改管理员初始化名字，部门管理员数据权限改成自己部门(Tower 任务: 默认角色命名错误 ( https://tower.im/teams/636204/todos/42140 )
update sys_role set  role_name = '管理员',remark = '管理员' where role_id = 1 ;
update sys_role set role_name = '部门管理员',remark = '部门管理员',data_scope = 3 where role_id = 6;

-- 更新角色名 超级管理员->管理员
update sys_role set role_name = '管理员' WHERE role_id = 1 ;


-- 修改员工邮箱长度
ALTER TABLE `we_user`
MODIFY COLUMN `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮箱' AFTER `mobile`;

-- 新增菜单查看定时任务日志菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2224', '定时任务日志查看', '2', '5', 'job/log', 'monitor/job/log', '1', 'C', '1', '0', '', '#', 'admin', '2021-09-01 13:41:15', 'admin', '2021-09-01 13:42:13', '');


-- 修改创建活码注释
ALTER TABLE `we_emple_code` MODIFY COLUMN `code_type` tinyint(4) NOT NULL DEFAULT '2' COMMENT '活码类型:1:单人;2:多人;3:批量';

-- 增加活码表 欢迎语字段长度
ALTER TABLE `we_emple_code`
    MODIFY COLUMN `welcome_msg`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '欢迎语' AFTER `scenario`;




