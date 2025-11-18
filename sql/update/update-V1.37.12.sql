-- tigger 2025-11-11 Tower 任务: 新增首页数据统计的定时任务 ( https://tower.im/teams/636204/todos/117541 )
INSERT INTO `sys_job`
VALUES (31, '首页数据统计第二次', 'SYSTEM', 'PageHomeDataTask.getPageHomeDataData()', '0 0 13 * * ?', '1', '1', '0', 'admin',
        '2025-11-11 16:52:23', '', '2025-11-11 23:45:07', '');
-- tigger 2025-11-11 长度调整 Tower 任务: 雷达链接相关上限调整 ( https://tower.im/teams/636204/todos/117470 )
ALTER TABLE we_radar modify column `content` varchar (512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '雷达链接摘要';
ALTER TABLE we_material modify column `material_name` varchar (128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片名称';
ALTER TABLE we_material modify column `digest` varchar (512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '摘要';
ALTER TABLE we_customer_seedmessage modify column `link_title` varchar (128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链接消息：图文消息标题';