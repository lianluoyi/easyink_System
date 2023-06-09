-- silver_chariot 2021-10-19 we_user增加主题颜色字段 Tower 任务: 每个账号支持自定义主题皮肤 ( https://tower.im/teams/636204/todos/43302 )
ALTER TABLE `we_user`
    ADD COLUMN `ui_color`  varchar(32) NOT NULL DEFAULT '#6BB4AB' COMMENT '后台界面主题颜色';

-- silver_chariot 2021-10-19 sys_user增加主题颜色字段 Tower 任务: 每个账号支持自定义主题皮肤 ( https://tower.im/teams/636204/todos/43302 )
ALTER TABLE `sys_user`
    ADD COLUMN `ui_color`  varchar(32) NOT NULL DEFAULT '#6BB4AB' COMMENT '后台界面主题颜色';

-- My society Sister Li 2021-10-23 Tower 任务: 话术库SQL初始化 ( https://tower.im/teams/636204/todos/44661/ )
CREATE TABLE `we_words_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '父分组（0为根节点）',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '话术类型（0：企业话术，1：部门话术，2：我的话术）',
  `use_range` varchar(255) NOT NULL DEFAULT '' COMMENT '使用范围（企业话术：存入根部门1，部门话术：部门id用逗号隔开，我的话术：员工id）',
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '文件夹名称',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '文件夹排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corpid_type_name` (`corp_id`,`type`,`name`) USING BTREE COMMENT '同一类型下的文件夹名不能重名'
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='话术库文件夹表';

CREATE TABLE `we_words_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `category_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件夹ID',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '话术标题',
  `seq` longtext NOT NULL COMMENT '附件ID用逗号隔开，从左往右表示先后顺序',
  `is_push` tinyint(1) NOT NULL COMMENT '是否推送到应用（0：不推送，1推送）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术库主表';

CREATE TABLE `we_words_detail` (
  `id`         BIGINT(20)    NOT NULL AUTO_INCREMENT
  COMMENT '话术库附件ID',
  `corp_id`    VARCHAR(64)   NOT NULL DEFAULT ''
  COMMENT '企业ID',
  `group_id`   BIGINT(20)    NOT NULL DEFAULT 0
  COMMENT '话术主表ID',
  `media_type` INT(1)        NOT NULL DEFAULT '0'
  COMMENT '话术类型(0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序)',
  `content`    VARCHAR(1500) NOT NULL DEFAULT ''
  COMMENT '话术详情',
  `title`      VARCHAR(64)   NOT NULL DEFAULT ''
  COMMENT '标题',
  `url`        VARCHAR(255)  NOT NULL DEFAULT ''
  COMMENT '链接地址',
  `cover_url`  VARCHAR(255)  NOT NULL DEFAULT ''
  COMMENT '封面',
  `is_defined` TINYINT(1)    NOT NULL DEFAULT '0'
  COMMENT '链接时使用(0:默认,1:自定义)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术库附件表';


-- silver_chariot 2021-10-29 去除离职继承页面的权限标识  Tower 任务: 离职继承权限异常 ( https://tower.im/teams/636204/todos/44984 )
UPDATE sys_menu SET
perms = ''
WHERE menu_id in (2028,2051) AND menu_name in ('查看已分配员工','查看已分配员工详情') AND menu_type = 'P';

-- My society Sister Li 2021-11-01 话术库文件夹的增、删、修、查  Tower 任务: (https://tower.im/teams/636204/todos/44675/)
ALTER TABLE `we_words_category`
  DROP INDEX `unique_corpid_type_name`,
  ADD UNIQUE INDEX `unique_corpid_type_name_userange`(`corp_id`, `type`, `name`, `use_range`) USING BTREE COMMENT '同一类型下的文件夹名不能重名';

-- My society Sister Li 2021-11-01 Tower 任务:删除之前初始化的数据 (https://tower.im/teams/636204/todos/45224/)
DELETE FROM we_words_category
WHERE id IN (SELECT id
             FROM (SELECT id
                   FROM we_words_category wwc
                   WHERE wwc.corp_id IN (SELECT corp_id
                                         FROM we_corp_account wca
                                         WHERE wca.del_flag = 0 AND wca.`status` = 0) AND wwc.parent_id = 0 AND
                         wwc.use_range = 1 AND wwc.`name` = '全部' AND wwc.sort = 0) a);

-- My society Sister Li 2021-11-01 Tower 任务:修改we_words_category的use_range默认值和注释 (https://tower.im/teams/636204/todos/45231/)
ALTER TABLE `we_words_category`
  MODIFY COLUMN `use_range` VARCHAR(255) NOT NULL DEFAULT '1'
  COMMENT '使用范围（企业话术：存入根部门1，部门话术：部门id用逗号隔开，我的话术：员工id）'
  AFTER `type`;

-- silver_chariot 2021-11-02 话术库菜单权限调整 Tower 任务: 菜单初始化sql调整 ( https://tower.im/teams/636204/todos/45047 )
UPDATE sys_menu
SET order_num = 2
WHERE menu_id = 2189 AND menu_name = '素材库';
INSERT INTO `sys_menu` VALUES
  ('2236', '话术库', '2062', '1', 'verbalTrickManage', 'verbalTrickLibrary/verbalTrickManage', '1', 'C', '0', '0', NULL,
   '#', 'admin', '2021-10-29 16:41:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES
  ('2237', '管理企业话术', '2236', '5', '', NULL, '1', 'F', '0', '0', 'wecom:corpWords:manage', '#', 'admin',
   '2021-11-02 10:38:49', '', NULL, '');
INSERT INTO `sys_menu` VALUES
  ('2238', '管理部门话术', '2236', '10', '', NULL, '1', 'F', '0', '0', 'wecom:deptWords:manage', '#', 'admin',
   '2021-11-02 10:39:26', '', NULL, '');
-- silver_chariot 2021-11-02  给所有系统管理员添加 新增的所有菜单权限 Tower 任务: 菜单初始化sql调整 ( https://tower.im/teams/636204/todos/45047 )
INSERT INTO sys_role_menu (role_id, menu_id)
  SELECT
    role_id,
    2236
  FROM sys_role
  WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
  SELECT
    role_id,
    2237
  FROM sys_role
  WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
  SELECT
    role_id,
    2238
  FROM sys_role
  WHERE role_type = 1;
-- silver_chariot 2021-11-02 给所有部门管理员添加 新增的管理部门话术菜单权限 Tower 任务: 菜单初始化sql调整 ( https://tower.im/teams/636204/todos/45047 )
INSERT INTO sys_role_menu (role_id, menu_id)
  SELECT
    role_id,
    2236
  FROM sys_role
  WHERE role_type = 2 AND role_key = 'depart';
INSERT INTO sys_role_menu (role_id, menu_id)
  SELECT
    role_id,
    2238
  FROM sys_role
  WHERE role_type = 2 AND role_key = 'depart';

-- 1*+ 2021-11-02 标签建群改造 ( https://tower.im/teams/636204/todos/45105 )
ALTER TABLE `we_pres_tag_group_stat`
  ADD COLUMN `user_id` VARCHAR(64) NOT NULL DEFAULT ''
COMMENT '员工ID'
  AFTER `external_userid`;

-- My society Sister Li 2021-11-02 Tower任务:员工活码改造(https://tower.im/teams/636204/todos/44627/)
CREATE TABLE `we_emple_code_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `emple_code_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '员工活码ID',
  `media_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '素材ID',
  `media_type` int(1) NOT NULL DEFAULT '0' COMMENT '-1：群活码、0：图片、1：语音、2：视频，3：文件、4：文本、5：图文链接、6：小程序',
  PRIMARY KEY (`id`),
  KEY `normal_emplecode` (`emple_code_id`) USING BTREE COMMENT '普通索引emple_code_id'
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='员工活码附件表';

-- My society Sister Li 2021-11-02 将原有员工活码的图片素材数据迁移到附件表
INSERT INTO `we_emple_code_material` ( `emple_code_id`, `media_id`, `media_type` )
SELECT  wec.id,wec.media_id ,0 FROM we_emple_code wec   INNER JOIN we_material wm ON wec.media_id = wm.id WHERE wec.media_id !=0;

-- My society Sister Li 2021-11-02 更新员工活码表结构
ALTER TABLE `we_emple_code`
ADD COLUMN `remark_type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '备注类型：0：不备注，1：在昵称前，2：在昵称后',
ADD COLUMN `remark_name` varchar(32) NOT NULL DEFAULT '' COMMENT '备注名',
ADD COLUMN `effect_time_open` varchar(8) NOT NULL DEFAULT '' COMMENT '开启时间 HH:mm' ,
ADD COLUMN `effect_time_close` varchar(8) NOT NULL DEFAULT '' COMMENT '关闭时间 HH:mm' ,
ADD COLUMN `material_sort` varchar(255) NOT NULL DEFAULT '' COMMENT '附件排序';
ALTER TABLE `we_emple_code`
MODIFY COLUMN `skip_verify` tinyint(4) NOT NULL DEFAULT 1 COMMENT '自动成为好友:0：否，1：全天，2：时间段' AFTER `code_type`;

-- My society Sister Li 2021-11-02 给we_emple_code.material_sort赋值
UPDATE we_emple_code wec,we_emple_code_material wecm SET wec.material_sort = wecm.media_id WHERE wec.id = wecm.emple_code_id;

-- My society Sister Li 2021-11-02 更新员工活码表结构
ALTER TABLE `we_emple_code`
DROP COLUMN `media_id`;
ALTER TABLE `we_emple_code`
DROP COLUMN `scan_times`;
-- yiming 2021-11-3 Tower 任务: 话术 ( https://tower.im/teams/636204/todos/44680 )
ALTER TABLE `we_words_detail`
MODIFY COLUMN `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标题' AFTER `content`;
ALTER TABLE `we_words_group`
ADD COLUMN `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序' AFTER `is_push`;

CREATE TABLE `we_words_last_use`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '员工id',
  `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '话术类型（0：企业话术，1：部门话术，2：我的话术）',
  `words_ids` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '话术id（用逗号隔开最多5个）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_corp_user_type`(`corp_id`, `user_id`, `type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '最近使用话术表' ROW_FORMAT = Dynamic;


-- My society Sister Li 2021-11-03 Tower任务: 通过好友的开启关闭时间检测(https://tower.im/teams/636204/todos/45257/)
INSERT INTO `sys_job` VALUES (11, '通过好友的开启关闭时间检测', 'SYSTEM', 'EmpleCodeThroughFriendTimeSwitchTask.empleCodeThroughFriendTimeSwitch()', '0 * * * * ? ', '1', '1', '0', 'admin', '2021-11-03 00:00:00', 'admin', '2021-11-03 00:00:00', '');

-- My society Sister Li 2021-11-04 Tower任务: 员工活码表增加索引(https://tower.im/teams/636204/todos/45371/)
ALTER TABLE `we_emple_code`
ADD INDEX `normal_effecttime_open`(`effect_time_open`) USING BTREE COMMENT '普通索引effect_time_open',
ADD INDEX `normal_effecttime_close`(`effect_time_close`) USING BTREE COMMENT '普通索引effect_time_close';

-- My society Sister Li 2021-11-04 Tower任务：员工活码数据统计(https://tower.im/teams/636204/todos/45317/)
CREATE TABLE `we_emple_code_analyse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `emple_code_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '员工活码主键ID',
  `user_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业成员userId',
  `external_userid` varchar(32) NOT NULL DEFAULT '' COMMENT '客户ID',
  `time` date NOT NULL COMMENT '添加时间',
  `type` tinyint(1) NOT NULL COMMENT '1:新增，0:流失',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_corpid_codeid_userid_extid_type_time` (`corp_id`,`emple_code_id`,`user_id`,`external_userid`,`type`,`time`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- silver_chariot 2021-11-05 删除原来的老客建群隐藏菜单 Tower 任务: 菜单名修改 ( https://tower.im/teams/636204/todos/45345 )
delete from sys_menu where menu_name = '老客建群' and menu_id = 2183;
-- silver_chariot 2021-11-05 把标签建群改名为老客建群 Tower 任务: 菜单名修改 ( https://tower.im/teams/636204/todos/45345 )
update sys_menu set
    menu_name = '老客进群' where menu_id = 2102 and menu_name = '标签建群';


-- silver_chariot 2021-11-03 增加客户-标签关系表的唯一键
ALTER TABLE `we_flower_customer_tag_rel`
    ADD UNIQUE INDEX `un_rel_tag_id` (`flower_customer_rel_id`, `tag_id`) USING BTREE ;

-- My society Sister Li 2021-11-08 Tower任务：素材库增加临时素材字段(https://tower.im/teams/636204/todos/45437/)
ALTER TABLE `we_material`
ADD COLUMN `temp_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为临时素材（0：正常显示的素材，1：临时素材）';
-- silver_chariot 2021-11-08 数据库字段长度调整 Tower 任务: 基础模块 ( https://tower.im/teams/636204/todos/45206 )
ALTER TABLE `we_user`
    MODIFY COLUMN `create_by`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人' AFTER `isOpenChat`,
    MODIFY COLUMN `update_by`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人' AFTER `create_by`;
ALTER TABLE `we_department`
    MODIFY COLUMN `name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门名称' AFTER `parent_id`;
ALTER TABLE `we_tag`
    MODIFY COLUMN `name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签名' AFTER `corp_id`;
ALTER TABLE `we_tag_group`
    MODIFY COLUMN `group_name`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签组名' AFTER `group_id`;
ALTER TABLE `we_leave_user`
    MODIFY COLUMN `main_department_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户所属主部门名字' AFTER `main_department`;
ALTER TABLE `we_group_member`
    MODIFY COLUMN `user_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群成员id' AFTER `id`;
ALTER TABLE `we_flower_customer_rel`
    MODIFY COLUMN `user_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '添加了此外部联系人的企业成员userid' AFTER `id`;
ALTER TABLE `we_allocate_customer_v2`
    MODIFY COLUMN `takeover_userid`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接替成员的userid' AFTER `id`,
    MODIFY COLUMN `handover_userid`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原跟进成员的userid' AFTER `external_userid`;


-- My society Sister Li 2021-11-08 员工活码增加是否自动打标签标志位
ALTER TABLE `we_emple_code`
ADD COLUMN `tag_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可打标签：0不自动打标签，1：自动打标签';

-- silver_chariot 2021-11-08 屏蔽欢迎语模板 Tower 任务: 欢迎语模板菜单显示状态置为屏蔽 ( https://tower.im/teams/636204/todos/45474 )
update sys_menu set visible= 1 where menu_id = 2060 and menu_name = '欢迎语模板';

-- My society Sister Li 2021-11-05 新客建群原有数据迁移(https://tower.im/teams/636204/todos/45410/)
UPDATE we_emple_code wec,
we_community_new_group wcng
SET wec.material_sort = wcng.group_code_id
WHERE
	wcng.empl_code_id = wec.id
	AND wec.source = 1
	AND wcng.del_flag = 0;

INSERT INTO we_emple_code_material (`emple_code_id`,`media_id`,`media_type`)
SELECT id,material_sort,-1 FROM we_emple_code WHERE source = 1 AND del_flag = 0 AND material_sort !='';


-- redhi 2021-11-09 数据库字段长度调整 Tower 任务: 会话存档 ( https://tower.im/teams/636204/todos/45207 )
ALTER TABLE `we_chat_contact_mapping`
    MODIFY COLUMN `from_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送人id' AFTER `id`,
    MODIFY COLUMN `receive_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收人id' AFTER `from_id`;

-- My society Sister Li 2021-11-10 数据库字段长度调整 Tower 任务: 运营中心 (https://tower.im/teams/636204/todos/45603/)
ALTER TABLE `we_emple_code_analyse`
MODIFY COLUMN `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业成员userId';
-- yiming 2021-11-4 Tower 任务: 客户群活码改造 ( https://tower.im/teams/636204/todos/44217 )
ALTER TABLE `we_group_code_actual`
ADD COLUMN `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序' AFTER `create_time`;

-- yiming 2021-11-8 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/44753 )
UPDATE
we_group_code w
INNER JOIN we_user wu ON wu.user_name = w.create_by AND wu.corp_id = w.corp_id
SET w.create_by = wu.user_id;

INSERT INTO `sys_job` VALUES (12, '定时检查客户群活码', 'SYSTEM', 'GroupCodeActualTimeTask.findExpireCode()', '0 0 10 * * ? *', '1', '1', '0', 'admin', '2021-11-8 00:00:00', 'admin', '2021-11-8 00:00:00', '');

ALTER TABLE `we_group_code_actual`
ADD COLUMN `update_time` datetime(0) NOT NULL COMMENT '更新时间' AFTER `status`;

ALTER TABLE `we_group_code`
ADD COLUMN `seq` varchar(255) NOT NULL DEFAULT '' COMMENT '实际群码顺序' AFTER `show_tip`;
ALTER TABLE `we_group_code_actual`
DROP COLUMN `sort`;

ALTER TABLE `we_group_code_actual`
MODIFY COLUMN `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `status`;

-- silver_chariot 2021-11-10 Tower 任务: 新客建群菜单名修改 ( https://tower.im/teams/636204/todos/45673 )
UPDATE sys_menu
SET menu_name = '新客进群'
WHERE
        menu_id = 2101
  AND menu_name = '新客建群';

-- silver_chariot 2011-11-11 编辑新客建群改成编辑老客进群  Tower 任务: 更改菜单名称 ( https://tower.im/teams/636204/todos/45737 )
update sys_menu set menu_name = '编辑老客进群' where menu_name = '编辑标签建群' and menu_id = 2107;


-- silver_chariot 2011-11-11 给普通员工增加话术库权限 Tower 任务: 话术库权限异常 ( https://tower.im/teams/636204/todos/45752 )
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
    role_id,
    2236
FROM sys_role
WHERE role_type = 2 AND role_key = 'employee';

-- yiming 2021-11-11 修改已过期的客户群码状态 Tower 任务: 新客进群已过期的二维码仍显示使用中 ( https://tower.im/teams/636204/todos/45795 )
UPDATE
we_group_code_actual
set
`status` = 1
WHERE NOW() > effect_time
AND del_flag = 0

