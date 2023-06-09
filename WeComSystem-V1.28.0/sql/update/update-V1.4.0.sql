-- yiming 2021.10.11 Tower 任务: 表结构修改 ( https://tower.im/teams/636204/todos/43799 )
ALTER TABLE `we_material`
ADD COLUMN `expire_time` datetime(0) NOT NULL DEFAULT '2099-01-01 00:00:00' COMMENT '过期时间' AFTER `audio_time`,
ADD COLUMN `show_material` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否发布到侧边栏（0否，1是）' AFTER `expire_time`;
DELETE FROM we_material WHERE del_flag = 1;
ALTER TABLE `we_material` DROP COLUMN `del_flag`;
-- ----------------------------
-- Table structure for we_material_tag
-- ----------------------------
CREATE TABLE `we_material_tag`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id',
  `tag_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '素材标签表' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for we_material_tag_rel
-- ----------------------------
CREATE TABLE `we_material_tag_rel`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(100) NOT NULL DEFAULT 0 COMMENT '素材id',
  `material_tag_id` int(10) NOT NULL DEFAULT 0 COMMENT '素材标签id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_material_tag`(`material_id`, `material_tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '素材标签关联表' ROW_FORMAT = Dynamic;

-- yiming 2021.10.11 修改素材分类表 Tower 任务: 表结构修改 ( https://tower.im/teams/636204/todos/43799 )
ALTER TABLE `we_category`
DROP COLUMN `parent_id`,
MODIFY COLUMN `media_type` int(1) NOT NULL DEFAULT 0 COMMENT '0 海报、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 、5图文链接、6小程序' AFTER `corp_id`,
ADD COLUMN `using` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用到侧边栏(0否，1是)' AFTER `media_type`;

-- yiming 2021.10.11 更新已发布的素材 https://tower.im/teams/636204/todos/43799
UPDATE we_material wm INNER JOIN we_chat_item wci ON wm.id = wci.material_id
SET wm.show_material = 1;

-- yiming 2021.10.11 新增企业素材过期配置表 https://tower.im/teams/636204/todos/43799
CREATE TABLE `we_material_config`  (
  `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id',
  `del_days` int(3) NOT NULL DEFAULT 0 COMMENT '自动删除天数',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '过期后是否自动删除（0否，1是）',
  PRIMARY KEY (`corp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业素材配置表' ROW_FORMAT = Dynamic;


-- My society Sister Li 2021.10.11 Tower 任务:素材类型导航栏调整-图片名称改为海报 (https://tower.im/teams/636204/todos/44112/)
UPDATE we_category SET `name` = '海报' WHERE `media_type` = 0 AND `del_flag` = 0;

-- My society Sister Li 2021.10.11 Tower 任务:素材类型导航栏调整-删除文本类型 (https://tower.im/teams/636204/todos/44112/)
UPDATE we_category SET del_flag = 1 WHERE media_type = 4;

-- My society Sister Li 2021.10.11 Tower 任务:素材类型导航栏调整-增加链接和小程序 (https://tower.im/teams/636204/todos/44112/)
INSERT INTO `we_category`(`id`,`corp_id`,`media_type`,`using`,`name`,create_by,create_time,update_by,update_time,del_flag)
SELECT  14412404968937267 + CEILING(RAND()*9999),a.corp_id,5,1,'链接','admin','2021-10-12 00:00:00','','2021-10-12 00:00:00',0 FROM we_corp_account a WHERE a.del_flag = 0;


INSERT INTO `we_category`(`id`,`corp_id`,`media_type`,`using`,`name`,create_by,create_time,update_by,update_time,del_flag)
SELECT  14423047601513984 + CEILING(RAND()*9999),a.corp_id,6,1,'小程序','admin','2021-10-12 00:00:00','','2021-10-12 00:00:00',0 FROM we_corp_account a WHERE a.del_flag = 0;

-- My society Sister Li 2021.10.13 Tower 任务:过期素材定时任务 (https://tower.im/teams/636204/todos/44132/)
INSERT INTO `sys_job` VALUES (10, '删除过期素材', 'SYSTEM', 'RemoveMaterialTask.removeExpireMaterial()', '0 0 4 * * ? ', '1', '1', '0', 'admin', '2021-10-13 00:00:00', 'admin', '2021-10-13 00:00:00', '');


-- silver_chariot 2021-10-13 删除 添加分组 权限项 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_menu where menu_id = 2068 and menu_name = '添加分组';
-- silver_chariot 2021-10-13  删除 编辑分组 权限项 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_menu where menu_id = 2070 and menu_name = '编辑分组';
-- silver_chariot 2021-10-13  删除 删除分组 权限项 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_menu where menu_id = 2069 and menu_name = '删除分组';
-- silver_chariot 2021-10-13  删除 更换分组 权限项 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_menu where menu_id = 2077  and menu_name = '更换分组';
-- silver_chariot 2021-10-13  删除对应关系表数据 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_role_menu where menu_id in (2068,2070,2069,2077);
-- silver_chariot 2021-10-13  删除侧边工具栏菜单项 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_menu where menu_id = 2162 and menu_name = '编辑分类';
delete from sys_menu where menu_id = 2202  and menu_name = '发布素材';
delete from sys_menu where menu_id = 2091 and menu_name = '侧边工具栏';
delete from sys_role_menu where menu_id in (2162,2202,2091);
-- silver_chariot 2021-10-13  修改发布素材的权限标识 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
update sys_menu set perms = 'wechat:material:publish'
where menu_id = 2076 and menu_name = '发布素材';
-- silver_chariot 2021-10-13  添加菜单项 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete from sys_menu where menu_id in(2226,2227,2228);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2226', '侧边栏展示开关', '2189', '1', '', NULL, '1', 'F', '0', '0', 'wechat:material:sidebar', '#', 'admin', '2021-10-13 17:34:02', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2227', '管理过期素材', '2189', '2', '', NULL, '1', 'F', '0', '0', 'wechat:material:expireManage', '#', 'admin', '2021-10-13 17:34:35', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('2228', '管理素材标签', '2189', '3', '', NULL, '1', 'F', '0', '0', 'wechat:material:tagManage', '#', 'admin', '2021-10-13 17:35:24', '', NULL, '');
-- silver_chariot 2021-10-13  给所有系统管理员添加 新增的菜单权限 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
insert into sys_role_menu (role_id,menu_id)
select role_id,2226 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2227 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2228 from sys_role where role_type = 1 ;

-- silver_chariot 2021-10-13  删除非管理员的素材库权限  Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44274 )
delete sys_role_menu from sys_role_menu left join sys_role on sys_role_menu.role_id = sys_role.role_id  where sys_role_menu.menu_id in (2189,2071,2072,2073,2076,2189) and sys_role.role_type != 1;


-- yiming 22021.10.13 Tower 任务: 修改群发消息内容保存 ( https://tower.im/teams/636204/todos/44310 )
ALTER TABLE `we_customer_seedmessage`
ADD COLUMN `pic_name` varchar(128) NOT NULL DEFAULT '' COMMENT '图片标题' AFTER `content`,
ADD COLUMN `file_name` varchar(128) NOT NULL DEFAULT '' COMMENT '文件名称' AFTER `pic_url`,
ADD COLUMN `file_url` varchar(255) NOT NULL DEFAULT '' COMMENT '文件url' AFTER `file_name`;
ALTER TABLE `we_customer_seedmessage`
ADD COLUMN `video_name` varchar(128) NOT NULL DEFAULT '' COMMENT '视频标题' AFTER `content`,
ADD COLUMN `video_url` varchar(255) NOT NULL DEFAULT '' COMMENT '视频url' AFTER `video_name`;
ALTER TABLE `we_customer_messageoriginal`
MODIFY COLUMN `message_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息 用逗号隔开' AFTER `push_type`;
ALTER TABLE `we_customer_message`
DROP COLUMN `external_userid`;

-- yiming 22021.10.13 Tower 任务: 群发相关接口调整 ( https://tower.im/teams/636204/todos/44133 )
ALTER TABLE `we_customer_seedmessage`
ADD COLUMN `message_type` varchar(32) NOT NULL DEFAULT '' COMMENT '消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息 ' AFTER `appid`;
ALTER TABLE `we_customer_seedmessage` DROP COLUMN `id`;
ALTER TABLE `we_customer_seedmessage`
MODIFY COLUMN `message_id` bigint(64) NOT NULL DEFAULT 0 COMMENT '微信消息表id' AFTER `seed_message_id`,
MODIFY COLUMN `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人' AFTER `create_time`;
-- yiming 22021.10.18 旧数据兼容 Tower 任务: 群发相关接口调整 ( https://tower.im/teams/636204/todos/44133 )
UPDATE we_customer_seedmessage
SET message_type = '0'
WHERE
	pic_url != '' AND message_type = '';

insert into we_customer_seedmessage(seed_message_id, message_id,media_id,message_type,content) select 14235586902749388 + CEILING(RAND()*9999), message_id,media_id,'4',content from we_customer_seedmessage WHERE content != '' AND pic_url != '';


-- 1*+ 2021-10-18 Tower 任务: 应用中心增加企微PRO入口 ( https://tower.im/teams/636204/todos/43796 )
CREATE TABLE `we_application_center` (
  `appid`              INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT '应用ID',
  `name`               VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '应用名',
  `description`        VARCHAR(512) NOT NULL DEFAULT ''
  COMMENT '应用描述',
  `logo_url`           VARCHAR(512) NOT NULL DEFAULT ''
  COMMENT '应用头像',
  `type`               TINYINT(2)   NOT NULL DEFAULT '0'
  COMMENT '应用类型(1:企业工具，2:客户资源，3:内容资源)',
  `introduction`       LONGTEXT     NOT NULL
  COMMENT '功能介绍',
  `instructions`       LONGTEXT     NOT NULL
  COMMENT '使用说明',
  `consulting_service` LONGTEXT     NOT NULL
  COMMENT '咨询服务',
  `enable`             TINYINT(1)   NOT NULL DEFAULT '1'
  COMMENT '启用(ON1Y)',
  `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '上架时间',
  PRIMARY KEY (`appid`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COMMENT ='应用中心表';

INSERT INTO `we_application_center` VALUES (1, '企微PRO', '企微号聚合管理，批量群发，智能问答，消息监管，帮助企业及时高效触发客户，为企业私域运营赋能.',
                                            'https://free-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/19/企业微信PRo.jpg',
                                            1, '', '', '', 1, '2021-10-18 09:59:03');


-- 1*+ 2021-10-18 Tower 任务: 应用中心增加企微PRO入口 ( https://tower.im/teams/636204/todos/43796 )
CREATE TABLE `we_my_application` (
  `id`           INT(11)     NOT NULL AUTO_INCREMENT
  COMMENT 'ID',
  `corp_id`      VARCHAR(64) NOT NULL DEFAULT ''
  COMMENT '企业ID',
  `appid`        INT(11)     NOT NULL DEFAULT '-1'
  COMMENT '应用ID',
  `config`       LONGTEXT    NOT NULL
  COMMENT '应用配置',
  `enable`       TINYINT(1)  NOT NULL DEFAULT '1'
  COMMENT '启用(ON1Y)',
  `install_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '安装时间',
  `expire_time`  DATETIME    NOT NULL DEFAULT '2099-01-01 00:00:00'
  COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_corpid_appid` (`corp_id`, `appid`) USING BTREE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COMMENT ='我的应用';
-- yiming 2021.9.30 Tower 任务: 离职员工分配业务场景改造 ( https://tower.im/teams/636204/todos/43581 )
ALTER TABLE `we_allocate_customer_v2`
    ADD COLUMN `allocate_result` tinyint(1) NOT NULL DEFAULT 0 COMMENT '交接结果,0-失败，1-成功' AFTER `fail_reason`;

ALTER TABLE `we_allocate_group_v2`
    ADD COLUMN `allocate_result` tinyint(1) NOT NULL DEFAULT 0 COMMENT '交接结果,0-失败，1-成功';

ALTER TABLE `we_allocate_group_v2`
    ADD COLUMN `fail_reason` varchar(64) NOT NULL DEFAULT '' COMMENT '失败原因';

ALTER TABLE `we_allocate_group_v2`
    MODIFY COLUMN `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '接替状态：1 - 跟进人离职;2 - 离职继承中;3 - 离职继承完成;' AFTER `old_owner`;

-- yiming 2021-10-18 创建人旧数据修改 Tower 任务: 新客建群返回创建人部门 ( https://tower.im/teams/636204/todos/44381 )
UPDATE
we_community_new_group wcng
INNER JOIN we_user wu ON wu.user_name = wcng.create_by AND wu.corp_id = wcng.corp_id
SET wcng.create_by = wu.user_id;

-- silver_chariot 2021-10-18 插入应用管理相关新菜单 Tower 任务: 菜单权限 ( https://tower.im/teams/636204/todos/44358 )

delete from sys_menu where menu_id in(2229,2230,2231,2232,2233,2234,2235);
INSERT INTO `sys_menu` VALUES ('2229', '应用管理', '0', '20', 'appManage', NULL, '1', 'M', '0', '0', '', 'ApplicationManagement', 'admin', '2021-10-18 17:34:09', 'admin', '2021-10-18 17:37:22', '');
INSERT INTO `sys_menu` VALUES ('2230', '我的应用', '2229', '1', 'myApp', 'appManage/myApp/index', '1', 'C', '0', '0', '', '#', 'admin', '2021-10-18 17:41:24', 'admin', '2021-10-18 17:42:42', '');
INSERT INTO `sys_menu` VALUES ('2231', '应用设置', '2230', '1', '', NULL, '1', 'F', '0', '0', 'wecom:myApplication:update', '#', 'admin', '2021-10-18 17:48:21', 'admin', '2021-10-18 17:49:44', '');
INSERT INTO `sys_menu` VALUES ('2232', '应用中心', '2229', '2', 'appCenter', 'appManage/appCenter/index', '1', 'C', '0', '0', NULL, '#', 'admin', '2021-10-18 17:49:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES ('2233', '添加应用', '2232', '1', '', NULL, '1', 'F', '0', '0', 'wecom:application:install', '#', 'admin', '2021-10-18 17:49:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES ('2234', '应用设置页面', '2230', '30', 'appConfig', 'appManage/appConfig/index', '1', 'P', '0', '0', '', '#', 'admin', '2021-10-18 20:24:52', 'admin', '2021-10-18 20:35:00', '');
INSERT INTO `sys_menu` VALUES ('2235', '应用详情页面', '2232', '79', 'appDetails', 'appManage/appDetails/index', '1', 'P', '0', '0', '', '#', 'admin', '2021-10-18 21:40:20', 'admin', '2021-10-18 21:42:45', '');


-- silver_chariot 2021-10-18 为所有超级管理员角色增加应用管理菜单 Tower 任务: 菜单权限 ( https://tower.im/teams/636204/todos/44358 )
insert into sys_role_menu (role_id,menu_id)
select role_id,2229 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2230 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2231 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2232 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2233 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2234 from sys_role where role_type = 1 ;
insert into sys_role_menu (role_id,menu_id)
select role_id,2235 from sys_role where role_type = 1 ;

-- silver_chariot 2021-10-20 修改页面的父菜单  Tower 任务: 菜单权限 ( https://tower.im/teams/636204/todos/44358 )
update sys_menu set parent_id = 2229
where menu_id in (2234,2235) and menu_name in ('应用设置页面','应用详情页面');


-- yiming 2021.10.19 Tower 任务: 客户中心群数量与数据概览客户群数量不一致 ( https://tower.im/teams/636204/todos/43496 )
ALTER TABLE `we_allocate_customer_v2`
    ADD COLUMN `remark` varchar(100) NOT NULL DEFAULT '' COMMENT '原跟进成员对客户的备注';

ALTER TABLE `we_allocate_customer_v2`
    ADD COLUMN `add_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '原跟进人添加客户时间';

ALTER TABLE `we_allocate_customer_v2`
    ADD COLUMN `description` tinytext NOT NULL COMMENT '该成员对此外部联系人的描述';

ALTER TABLE `we_allocate_customer_v2`
    ADD COLUMN `remark_mobiles` varchar(255) NOT NULL DEFAULT '' COMMENT '该成员对此客户备注的手机号码';

-- silver_chariot 2021-10-21 设置隐藏 Tower 任务: 应用中心页面没有隐藏异常 ( https://tower.im/teams/636204/todos/44645 )
update sys_menu set visible = 1
where menu_id in (2234,2235) and menu_name in ('应用设置页面','应用详情页面');


-- My society Sister Li 2021.10.21 Tower任务: 为企业初始化素材库 (https://tower.im/teams/636204/todos/44580/)
-- 海报
INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955634012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/企微Pro宣传海报.jpg' AS material_url,'490496' AS content,'企微Pro宣传海报.jpg' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+1 AS create_time,'system' AS update_by,SYSDATE()+1 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 0 	AND wca.del_flag = 0 	AND wc.del_flag = 0;

INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955634012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/联络易宣传海报.jpg' AS material_url,'582656' AS content,'联络易宣传海报.jpg' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+2 AS create_time,'system' AS update_by,SYSDATE()+2 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM	we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 0 	AND wca.del_flag = 0 	AND wc.del_flag = 0;

INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955634012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easyWeCom宣传海报.jpg' AS material_url,'369664' AS content,'easyWeCom宣传海报.jpg' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+3 AS create_time,'system' AS update_by,SYSDATE()+3 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM	we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 0 	AND wca.del_flag = 0 	AND wc.del_flag = 0;

-- 视频
INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955734012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/中秋福利.mp4' AS material_url,'8713216' AS content,'中秋福利.mp4' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+1 AS create_time,'system' AS update_by,SYSDATE()+1 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 2 	AND wca.del_flag = 0 	AND wc.del_flag = 0;

INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955734012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/被研发抓住做测试小白是什么体验.mp4' AS material_url,'9373696' AS content,'被研发抓住做测试小白是什么体验.mp4' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+2 AS create_time,'system' AS update_by,SYSDATE()+2 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM	we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 2 	AND wca.del_flag = 0 	AND wc.del_flag = 0;

-- 文件
INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955834012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easyWeCom注册使用服务表.doc' AS material_url,'58368' AS content,'easyWeCom注册使用服务表.doc' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+1 AS create_time,'system' AS update_by,SYSDATE()+1 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 3 	AND wca.del_flag = 0 	AND wc.del_flag = 0;

INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955834012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easyWeCom功能列表.xlsx' AS material_url,'14336' AS content,'easyWeCom功能列表.xlsx' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+2 AS create_time,'system' AS update_by,SYSDATE()+2 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM	we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 3 AND wca.del_flag = 0 AND wc.del_flag = 0;

INSERT INTO we_material (id,category_id,material_url,content,material_name,digest,cover_url,create_by,create_time,update_by,update_time,audio_time,expire_time,show_material )
SELECT 14506653955834012 + CEILING( RAND()* 9999 ) AS id,wc.id AS category_id,'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/easywecom产品介绍.pdf' AS material_url,'2799161' AS content,'easywecom产品介绍.pdf' AS material_name,'' AS digest,'' AS cover_url,'system' AS create_by,SYSDATE()+3 AS create_time,'system' AS update_by,SYSDATE()+2 AS update_time,'' AS audio_time,'2099-01-01 00:00:00' AS expire_time,0 AS show_material FROM	we_category wc	LEFT JOIN we_corp_account wca ON wca.corp_id = wc.corp_id WHERE	wc.media_type = 3 AND wca.del_flag = 0 AND wc.del_flag = 0;

-- 修改素材到同一个目录下 yiming 2021/10/25 Tower 任务: 侧边栏素材库分类显示异常 ( https://tower.im/teams/636204/todos/44757 )
UPDATE we_material wm
LEFT JOIN we_category wc ON wm.category_id = wc.id
LEFT JOIN (
	SELECT
        wc2.id,
        wc2.corp_id,
        wc2.media_type
    FROM we_category wc2
             LEFT JOIN we_material wm2 ON wm2.category_id = wc2.id
    GROUP BY wc2.corp_id, wc2.media_type
    ) AS t ON t.corp_id = wc.corp_id AND t.media_type = wc.media_type
SET wm.category_id = t.id;

-- 删除多余的侧边栏 yiming 2021/10/25 Tower 任务: 侧边栏素材库分类显示异常 ( https://tower.im/teams/636204/todos/44757 )
DELETE wc
FROM we_category wc
         LEFT JOIN (
    SELECT wc2.id,
           wc2.corp_id,
           wc2.media_type
    FROM we_category wc2
             LEFT JOIN we_material wm2 ON wm2.category_id = wc2.id
    GROUP BY wc2.corp_id, wc2.media_type
) AS t ON t.id = wc.id
WHERE t.id is null;

-- 重命名名称 yiming 2021/10/25 Tower 任务: 侧边栏素材库分类显示异常 ( https://tower.im/teams/636204/todos/44757 )
UPDATE we_category
SET `name` = '海报'
WHERE media_type = 0;
	UPDATE we_category
	SET `name` = '视频' WHERE media_type = 2;
	UPDATE we_category
	SET `name` = '文件' WHERE media_type = 3;
	UPDATE we_category
	SET `name` = '链接' WHERE media_type = 5;
	UPDATE we_category
	SET `name` = '小程序' WHERE media_type = 6;

-- yiming 2021/10/25 Tower 任务: 侧边栏素材库分类显示异常 ( https://tower.im/teams/636204/todos/44757 )
ALTER TABLE `we_category`
ADD UNIQUE INDEX `uidx`(`corp_id`, `media_type`) USING BTREE;

-- 1*+ 2021/10/27 Tower 任务: 修改企微pro介绍与图片 ( https://tower.im/teams/636204/todos/44935 )
UPDATE we_application_center
SET `description` = '企微号聚合管理，批量群发，智能问答，消息监管，帮助企业及时高效触发客户，为企业私域运营赋能。',
  `logo_url`      = 'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/10/27/企业微信PRo.jpg'
WHERE `name` = '企微PRO';

-- silver_chariot 2021/10/27  分配离职员工记录表增加唯一键 Tower 任务: 已分配员工数据异常 ( https://tower.im/teams/636204/todos/44971 )
ALTER TABLE `we_allocate_customer_v2`
    ADD UNIQUE INDEX `un_allocate_customer` (`handover_userid`, `external_userid`, `corp_id`, `dimission_time`) USING BTREE ;
-- 1*+ 2021/10/27 修复客户群公告字段过短
ALTER TABLE `we_group`
  MODIFY COLUMN `notice` VARCHAR(2048) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '群公告'
  AFTER `create_time`;

