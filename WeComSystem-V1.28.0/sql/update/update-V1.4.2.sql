-- 2021.11.1 silver_chariot 增加we_customer客户姓名字段长度  Tower 任务: 数据库字段长度调整 ( https://tower.im/teams/636204/todos/45199 )
ALTER TABLE `we_customer`
  MODIFY COLUMN `name` VARCHAR(128) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '外部联系人名称';

-- 2021.11.1 silver_chariot 增加we_allocate_customer_v2离职分配客户姓名字段长度  Tower 任务: 数据库字段长度调整 ( https://tower.im/teams/636204/todos/45199 )
ALTER TABLE `we_allocate_customer_v2`
  MODIFY COLUMN `takeover_userid` VARCHAR(64) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '接替成员的userid',
  MODIFY COLUMN `handover_userid` VARCHAR(64) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '原跟进成员的userid',
  MODIFY COLUMN `customer_name` VARCHAR(128) CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci NOT NULL DEFAULT ''
  COMMENT '外部联系人名称';