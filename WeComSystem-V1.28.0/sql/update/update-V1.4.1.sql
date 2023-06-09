-- yiming 2021.10.29 旧数据兼容 Tower 任务: 只有一条文本没有返回消息类型 ( https://tower.im/teams/636204/todos/45116 )
UPDATE we_customer_seedmessage
SET message_type = '4'
WHERE
	content != '' AND message_type = '';