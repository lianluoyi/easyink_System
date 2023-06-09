-- tigger 2022-1-7 欢迎语改造 Tower 任务: 欢迎语改造 ( https://tower.im/teams/636204/todos/44257 )
DROP TABLE IF EXISTS `we_msg_tlp`;
CREATE TABLE `we_msg_tlp` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID',
                              `default_welcome_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '默认欢迎语',
                              `welcome_msg_tpl_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '欢迎语适用对象类型:1:员工欢迎语;2:客户群欢迎语',
                              `exist_special_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否存在有特殊时段欢迎语(存在则有关联rule_id) 0:否 1:是',
                              `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='欢迎语模板表';
DROP TABLE IF EXISTS `we_msg_tlp_material`;
CREATE TABLE `we_msg_tlp_material` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '欢迎语素材主键id',
                                       `default_msg_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '默认欢迎语模板id',
                                       `special_msg_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '特殊规则欢迎语模板id(如果不存在特殊时段欢迎语，且没有素材则该字段为0)',
                                       `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '素材类型 0:文本 1:图片 2:链接 3:小程序 4:文件 5:视频媒体文件',
                                       `template_id` varchar(64) NOT NULL DEFAULT '' COMMENT '入群欢迎语返回的模板id',
                                       `content` varchar(255) NOT NULL DEFAULT '' COMMENT '文本消息',
                                       `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '图片url',
                                       `description` varchar(255) NOT NULL DEFAULT '' COMMENT '消息描述和小程序的appid',
                                       `url` varchar(255) NOT NULL DEFAULT '' COMMENT '封面url和小程序的page',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='欢迎语素材表';
DROP TABLE IF EXISTS `we_msg_tlp_scope`;
CREATE TABLE `we_msg_tlp_scope` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `msg_tlp_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '默认欢迎语模板id',
                                    `use_user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '使用人id',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE KEY `unique_msgId_ruleId` (`msg_tlp_id`,`use_user_id`) USING BTREE COMMENT '欢迎语id和员工唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='模板使用人员范围';
DROP TABLE IF EXISTS `we_msg_tlp_special_rule`;
CREATE TABLE `we_msg_tlp_special_rule` (
                                           `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '欢迎语规则主键id',
                                           `msg_tlp_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '默认欢迎语模板id',
                                           `special_welcome_msg` varchar(255) NOT NULL DEFAULT '' COMMENT '特殊欢迎语模板消息',
                                           `rule_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '特殊欢迎语消息规则类型 1:周策略欢迎语',
                                           `weekends` varchar(16) NOT NULL DEFAULT '0' COMMENT '1-7 周一到周日,多个逗号隔开',
                                           `weekend_begin_time` time NOT NULL DEFAULT '00:00:00' COMMENT '周策略开始时间',
                                           `weekend_end_time` time NOT NULL DEFAULT '00:00:00' COMMENT '周策略结束时间',
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='特殊规则欢迎语表';
ALTER TABLE `we_msg_tlp` ADD COLUMN `template_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '入群欢迎语返回的模板id';
ALTER TABLE `we_msg_tlp_material` DROP COLUMN `template_id`;
ALTER TABLE  `we_msg_tlp_material` MODIFY  COLUMN `content` varchar(255) NOT NULL DEFAULT '' COMMENT '文本内容,链接消息标题,小程序消息标题，(前端: 图片,文件,视频的标题)';
ALTER TABLE  `we_msg_tlp_material` MODIFY  COLUMN `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '图片url,链接封面url,小程序picurl,文件url,视频url';
ALTER TABLE  `we_msg_tlp_material` MODIFY  COLUMN `description` varchar(255) NOT NULL DEFAULT '' COMMENT '链接消息描述,小程序appid(前端: 文件大小)';
ALTER TABLE  `we_msg_tlp_material` MODIFY  COLUMN `url` varchar(255) NOT NULL DEFAULT '' COMMENT  '链接url,小程序page';
ALTER TABLE `we_msg_tlp_material` ADD COLUMN `sort_no` tinyint(2) NOT NULL DEFAULT '0' COMMENT '排序字段';
ALTER TABLE `we_msg_tlp` ADD COLUMN `notice_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '群素材是否通知员工标识(0: 不通知(默认) 1:通知)';


-- yiming  ( https://tower.im/teams/636204/todos/48537 )
CREATE TABLE `we_moment_detail_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `moment_task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '朋友圈任务id',
  `detail_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '附件id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈任务附件关联表';

CREATE TABLE `we_moment_task` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
  `moment_id` varchar(64) NOT NULL DEFAULT '' COMMENT '朋友圈id',
  `job_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业微信异步任务id 24小时有效',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发布类型（0：企业 1：个人）',
  `task_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型（0：立即发送 1：定时发送）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成',
  `send_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发布时间',
  `users` text NOT NULL COMMENT '所属员工',
  `tags` text NOT NULL COMMENT '客户标签',
  PRIMARY KEY (`id`),
  KEY `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈任务信息表';

CREATE TABLE `we_moment_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `moment_task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '朋友圈任务id',
  `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
  `publish_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '成员发表状态。0:待发布 1：已发布 2：已过期 3：不可发布',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈任务执行结果';

CREATE TABLE `we_moment_user_customer_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
  `external_userid` varchar(64) NOT NULL DEFAULT '' COMMENT '客户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈客户员工关联表';

INSERT INTO `sys_job`
VALUES (17, '更新企业朋友圈创建结果', 'SYSTEM', 'momentUpdateCreatedStatusTask.updateMomentTaskStatus()', '0/10 * * * * ? ', '1', '1', '0',
        'admin', '2022-1-11 14:43:11', '', '2022-1-11 14:43:11', '');



-- silver_chariot 2022-01-12 新增欢迎语相关菜单权限  Tower 任务: 增加菜单权限 ( https://tower.im/teams/636204/todos/48495 )
-- 设置原欢迎语模板菜单为可见 ，并改名为‘欢迎语’
update sys_menu
set menu_name = '欢迎语',
    visible   = 0
where menu_id = 2060
  and menu_name = '欢迎语模板';

-- 修改原来好友欢迎语菜单名
update sys_menu
set menu_name = '新增好友欢迎语'
where menu_id = 2156;

update sys_menu
set menu_name = '编辑好友欢迎语'
where menu_id = 2157
  and menu_name = '编辑欢迎语 ';

update sys_menu
set menu_name = '删除好友欢迎语'
where menu_id = 2158
  and menu_name = '删除欢迎语 ';

-- 新增 群欢迎语权限
INSERT INTO `sys_menu`
VALUES ('2269', '新增入群欢迎语', '2060', '7', '', NULL, '1', 'F', '0', '0', 'wecom:groupWelcome:add', '#', 'admin',
        '2022-01-12 14:42:38', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2270', '编辑入群欢迎语', '2060', '10', '', NULL, '1', 'F', '0', '0', 'wecom:groupWelcome:edit', '#', 'admin',
        '2022-01-12 14:43:00', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2271', '删除入群欢迎语', '2060', '15', '', NULL, '1', 'F', '0', '0', 'wecom:groupWelcome:del', '#', 'admin',
        '2022-01-12 14:43:23', '', NULL, '');


-- 删除原来的 欢迎语相关权限
delete
from sys_role_menu
where menu_id in (2060, 2156, 2157, 2158);

-- 给管理员增加所有欢迎语权限
insert into sys_role_menu (role_id, menu_id)
select role_id, 2060
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2156
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2157
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2158
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2269
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2270
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2271
from sys_role
where role_type = 1;
-- 给部门管理员增加 欢迎语
insert into sys_role_menu (role_id, menu_id)
select role_id, 2060
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2156
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2157
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2158
from sys_role
where role_type = 2
  AND role_key = 'depart';

-- silver_chariot 2022-1-13 朋友圈 ： 增加菜单权限 Tower 任务: 菜单权限 ( https://tower.im/teams/636204/todos/48669 )

--  新增朋友圈菜单项
INSERT INTO `sys_menu`
VALUES ('2272', '朋友圈', '2188', '25', 'moments', NULL, '1', 'M', '0', '0', NULL, '#', 'admin', '2022-01-13 10:53:17', '',
        NULL, '');
INSERT INTO `sys_menu`
VALUES ('2273', '发布朋友圈', '2272', '1', 'release', 'friendsCircle/release', '1', 'C', '0', '0', 'wecom:moments:publish',
        '#', 'admin', '2022-01-13 10:55:03', 'admin', '2022-01-13 14:32:16', '');
INSERT INTO `sys_menu`
VALUES ('2274', '朋友圈发布记录', '2272', '5', 'record', 'friendsCircle/record', '1', 'C', '0', '0', '', '#', 'admin',
        '2022-01-13 10:55:35', 'admin', '2022-01-13 14:36:58', '');
INSERT INTO `sys_menu`
VALUES ('2275', '查看朋友圈详情', '2274', '5', '', NULL, '1', 'F', '0', '0', 'wecom:moments:detail', '#', 'admin',
        '2022-01-13 10:56:02', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2276', '编辑定时朋友圈', '2274', '10', '', NULL, '1', 'F', '0', '0', 'wecom:moments:edit', '#', 'admin',
        '2022-01-13 10:56:40', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2277', '删除朋友圈', '2274', '15', '', NULL, '1', 'F', '0', '0', 'wecom:moments:del', '#', 'admin',
        '2022-01-13 10:57:03', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2278', '查看朋友圈详情页面', '2272', '33', 'detail', 'friendsCircle/detail', '1', 'P', '1', '0', NULL, '#', 'admin',
        '2022-01-13 14:38:36', '', NULL, '');


--  给管理员/部门管理员 增加所有朋友圈菜单权限, 权限

insert into sys_role_menu (role_id, menu_id)
select role_id, 2272
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2273
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2274
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2275
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2276
from sys_role
where role_type = 1;
insert into sys_role_menu (role_id, menu_id)
select role_id, 2277
from sys_role
where role_type = 1;

insert into sys_role_menu (role_id, menu_id)
select role_id, 2272
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2273
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2274
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2275
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2276
from sys_role
where role_type = 2
  AND role_key = 'depart';
insert into sys_role_menu (role_id, menu_id)
select role_id, 2277
from sys_role
where role_type = 2
  AND role_key = 'depart';
-- 增加 ‘新增欢迎语页面 ’
INSERT INTO `sys_menu`
VALUES ('2279', '新增/修改入群欢迎语', '2062', '82', 'groupWelcomeAdd', 'drainageCode/welcome/groupWelcomeAdd', '1', 'P', '1',
        '0', '', '#', 'admin', '2022-01-13 17:58:19', 'admin', '2022-01-13 20:12:22', '');

-- tigger 修改特殊欢迎语长度限制
ALTER TABLE `we_msg_tlp_special_rule` MODIFY COLUMN `special_welcome_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '特殊欢迎语模板消息';


-- yiming ( https://tower.im/teams/636204/todos/48537 )
DROP TABLE IF EXISTS `we_moment_user_customer_rel`;
CREATE TABLE `we_moment_user_customer_rel` (
  `moment_task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '朋友圈任务id',
  `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
  `external_userid` varchar(64) NOT NULL DEFAULT '' COMMENT '客户id',
  PRIMARY KEY (`moment_task_id`,`user_id`,`external_userid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈客户员工关联表';

INSERT INTO `sys_job`
VALUES (18, '更新企业朋友圈执行结果', 'SYSTEM', 'momentPublishStatusTask.updateMomentPublishStatus()', '0 0/30 * * * ? *', '1', '1', '0',
        'admin', '2022-1-12 14:43:11', '', '2022-1-12 14:43:11', '');

ALTER TABLE `we_moment_task`
ADD COLUMN `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `tags`,
ADD COLUMN `create_by` varchar(255) NOT NULL DEFAULT '' COMMENT '创建人' AFTER `create_time`;

ALTER TABLE `we_moment_task`
MODIFY COLUMN `send_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间' AFTER `status`;

INSERT INTO `sys_job`
VALUES (19, '发送定时朋友圈任务', 'SYSTEM', 'momentStartCreateTask.createMoment()', '0 0/2 * * * ? *', '1', '1', '0',
        'admin', '2022-1-12 14:43:11', '', '2022-1-12 14:43:11', '');

ALTER TABLE `we_moment_task`
ADD COLUMN `content` varchar(2000) NOT NULL DEFAULT '' COMMENT '文本内容' AFTER `job_id`;

ALTER TABLE `we_moment_task_result`
ADD COLUMN `publish_time` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发布时间' AFTER `user_id`;

ALTER TABLE `we_moment_task`
ADD COLUMN `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`;

UPDATE
sys_job
SET job_id = 20
WHERE job_name = '定期查询客户分配情况任务';

UPDATE qrtz_cron_triggers SET trigger_name = 'TASK_CLASS_NAME20' WHERE trigger_name = 'TASK_CLASS_NAME15' AND  cron_expression = '0 */5 * * * ?';

ALTER TABLE `we_moment_task`
ADD COLUMN `push_range` tinyint(1) NOT NULL DEFAULT 0 COMMENT '可见范围（0：全部客户 1：部分客户）' AFTER `task_type`;

ALTER TABLE `we_words_detail`
ADD COLUMN `size` bigint(20) NOT NULL DEFAULT 0 COMMENT '视频大小' AFTER `is_defined`;

ALTER TABLE `we_moment_task`
ADD COLUMN `select_user` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否选择员工（0：未选择 1：已选择）' AFTER `send_time`;

-- silver_chariot 2022-01-18 把朋友圈发布记录 改成 发布记录  Tower 任务: 将朋友圈发布记录修改为“发布记录” ( https://tower.im/teams/636204/todos/48959 )
UPDATE sys_menu
SET menu_name = '发布记录'
WHERE menu_id = 2274
  AND menu_name = '朋友圈发布记录';

-- 修改欢迎语的父菜单为引流获客
UPDATE `sys_menu`
SET parent_id = 2052,
    order_num = 45
WHERE menu_id = 2060
  AND menu_name = '欢迎语';

-- silver_chariot 2022-01-18 Tower 任务: 入群欢迎语菜单权限异常 ( https://tower.im/teams/636204/todos/48971 )
update sys_menu
set parent_id = 2052
where menu_id = 2279
  and menu_name = '新增/修改入群欢迎语';

update sys_menu
set parent_id = 2052
where menu_id = 2163
  and menu_name = '新增/修改欢迎语';

-- yiming Tower 任务: 详情页面触发对象异常 ( https://tower.im/teams/636204/todos/48987 )
ALTER TABLE `we_moment_task_result`
ADD INDEX `idx_moment_task_id`(`moment_task_id`) USING BTREE;

-- yiming Tower 任务: 后端 ( https://tower.im/teams/636204/todos/48986 )
ALTER TABLE `we_moment_task_result`
ADD COLUMN `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '失败备注' AFTER `publish_status`;
