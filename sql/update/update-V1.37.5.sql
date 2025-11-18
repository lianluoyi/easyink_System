-- tigger 2025-07-15 客户报表支持部门维度查询 Tower 任务: 报表统计支持按部门维度查看 ( https://tower.im/teams/636204/todos/112095 )
-- 为we_user_behavior_data表添加部门快照字段，支持快照查询
-- 统计时记录员工当时所属的部门信息，避免因部门变动导致的历史数据不准确

ALTER TABLE `we_user_behavior_data`
    ADD COLUMN `current_department_id` varchar(64) NOT NULL DEFAULT '' COMMENT '员工当前部门ID（快照，记录统计时员工所属的当前部门）' AFTER `user_id`,
ADD COLUMN `current_department_name` varchar(255) NOT NULL DEFAULT '' COMMENT '员工当前部门名称（快照，记录统计时的部门名称）' AFTER `current_department_id`,
ADD COLUMN `parent_department_id` varchar(512) NOT NULL DEFAULT '' COMMENT '完整上级部门路径ID（快照，如：1,2,3 表示从根部门到当前部门的完整路径）' AFTER `current_department_name`,
ADD COLUMN `parent_department_name` varchar(1024) NOT NULL DEFAULT '' COMMENT '完整上级部门路径名称（快照，如：总公司/销售部/华南区 表示完整部门路径）' AFTER `parent_department_id`;

-- 添加部门维度查询索引
CREATE INDEX `idx_corp_current_dept_stat_time` ON `we_user_behavior_data` (`corp_id`, `current_department_id`, `stat_time`) COMMENT '企业-当前部门-统计时间索引';

-- 为历史数据补充部门信息（可选执行，用于兼容历史数据）
-- 注意：完整的部门路径构建需要通过应用程序逻辑处理，这里只做基础的当前部门设置
-- 获取员工当前部门（取department字段的最后一个部门ID作为当前部门）
UPDATE `we_user_behavior_data` ubd
    LEFT JOIN `we_user` wu ON wu.user_id = ubd.user_id AND wu.corp_id = ubd.corp_id
    LEFT JOIN `we_department` wd ON wd.id = SUBSTRING_INDEX(wu.department, ',', -1) AND wd.corp_id = wu.corp_id
    SET
        ubd.current_department_id = IFNULL(SUBSTRING_INDEX(wu.department, ',', -1), ''),
        ubd.current_department_name = IFNULL(wd.name, ''),
        ubd.parent_department_id = IFNULL(SUBSTRING_INDEX(wu.department, ',', -1), ''), -- 临时设置，建议重新运行统计任务
        ubd.parent_department_name = IFNULL(wd.name, '') -- 临时设置，建议重新运行统计任务
WHERE ubd.current_department_id = '';

-- 建议：为了获得完整的部门路径信息，建议在更新字段后重新运行统计任务
-- 这样可以通过应用程序逻辑正确构建完整的部门层级路径