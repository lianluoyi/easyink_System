-- tigger 2025-08-11 地址加密字段长度修改 Tower 任务: 侧边栏编辑客户资料异常 ( https://tower.im/teams/636204/todos/114532 )
ALTER TABLE we_flower_customer_rel
    modify COLUMN `address_encrypt` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户地址加密' AFTER `address`;
-- tigger Tower 任务: “安全设置”权限下多出来一个菜单按钮 ( https://tower.im/teams/636204/todos/114603 )
update sys_menu SET status = 1 where menu_id = 2339;