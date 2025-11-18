-- tigger 2025-09-16 专属活码参数必填 Tower 任务: 专属活码支持按标签组校验必填 ( https://tower.im/teams/636204/todos/115761 )
ALTER TABLE we_emple_code ADD COLUMN `tag_group_valid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标签组校验规则 0:全部标签 1:任一标签 ' after `customer_link`;
