-- 2022-08-15 wx 更新we_material表 Tower 任务: 替换素材库默认内容 ( https://tower.im/teams/636204/todos/55509 )

update we_material
set material_name = '联络易宣传海报.jpg',
		material_url = 'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/15/素材库-easyink联络易宣传海报示例.jpg'
		where content = 582656;

update we_material
set material_name = 'easyink企业宣传海报.jpg',
		material_url = 'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2022/08/15/素材库-easyink企业宣传海报示例.jpg'
		where content = 369664;

delete from we_material
where content in (58368,14336,2799616);
-- 2022-08-16 wangzimo 修改群聊统计cron Tower 任务: 新增客户统计数据异常 ( https://tower.im/teams/636204/todos/55477 )
update sys_job
set cron_expression = '0 0 5 * * ? '
where job_name = '群聊数据统计数据拉取' and invoke_target = 'GroupChatStatisticTask.getGroupChatData()' ;
update sys_job
set cron_expression = '0 0 4 * * ? '
where job_name = '联系客户统计数据拉取' and invoke_target = 'UserBehaviorDataTak.getUserBehaviorData()' ;

