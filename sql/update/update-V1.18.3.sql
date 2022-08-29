-- 2022-08-23 silver_chariot 修改统计首屏数据的cron表达式 Tower 任务: easyink首页统计异常 ( https://tower.im/teams/636204/todos/55731 )
update sys_job set cron_expression = '0 0 7 * * ? '
where job_name ='首页数据统计' and invoke_target = 'PageHomeDataTask.getPageHomeDataData()';

-- 2022-08-24 silver_chariot 把授权过的企业authorized都改成1 Tower 任务: 接口调用异常 ( https://tower.im/teams/636204/todos/55809 )
UPDATE we_corp_account a
    LEFT JOIN we_auth_corp_info b ON a.corp_id = b.corp_id
    OR a.external_corp_id = b.corp_id
    SET a.authorized = 1
where b.cancel_auth = 0