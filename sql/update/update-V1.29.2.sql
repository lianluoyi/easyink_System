-- 2023-06-16 lcy 修改标签表备注 Tower 任务: 标签表说明错误 ( https://tower.im/teams/636204/todos/69757 )
ALTER TABLE we_tag_group MODIFY COLUMN `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 2删除）';
ALTER TABLE we_tag MODIFY COLUMN `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 2删除）';
-- 2023-06-19 lcy 修改标签与员工使用范围表备注 Tower 任务: 触发新客打标签规则异常 ( https://tower.im/teams/636204/todos/69247 )
ALTER TABLE we_auto_tag_user_rel MODIFY COLUMN `type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '传入全部员工/员工/部门 3-全部员工 2-员工 1-部门';