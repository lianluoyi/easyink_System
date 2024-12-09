-- tigger 2024-11-25 明文转换映射关系处理 Tower 任务: 会话存档数据导入增加明文转换 ( https://tower.im/teams/636204/todos/100147 )
ALTER TABLE lock_self_build_config CHANGE self_build_url decrypt_external_userid_url varchar(255) NOT NULL DEFAULT '' COMMENT '请求自建应用解密外部联系人id的url';
ALTER TABLE lock_self_build_config ADD COLUMN decrypt_userid_url varchar(255) NOT NULL DEFAULT '' COMMENT '请求自建应用解密员工id的url';
ALTER TABLE lock_self_build_config COMMENT 'lock 自建应用配置表';
UPDATE lock_self_build_config SET decrypt_userid_url = CONCAT(SUBSTRING_INDEX(decrypt_external_userid_url,'/getFromServiceExternalUserId',1),'/','getOpenUserIdToUserId');

CREATE TABLE `third_external_userid_mapping` (
                                                 `corp_id` varchar(64) NOT NULL COMMENT '密文corpId',
                                                 `external_userid` varchar(64) NOT NULL COMMENT '明文externalUserId',
                                                 `open_external_userid` varchar(64) NOT NULL DEFAULT '' COMMENT '密文externalUserId',
                                                 PRIMARY KEY (`corp_id`,`external_userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方服务商外部联系人externalUserId明密文映射表';

CREATE TABLE `third_user_id_mapping` (
                                         `corp_id` varchar(64) NOT NULL COMMENT '密文企业id',
                                         `user_id` varchar(64) NOT NULL COMMENT '明文userId',
                                         `open_user_id` varchar(64) DEFAULT '' COMMENT '密文userId',
                                         PRIMARY KEY (`corp_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方服务商员工userId明密文映射表';


-- tigger 2024-11-26 sop标签支持过滤模式 Tower 任务: SOP标签筛选方式优化 ( https://tower.im/teams/636204/todos/100034 )
ALTER TABLE we_operations_center_customer_sop_filter ADD COLUMN `include_tag_mode` tinyint(1) NOT NULL DEFAULT '1' COMMENT '包含标签模式, 1:满足全部 2:满足任一' AFTER tag_id;
ALTER TABLE we_operations_center_customer_sop_filter ADD COLUMN `filter_tag_mode` tinyint(1) NOT NULL DEFAULT '2' COMMENT '过滤标签模式, 1:满足全部 2:满足任一' AFTER filter_tag_id;
ALTER TABLE we_operations_center_group_sop_filter ADD COLUMN `include_tag_mode` tinyint(1) NOT NULL DEFAULT '2' COMMENT '包含标签模式, 1:满足全部 2:满足任一' AFTER tag_id;