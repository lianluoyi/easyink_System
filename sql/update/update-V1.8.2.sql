-- yiming Tower 任务: 更改applicationEntranceUrl的数据 ( https://tower.im/teams/636204/todos/47814 )
UPDATE `we_application_center`
SET `application_entrance_url` = 'http://121.37.253.126:8088'
WHERE `appid` = 2 AND `name` = '青鸾智能工单系统';