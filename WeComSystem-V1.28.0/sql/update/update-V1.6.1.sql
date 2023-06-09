-- 1*+ 2021-12-01 授权企业表主键corp_id ===> 主键 (corp_id,suite_id) (Tower 任务: 后端变更 ( https://tower.im/teams/636204/todos/46638 ))
ALTER TABLE `we_auth_corp_info`
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`corp_id`, `suite_id`) USING BTREE;
-- 1*+ 授权企业拓展表主键corp_id ===> 主键 (corp_id,suite_id) (Tower 任务: 后端变更 ( https://tower.im/teams/636204/todos/46638 ))
ALTER TABLE `we_auth_corp_info_extend`
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`corp_id`, `suite_id`) USING BTREE;

-- 1*+ 2021-12-01 企业配置表中增加三方应用企业ID =>用于判断该配置应用于那个三方授权企业 (Tower 任务: 后端变更 ( https://tower.im/teams/636204/todos/46638 ))
ALTER TABLE `we_corp_account`
  ADD COLUMN `external_corp_id` VARCHAR(255) NOT NULL DEFAULT ''
COMMENT '三方应用企业ID'
  AFTER `corp_id`;
-- 1*+ 2021-12-01 补偿旧数据记录中的external_corp_id字段 (Tower 任务: 后端变更 ( https://tower.im/teams/636204/todos/46638 ))
UPDATE we_corp_account
SET external_corp_id = corp_id
WHERE external_corp_id = '';
-- 1*+ 2021-12-01 增加三方员工ID和内部员工ID映射关系表 (Tower 任务: 后端变更 ( https://tower.im/teams/636204/todos/46638 ))
CREATE TABLE `we_external_user_mapping_user` (
  `external_corp_id` VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '三方应用企业ID',
  `external_user_id` VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '三方应用员工ID',
  `corp_id`          VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '内部应用企业ID',
  `user_id`          VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '内部应用员工ID',
  PRIMARY KEY (`external_corp_id`, `external_user_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='三方应用员工映射表';