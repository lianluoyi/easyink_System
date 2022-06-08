-- yiming 2021-11-12 群发表结构修改增加字段 ( https://tower.im/teams/636204/todos/45973 )
ALTER TABLE `we_customer_messageoriginal`
ADD COLUMN `task_name` varchar(32) NOT NULL DEFAULT '' COMMENT '任务名称' AFTER `staff_id`;
ALTER TABLE `we_customer_messgaeresult`
ADD COLUMN `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注' AFTER `status`;

-- My society sister Li  2021-11-12  Tower 任务: 客户群改造-群标签组和群标签 (https://tower.im/teams/636204/todos/45965/)
CREATE TABLE `we_group_tag_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `name` varchar(16) NOT NULL COMMENT '群标签组名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_corpid_name` (`corp_id`,`name`) USING BTREE COMMENT '唯一索引（corp_id、name）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群标签组表';

CREATE TABLE `we_group_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `group_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '群标签组ID',
  `name` varchar(16) NOT NULL COMMENT '群标签名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_groupid_name` (`group_id`,`name`) USING BTREE COMMENT '唯一索引（group_id、name）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群标签表';

-- My society sister Li  2021-11-15 Tower 任务: 客户群改造-群和标签关联关系 (https://tower.im/teams/636204/todos/45967/)
CREATE TABLE `we_group_tag_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
  `chat_id` varchar(32) NOT NULL DEFAULT '' COMMENT '群ID',
  `tag_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_corpid_chatid_tagid` (`corp_id`,`chat_id`,`tag_id`) USING BTREE COMMENT '唯一索引（corp_id，chat_id，tag_id）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户群和标签关联表';

-- My society sister Li  2021-11-16 Tower 任务: 客户群改造-增加邀请者信息 (https://tower.im/teams/636204/todos/45968/)
ALTER TABLE `we_group_member`
ADD COLUMN `invitor` varchar(64) NOT NULL DEFAULT '' COMMENT '邀请者userId' AFTER `name`;

-- yiming 2021-11-16 Tower 任务: 增加定时任务 ( https://tower.im/teams/636204/todos/46074 )
INSERT INTO `sys_job` VALUES (13, '定时拉取群发未发送消息结果', 'SYSTEM', 'MessageResultTask.asyncSendResult()', '0 0/2 * * * ? ', '1', '1', '0', 'admin', '2021-11-16 00:00:00', 'admin', '2021-11-16 00:00:00', '');

-- silver_chariot 2021-11-16 创建自定义字段表 Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
CREATE TABLE `we_customer_extend_property`
(
    `id`            bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`       varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `name`          varchar(64) NOT NULL DEFAULT '' COMMENT '扩展字段名称',
    `type`          int(11)     NOT NULL DEFAULT '2' COMMENT '字段类型（1系统默认字段,2单行文本，3多行文本，4单选框，5多选框，6下拉框，7日期，8图片，9文件）',
    `required`      tinyint(1)  NOT NULL DEFAULT '0' COMMENT '是否必填（1必填0非必填）',
    `property_sort` int(11)     NOT NULL DEFAULT '20' COMMENT '字段排序',
    `status`        tinyint(1)  NOT NULL DEFAULT '1' COMMENT '状态（0停用1启用）',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_corp_id_name` (`corp_id`, `name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='自定义属性关系表';

-- silver_chariot 2021-11-16 创建自定义字段多选值表 Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
CREATE TABLE `extend_property_multiple_option`
(
    `id`                 bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `extend_property_id` bigint(20)   NOT NULL DEFAULT '0' COMMENT '扩展属性ID',
    `multiple_value`     varchar(400) NOT NULL DEFAULT '' COMMENT '多选框.下拉框,单选框可选值',
    `option_sort`        int(11)      NOT NULL DEFAULT '1' COMMENT '多选值的排序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_property_id` (`extend_property_id`, `multiple_value`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='扩展属性多选项可选值表';

-- silver_chariot 2021-11-16 创建自定义字段-客户关系表 Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
CREATE TABLE `we_customer_extend_property_rel`
(
    `corp_id`            varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `external_userid`    varchar(32) NOT NULL DEFAULT '' COMMENT '成员客户关系ID',
    `extend_property_id` int(20)     NOT NULL DEFAULT '0' COMMENT '扩展属性id',
    `property_value`     varchar(64) NOT NULL DEFAULT '' COMMENT '自定义属性的值',
    PRIMARY KEY (`corp_id`, `external_userid`, `extend_property_id`, `property_value`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客户-自定义属性关系表';

-- silver_chariot 2021-11-16 新增 客户自定义字段相关菜单项  Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
INSERT INTO `sys_menu`
VALUES ('2239', '客户设置', '2001', '32', 'customerSetting', 'customerManage/customerSetting', '1', 'C', '0', '0', NULL,
        '#', 'admin', '2021-11-15 11:33:57', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2240', '新增字段', '2239', '1', '', NULL, '1', 'F', '0', '0', 'customer:extendProp:add', '#', 'admin',
        '2021-11-16 13:56:31', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2241', '编辑字段', '2239', '3', '', NULL, '1', 'F', '0', '0', 'customer:extendProp:edit', '#', 'admin',
        '2021-11-16 14:33:16', '', NULL, '');
INSERT INTO `sys_menu`
VALUES ('2242', '删除字段', '2239', '6', '', NULL, '1', 'F', '0', '0', 'customer:extendProp:remove', '#', 'admin',
        '2021-11-16 14:33:56', '', NULL, '');

-- silver_chariot 2021-11-16 给以前的管理员添加客户设置的菜单权限 Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2239
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2240
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2241
FROM sys_role
WHERE role_type = 1;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id,
       2242
FROM sys_role
WHERE role_type = 1;

-- silver_chariot 2021-11-18 给当前所有已存在企业 初始化系统默认属性 Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
INSERT IGNORE INTO `we_customer_extend_property` (`corp_id`,
                                                  `name`,
                                                  `type`,
                                                  `required`,
                                                  `property_sort`,
                                                  `status`,
                                                  `create_time`,
                                                  `create_by`,
                                                  `update_time`)
SELECT corp_id,
       '出生日期',
       '1',
       '0',
       '1',
       '1',
       now(),
       'admin',
       now()
FROM we_corp_account
WHERE del_flag = 0
  AND `status` = 0;

INSERT IGNORE INTO `we_customer_extend_property` (`corp_id`,
                                                  `name`,
                                                  `type`,
                                                  `required`,
                                                  `property_sort`,
                                                  `status`,
                                                  `create_time`,
                                                  `create_by`,
                                                  `update_time`)
SELECT corp_id,
       '邮箱',
       '1',
       '0',
       '2',
       '1',
       now(),
       'admin',
       now()
FROM we_corp_account
WHERE del_flag = 0
  AND `status` = 0;

INSERT IGNORE INTO `we_customer_extend_property` (`corp_id`,
                                                  `name`,
                                                  `type`,
                                                  `required`,
                                                  `property_sort`,
                                                  `status`,
                                                  `create_time`,
                                                  `create_by`,
                                                  `update_time`)
SELECT corp_id,
       '电话',
       '1',
       '0',
       '3',
       '1',
       now(),
       'admin',
       now()
FROM we_corp_account
WHERE del_flag = 0
  AND `status` = 0;

INSERT IGNORE INTO `we_customer_extend_property` (`corp_id`,
                                                  `name`,
                                                  `type`,
                                                  `required`,
                                                  `property_sort`,
                                                  `status`,
                                                  `create_time`,
                                                  `create_by`,
                                                  `update_time`)
SELECT corp_id,
       '地址',
       '1',
       '0',
       '4',
       '1',
       now(),
       'admin',
       now()
FROM we_corp_account
WHERE del_flag = 0
  AND `status` = 0;


INSERT IGNORE INTO `we_customer_extend_property` (`corp_id`,
                                                  `name`,
                                                  `type`,
                                                  `required`,
                                                  `property_sort`,
                                                  `status`,
                                                  `create_time`,
                                                  `create_by`,
                                                  `update_time`)
SELECT corp_id,
       '描述',
       '1',
       '0',
       '5',
       '1',
       now(),
       'admin',
       now()
FROM we_corp_account
WHERE del_flag = 0
  AND `status` = 0;
-- 2021-11-18 silver_chariot 因为扩展字段值存在文件所以需要加大字段长度 Tower 任务: 可自定义客户资料字段 ( https://tower.im/teams/636204/todos/44240 )
ALTER TABLE `we_customer_extend_property_rel`
    MODIFY COLUMN `property_value` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '自定义属性的值' AFTER `extend_property_id`;


-- yiming Tower 任务: 复制信息接口 ( https://tower.im/teams/636204/todos/46109 )
ALTER TABLE `we_customer_messageoriginal`
ADD COLUMN `filter_tags` varchar(1000) NOT NULL DEFAULT '' COMMENT '过滤标签id列表' AFTER `tag`,
ADD COLUMN `gender` tinyint(1) NOT NULL DEFAULT 0 COMMENT '外部联系人性别 0-未知 1-男性 2-女性 -1-全部' AFTER `filter_tags`;
ALTER TABLE `we_customer_seedmessage`
ADD COLUMN `size` int(10) NOT NULL DEFAULT 0 COMMENT '视频大小' AFTER `video_url`;

-- yiming 2021-11-18 保存群发任务增加时间参数 Tower 任务: 详情页查询接口 ( https://tower.im/teams/636204/todos/46119 )
ALTER TABLE `we_customer_messageoriginal`
ADD COLUMN `customer_start_time` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加客户开始时间' AFTER `push_range`,
ADD COLUMN `customer_end_time` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加客户结束时间' AFTER `customer_start_time`;


-- silver_chariot 增加待办事项status的状态说明 Tower 任务: 客户资料侧边栏改造 ( https://tower.im/teams/636204/todos/44227 )
ALTER TABLE `we_customer_trajectory`
    MODIFY COLUMN `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '0:正常;1:完成;2:删除;4:已通知未完成' AFTER `end_time`;


-- silver_chariot 新增待办任务提示任务 Tower 任务: 客户资料侧边栏改造 ( https://tower.im/teams/636204/todos/44227 )
INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`,
                       `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('待办事项提醒任务', 'SYSTEM', 'todoReminderTask.execute', '0 */5 * * * ?', '1', '1', '0', 'admin',
        '2021-11-19 16:06:51', 'admin', '2021-11-19 16:19:19', '');

-- yiming 2021-11-19 https://tower.im/teams/636204/todos/46210 )
UPDATE
we_customer_message wcm
LEFT JOIN we_customer_messagetimetask wcmt ON wcmt.message_id = wcm.message_id
SET
wcm.check_status = '1'
WHERE
wcmt.solved = 1 OR wcmt.solved is null;
-- yiming 2021-11-22 https://tower.im/teams/636204/todos/46210 )
ALTER TABLE `we_customer_message`
MODIFY COLUMN `sender` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送企业群发消息的成员userid，当类型为发送给客户群时必填(和企微客户沟通后确认是群主id)' AFTER `chat_type`;

-- silver_chariot 客户轨迹表增加子类型和详情字段   Tower 任务: 客户资料侧边栏改造 ( https://tower.im/teams/636204/todos/44227 )
ALTER TABLE `we_customer_trajectory`
    ADD COLUMN `detail`   varchar(500) NOT NULL DEFAULT '' COMMENT '操作细节（如果是文件图片则是url,如果多个选项则,隔开)' AFTER `status`,
    ADD COLUMN `sub_type` varchar(64)  NOT NULL DEFAULT '' COMMENT '子类型（修改备注：edit_remark;修改标签：edit_tag;编辑多选框：edit_multi;编辑单选：edit_choice;编辑图片：edit_pic;编辑文件：edit_file;加入群聊:join_group;退出群聊：quit_group;加好友：add_user；删除好友：del_user;' AFTER `detail`;

ALTER TABLE `we_customer_trajectory`
    MODIFY COLUMN `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '0:正常;1:已通知未完成;2:删除;3:已完成' AFTER `end_time`;

-- silver_chariot 2021-11-23 Tower 任务: 客户群改造:增加菜单权限 ( https://tower.im/teams/636204/todos/45948 )
INSERT INTO `sys_menu`
VALUES ('2243', '编辑客户群', '2003', '5', '', NULL, '1', 'F', '0', '0', 'customerManage:group:edit', '#', 'admin',
        '2021-11-23 15:30:54', '', NULL, '');

-- silver_chariot 2021-11-23 给以前所有角色增加 编辑客户群权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT role_id, 2243
FROM sys_role
WHERE status = '0'
  and del_flag = '0';
-- yiming 2021-11-23 Tower 任务: 客户群群发详情备注异常 ( https://tower.im/teams/636204/todos/46249 )
UPDATE
we_customer_messgaeresult
SET
remark = ''
WHERE
`status` = 1;


-- yiming 2021-11-23 兼容导入话术旧数据 Tower 任务: 侧边栏话术库加载异常 ( https://tower.im/teams/636204/todos/45920 )
UPDATE
we_words_group
SET
sort = id
WHERE
sort = 0;


-- silver_chariot Tower 任务: 系统字段默认排序错误 ( https://tower.im/teams/636204/todos/46347 )
update we_customer_extend_property
set property_sort = 2
where name = '电话';
update we_customer_extend_property
set property_sort = 3
where name = '邮箱';

-- yiming 补充群发结果userId Tower 任务: 客户群群发详情备注异常 ( https://tower.im/teams/636204/todos/46249 )
UPDATE
we_customer_messgaeresult wcmr
INNER JOIN we_group wg ON wcmr.chat_id = wg.chat_id
SET
wcmr.userid = wg.`owner`
WHERE
wcmr.userid = '';


-- silver_chariot 扩展字段关系表Tower 任务: 重复客户字段信息异常 ( https://tower.im/teams/636204/todos/46428 )
delete from we_customer_extend_property_rel ;
ALTER TABLE `we_customer_extend_property_rel`
    ADD COLUMN `user_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '跟进人userId' AFTER `corp_id`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`corp_id`, `user_id`, `external_userid`, `extend_property_id`, `property_value`);


-- yiming 修改群发备注旧数据 Tower 任务: 群发结果错误语调整 ( https://tower.im/teams/636204/todos/46415 )
UPDATE we_customer_messgaeresult
SET remark = '未执行（员工未执行群发操作）'
WHERE
	`status` = '0';
UPDATE we_customer_messgaeresult
SET remark = '发送成功（员工执行群发并成功送达客户）'
WHERE
	`status` = '1';
UPDATE we_customer_messgaeresult
SET remark = '已不是好友（员工执行群发，但客户已不是好友）'
WHERE
	`status` = '2';
UPDATE we_customer_messgaeresult
SET remark = '其他员工已发送（员工执行群发，但本次群发中其他员工对该客户执行了群发）'
WHERE
	`status` = '3';
UPDATE we_customer_messgaeresult
SET remark = '接收已达上限（该客户的群发接收次数上限）'
WHERE
	`status` = '4';
UPDATE we_customer_messgaeresult
SET remark = '创建失败，小程序未关联企业或信息错误（小程序不可发送）'
WHERE
	`status` = '5';
UPDATE we_customer_messgaeresult
SET remark = '创建失败，未获取到有效附件信息（获取附件失败）")'
WHERE
	`status` = '6';
UPDATE we_customer_messgaeresult
SET remark = '未发送（当员工对群群发时只选择了部分群，剩余群未发送）'
WHERE
	`status` = '7';
UPDATE we_customer_messgaeresult
SET remark = '创建失败，群发内容异常'
WHERE
	`status` = '8';

-- yiming 修改群发备注旧数据 Tower 任务: 群发结果错误语调整 ( https://tower.im/teams/636204/todos/46415 )
UPDATE we_customer_messgaeresult
SET remark = '未执行'
WHERE
	`status` = '0';
UPDATE we_customer_messgaeresult
SET remark = '发送成功'
WHERE
	`status` = '1';
UPDATE we_customer_messgaeresult
SET remark = '已不是好友'
WHERE
	`status` = '2';
UPDATE we_customer_messgaeresult
SET remark = '其他员工已发送'
WHERE
	`status` = '3';
UPDATE we_customer_messgaeresult
SET remark = '接收已达上限'
WHERE
	`status` = '4';
UPDATE we_customer_messgaeresult
SET remark = '创建失败，小程序未关联企业或信息错误'
WHERE
	`status` = '5';
UPDATE we_customer_messgaeresult
SET remark = '创建失败，未获取到有效附件信息'
WHERE
	`status` = '6';
UPDATE we_customer_messgaeresult
SET remark = '未发送'
WHERE
	`status` = '7';
UPDATE we_customer_messgaeresult
SET remark = '创建失败，群发内容异常'
WHERE
	`status` = '8';

ALTER TABLE `we_customer_messgaeresult` 
MODIFY COLUMN `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '0：未执行（员工未执行群发操作）1：发送成功（员工执行群发并成功送达客户）2：已不是好友（员工执行群发，但客户已不是好友）3：其他员工已发送（员工执行群发，但本次群发中其他员工对该客户执行了群发）4：接收已达上限（该客户的群发接收次数上限）5：创建失败，小程序未关联企业或信息错误（小程序不可发送）6：创建失败，未获取到有效附件信息（获取附件失败）7：未发送（当员工对群群发时只选择了部分群，剩余群未发送）8：创建失败，群发内容异常' AFTER `userid`;