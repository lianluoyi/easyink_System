-- tigger 2025-10-17 更新异常遗漏的initsql, 解决后续添加的jobid的冲突 Tower 任务: 补充缺失的数据统计定时任务sql ( https://tower.im/teams/636204/todos/116699 )
UPDATE sys_job SET job_id = 28 WHERE job_id = 25 and job_name = '更新获客链接消耗情况定时任务' and invoke_target = 'customerAssistantSituationTask.getCustomerAssistantSituationData()' and job_group = 'SYSTEM';
UPDATE sys_job SET job_id = 29 WHERE job_id = 26 and job_name = '清除过期专属活码定时任务' and invoke_target = 'customerTempEmpleCodeExpireTask.deleteExpireCustomerTempEmpleCode()' and job_group = 'SYSTEM';
UPDATE sys_job SET job_id = 30 WHERE job_id = 27 and job_name = '满意度表单超时检查定时任务(每5分钟)' and invoke_target = 'FormSubmitTimeoutTask.checkTimeoutForms()' and job_group = 'SYSTEM';

INSERT IGNORE INTO `sys_job`
VALUES ('25', '联系客户统计数据拉取', 'SYSTEM', 'UserBehaviorDataTak.getUserBehaviorData()', '0 0 12 * * ?', '1', '1', '0', 'admin',
        '2023-07-11 10:15:59', 'admin', '2023-07-11 10:16:37', '');
INSERT IGNORE INTO `sys_job`
VALUES ('26', '群聊数据统计数据拉取', 'SYSTEM', 'GroupChatStatisticTask.getGroupChatData()', '0 30 12 * * ? ', '1', '1', '0', 'admin',
        '2023-07-11 10:16:44', '', '2023-07-11 10:16:16', '');
INSERT IGNORE INTO `sys_job`
VALUES ('27', '数据统计定时任务', 'SYSTEM', 'DataStatisticsTask.getDataStatistics()', '0 30 13 * * ?', '2', '1', '0', 'admin',
        '2023-07-11 10:17:13', '', NULL, '');