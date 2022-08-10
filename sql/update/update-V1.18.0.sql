-- tigger Tower 任务: 应用管理移除企微Plus ( https://tower.im/teams/636204/todos/55010 ) 删除plus应用
DELETE FROM we_application_center WHERE `appid` = 1 AND  `name` = '企微Plus';

-- 2022.08.03 silver_chariot Tower 任务: 6.28后安装应用的企业企微配置异常 ( https://tower.im/teams/636204/todos/53966 )
ALTER TABLE `we_corp_account`
    ADD COLUMN `authorized`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否授权' AFTER `callback_uri`;

-- 2022.08.03 silver_chariot 删除企微plus素材
delete from we_material where material_name ='企微Pro宣传海报.jpg';

-- tigger 2022-08-09 Tower 任务: 后端 ( https://tower.im/teams/636204/todos/55316 ) 删除we_application_center关联表
DELETE FROM we_my_application WHERE `appid` = 1;

-- 2022-08-09  silver_chariot Tower 任务: admin所属部门名称修改 ( https://tower.im/teams/636204/todos/55327 )
update sys_dept set dept_name = 'EasyInk',leader = 'EasyInk'  where dept_name = 'EasyWeCom';