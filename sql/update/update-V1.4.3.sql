-- 2021-11-03 1*+ Tower 任务: 企微PRO替换为企微plus ( https://tower.im/teams/636204/todos/45269 )
UPDATE `we_application_center`
SET `name` = '企微Plus',
    `logo_url` = 'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/11/03/应用中心-企微Plus.jpeg'
WHERE `name` = '企微PRO';

