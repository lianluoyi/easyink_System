-- tigger 24-11-20 Tower 任务: 客户资料导出增加明文externalUserId ( https://tower.im/teams/636204/todos/99982 )
CREATE TABLE `lock_self_build_config` (
                                          `encrypt_corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id密文',
                                          `self_build_url` varchar(255) NOT NULL DEFAULT '' COMMENT '请求自建应用url',
                                          PRIMARY KEY (`encrypt_corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业公众号配置表';