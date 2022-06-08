-- redhi 2021.10.08 Tower 任务: 敏感行为互发红包文案优化 ( https://tower.im/teams/636204/todos/43693 )
UPDATE we_sensitive_act
SET act_name = '员工/客户发送红包'
WHERE act_name = '互发红包';
