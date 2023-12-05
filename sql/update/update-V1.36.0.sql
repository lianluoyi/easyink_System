-- 2023-11-15 lcy 去除we_corp_account表的custom_secret字段 Tower 任务: 修改客户联系接口的密钥传参 ( https://tower.im/teams/636204/todos/78219 )
ALTER TABLE we_corp_account DROP COLUMN custom_secret;
-- 2023-11-23 lcy we_emple_code_analyse表新增add_time冗余字段，用于获客链接客户维度显示 Tower 任务: 获客链接详情中添加时间字段异常 ( https://tower.im/teams/636204/todos/77918 )
ALTER TABLE we_emple_code_analyse ADD COLUMN `add_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间（冗余字段），用于获客链接客户维度显示，格式YYYY-MM-DD HH:MM:SS';
-- 2023-11-23 lcy 更新冗余字段的时间为旧数据时间。 Tower 任务: 获客链接详情中添加时间字段异常 ( https://tower.im/teams/636204/todos/77918 )
UPDATE we_emple_code_analyse SET add_time = `time`;