-- 2022-06-17 wx SOP作用范围表 增加类型字段type 用来判断写入的是员工/部门数据 Tower 任务: 客户SOP ( https://tower.im/teams/636204/todos/52978 )
ALTER TABLE we_operations_center_sop_scope
    ADD type tinyint(1) NOT NULL DEFAULT '2' COMMENT '传入员工/部门 2-员工 1部门';

-- 2022-06-17 wx SOP作用范围表 修改字段target_id注释 Tower 任务: 客户SOP ( https://tower.im/teams/636204/todos/52978 )
ALTER TABLE we_operations_center_sop_scope
    modify COLUMN target_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '当为群sop时，为chatId;当为客户sop时，为userId,传入部门时为partyId';

-- 2022-06-17 wx 客户SOP筛选条件表 添加departments字段 用来写入传入的部门信息 Tower 任务: 客户SOP ( https://tower.im/teams/636204/todos/52978 )
ALTER TABLE we_operations_center_customer_sop_filter
    ADD departments text NOT NULL COMMENT '所属部门（多个逗号隔开 ）';

-- 2022-06-20 wx 标签与员工使用范围表 增加类型字段type 用来判断写入的是员工/部门数据 Tower 任务: 自动标签 ( https://tower.im/teams/636204/todos/52973 )
ALTER TABLE we_auto_tag_user_rel
    ADD type tinyint(1) NOT NULL DEFAULT '2' COMMENT '传入员工/部门 2-员工 1部门';

-- 2022-06-20 wx 标签与员工使用范围表 变更user_id字段为target_id字段 用来写入员工id或部门id Tower 任务: 自动标签 ( https://tower.im/teams/636204/todos/52973 )
ALTER TABLE we_auto_tag_user_rel CHANGE user_id target_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'type:2时写入员工id， type:1时写入部门id';

-- 2022-06-21 wx 朋友圈任务信息表 添加添加departments字段 用来写入传入的部门信息 Tower 任务: 朋友圈 ( https://tower.im/teams/636204/todos/52975 )
ALTER TABLE we_moment_task
    ADD departments text NOT NULL COMMENT '所属部门（多个逗号隔开 ）';


-- 2022-06-29 silver_chariot Tower 任务: 关闭druid访问服务 ( https://tower.im/teams/636204/todos/52906 )
delete from sys_menu where menu_id  = 111 and menu_name = '数据监控';

-- 2022-07-04 wx Tower 任务: 后端调整 ( https://tower.im/teams/636204/todos/53841 )
ALTER TABLE we_my_application_use_scope modify COLUMN type tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用类型(1指定员工，2指定角色, 3指定部门)';

-- 2022-07-04 wx Tower 任务: 后端调整 ( https://tower.im/teams/636204/todos/53841 )
ALTER TABLE we_my_application_use_scope modify COLUMN val varchar(255) NOT NULL DEFAULT '' COMMENT '指定员工存userId,指定角色存角色ID,指定部门存部门ID';