-- tigger 2025-1-15 专属活码sql Tower 任务: 专属活码 ( https://tower.im/teams/636204/todos/99351 )
CREATE TABLE `we_customer_temp_emple_code_setting` (
                                                       `id` bigint(20) NOT NULL,
                                                       `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID',
                                                       `origin_emple_code_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '原员工活码id',
                                                       `remark_open` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否打备注开关',
                                                       `remark_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '备注类型：0：不备注，1：在昵称前，2：在昵称后',
                                                       `remark_name` varchar(32) NOT NULL DEFAULT '' COMMENT '备注名',
                                                       `tag_ids` varchar(4096) NOT NULL DEFAULT '' COMMENT '标签id列表',
                                                       `customer_extend_info` varchar(4096) NOT NULL DEFAULT '' COMMENT '客户扩展信息',
                                                       `state` varchar(30) NOT NULL DEFAULT '' COMMENT '好友回调自定义参数state',
                                                       `config_id` varchar(50) NOT NULL DEFAULT '' COMMENT '新增联系方式的配置id',
                                                       `qr_code` varchar(100) NOT NULL DEFAULT '' COMMENT '二维码链接',
                                                       `expire_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
                                                       `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人(后续员工专属活码存储员工id)',
                                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                       PRIMARY KEY (`id`) USING BTREE,
                                                       KEY `idx_corpId_state` (`corp_id`,`state`),
                                                       KEY `idx_emple_code_id` (`origin_emple_code_id`),
                                                       KEY `idx_config_id` (`config_id`),
                                                       KEY `idx_expire_time` (`corp_id`,`expire_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户专属员工活码配置表';

ALTER TABLE we_emple_code ADD COLUMN `customer_link` VARCHAR ( 128 ) NOT NULL DEFAULT '' COMMENT '客户专属活码链接';

-- 添加删除过期数据定时任务
INSERT INTO `sys_job`(`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`,
                      `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('26', '清除过期专属活码定时任务', 'SYSTEM', 'customerTempEmpleCodeExpireTask.deleteExpireCustomerTempEmpleCode()', '0 0 * * * ?', '2', '1',
        '0', 'admin', '2025-01-15 11:09:36', '', '2025-01-15 11:09:48', '');

-- tigger 修改给客户备注手机号长度 Tower 任务: 客户信息同步异常 ( https://tower.im/teams/636204/todos/101895 )
ALTER table  `we_flower_customer_rel` MODIFY COLUMN `remark_mobiles`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此客户备注的手机号码';

