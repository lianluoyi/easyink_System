-- tigger 2025-08-27 专属活码参数必填 Tower 任务: 专属活码客户字段支持必填校验 ( https://tower.im/teams/636204/todos/115307 )
ALTER TABLE we_customer_extend_property ADD COLUMN `live_code_required`  tinyint(1) NOT NULL DEFAULT '0' COMMENT '专属活码是否必填（1必填0非必填）' after `required`;


-- tigger 2025-08-27 添加客户标签系统字段 Tower 任务: 活码的客户标签支持设置必填项 ( https://tower.im/teams/636204/todos/108908 )
INSERT INTO we_customer_extend_property
(
    corp_id,
    `name`,
    type,
    property_sort,
    create_by
)
SELECT corp_id, '客户标签', 1, 6, 'admin' FROM we_corp_account
    ON DUPLICATE KEY UPDATE
                         `name` = VALUES(`name`),
                         type = VALUES(type),
                         property_sort = VALUES(property_sort),
                         `status` = VALUES(`status`);

-- tigger 2024-08-28 推送设置权限 Tower 任务: 满意度评价结果推送 ( https://tower.im/teams/636204/todos/115322 )
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2342, '编辑推送设置', 2304, 3, '', NULL, 1, 'F', '0', '0', 'intelligentForm:manage:push', '#', 'admin', '2025-08-28 17:43:33', '', NULL, '');

-- tigger 2024-08-28 客户满意度评价结果 Tower 任务: 满意度评价结果推送 ( https://tower.im/teams/636204/todos/115322 )
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, '满意度表单超时检查定时任务(每5分钟)', 'SYSTEM', 'FormSubmitTimeoutTask.checkTimeoutForms()', '0 */5 * * * ?', '2', '1', '0', 'admin', '2023-02-13 16:34:13', '', NULL, '');

CREATE TABLE `we_form_send_record` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
                                       `external_userid` varchar(64) NOT NULL DEFAULT '' COMMENT '客户外部联系人ID',
                                       `user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工用户ID',
                                       `form_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '表单ID',
                                       `source` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发送渠道 1:欢迎语 2:侧边栏',
                                       `sent_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发送时间',
                                       `submit_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '提交时间',
                                       `submit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '提交状态(0:未提交 1:已提交)',
                                       `form_result_data` varchar(4096) NOT NULL DEFAULT '' COMMENT '表单提交内容',
                                       `push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送状态(0:未推送 1:已推送 2:推送失败)',
                                       `push_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '推送时间',
                                       `timeout_push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '超时推送状态(0:未推送 1:已推送 2:推送失败)',
                                       `timeout_push_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '超时推送时间',
                                       `state` varchar(64) NOT NULL DEFAULT '' COMMENT '添加好友state',
                                       `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       KEY `uk_customer_form` (`corp_id`,`user_id`,`external_userid`,`form_id`),
                                       KEY `idx_corp_id_submit_status` (`corp_id`,`submit_status`),
                                       KEY `idx_external_userid` (`external_userid`),
                                       KEY `idx_user_id` (`user_id`),
                                       KEY `idx_sent_time` (`sent_time`),
                                       KEY `idx_timeout_push` (`corp_id`,`submit_status`,`sent_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单发送记录表';

CREATE TABLE `we_third_party_push_log` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
                                           `record_id` bigint(20) NOT NULL COMMENT '关联表单发送记录ID',
                                           `push_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送类型(1:表单提交推送 2:超时未提交推送)',
                                           `push_url` varchar(500) NOT NULL DEFAULT '' COMMENT '推送URL',
                                           `push_data` varchar(1024) NOT NULL DEFAULT '' COMMENT '推送数据(JSON格式)',
                                           `push_result` varchar(1024) NOT NULL DEFAULT '' COMMENT '推送结果',
                                           `http_code` int(11) NOT NULL DEFAULT '-1' COMMENT 'HTTP响应码',
                                           `push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送状态(0:推送中 1:成功 2:失败)',
                                           `error_msg` varchar(1000) NOT NULL DEFAULT '' COMMENT '错误信息',
                                           `push_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '推送时间',
                                           `response_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '响应时间',
                                           `cost_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '耗时(毫秒)',
                                           `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
                                           `update_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
                                           KEY `idx_corp_id_push_status` (`corp_id`,`push_status`),
                                           KEY `idx_record_id_push_type` (`record_id`,`push_type`),
                                           KEY `idx_push_time` (`push_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方推送日志表';
-- 第三方推送表
CREATE TABLE `we_corp_third_party_config` (
                                              `corp_id` varchar(64) NOT NULL COMMENT '企业ID',
                                              `push_url` varchar(500) NOT NULL DEFAULT '' COMMENT '第三方推送URL',
                                              `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(0:停用 1:启用)',
                                              `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
                                              `update_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              PRIMARY KEY (`corp_id`),
                                              KEY `idx_corp_id_status` (`corp_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业第三方服务推送配置表';

alter table we_form_advance_setting add column `push_content_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送表单提交内容开关(false:关闭 true:开启)' after `label_setting_json`;
alter table we_form_advance_setting add column `customer_property_id_json` varchar(1024) NOT NULL DEFAULT '' COMMENT '客户字段idjson' after `push_content_flag`;
alter table we_form_advance_setting add column `timeout_hours` int(11) NOT NULL DEFAULT '0' COMMENT '超时推送时间配置,单位小时, 0则不推送' after `customer_property_id_json`;
