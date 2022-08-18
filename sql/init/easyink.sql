/*
 Navicat Premium Data Transfer

 Source Server         : wecom_scrm_test
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : localhost:9999
 Source Schema         : wecomscrm

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 09/08/2021 22:01:10
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_id`    int(11) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
    `config_key`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
    `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
    `config_type`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time`  datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time`  datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config`
VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config`
VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', 'easyink2021', 'Y', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '初始化密码 easyink2021');
INSERT INTO `sys_config`
VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '深色主题theme-dark，浅色主题theme-light');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `dept_id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `parent_id`   bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
    `ancestors`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
    `dept_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
    `order_num`   int(11) NULL DEFAULT 0 COMMENT '显示顺序',
    `leader`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
    `phone`       varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
    `email`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    `del_flag`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept`
VALUES (100, 0, '0', 'EasyInk', 0, 'EasyInk', '', '', '0', '0', 'admin', '2018-03-16 11:33:00', 'admin',
        '2021-08-09 20:34:24');
INSERT INTO `sys_dept`
VALUES (101, 100, '0,100', '总经理办公室', 1, 'admin', NULL, NULL, '0', '0', 'admin', '2021-08-09 20:33:52', '', NULL);
INSERT INTO `sys_dept`
VALUES (102, 100, '0,100', '研发部', 2, 'depart', NULL, NULL, '0', '0', 'admin', '2021-08-09 20:34:10', 'admin',
        '2021-08-09 20:34:24');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`
(
    `dict_code`   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort`   int(11) NULL DEFAULT 0 COMMENT '字典排序',
    `dict_label`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
    `dict_value`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
    `dict_type`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
    `css_class`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
    `list_class`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
    `is_default`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data`
VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00',
        '性别男');
INSERT INTO `sys_dict_data`
VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00',
        '性别女');
INSERT INTO `sys_dict_data`
VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00',
        '性别未知');
INSERT INTO `sys_dict_data`
VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '显示菜单');
INSERT INTO `sys_dict_data`
VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '隐藏菜单');
INSERT INTO `sys_dict_data`
VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data`
VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '停用状态');
INSERT INTO `sys_dict_data`
VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data`
VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '停用状态');
INSERT INTO `sys_dict_data`
VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '默认分组');
INSERT INTO `sys_dict_data`
VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '系统分组');
INSERT INTO `sys_dict_data`
VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '系统默认是');
INSERT INTO `sys_dict_data`
VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '系统默认否');
INSERT INTO `sys_dict_data`
VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '通知');
INSERT INTO `sys_dict_data`
VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '公告');
INSERT INTO `sys_dict_data`
VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data`
VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '关闭状态');
INSERT INTO `sys_dict_data`
VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '新增操作');
INSERT INTO `sys_dict_data`
VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '修改操作');
INSERT INTO `sys_dict_data`
VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '删除操作');
INSERT INTO `sys_dict_data`
VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '授权操作');
INSERT INTO `sys_dict_data`
VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '导出操作');
INSERT INTO `sys_dict_data`
VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '导入操作');
INSERT INTO `sys_dict_data`
VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '强退操作');
INSERT INTO `sys_dict_data`
VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '生成操作');
INSERT INTO `sys_dict_data`
VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '清空操作');
INSERT INTO `sys_dict_data`
VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data`
VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry',
        '2018-03-16 11:33:00', '停用状态');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `dict_id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
    `dict_type`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_id`) USING BTREE,
    UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type`
VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '用户性别列表');
INSERT INTO `sys_dict_type`
VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '菜单状态列表');
INSERT INTO `sys_dict_type`
VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统开关列表');
INSERT INTO `sys_dict_type`
VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '任务状态列表');
INSERT INTO `sys_dict_type`
VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '任务分组列表');
INSERT INTO `sys_dict_type`
VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统是否列表');
INSERT INTO `sys_dict_type`
VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '通知类型列表');
INSERT INTO `sys_dict_type`
VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '通知状态列表');
INSERT INTO `sys_dict_type`
VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '操作类型列表');
INSERT INTO `sys_dict_type`
VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '登录状态列表');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `job_id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '任务名称',
    `job_group`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
    `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
    `misfire_policy`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time`     datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time`     datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
    PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job`
VALUES (4, '会话存档', 'SYSTEM', 'ryTask.FinanceTask', '0 */2 * * * ?', '3', '1', '0', 'admin', '2020-12-13 12:14:38',
        'admin', '2021-07-30 15:23:08', '');
INSERT INTO `sys_job`
VALUES (6, '定时消息群发', 'SYSTEM', 'ryTask.messageTask', '0 */2 * * * ?', '1', '1', '0', 'admin', '2021-02-10 10:07:22',
        'admin', '2021-07-27 09:09:55', '');
INSERT INTO `sys_job`
VALUES (7, '联系客户统计数据拉取', 'SYSTEM', 'UserBehaviorDataTak.getUserBehaviorData()', '0 0 4 * * ?', '1', '1', '0', 'admin',
        '2021-02-25 16:47:59', 'admin', '2021-02-25 23:45:03', '');
INSERT INTO `sys_job`
VALUES (8, '群聊数据统计数据拉取', 'SYSTEM', 'GroupChatStatisticTask.getGroupChatData()', '0 0 5 * * ? ', '1', '1', '0', 'admin',
        '2021-02-25 16:49:44', '', '2021-02-25 23:45:05', '');
INSERT INTO `sys_job`
VALUES (9, '首页数据统计', 'SYSTEM', 'PageHomeDataTask.getPageHomeDataData()', '0 0 3 * * ? ', '1', '1', '0', 'admin',
        '2021-02-25 16:52:23', '', '2021-02-25 23:45:07', '');
INSERT INTO `sys_job`
VALUES (10, '删除过期素材', 'SYSTEM', 'RemoveMaterialTask.removeExpireMaterial()', '0 0 4 * * ? ', '1', '1', '0', 'admin',
        '2021-10-13 00:00:00', 'admin', '2021-10-13 00:00:00', '');
INSERT INTO `sys_job`
VALUES (11, '通过好友的开启关闭时间检测', 'SYSTEM', 'EmpleCodeThroughFriendTimeSwitchTask.empleCodeThroughFriendTimeSwitch()',
        '0 * * * * ? ', '1', '1', '0', 'admin', '2021-11-03 00:00:00', 'admin', '2021-11-03 00:00:00', '');
INSERT INTO `sys_job`
VALUES (12, '定时检查客户群活码', 'SYSTEM', 'GroupCodeActualTimeTask.findExpireCode()', '0 0 10 * * ? *', '1', '1', '0', 'admin',
        '2021-11-8 00:00:00', 'admin', '2021-11-8 00:00:00', '');
INSERT INTO `sys_job`
VALUES (13, '定时拉取群发未发送消息结果', 'SYSTEM', 'MessageResultTask.asyncSendResult()', '0 0/2 * * * ? ', '1', '1', '0', 'admin',
        '2021-11-16 00:00:00', 'admin', '2021-11-16 00:00:00', '');
INSERT INTO `sys_job`
VALUES (14, '待办事项提醒任务', 'SYSTEM', 'todoReminderTask.execute', '0 */5 * * * ?', '1', '1', '0', 'admin',
        '2021-11-19 16:06:51', 'admin', '2021-11-19 16:19:19', '');
INSERT INTO `sys_job`
VALUES (15, '运营中心SOP任务', 'SYSTEM', 'weOperationsCenterSopTask.execute', '0 * * * * ?', '1', '1', '0', 'admin',
        '2021-12-03 00:00:00', 'admin', '2021-12-03 00:00:00', '');
INSERT INTO `sys_job`
VALUES (16, '更新离职员工客户接替结果任务', 'SYSTEM', 'transferResignedResultTask.execute', '0 /10 * * * ? *', '1', '1', '0',
        'admin', '2021-12-08 14:43:11', '', '2021-12-08 14:43:17', '');
INSERT INTO `sys_job`
VALUES (17, '更新企业朋友圈创建结果', 'SYSTEM', 'momentUpdateCreatedStatusTask.updateMomentTaskStatus()', '0/10 * * * * ? ', '1',
        '1', '0',
        'admin', '2022-1-11 14:43:11', '', '2022-1-11 14:43:11', '');
INSERT INTO `sys_job`
VALUES (18, '更新企业朋友圈执行结果', 'SYSTEM', 'momentPublishStatusTask.updateMomentPublishStatus()', '0 0/30 * * * ? *', '1',
        '1', '0',
        'admin', '2022-1-12 14:43:11', '', '2022-1-12 14:43:11', '');
INSERT INTO `sys_job`
VALUES (19, '发送定时朋友圈任务', 'SYSTEM', 'momentStartCreateTask.createMoment()', '0 0/2 * * * ? *', '1', '1', '0',
        'admin', '2022-1-12 14:43:11', '', '2022-1-12 14:43:11', '');

INSERT INTO `sys_job`
VALUES (20, '定期查询客户分配情况任务', 'SYSTEM', 'transferResultTask.execute', '0 */5 * * * ?', '2', '1', '0', 'admin',
        '2021-12-01 18:25:03', 'admin', '2021-12-01 18:25:51', '');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
    `order_num`   int(11) NULL DEFAULT 0 COMMENT '显示顺序',
    `path`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
    `is_frame`    int(11) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    `menu_type`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`     char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu`
VALUES ('1', '系统设置', '0', '70', 'system', null, '1', 'M', '0', '0', '', 'tree', 'admin', '2018-03-16 11:33:00', 'admin',
        '2021-08-04 14:13:51', '系统管理目录');
INSERT INTO `sys_menu`
VALUES ('2', '系统监控', '0', '90', 'monitor', null, '1', 'M', '0', '0', '', 'monitor', 'admin', '2018-03-16 11:33:00',
        'admin', '2020-10-22 01:22:31', '系统监控目录');
INSERT INTO `sys_menu`
VALUES ('101', '角色管理', '2283', '6', 'role', 'system/role/index', '1', 'C', '0', '0', 'system:role:list', '#', 'admin',
        '2018-03-16 11:33:00', 'admin', '2021-08-25 22:43:01', '角色管理菜单');
INSERT INTO `sys_menu`
VALUES ('102', '菜单管理', '2283', '11', 'menu', 'system/menu/index', '1', 'C', '0', '0', 'system:menu:list', '#', 'admin',
        '2018-03-16 11:33:00', 'admin', '2021-08-27 21:32:15', '菜单管理菜单');
INSERT INTO `sys_menu`
VALUES ('108', '日志管理', '2', '9', 'log', 'system/log/index', '1', 'M', '0', '0', '', 'log-analyse', 'admin',
        '2018-03-16 11:33:00', 'admin', '2022-02-28 11:27:53', '日志管理菜单');
INSERT INTO `sys_menu`
VALUES ('109', '在线账号', '2284', '1', 'online', 'monitor/online/index', '1', 'C', '0', '0', 'monitor:online:list', '#',
        'admin', '2018-03-16 11:33:00', 'admin', '2021-08-09 17:17:53', '在线用户菜单');
INSERT INTO `sys_menu`
VALUES ('110', '定时任务', '2284', '2', 'job', 'monitor/job/index', '1', 'C', '0', '0', 'monitor:job:list', '#', 'admin',
        '2018-03-16 11:33:00', 'admin', '2021-08-06 15:33:30', '定时任务菜单');
INSERT INTO `sys_menu`
VALUES ('112', '服务监控', '2284', '4', 'server', 'monitor/server/index', '1', 'C', '0', '0', 'monitor:server:list', '#',
        'admin', '2018-03-16 11:33:00', 'admin', '2021-08-06 15:33:35', '服务监控菜单');
INSERT INTO `sys_menu`
VALUES ('500', '操作日志', '108', '3', 'operlog', 'monitor/operlog/index', '1', 'C', '0', '0', 'monitor:operlog:list', '#',
        'admin', '2018-03-16 11:33:00', 'admin', '2021-08-09 13:59:07', '操作日志菜单');
INSERT INTO `sys_menu`
VALUES ('501', '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '1', 'C', '0', '0',
        'monitor:logininfor:list', '#', 'admin', '2018-03-16 11:33:00', 'admin', '2021-08-09 17:18:10', '登录日志菜单');
INSERT INTO `sys_menu`
VALUES ('1009', '新增角色', '101', '2', '', '', '1', 'F', '0', '0', 'system:role:add', '#', 'admin', '2018-03-16 11:33:00',
        'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1010', '编辑角色', '101', '3', '', '', '1', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2018-03-16 11:33:00',
        'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1011', '删除角色', '101', '4', '', '', '1', 'F', '0', '0', 'system:role:remove', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1014', '新增', '102', '2', '', '', '1', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2018-03-16 11:33:00',
        'admin', '2021-08-30 11:12:47', '');
INSERT INTO `sys_menu`
VALUES ('1015', '修改', '102', '3', '', '', '1', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2018-03-16 11:33:00',
        'admin', '2021-08-30 11:12:56', '');
INSERT INTO `sys_menu`
VALUES ('1016', '删除', '102', '4', '', '', '1', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2018-03-16 11:33:00',
        'admin', '2021-08-30 11:13:00', '');
INSERT INTO `sys_menu`
VALUES ('1018', '新增部门', '2201', '9', '', '', '1', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2018-03-16 11:33:00',
        'admin', '2021-08-26 11:32:52', '');
INSERT INTO `sys_menu`
VALUES ('1019', '编辑部门', '2201', '10', '', '', '1', 'F', '0', '0', 'system:dept:edit', '#', 'admin',
        '2018-03-16 11:33:00', 'admin', '2021-08-26 11:33:02', '');
INSERT INTO `sys_menu`
VALUES ('1020', '删除部门', '2201', '11', '', '', '1', 'F', '0', '0', 'system:dept:remove', '#', 'admin',
        '2018-03-16 11:33:00', 'admin', '2021-08-26 11:33:07', '');
INSERT INTO `sys_menu`
VALUES ('1041', '删除日志', '500', '2', '#', '', '1', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1044', '删除日志', '501', '2', '#', '', '1', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1048', '强制退出', '109', '3', '#', '', '1', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1050', '新增定时任务', '110', '2', '#', '', '1', 'F', '0', '0', 'monitor:job:add', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1051', '编辑定时任务', '110', '3', '#', '', '1', 'F', '0', '0', 'monitor:job:edit', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1052', '删除定时任务', '110', '4', '#', '', '1', 'F', '0', '0', 'monitor:job:remove', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1053', '执行定时任务', '110', '5', '#', '', '1', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('1054', '导出定时任务', '110', '7', '#', '', '1', 'F', '0', '0', 'monitor:job:export', '#', 'admin',
        '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_menu`
VALUES ('2001', '客户中心', '0', '2', 'customerManage', null, '1', 'M', '0', '0', '', 'customer-manage', 'admin',
        '2020-10-20 22:03:52', '王子默', '2021-09-02 21:46:21', '');
INSERT INTO `sys_menu`
VALUES ('2002', '客户', '2280', '10', 'customer', 'customerManage/customer', '1', 'C', '0', '0',
        'customerManage:customer:list', '#', 'admin', '2020-10-20 22:15:31', 'admin', '2021-08-03 16:41:08', '');
INSERT INTO `sys_menu`
VALUES ('2003', '客户群', '2280', '20', 'group', 'customerManage/group', '1', 'C', '0', '0', 'customerManage:group:list',
        '#', 'admin', '2020-10-20 22:32:55', 'admin', '2021-08-04 14:51:34', '');
INSERT INTO `sys_menu`
VALUES ('2004', '标签管理', '2280', '30', 'tag', 'customerManage/tag', '1', 'C', '0', '0', 'customerManage:tag:list', '#',
        'admin', '2020-10-20 22:33:45', 'admin', '2021-08-03 16:42:19', '');
INSERT INTO `sys_menu`
VALUES ('2005', '离职继承', '2179', '40', 'dimission', 'customerManage/dimission/index', '1', 'C', '0', '0',
        'customerManage:dimission:list', '#', 'admin', '2020-10-20 22:34:59', 'admin', '2021-08-06 10:06:15', '');
INSERT INTO `sys_menu`
VALUES ('2006', '客户详情', '2001', '11', 'customerCenter/customerDetail', 'customerManage/customerDetail', '1', 'P', '1',
        '0',
        'customerManage:customerDetail:list', '#', 'admin', '2020-10-20 22:37:06', 'admin', '2021-09-01 22:57:07', '');
INSERT INTO `sys_menu`
VALUES ('2007', '客户群聊详情', '2001', '21', 'customerCenter/groupDetail', 'customerManage/groupDetail', '1', 'P', '1', '0',
        'customerManage:groupDetail:list', '#', 'admin', '2020-10-20 22:37:59', 'admin', '2021-09-01 22:58:02', '');
INSERT INTO `sys_menu`
VALUES ('2010', '企微配置', '2283', '7', 'enterpriseWechat', 'enterpriseWechat/list', '1', 'C', '0', '0',
        'system:enterpriseWechat:list', '#', 'admin', '2020-10-20 22:48:05', 'admin', '2021-08-27 21:31:57', '');
INSERT INTO `sys_menu`
VALUES ('2013', '导出列表', '2002', '2', '', null, '1', 'F', '0', '0', 'customerManage:customer:export', '#', 'admin',
        '2020-10-22 18:22:39', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2014', '打标签', '2002', '3', '', null, '1', 'F', '0', '0', 'customerManage:customer:makeTag', '#', 'admin',
        '2020-10-22 18:25:47', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2015', '移除标签', '2002', '4', '', null, '1', 'F', '0', '0', 'customerManage:customer:removeTag', '#', 'admin',
        '2020-10-22 18:26:24', 'admin', '2021-08-09 18:14:24', '');
INSERT INTO `sys_menu`
VALUES ('2016', '同步客户', '2002', '1', '', null, '1', 'F', '0', '0', 'customerManage:customer:sync', '#', 'admin',
        '2020-10-22 18:28:27', 'admin', '2021-08-30 10:55:33', '');
INSERT INTO `sys_menu`
VALUES ('2020', '同步客户群', '2003', '3', '', null, '1', 'F', '0', '0', 'customerManage:group:sync', '#', 'admin',
        '2020-10-22 18:35:13', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2021', '查看客户群详情', '2003', '4', '', null, '1', 'F', '0', '0', 'customerManage:group:view', '#', 'admin',
        '2020-10-22 18:40:52', 'admin', '2021-08-09 19:12:47', '');
INSERT INTO `sys_menu`
VALUES ('2022', '查看客户详情', '2002', '7', '', null, '1', 'F', '0', '0', 'customerManage:customer:view', '#', 'admin',
        '2020-10-22 18:43:41', 'admin', '2021-08-09 18:17:58', '');
INSERT INTO `sys_menu`
VALUES ('2023', '新建标签组', '2004', '1', '', null, '1', 'F', '0', '0', 'customerManage:tag:add', '#', 'admin',
        '2020-10-22 18:46:02', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2024', '同步标签组', '2004', '2', '', null, '1', 'F', '0', '0', 'customerManage:tag:sync', '#', 'admin',
        '2020-10-22 18:46:33', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2025', '编辑标签组', '2004', '3', '', null, '1', 'F', '0', '0', 'customerManage:tag:edit', '#', 'admin',
        '2020-10-22 18:47:00', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2026', '删除标签组', '2004', '4', '', null, '1', 'F', '0', '0', 'customerManage:tag:remove', '#', 'admin',
        '2020-10-22 18:47:20', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2028', '查看已分配员工', '2179', '2', 'allocatedStaffList', 'customerManage/dimission/allocatedStaffList', '1', 'P',
        '1', '0', '', '#', 'admin', '2020-10-22 18:55:40', 'admin', '2021-09-02 15:17:29', '');
INSERT INTO `sys_menu`
VALUES ('2029', '分配离职员工', '2005', '3', '', null, '1', 'F', '0', '0', 'customerManage:dimission:allocate', '#', 'admin',
        '2020-10-22 18:56:28', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2050', '已分配离职员工', '2005', '41', 'allocatedStaffList', 'customerManage/dimission/allocatedStaffList', '1', 'C',
        '1', '0', 'customerManage:dimission:allocatedStaffList', '#', 'admin', '2020-10-28 15:11:17', 'admin',
        '2020-12-13 16:25:53', '');
INSERT INTO `sys_menu`
VALUES ('2051', '查看已分配员工详情', '2179', '3', 'allocatedStaffDetail', 'customerManage/dimission/allocatedStaffDetail', '1',
        'P', '1', '0', '', '#', 'admin', '2020-10-28 15:12:49', 'admin', '2021-09-02 20:31:00', '');
INSERT INTO `sys_menu`
VALUES ('2052', '引流获客', '2188', '2', 'drainageCode', null, '1', 'M', '0', '0', '', 'huoma-manage', 'admin',
        '2020-11-08 12:11:44', 'admin', '2022-02-28 11:02:27', '');
INSERT INTO `sys_menu`
VALUES ('2053', '员工活码', '2052', '10', 'staff', 'drainageCode/staff/list', '1', 'C', '0', '0', '', '#', 'admin',
        '2020-11-08 12:13:21', 'admin', '2021-08-04 15:29:55', '');
INSERT INTO `sys_menu`
VALUES ('2056', '客户群活码', '2052', '12', 'customerGroup', 'drainageCode/group/list', '1', 'C', '0', '0', '', '#', 'admin',
        '2020-11-08 12:26:15', 'admin', '2021-02-22 15:32:34', '');
INSERT INTO `sys_menu`
VALUES ('2060', '欢迎语', '2052', '45', 'welcome', 'drainageCode/welcome/list', '1', 'C', '0', '0', '', '#', 'admin',
        '2020-11-08 12:39:34', 'admin', '2021-11-08 16:43:15', '');
INSERT INTO `sys_menu`
VALUES ('2062', '内容管理', '2188', '5', 'material', null, '1', 'M', '0', '0', '', 'content-manage', 'admin',
        '2020-11-08 12:41:21', 'admin', '2022-02-28 11:14:37', '');
INSERT INTO `sys_menu`
VALUES ('2071', '添加素材', '2189', '4', '', null, '1', 'F', '0', '0', 'wechat:material:add', '#', 'admin',
        '2020-11-08 13:04:22', 'admin', '2021-08-09 18:08:25', '');
INSERT INTO `sys_menu`
VALUES ('2072', '删除素材', '2189', '6', '', null, '1', 'F', '0', '0', 'wechat:material:remove', '#', 'admin',
        '2020-11-08 13:04:57', 'admin', '2021-08-30 11:11:33', '');
INSERT INTO `sys_menu`
VALUES ('2073', '编辑素材', '2189', '5', '', null, '1', 'F', '0', '0', 'wechat:material:edit', '#', 'admin',
        '2020-11-08 13:05:22', 'admin', '2021-08-30 11:11:29', '');
INSERT INTO `sys_menu`
VALUES ('2074', '编辑客户', '2002', '10', '', null, '1', 'F', '0', '0', 'customerManage:customer:edit', '#', 'admin',
        '2020-11-08 15:46:19', 'admin', '2021-08-09 18:17:07', '');
INSERT INTO `sys_menu`
VALUES ('2076', '发布素材', '2189', '8', '', null, '1', 'F', '0', '0', 'wechat:material:publish', '#', 'admin',
        '2020-11-09 11:12:29', 'admin', '2021-08-09 18:09:06', '');
INSERT INTO `sys_menu`
VALUES ('2079', '企业风控', '0', '8', 'conversation', null, '1', 'M', '0', '0', '', 'chat-archive', 'admin',
        '2020-12-07 10:33:45', 'admin', '2021-08-05 15:13:00', '');
INSERT INTO `sys_menu`
VALUES ('2080', '会话存档', '2282', '10', 'content', 'conversation/content', '1', 'C', '0', '0', '', '#', 'admin',
        '2020-12-07 10:35:56', 'admin', '2021-08-05 16:31:57', '');
INSERT INTO `sys_menu`
VALUES ('2081', '消息审计', '2282', '20', 'security', 'conversation/security', '1', 'C', '0', '0', '', '#', 'admin',
        '2020-12-07 10:38:12', 'admin', '2021-08-05 16:32:01', '');
INSERT INTO `sys_menu`
VALUES ('2082', '流失提醒', '2179', '50', 'lossRemind', 'customerManage/lossRemind', '1', 'C', '0', '0',
        'customerManage:lossRemind', '#', 'admin', '2020-12-13 16:22:25', 'admin', '2020-12-13 16:23:18', '');
INSERT INTO `sys_menu`
VALUES ('2083', '群发管理', '2188', '20', 'groupMessage', null, '1', 'M', '0', '0', '', 'guide', 'admin',
        '2020-12-14 21:40:40', 'admin', '2022-02-28 11:11:08', '');
INSERT INTO `sys_menu`
VALUES ('2084', '新增群发', '2083', '10', 'add', 'groupMessage/add', '1', 'C', '0', '0', 'customerMessagePush:push:add',
        '#', 'admin', '2020-12-14 21:41:38', '王子默', '2021-09-02 22:21:35', '');
INSERT INTO `sys_menu`
VALUES ('2085', '群发记录', '2083', '20', 'record', 'groupMessage/record', '1', 'C', '0', '0',
        'customerMessagePush:push:list', '#', 'admin', '2020-12-14 21:44:36', 'admin', '2021-02-21 23:16:52', '');
INSERT INTO `sys_menu`
VALUES ('2086', '设置通知提醒', '2082', '1', 'wechatCorp', 'wechat:corp:startCustomerChurnNoticeSwitch', '1', 'F', '0', '0',
        'wechat:corp:startCustomerChurnNoticeSwitch', 'switch', 'admin', '2020-12-15 01:52:19', 'admin',
        '2020-12-15 01:55:07', '');
INSERT INTO `sys_menu`
VALUES ('2100', '社群管理', '2188', '25', 'communityOperating', null, '1', 'M', '0', '1', '', '#', 'admin',
        '2020-12-30 21:27:29', 'admin', '2021-08-09 17:15:03', '');
INSERT INTO `sys_menu`
VALUES ('2101', '新客进群', '2052', '15', 'newCustomer', 'communityOperating/newCustomer/list', '1', 'C', '0', '0',
        'communityOperating/newCustomer/list', '#', 'admin', '2020-12-30 21:29:50', 'admin', '2021-08-04 16:13:54', '');
INSERT INTO `sys_menu`
VALUES ('2102', '老客进群', '2052', '30', 'tag', 'communityOperating/oldCustomer/list', '1', 'C', '0', '0',
        'oldCustomer/list', '#', 'admin', '2020-12-30 21:30:31', 'admin', '2021-08-27 21:26:30', '');
INSERT INTO `sys_menu`
VALUES ('2105', '查看群发详情', '2083', '1', 'detail', 'groupMessage/detail', '1', 'P', '1', '0', '', '#', 'admin',
        '2020-12-30 22:35:29', 'admin', '2021-08-30 11:19:12', '');
INSERT INTO `sys_menu`
VALUES ('2106', '新客拉群增改查', '2052', '20', 'newCustomerAev', 'communityOperating/newCustomer/aev', '1', 'P', '1', '0',
        'communityOperating/newCustomer/aev', '#', 'admin', '2020-12-31 19:36:24', 'admin', '2021-09-02 15:07:45', '');
INSERT INTO `sys_menu`
VALUES ('2107', '编辑老客进群', '2052', '40', 'oldCustomerAev', 'communityOperating/oldCustomer/aev', '1', 'P', '1', '0',
        'communityOperating/oldCustomer/aev', '#', 'admin', '2020-12-31 19:37:51', 'admin', '2021-09-02 15:08:07', '');
INSERT INTO `sys_menu`
VALUES ('2120', '同步群发结果', '2085', '3', '', null, '1', 'F', '0', '0', 'customerMessagePush:push:asyncResult', '#',
        'admin', '2021-02-21 23:19:49', 'admin', '2021-08-30 11:09:29', '');
INSERT INTO `sys_menu`
VALUES ('2124', '查看敏感行为记录', '2081', '5', '', null, '1', 'F', '0', '0', 'wecom:sensitiveact:list', '#', 'WeCome',
        '2021-02-22 11:48:41', 'admin', '2021-08-30 11:04:16', '');
INSERT INTO `sys_menu`
VALUES ('2127', '管理敏感行为', '2081', '6', '', null, '1', 'F', '0', '0', 'wecom:sensitiveact:edit', '#', 'admin',
        '2021-02-22 11:50:59', 'admin', '2021-08-30 11:04:26', '');
INSERT INTO `sys_menu`
VALUES ('2131', '查看敏感消息', '2081', '1', '', null, '1', 'F', '0', '0', 'wecom:sensitive:list', '#', 'admin',
        '2021-02-22 15:16:10', 'admin', '2021-08-30 10:56:51', '');
INSERT INTO `sys_menu`
VALUES ('2133', '添加敏感词', '2081', '2', '', null, '1', 'F', '0', '0', 'wecom:sensitive:add', '#', 'admin',
        '2021-02-22 15:16:52', 'admin', '2021-08-30 10:57:02', '');
INSERT INTO `sys_menu`
VALUES ('2134', '编辑敏感词', '2081', '3', '', null, '1', 'F', '0', '0', 'wecom:sensitive:edit', '#', 'admin',
        '2021-02-22 15:17:27', 'admin', '2021-08-30 11:03:50', '');
INSERT INTO `sys_menu`
VALUES ('2135', '删除敏感词', '2081', '4', '', null, '1', 'F', '0', '0', 'wecom:sensitive:remove', '#', 'admin',
        '2021-02-22 15:17:51', 'admin', '2021-08-30 11:04:08', '');
INSERT INTO `sys_menu`
VALUES ('2138', '查看活码详情', '2053', '7', '', null, '1', 'F', '0', '0', 'wecom:code:query', '#', 'admin',
        '2021-02-22 15:35:10', 'admin', '2021-08-30 11:07:25', '');
INSERT INTO `sys_menu`
VALUES ('2139', '新增员工活码', '2053', '2', '', null, '1', 'F', '0', '0', 'wecom:code:add', '#', 'admin',
        '2021-02-22 15:35:29', 'admin', '2021-08-30 11:06:49', '');
INSERT INTO `sys_menu`
VALUES ('2141', '编辑员工活码', '2053', '6', '', null, '1', 'F', '0', '0', 'wecom:code:edit', '#', 'admin',
        '2021-02-22 15:36:17', 'admin', '2021-08-30 11:07:18', '');
INSERT INTO `sys_menu`
VALUES ('2142', '删除员工活码', '2053', '3', '', null, '1', 'F', '0', '0', 'wecom:code:remove', '#', 'admin',
        '2021-02-22 15:36:34', 'admin', '2021-08-30 11:06:58', '');
INSERT INTO `sys_menu`
VALUES ('2151', '新建群活码', '2056', '1', '', null, '1', 'F', '0', '0', 'wecom:groupCode:add', '#', 'admin',
        '2021-02-22 15:42:43', 'admin', '2021-08-30 11:07:41', '');
INSERT INTO `sys_menu`
VALUES ('2152', '编辑群活码', '2056', '6', '', null, '1', 'F', '0', '0', 'wecom:groupCode:edit', '#', 'admin',
        '2021-02-22 15:43:06', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2153', '删除群活码', '2056', '2', '', null, '1', 'F', '0', '0', 'wecom:groupCode:remove', '#', 'admin',
        '2021-02-22 15:43:24', 'admin', '2021-08-30 11:07:47', '');
INSERT INTO `sys_menu`
VALUES ('2156', '新增好友欢迎语', '2060', '3', '', null, '1', 'F', '0', '0', 'wecom:tlp:add', '#', 'admin',
        '2021-02-22 15:44:30', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2157', '编辑好友欢迎语', '2060', '4', '', null, '1', 'F', '0', '0', 'wecom:tlp:edit', '#', 'admin',
        '2021-02-22 15:44:46', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2158', '删除好友欢迎语', '2060', '5', '', null, '1', 'F', '0', '0', 'wecom:tlp:remove', '#', 'admin',
        '2021-02-22 15:45:00', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2159', '员工活码详情', '2052', '12', 'staffDetail', 'drainageCode/staff/detail', '1', 'P', '1', '0',
        'drainageCode:staff:detail', '#', 'admin', '2021-02-22 16:32:05', 'admin', '2021-09-02 20:36:14', '');
INSERT INTO `sys_menu`
VALUES ('2160', '新建员工活码', '2052', '11', 'staffAdd', 'drainageCode/staff/add', '1', 'P', '1', '0',
        'drainageCode:staff:add', '#', 'admin', '2021-02-22 16:38:11', 'admin', '2021-09-02 15:04:43', '');
INSERT INTO `sys_menu`
VALUES ('2163', '新增/修改欢迎语', '2052', '81', 'welcomeAdd', 'drainageCode/welcome/add', '1', 'P', '1', '0',
        'drainageCode:welcome:add', '#', 'admin', '2021-02-22 23:23:40', 'admin', '2021-09-02 15:08:37', '');
INSERT INTO `sys_menu`
VALUES ('2164', '新建群活码', '2052', '50', 'groupAdd', 'drainageCode/group/add', '1', 'P', '1', '0',
        'drainageCode:group:add', '#', 'admin', '2021-02-23 00:11:41', 'admin', '2021-09-02 15:05:22', '');
INSERT INTO `sys_menu`
VALUES ('2165', '客户群活码信息', '2052', '60', 'customerGroupDetail', 'drainageCode/group/detail', '1', 'P', '1', '0',
        'drainageCode:group:detail', '#', 'admin', '2021-02-23 00:14:50', 'admin', '2021-09-02 17:53:07', '');
INSERT INTO `sys_menu`
VALUES ('2166', '客户群信息', '2052', '70', 'groupBaseInfo', 'drainageCode/group/baseInfo', '1', 'P', '1', '0',
        'drainageCode:group:baseInfo', '#', 'admin', '2021-02-23 00:17:18', 'admin', '2021-09-02 20:34:49', '');
INSERT INTO `sys_menu`
VALUES ('2179', '客户继承', '2001', '35', 'extend', null, '1', 'M', '0', '0', '', 'customer-inheritance', 'admin',
        '2021-08-03 16:43:19', 'admin', '2022-02-28 11:17:59', '');
INSERT INTO `sys_menu`
VALUES ('2188', '运营中心', '0', '1', 'operationsCenter', null, '1', 'M', '0', '0', '', 'content-manage1', 'admin',
        '2021-08-05 10:42:01', 'admin', '2022-02-28 11:16:10', '');
INSERT INTO `sys_menu`
VALUES ('2189', '素材库', '2062', '2', 'materialManage', 'material/materialManage', '1', 'C', '0', '0',
        'wechat:category:list', '#', 'admin', '2021-08-05 11:11:57', 'admin', '2021-10-29 16:41:34', '');
INSERT INTO `sys_menu`
VALUES ('2196', '编辑配置', '2010', '4', '', null, '1', 'F', '0', '0', 'wechat:corp:edit', '#', 'admin',
        '2021-08-09 11:26:55', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2201', '员工管理', '2283', '5', 'staff', 'system/staff/index', '1', 'C', '0', '0', 'system:user:list', '#',
        'admin', '2021-08-24 09:43:01', 'admin', '2021-08-27 21:31:33', '');
INSERT INTO `sys_menu`
VALUES ('2205', '编辑定时待群发任务', '2085', '2', '', null, '1', 'F', '0', '0', 'customerMessagePush:push:edit', '#', 'admin',
        '2021-08-25 18:35:16', 'admin', '2021-08-30 11:09:22', '');
INSERT INTO `sys_menu`
VALUES ('2206', '新建自动拉群', '2101', '1', '', null, '1', 'F', '0', '0', 'wecom:communityNewGroup:add', '#', 'admin',
        '2021-08-25 18:41:10', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2207', '编辑自动拉群', '2101', '2', '', null, '1', 'F', '0', '0', 'wecom:communityNewGroup:edit', '#', 'admin',
        '2021-08-25 18:44:21', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2208', '删除自动拉群', '2101', '3', '', null, '1', 'F', '0', '0', 'wecom:communityNewGroup:remove', '#', 'admin',
        '2021-08-25 18:44:50', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2210', '新建任务', '2102', '1', '', null, '1', 'F', '0', '0', 'wecom:communitytagGroup:add', '#', 'admin',
        '2021-08-25 20:31:14', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2212', '删除任务', '2102', '3', '', null, '1', 'F', '0', '0', 'wecom:communitytagGroup:remove', '#', 'admin',
        '2021-08-25 20:32:51', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2213', '同步员工', '2201', '1', '', null, '1', 'F', '0', '0', 'contacts:organization:sync', '#', 'admin',
        '2021-08-26 11:23:56', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2214', '查看员工详情', '2201', '12', '', null, '1', 'F', '0', '0', 'contacts:organization:info', '#', 'admin',
        '2021-08-26 11:31:58', 'admin', '2021-08-26 11:33:27', '');
INSERT INTO `sys_menu`
VALUES ('2215', '禁用/启用员工', '2201', '14', '', null, '1', 'F', '0', '0', 'contacts:organization:forbidden', '#', 'admin',
        '2021-08-26 11:32:45', 'admin', '2021-08-26 11:33:37', '');
INSERT INTO `sys_menu`
VALUES ('2216', '编辑员工详情', '2201', '13', '', null, '1', 'F', '0', '0', 'contacts:organization:edit', '#', 'admin',
        '2021-08-26 11:35:40', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2217', '删除员工', '2201', '15', '', null, '1', 'F', '0', '0', 'contacts:organization:removeMember', '#', 'admin',
        '2021-08-26 11:36:18', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2218', '查看已分配员工', '2005', '43', '', null, '1', 'F', '0', '0', 'customerManage:dimission:allocatedStaffList',
        '#', 'admin', '2021-08-26 22:05:08', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2219', '查看已分配员工详情', '2005', '44', '', null, '1', 'F', '0', '0',
        'customerManage:dimission/allocatedStaffDetail', '#', 'admin', '2021-08-26 22:16:14', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2221', '查看流失客户详情', '2082', '2', '', null, '1', 'F', '0', '0', 'customerManage:lossRemind:view', '#', 'admin',
        '2021-08-30 10:52:33', 'admin', '2021-08-30 10:52:45', '');
INSERT INTO `sys_menu`
VALUES ('2222', '导出列表', '2082', '3', '', null, '1', 'F', '0', '0', 'customerManage:lossRemind:export', '#', 'admin',
        '2021-08-30 10:53:03', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2223', '查看群发详情', '2085', '1', '', null, '1', 'F', '0', '0', 'customerMessagePush:push:view', '#', 'admin',
        '2021-08-30 11:19:07', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2224', '定时任务日志查看', '2', '10', 'sysMonitor/job/log', 'monitor/job/log', '1', 'P', '1', '0', '', '#', 'admin',
        '2021-09-01 13:41:15', 'admin', '2021-09-01 23:44:20', '');
INSERT INTO `sys_menu`
VALUES ('2226', '侧边栏展示开关', '2189', '1', '', null, '1', 'F', '0', '0', 'wechat:material:sidebar', '#', 'admin',
        '2021-10-13 17:34:02', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2227', '管理过期素材', '2189', '2', '', null, '1', 'F', '0', '0', 'wechat:material:expireManage', '#', 'admin',
        '2021-10-13 17:34:35', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2228', '管理素材标签', '2189', '3', '', null, '1', 'F', '0', '0', 'wechat:material:tagManage', '#', 'admin',
        '2021-10-13 17:35:24', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2229', '应用管理', '0', '5', 'appManage', null, '1', 'M', '0', '0', '', 'ApplicationManagement', 'admin',
        '2021-10-18 17:34:09', 'admin', '2021-10-18 17:37:22', '');
INSERT INTO `sys_menu`
VALUES ('2230', '我的应用', '2281', '1', 'myApp', 'appManage/myApp/index', '1', 'C', '0', '0', '', '#', 'admin',
        '2021-10-18 17:41:24', 'admin', '2021-10-18 17:42:42', '');
INSERT INTO `sys_menu`
VALUES ('2231', '应用设置', '2230', '1', '', null, '1', 'F', '0', '0', 'wecom:myApplication:update', '#', 'admin',
        '2021-10-18 17:48:21', 'admin', '2021-10-18 17:49:44', '');
INSERT INTO `sys_menu`
VALUES ('2232', '应用中心', '2281', '2', 'appCenter', 'appManage/appCenter/index', '1', 'C', '0', '0', null, '#', 'admin',
        '2021-10-18 17:49:08', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2233', '添加应用', '2232', '1', '', null, '1', 'F', '0', '0', 'wecom:application:install', '#', 'admin',
        '2021-10-18 17:49:35', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2234', '应用设置页面', '2229', '30', 'appManage/appConfig', 'appManage/appConfig/index', '1', 'P', '1', '0', '', '#',
        'admin',
        '2021-10-18 20:24:52', 'admin', '2021-10-20 10:01:52', '');
INSERT INTO `sys_menu`
VALUES ('2235', '应用详情页面', '2229', '79', 'appManage/appDetails', 'appManage/appDetails/index', '1', 'P', '1', '0', '',
        '#',
        'admin', '2021-10-18 21:40:20', 'admin', '2021-10-20 10:02:00', '');
INSERT INTO `sys_menu`
VALUES ('2236', '话术库', '2062', '1', 'verbalTrickManage', 'verbalTrickLibrary/verbalTrickManage', '1', 'C', '0', '0',
        null, '#', 'admin', '2021-10-29 16:41:12', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2237', '管理企业话术', '2236', '5', '', null, '1', 'F', '0', '0', 'wecom:corpWords:manage', '#', 'admin',
        '2021-11-02 10:38:49', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2238', '管理部门话术', '2236', '10', '', null, '1', 'F', '0', '0', 'wecom:deptWords:manage', '#', 'admin',
        '2021-11-02 10:39:26', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2239', '客户设置', '2280', '32', 'customerSetting', 'customerManage/customerSetting', '1', 'C', '0', '0', null,
        '#', 'admin', '2021-11-15 11:33:57', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2240', '新增字段', '2239', '1', '', null, '1', 'F', '0', '0', 'customer:extendProp:add', '#', 'admin',
        '2021-11-16 13:56:31', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2241', '编辑字段', '2239', '3', '', null, '1', 'F', '0', '0', 'customer:extendProp:edit', '#', 'admin',
        '2021-11-16 14:33:16', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2242', '删除字段', '2239', '6', '', null, '1', 'F', '0', '0', 'customer:extendProp:remove', '#', 'admin',
        '2021-11-16 14:33:56', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2243', '编辑客户群', '2003', '5', '', null, '1', 'F', '0', '0', 'customerManage:group:edit', '#', 'admin',
        '2021-11-23 15:30:54', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2245', '在职继承', '2179', '45', 'inherit', 'customerManage/inherit/index', '1', 'C', '0', '0', '', '#', 'admin',
        '2021-11-29 14:35:48', 'admin', '2021-12-01 13:48:49', '');
INSERT INTO `sys_menu`
VALUES ('2246', '分配记录', '2179', '46', 'inheritRecord', 'customerManage/inherit/record', '1', 'P', '1', '0', '', '#',
        'admin', '2021-12-01 13:50:48', 'admin', '2021-12-01 14:21:25', '');
INSERT INTO `sys_menu`
VALUES ('2247', '分配客户', '2245', '2', '', null, '1', 'F', '0', '0', 'customerManage:active:transfer', '#', 'admin',
        '2021-12-03 10:30:36', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2248', '查看分配记录', '2245', '6', '', null, '1', 'F', '0', '0', 'customerManage:transfer:record', '#', 'admin',
        '2021-12-03 10:31:18', 'admin', '2021-12-03 10:32:38', '');
INSERT INTO `sys_menu`
VALUES ('2249', '继承设置', '2245', '12', '', null, '1', 'F', '0', '0', 'customerManage:transfer:config', '#', 'admin',
        '2021-12-03 10:31:48', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2250', '留存转化', '2188', '31', 'retainedConversion', null, '1', 'M', '0', '0', '', 'transform-retained', 'admin',
        '2021-11-29 15:36:04', 'admin', '2022-02-28 11:15:56', '');
INSERT INTO `sys_menu`
VALUES ('2251', '客户SOP', '2250', '3', 'customerSOP', 'retainedConversion/SOP/customerSOP', '1', 'C', '0', '0', '', '#',
        'admin', '2021-11-29 15:41:36', 'admin', '2021-11-30 13:49:09', '');
INSERT INTO `sys_menu`
VALUES ('2252', '群SOP', '2250', '4', 'groupSOP', 'retainedConversion/SOP/groupSOP', '1', 'C', '0', '0', '', '#',
        'admin', '2021-11-29 15:43:48', 'admin', '2021-11-30 13:46:18', '');
INSERT INTO `sys_menu`
VALUES ('2254', '新增SOP页面', '2250', '8', 'addSOP', 'retainedConversion/SOP/addSOP', '1', 'P', '1', '0', '', '#', 'admin',
        '2021-11-30 11:35:36', 'admin', '2021-12-07 10:08:28', '');
INSERT INTO `sys_menu`
VALUES ('2255', 'SOP详情', '2250', '9', 'SOPDetail', 'retainedConversion/SOP/SOPDetail', '1', 'P', '1', '0', null, '#',
        'admin', '2021-12-01 19:19:47', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2256', '新增SOP', '2251', '1', '', null, '1', 'F', '0', '0', 'wecom:customerSop:add', '#', 'admin',
        '2021-12-08 10:26:25', 'admin', '2021-12-08 10:27:10', '');
INSERT INTO `sys_menu`
VALUES ('2257', '启用/关闭SOP', '2251', '5', '', null, '1', 'F', '0', '0', 'wecom:customerSop:switch', '#', 'admin',
        '2021-12-08 10:27:02', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2258', '删除SOP', '2251', '10', '', null, '1', 'F', '0', '0', 'wecom:customerSop:del', '#', 'admin',
        '2021-12-08 10:27:36', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2259', '编辑SOP', '2251', '20', '', null, '1', 'F', '0', '0', 'wecom:customerSop:edit', '#', 'admin',
        '2021-12-08 10:28:00', 'admin', '2021-12-08 10:28:15', '');
INSERT INTO `sys_menu`
VALUES ('2260', '新增SOP', '2252', '1', '', null, '1', 'F', '0', '0', 'wecom:groupSop:add', '#', 'admin',
        '2021-12-08 10:28:46', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2261', '启用/关闭SOP', '2252', '5', '', null, '1', 'F', '0', '0', 'wecom:groupSop:switch', '#', 'admin',
        '2021-12-08 10:29:16', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2262', '删除SOP', '2252', '10', '', null, '1', 'F', '0', '0', 'wecom:groupSop:del', '#', 'admin',
        '2021-12-08 10:29:50', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2263', '编辑SOP', '2252', '15', '', null, '1', 'F', '0', '0', 'wecom:groupSop:edit', '#', 'admin',
        '2021-12-08 10:30:19', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2264', '群日历', '2250', '5', 'groupCalendarSOP', 'retainedConversion/SOP/groupCalendarSOP', '1', 'C', '0', '0',
        null, '#', 'admin', '2021-12-21 10:50:37', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2265', '新增日历', '2264', '2', '', null, '1', 'F', '0', '0', 'wecom:groupCalendar:add', '#', 'admin',
        '2021-12-21 15:27:38', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2266', '启用/关闭日历', '2264', '6', '', null, '1', 'F', '0', '0', 'wecom:groupCalendar:switch', '#', 'admin',
        '2021-12-21 15:28:50', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2267', '编辑日历', '2264', '10', '', null, '1', 'F', '0', '0', 'wecom:groupCalendar:edit', '#', 'admin',
        '2021-12-21 15:29:21', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2268', '删除日历', '2264', '8', '', null, '1', 'F', '0', '0', 'wecom:groupCalendar:del', '#', 'admin',
        '2021-12-22 09:19:24', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2269', '新增入群欢迎语', '2060', '7', '', null, '1', 'F', '0', '0', 'wecom:groupWelcome:add', '#', 'admin',
        '2022-01-12 14:42:38', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2270', '编辑入群欢迎语', '2060', '10', '', null, '1', 'F', '0', '0', 'wecom:groupWelcome:edit', '#', 'admin',
        '2022-01-12 14:43:00', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2271', '删除入群欢迎语', '2060', '15', '', null, '1', 'F', '0', '0', 'wecom:groupWelcome:del', '#', 'admin',
        '2022-01-12 14:43:23', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2272', '朋友圈', '2188', '25', 'moments', null, '1', 'M', '0', '0', '', 'friend-circle', 'admin',
        '2022-01-13 10:53:17', 'admin', '2022-02-28 11:12:45', '');
INSERT INTO `sys_menu`
VALUES ('2273', '发布朋友圈', '2272', '1', 'release', 'friendsCircle/release', '1', 'C', '0', '0', 'wecom:moments:publish',
        '#', 'admin', '2022-01-13 10:55:03', 'admin', '2022-01-13 14:32:16', '');
INSERT INTO `sys_menu`
VALUES ('2274', '发布记录', '2272', '5', 'record', 'friendsCircle/record', '1', 'C', '0', '0', '', '#', 'admin',
        '2022-01-13 10:55:35', 'admin', '2022-01-13 14:36:58', '');
INSERT INTO `sys_menu`
VALUES ('2275', '查看朋友圈详情', '2274', '5', '', null, '1', 'F', '0', '0', 'wecom:moments:detail', '#', 'admin',
        '2022-01-13 10:56:02', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2276', '编辑定时朋友圈', '2274', '10', '', null, '1', 'F', '0', '0', 'wecom:moments:edit', '#', 'admin',
        '2022-01-13 10:56:40', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2277', '删除朋友圈', '2274', '15', '', null, '1', 'F', '0', '0', 'wecom:moments:del', '#', 'admin',
        '2022-01-13 10:57:03', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2278', '查看朋友圈详情页面', '2272', '33', 'detail', 'friendsCircle/detail', '1', 'P', '1', '0', null, '#', 'admin',
        '2022-01-13 14:38:36', '', null, '');
INSERT INTO `sys_menu`
VALUES ('2279', '新增/修改入群欢迎语', '2052', '82', 'groupWelcomeAdd', 'drainageCode/welcome/groupWelcomeAdd', '1', 'P', '1',
        '0', '', '#', 'admin', '2022-01-13 17:58:19', 'admin', '2022-01-13 20:12:22', '');
INSERT INTO `sys_menu`
VALUES ('2280', '客户管理', '2001', '10', 'customerCenter', null, '1', 'M', '0', '0', '', 'customer-manage', 'admin',
        '2022-02-28 10:32:21', 'admin', '2022-02-28 11:16:42', '');
INSERT INTO `sys_menu`
VALUES ('2281', '应用管理', '2229', '10', 'appManage', null, '1', 'M', '0', '0', '', 'application-manage', 'admin',
        '2022-02-28 10:41:37', 'admin', '2022-02-28 11:19:35', '');
INSERT INTO `sys_menu`
VALUES ('2282', '企业风控', '2079', '10', 'corpSecurity', null, '1', 'M', '0', '0', '', 'enterprise-risk-control', 'admin',
        '2022-02-28 10:46:49', 'admin', '2022-02-28 11:21:05', '');
INSERT INTO `sys_menu`
VALUES ('2283', '系统设置', '1', '10', 'sysSetting', null, '1', 'M', '0', '0', '', 'system-hollow', 'admin',
        '2022-02-28 10:52:43', 'admin', '2022-02-28 11:24:52', '');
INSERT INTO `sys_menu`
VALUES ('2284', '系统监控', '2', '5', 'sysMonitor', null, '1', 'M', '0', '0', '', 'system-monitor', 'admin',
        '2022-02-28 10:56:04', 'admin', '2022-02-28 11:27:45', '');

INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2285, '自动标签', 2280, 25, 'autoLabel', 'customerManage/autoLabel', 1, 'C', '0', '0',
        'customerManage:autoLabel:list', '#', 'admin', '2022-03-04 13:47:45', 'admin', '2022-03-04 17:37:12', '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2286, '新增规则', 2285, 10, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:add', '#', 'admin', '2022-03-04 13:53:47',
        '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2287, '启用/关闭规则', 2285, 20, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:enable', '#', 'admin',
        '2022-03-04 13:56:42',
        '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2288, '删除规则', 2285, 30, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:del', '#', 'admin', '2022-03-04 13:57:12',
        '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2289, '编辑规则', 2285, 40, '', NULL, 1, 'F', '0', '0', 'wecom:autotag:edit', '#', 'admin', '2022-03-04 13:57:36',
        '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2290, '规则详情', 2280, 33, 'labelDetail', 'customerManage/autoLabel/labelDetail', 1, 'P', '1', '0', NULL, '#',
        'admin', '2022-03-04 18:10:05', '', NULL, '');
INSERT INTO `sys_menu`(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                       `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                       `remark`)
VALUES (2291, '新增规则页面', 2280, 22, 'addRule', 'customerManage/autoLabel/addRule', 1, 'P', '1', '0', '', '#', 'admin',
        '2022-03-04 16:22:16', 'admin', '2022-03-04 18:08:09', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2292', '营销活动', '2188', '3', 'conversionCode', NULL, '1', 'M', '0', '0', '', 'conversion-code', 'admin',
        '2022-07-08 09:15:22', 'admin', '2022-07-08 09:15:32', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2293', '兑换码', '2292', '10', 'list', 'marketingActivities/conversionCode/list', '1', 'C', '0', '0', '', '#',
        'admin', '2022-07-08 09:17:54', 'admin', '2022-07-08 09:54:33', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2294', '新建兑换活动', '2292', '11', 'conversionCodeAdd', 'marketingActivities/conversionCode/add', '1', 'P', '1',
        '0', '', '#', 'admin', '2022-07-08 09:19:54', 'admin', '2022-07-08 09:52:25', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2295', '兑换码详情', '2292', '1', 'conversionCodeDetail', 'marketingActivities/conversionCode/detail', '1', 'P',
        '1', '0', '', '#', 'admin', '2022-07-08 09:21:59', 'admin', '2022-07-08 09:52:17', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2296', '新增兑换活动', '2293', '5', '', NULL, '1', 'F', '0', '0', 'redeeomCode:activity:add', '#', 'admin',
        '2022-07-11 09:52:22', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2297', '删除兑换活动', '2293', '10', '', NULL, '1', 'F', '0', '0', 'redeeomCode:activity:del', '#', 'admin',
        '2022-07-11 09:53:02', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2298', '编辑兑换活动', '2293', '15', '', NULL, '1', 'F', '0', '0', 'redeeomCode:activity:edit', '#', 'admin',
        '2022-07-11 09:53:33', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2299', '雷达库', '2062', '3', 'radarManage', 'radarLibrary/radarManage', '1', 'C', '0', '0', '', '#', 'admin',
        '2022-07-15 15:14:42', 'admin', '2022-07-15 15:27:08', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2300', '查看雷达链接详情', '2062', '1', 'radarDetail', 'radarLibrary/radarDetail', '1', 'P', '1', '0', NULL, '#',
        'admin', '2022-07-19 09:12:38', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2301', '管理企业雷达', '2299', '5', '', NULL, '1', 'F', '0', '0', 'radar:corp:manage', '#', 'admin',
        '2022-07-23 21:52:48', 'admin', '2022-07-24 16:07:55', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`,
                        `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`,
                        `remark`)
VALUES ('2302', '管理部门雷达', '2299', '10', '', NULL, '1', 'F', '0', '0', 'radar:department:manage', '#', 'admin',
        '2022-07-23 21:53:42', 'admin', '2022-07-24 16:08:00', '');


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `role_id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `corp_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '公司id',
    `role_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '角色名称',
    `role_key`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色权限字符串',
    `role_sort`   int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `data_scope`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
    `del_flag`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `role_type`   tinyint(4) NOT NULL DEFAULT '3' COMMENT '角色类型（1：系统默认超级管理员角色, 2:系统默认角色,3:自定义角色)',
    PRIMARY KEY (`role_id`),
    KEY           `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`
(
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '公司Id',
    `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`role_id`, `corp_id`, `dept_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------


-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------


-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id`      bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
    `user_name`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
    `nick_name`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
    `user_type`    varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）(11:企业微信用户)',
    `email`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
    `phonenumber`  varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
    `sex`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
    `password`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
    `status`       char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `del_flag`     char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `login_ip`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登陆IP',
    `login_date`   datetime(0) NULL DEFAULT NULL COMMENT '最后登陆时间',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time`  datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time`  datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `crop_account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `we_user_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `ui_color`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '#6BB4AB' COMMENT '后台界面主题颜色',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, 100, 'admin', 'admin', '00', NULL, NULL, '0', '',
        '$2a$10$EYb5boK0XMy8c4kzXnm3e.SSrSOg4TpHfe4PrVtvZL135jizzfiOi', '0', '0', '127.0.0.1', now(), 'admin', now(),
        'ry', now(), '管理员', NULL, NULL, '#6BB4AB');

-- ----------------------------
-- Table structure for we_user_role
-- ----------------------------
DROP TABLE IF EXISTS `we_user_role`;
CREATE TABLE `we_user_role`
(
    `corp_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公司ID',
    `user_id` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企微用户ID',
    `role_id` BIGINT (20) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`corp_id`, `user_id`, `role_id`),
    UNIQUE KEY `un_corp_user_id` (`corp_id`,`user_id`) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企微用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `job_log_id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务名称',
    `job_group`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务组名',
    `invoke_target`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
    `job_message`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '异常信息',
    `create_time`    datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`
(
    `info_id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `corp_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业ID',
    `user_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
    `ipaddr`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
    `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
    `browser`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
    `os`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    `msg`            varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
    `login_time`     datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
    `login_type`     tinyint(2) NOT NULL DEFAULT '1' COMMENT '登录方式(1账号密码登录,2企微扫码登录)',
    PRIMARY KEY (`info_id`) USING BTREE,
    KEY              `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`
(
    `oper_id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `corp_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公司ID',
    `title`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
    `business_type`  int(11) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
    `operator_type`  int(11) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
    `dept_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
    `oper_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
    `oper_ip`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
    `oper_location`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
    `oper_param`     varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
    `json_result`    varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
    `status`         int(11) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    `error_msg`      varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
    `oper_time`      datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
    PRIMARY KEY (`oper_id`) USING BTREE,
    KEY              `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_category
-- ----------------------------
DROP TABLE IF EXISTS `we_category`;
CREATE TABLE `we_category`
(
    `id`          bigint(100) NOT NULL COMMENT '主键id',
    `corp_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `media_type`  int(1) NOT NULL DEFAULT 0 COMMENT '0 海报、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 、5图文链接、6小程序',
    `using`       tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用到侧边栏(0否，1是)',
    `name`        varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类名称',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '修改人',
    `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `del_flag`    tinyint(1) NOT NULL DEFAULT 0 COMMENT '0  未删除 1 已删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '素材分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of we_category
-- ----------------------------
-- INSERT INTO `we_category` VALUES (1425730086098833408, 0, 0, '图片', '', '2021-08-12 16:05:16', '', '2021-08-12 16:05:16', 0);
-- INSERT INTO `we_category` VALUES (1425730155204186112, 0, 2, '视频', '', '2021-08-12 16:05:32', '', '2021-08-12 16:05:32', 0);
-- INSERT INTO `we_category` VALUES (1425730190193070080, 0, 3, '文件', '', '2021-08-12 16:05:41', '', '2021-08-12 16:05:41', 0);
-- INSERT INTO `we_category` VALUES (1425730227862114304, 0, 4, '文本', '', '2021-08-12 16:05:50', '', '2021-08-12 16:05:50', 0);


-- ----------------------------
-- Table structure for we_chat_contact_mapping
-- ----------------------------
DROP TABLE IF EXISTS `we_chat_contact_mapping`;
CREATE TABLE `we_chat_contact_mapping`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `from_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送人id',
    `receive_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收人id',
    `room_id`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群聊id',
    `is_custom`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '接收人是否为客户 0-成员 1-客户 2-机器人',
    `corp_id`    varchar(64)                                                  NOT NULL DEFAULT '' COMMENT '企业id',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX        `we_chat_contact_mapping_from_id_IDX`(`from_id`, `is_custom`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天关系映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_chat_side
-- ----------------------------
DROP TABLE IF EXISTS `we_chat_side`;
CREATE TABLE `we_chat_side`
(
    `side_id`     bigint(20) NOT NULL COMMENT '主键id',
    `corp_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `media_type`  tinyint(4) NOT NULL DEFAULT -1 COMMENT '素材类型 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 5 海报',
    `side_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '聊天工具栏名称',
    `total`       int(10) NOT NULL DEFAULT 0 COMMENT '已抓取素材数量',
    `using`       tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用 0 启用 1 未启用',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1) NOT NULL DEFAULT 0 COMMENT '0  未删除 1 已删除',
    PRIMARY KEY (`side_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '侧边栏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of we_chat_side
-- ----------------------------
-- INSERT INTO `we_chat_side` VALUES (1, 0, '图片', 1, 0, 'admin', '2021-08-05 21:38:00', 'admin', '2021-08-05 21:38:00', 0);
-- INSERT INTO `we_chat_side` VALUES (2, 1, '语音', 0, 0, 'admin', '2021-08-05 21:38:42', 'admin', '2021-08-05 21:38:42', 0);
-- INSERT INTO `we_chat_side` VALUES (3, 2, '视频', 0, 0, 'admin', '2021-08-05 21:38:58', 'admin', '2021-08-05 21:38:58', 0);
-- INSERT INTO `we_chat_side` VALUES (4, 3, '文件', 0, 0, 'admin', '2021-08-05 21:39:20', 'admin', '2021-08-05 21:39:20', 0);
-- INSERT INTO `we_chat_side` VALUES (5, 4, '文本', 0, 0, 'admin', '2021-08-05 21:39:33', 'admin', '2021-08-05 21:39:33', 0);
-- INSERT INTO `we_chat_side` VALUES (6, 5, '海报', 0, 0, 'admin', '2021-08-05 21:39:53', 'admin', '2021-08-05 21:39:53', 0);

-- ----------------------------
-- Table structure for we_community_new_group
-- ----------------------------
DROP TABLE IF EXISTS `we_community_new_group`;
CREATE TABLE `we_community_new_group`
(
    `id`             bigint(20) NOT NULL COMMENT '主键ID',
    `corp_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业Id',
    `group_code_id`  bigint(64) NOT NULL DEFAULT 0 COMMENT '群活码ID',
    `empl_code_id`   bigint(20) NOT NULL DEFAULT 0 COMMENT '员工活码id',
    `new_group_id`   bigint(20) NOT NULL DEFAULT 0,
    `empl_code_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '员工活码名称',
    `create_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '修改人',
    `update_time`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `del_flag`       tinyint(1) NOT NULL DEFAULT 0 COMMENT '0:正常;1:删除;',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `idx_create_time`(`create_time`) USING BTREE COMMENT '创建时间',
    INDEX            `idx_create_by`(`create_by`) USING BTREE COMMENT '创建人',
    INDEX            `idx_empl_code`(`empl_code_name`, `empl_code_id`) USING BTREE COMMENT '员工活码'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户进群活码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_corp_account
-- ----------------------------
DROP TABLE IF EXISTS `we_corp_account`;
CREATE TABLE `we_corp_account`
(
    `id`                           int(11) NOT NULL AUTO_INCREMENT,
    `company_name`                 varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业名称',
    `corp_id`                      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `external_corp_id`             VARCHAR(255) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                               NOT NULL DEFAULT ''
        COMMENT '三方应用企业ID',
    `crop_account`                 varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业管理员账号',
    `corp_secret`                  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '应用的密钥凭证',
    `contact_secret`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '外部联系人密钥',
    `wx_qr_login_redirect_uri`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业微信扫码登陆系统回调地址',
    `provider_secret`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '服务商密钥',
    `chat_secret`                  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会话存档密钥',
    `agent_id`                     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '应用id',
    `agent_secret`                 varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '应用密钥',
    `status`                       char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用 2已授权未启用)',
    `del_flag`                     char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '0 未删除 1 已删除',
    `create_by`                    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`                  datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`                  datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `customer_churn_notice_switch` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '客户流失通知开关 0:关闭 1:开启',
    `corp_account`                 varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业管理员账号',
    `custom_secret`                varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `encoding_aes_key`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用回调aesKey',
    `h5_do_main_name`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'H5域名链接',
    `token`                        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用回调token',
    `cert_file_path`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '侧边栏证书',
    `callback_uri`                 VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用回调url',
    `authorized`                   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否授权',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业id相关配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer
-- ----------------------------
DROP TABLE IF EXISTS `we_customer`;
CREATE TABLE `we_customer`
(
    `external_userid` VARCHAR(32) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL
        COMMENT '外部联系人的userid',
    `unionid`         VARCHAR(32) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL DEFAULT ''
        COMMENT '外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。',
    `name`            VARCHAR(128) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL DEFAULT ''
        COMMENT '外部联系人名称',
    `corp_id`         VARCHAR(64) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL DEFAULT ''
        COMMENT '企业ID',
    `avatar`          VARCHAR(200) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL DEFAULT ''
        COMMENT '外部联系人头像',
    `type`            TINYINT(4) NOT NULL DEFAULT 0
        COMMENT '外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户',
    `gender`          TINYINT(4) NOT NULL DEFAULT 0
        COMMENT '外部联系人性别 0-未知 1-男性 2-女性',
    `birthday`        DATETIME(0) NOT NULL DEFAULT '0000-00-00 00:00:00'
        COMMENT '生日',
    `corp_name`       VARCHAR(100) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL DEFAULT ''
        COMMENT '客户企业简称',
    `corp_full_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户企业全称',
    `position`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户职位',
    `is_open_chat`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否开启会话存档 0：关闭 1：开启',
    `create_time`     datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `update_by`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`external_userid`, `corp_id`) USING BTREE,
    KEY               `idx_union_id` (`unionid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业微信客户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer_message
-- ----------------------------
DROP TABLE IF EXISTS `we_customer_message`;
CREATE TABLE `we_customer_message`
(
    `message_id`   bigint(64) NOT NULL,
    `original_id`  bigint(64) NOT NULL DEFAULT 0 COMMENT '原始数据表id',
    `chat_type`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '0' COMMENT '群发任务的类型，默认为0表示发送给客户，1表示发送给客户群',
    `sender`       longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送企业群发消息的成员userid，当类型为发送给客户群时必填(和企微客户沟通后确认是群主id)',
    `check_status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '消息发送状态 0 未发送  1 已发送',
    `msgid`        longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '企业群发消息的id，可用于<a href=\"https://work.weixin.qq.com/api/doc/90000/90135/92136\">获取群发消息发送结果</a>',
    `content`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`  datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`  datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`     int(11) NOT NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    `setting_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送时间',
    `expect_send`  int(11) NOT NULL DEFAULT 0 COMMENT '预计发送消息数（客户对应多少人 客户群对应多个群）',
    `actual_send`  int(11) NOT NULL DEFAULT 0 COMMENT '实际发送消息数（客户对应多少人 客户群对应多个群）',
    `timed_task`   tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否定时任务 0 常规 1 定时发送',
    PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '群发消息  微信消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer_messageoriginal
-- ----------------------------
DROP TABLE IF EXISTS `we_customer_messageoriginal`;
CREATE TABLE `we_customer_messageoriginal`
(
    `message_original_Id` bigint(64) NOT NULL,
    `corp_id`             varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '企业ID',
    `staff_id`            text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '员工id',
    `task_name`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '任务名称',
    `department`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '部门id',
    `group`               varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群组名称id',
    `tag`                 varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户标签id列表',
    `filter_tags`         varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '过滤标签id列表',
    `gender`              tinyint(1) NOT NULL DEFAULT 0 COMMENT '外部联系人性别 0-未知 1-男性 2-女性 -1-全部',
    `push_type`           varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '群发类型 0 发给客户 1 发给客户群',
    `message_type`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息 用逗号隔开',
    `push_range`          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '消息范围 0 全部客户  1 指定客户',
    `customer_start_time` datetime                                                       NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加客户开始时间',
    `customer_end_time`   datetime                                                       NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加客户结束时间',
    `create_by`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`         datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`         datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`            tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`message_original_Id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '群发消息 原始数据信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer_messagetimetask
-- ----------------------------
DROP TABLE IF EXISTS `we_customer_messagetimetask`;
CREATE TABLE `we_customer_messagetimetask`
(
    `task_id`        bigint(64) NOT NULL AUTO_INCREMENT COMMENT '任务id',
    `message_id`     bigint(64) NOT NULL DEFAULT 0 COMMENT '消息id',
    `message_info`   longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息原始信息',
    `customers_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户信息列表',
    `groups_info`    longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户群组信息列表',
    `setting_time`   bigint(64) NOT NULL DEFAULT 0 COMMENT '定时时间的毫秒数',
    `create_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `solved`         tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 未解决 1 已解决',
    `del_flag`       tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 未删除 1 已删除',
    PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer_messgaeresult
-- ----------------------------
DROP TABLE IF EXISTS `we_customer_messgaeresult`;
CREATE TABLE `we_customer_messgaeresult`
(
    `messgae_result_id` bigint(64) NOT NULL,
    `message_id`        bigint(64) NOT NULL DEFAULT 0 COMMENT '微信消息表id',
    `external_userid`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人userid',
    `chat_id`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部客户群id',
    `userid`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业服务人员的userid',
    `status`            varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '0' COMMENT '0：未执行（员工未执行群发操作）1：发送成功（员工执行群发并成功送达客户）2：已不是好友（员工执行群发，但客户已不是好友）3：其他员工已发送（员工执行群发，但本次群发中其他员工对该客户执行了群发）4：接收已达上限（该客户的群发接收次数上限）5：创建失败，小程序未关联企业或信息错误（小程序不可发送）6：创建失败，未获取到有效附件信息（获取附件失败）7：未发送（当员工对群群发时只选择了部分群，剩余群未发送）8：创建失败，群发内容异常',
    `remark`            varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    `send_time`         varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `external_name`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人名称',
    `user_name`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业服务人员的名称',
    `send_type`         varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '0 发给客户 1 发给客户群 2 定时发送',
    `setting_time`      varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '定时发送时间',
    `create_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`       datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`          tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 未删除 、1 已删除',
    `chat_name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部客户群名称',
    PRIMARY KEY (`messgae_result_id`) USING BTREE,
    INDEX               `idx_message_id`(`message_id`) USING BTREE COMMENT '消息表id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '群发消息  微信消息发送结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer_seedmessage
-- ----------------------------
DROP TABLE IF EXISTS `we_customer_seedmessage`;
CREATE TABLE `we_customer_seedmessage`
(
    `seed_message_id`      bigint(20) NOT NULL,
    `message_id`           bigint(64) NOT NULL DEFAULT 0 COMMENT '微信消息表id',
    `media_id`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '图片消息：图片的media_id，可以通过 <a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90253\">素材管理接口</a>获得',
    `miniprogram_media_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '小程序消息封面的mediaid，封面图建议尺寸为520*416',
    `appid`                varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '小程序appid，必须是关联到企业的小程序应用',
    `message_type`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '消息类型 0 图片消息 2视频 3文件 4 文本消息   5 链接消息   6 小程序消息 ',
    `content`              text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息文本内容，最多4000个字节',
    `video_name`           varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '视频标题',
    `video_url`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '视频url',
    `size`                 int(10) NOT NULL DEFAULT 0 COMMENT '视频大小',
    `pic_name`             varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '图片标题',
    `pic_url`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '图片消息：图片的链接，仅可使用<a href=\"https://work.weixin.qq.com/api/doc/90000/90135/90256\">上传图片接口</a>得到的链接',
    `file_name`            varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '文件名称',
    `file_url`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '文件url',
    `link_title`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '链接消息：图文消息标题',
    `link_picurl`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '链接消息：图文消息封面的url',
    `lin_desc`             varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链接消息：图文消息的描述，最多512个字节',
    `is_defined`           TINYINT(1) NOT NULL
        COMMENT '链接消息：图文消息数据来源(0:默认,1:自定义)',
    `link_url`             varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '链接消息：图文消息的链接',
    `miniprogram_title`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '小程序消息标题，最多64个字节',
    `page`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '小程序page路径',
    `create_by`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`          datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`          datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `radar_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用',
    `del_flag`             int(1) NOT NULL DEFAULT 0 COMMENT '0 未删除 1 已删除',
    PRIMARY KEY (`seed_message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '群发消息  子消息表(包括 文本消息、图片消息、链接消息、小程序消息) ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_customer_trajectory
-- ----------------------------
DROP TABLE IF EXISTS `we_customer_trajectory`;
CREATE TABLE `we_customer_trajectory`
(
    `id`              BIGINT ( 20 ) NOT NULL,
    `user_id`         VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '',
    `external_userid` VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '0' COMMENT '外部联系人id',
    `agent_id`        VARCHAR(126) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '应用id',
    `corp_id`         VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `detail_id`       bigint(20) NOT NULL DEFAULT '-1' COMMENT 'sop任务详情id',
    `sop_task_ids`    varchar(1000)                                                 NOT NULL DEFAULT '' COMMENT 'sop任务待办id 逗号隔开',
    `trajectory_type` TINYINT ( 4 ) NOT NULL DEFAULT 0 COMMENT '轨迹类型(1:信息动态;2:社交动态;3:活动规则;4:待办动态)',
    `content`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文案内容',
    `create_date`     date                                                          NOT NULL DEFAULT '0000-00-00' COMMENT '处理日期',
    `start_time`      time(0)                                                       NOT NULL DEFAULT '00:00:00' COMMENT '处理开始时间',
    `end_time`        time(0)                                                       NOT NULL DEFAULT '00:00:00' COMMENT '处理结束时间',
    `status`          CHAR(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '0:正常;1:已通知未完成;2:删除;3:已完成',
    `detail`          VARCHAR(500)                                                  NOT NULL DEFAULT '' COMMENT '操作细节（如果是文件图片则是url,如果多个选项则,隔开)',
    `sub_type`        VARCHAR(64)                                                   NOT NULL DEFAULT '' COMMENT '子类型（修改备注：edit_remark;修改标签：edit_tag;编辑多选框：edit_multi;编辑单选：edit_choice;编辑图片：edit_pic;编辑文件：edit_file;加入群聊:join_group;退出群聊：quit_group;加好友：add_user；删除好友：del_user',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = INNODB CHARACTER
SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户轨迹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_department
-- ----------------------------
DROP TABLE IF EXISTS `we_department`;
CREATE TABLE `we_department`
(
    `corp_id`   varchar(64)                                                   NOT NULL DEFAULT '' COMMENT '公司ID',
    `id`        bigint(20) NOT NULL,
    `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父部门id。根部门为1',
    `name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门名称',
    PRIMARY KEY (`corp_id`, `id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业微信部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_emple_code
-- ----------------------------
DROP TABLE IF EXISTS `we_emple_code`;
CREATE TABLE `we_emple_code`
(
    `id`                         bigint(20) NOT NULL,
    `corp_id`                    varchar(64)   NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `config_id`                  varchar(50)   NOT NULL DEFAULT '' COMMENT '新增联系方式的配置id',
    `code_type`                  tinyint(4) NOT NULL DEFAULT '2' COMMENT '活码类型:1:单人;2:多人;3:批量',
    `skip_verify`                tinyint(4) NOT NULL DEFAULT '1' COMMENT '自动成为好友:0：否，1：全天，2：时间段',
    `scenario`                   varchar(300)  NOT NULL DEFAULT '' COMMENT '活动场景',
    `welcome_msg`                varchar(2000) NOT NULL DEFAULT '' COMMENT '欢迎语',
    `create_by`                  varchar(64)   NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`                datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `del_flag`                   tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:正常;1:删除;',
    `qr_code`                    varchar(100)  NOT NULL DEFAULT '' COMMENT '二维码链接',
    `update_by`                  varchar(64)   NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`                datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `state`                      varchar(30)   NOT NULL DEFAULT '' COMMENT '用于区分客户具体是通过哪个「联系我」添加。不能超过30个字符',
    `source`                     tinyint(4) NOT NULL DEFAULT '0' COMMENT '来源类型：0：活码创建，1：新客建群创建',
    `remark_type`                tinyint(2) NOT NULL DEFAULT '0' COMMENT '备注类型：0：不备注，1：在昵称前，2：在昵称后',
    `remark_name`                varchar(32)   NOT NULL DEFAULT '' COMMENT '备注名',
    `effect_time_open`           varchar(8)    NOT NULL DEFAULT '' COMMENT '开启时间 HH:mm',
    `effect_time_close`          varchar(8)    NOT NULL DEFAULT '' COMMENT '关闭时间 HH:mm',
    `material_sort`              varchar(255)  NOT NULL DEFAULT '' COMMENT '附件排序',
    `tag_flag`                   tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可打标签：0不自动打标签，1：自动打标签',
    `welcome_msg_type`           tinyint(1) NOT NULL DEFAULT '0' COMMENT '欢迎语类型，0：普通欢迎语，1：活动欢迎语',
    `code_activity_id`           bigint(20) NOT NULL DEFAULT '0' COMMENT '兑换码活动id',
    `code_success_material_sort` varchar(255)  NOT NULL DEFAULT '' COMMENT '有可使用兑换码，发送该附件',
    `code_success_msg`           varchar(2000) NOT NULL DEFAULT '' COMMENT '有可使用兑换码，发送该欢迎语',
    `code_fail_material_sort`    varchar(255)  NOT NULL DEFAULT '' COMMENT '没有可用的兑换码，或者兑换码已被删除，发送该附件',
    `code_fail_msg`              varchar(2000) NOT NULL DEFAULT '' COMMENT '没有可用的兑换码，或者兑换活动已被删除，发送该欢迎语',
    `code_repeat_material_sort`  varchar(255)  NOT NULL DEFAULT '' COMMENT '客户再次触发，若活动开启参与限制，发送该附件',
    `code_repeat_msg`            varchar(2000) NOT NULL DEFAULT '' COMMENT '客户再次触发，若活动开启参与限制，发送该欢迎语',
    `app_link`                   varchar(128)  NOT NULL DEFAULT '' COMMENT '活码小程序链接',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                          `normal_effecttime_open` (`effect_time_open`) USING BTREE COMMENT '普通索引effect_time_open',
    KEY                          `normal_effecttime_close` (`effect_time_close`) USING BTREE COMMENT '普通索引effect_time_close'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='员工活码表';
-- ----------------------------
-- Table structure for we_emple_code_tag
-- ----------------------------
DROP TABLE IF EXISTS `we_emple_code_tag`;
CREATE TABLE `we_emple_code_tag`
(
    `id`            bigint(20) NOT NULL,
    `tag_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签id',
    `tag_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签名称',
    `emple_code_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '员工活码id',
    `del_flag`      tinyint(4) NOT NULL DEFAULT 0 COMMENT '0 未删除 1 已删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工活码标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_emple_code_use_scop
-- ----------------------------
DROP TABLE IF EXISTS `we_emple_code_use_scop`;
CREATE TABLE `we_emple_code_use_scop`
(
    `id`               bigint(20) NOT NULL,
    `emple_code_id`    bigint(20) NOT NULL DEFAULT 0 COMMENT '员工活码id',
    `business_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '0' COMMENT '活码类型下业务使用人的id',
    `party_id`         bigint(10) NOT NULL DEFAULT 0 COMMENT '部门id列表，只在多人时有效',
    `business_id_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '业务id类型1:组织机构id,2:成员id',
    `business_name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活码下使用人姓名',
    `del_flag`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '0 未删除 1 已删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工活码使用人' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_flower_customer_rel
-- ----------------------------
DROP TABLE IF EXISTS `we_flower_customer_rel`;
CREATE TABLE `we_flower_customer_rel`
(
    `id`               bigint(20) NOT NULL,
    `user_id`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '添加了此外部联系人的企业成员userid',
    `external_userid`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '客户id',
    `corp_id`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `oper_userid`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid',
    `remark`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此外部联系人的备注',
    `description`      varchar(258) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此外部联系人的描述',
    `create_time`      datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '该成员添加此外部联系人的时间',
    `remark_corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此客户备注的企业名称\r\n',
    `remark_mobiles`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '该成员对此客户备注的手机号码',
    `qq`               varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '客户QQ',
    `address`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户地址',
    `email`            varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮件',
    `add_way`          varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '该成员添加此客户的来源，',
    `state`            varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定',
    `status`           char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除流失 2员工删除用户）',
    `delete_time`      datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '删除时间',
    `wechat_channel`   varchar(64)                                                   NOT NULL DEFAULT '' COMMENT '该成员添加此客户的来源add_way为10时，对应的视频号信息',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `un_user_external_userid_corpid` (`external_userid`, `user_id`, `corp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业员工与客户的关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_flower_customer_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `we_flower_customer_tag_rel`;
CREATE TABLE `we_flower_customer_tag_rel`
(
    `id`                     bigint(20) NOT NULL,
    `flower_customer_rel_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '添加客户的企业微信用户',
    `tag_id`                 varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签id',
    `create_time`            datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `external_userid`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `un_rel_tag_id` (`flower_customer_rel_id`,`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户标签关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_group
-- ----------------------------
DROP TABLE IF EXISTS `we_group`;
CREATE TABLE `we_group`
(
    `chat_id`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `owner`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '群主userId',
    `corp_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `group_name`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '群聊' COMMENT '群名',
    `create_time` timestamp(0)                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `notice`      VARCHAR(2048) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                              NOT NULL DEFAULT ''
        COMMENT '群公告',
    `status`      tinyint(1) NOT NULL DEFAULT 0 COMMENT '0 - 正常;1 - 跟进人离职;2 - 离职继承中;3 - 离职继承完成',
    PRIMARY KEY (`chat_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业微信群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_group_code
-- ----------------------------
DROP TABLE IF EXISTS `we_group_code`;
CREATE TABLE `we_group_code`
(
    `id`                      bigint(20) NOT NULL,
    `corp_id`                 varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业Id',
    `code_url`                varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '二维码链接',
    `uuid`                    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '二维码标识符',
    `activity_head_url`       varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活码头像链接',
    `activity_name`           varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '活码名称',
    `activity_desc`           varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '活码描述',
    `activity_scene`          varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '场景',
    `guide`                   varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '引导语',
    `join_group_is_tip`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '进群是否提示:1:是;0:否;',
    `tip_msg`                 varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '进群提示语',
    `customer_server_qr_code` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客服二维码',
    `del_flag`                tinyint(4) NOT NULL DEFAULT 0 COMMENT '0 未删除 1 已删除',
    `create_by`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`             datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`             datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`                  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    `avatar_url`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '活码头像链接',
    `show_tip`                int(11) NOT NULL DEFAULT 0,
    `seq`                     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '实际群码顺序',
    `create_type`             tinyint(2) NOT NULL DEFAULT '0' COMMENT '创建类型 1:群二维码 2: 企微活码',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户群活码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_group_code_actual
-- ----------------------------
DROP TABLE IF EXISTS `we_group_code_actual`;
CREATE TABLE `we_group_code_actual`
(
    `id`                    bigint(20) NOT NULL,
    `group_code_id`         bigint(20) NOT NULL DEFAULT 0 COMMENT '群活码id',
    `chat_id`               varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '群聊id',
    `actual_group_qr_code`  varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '实际群码',
    `group_name`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群名称',
    `effect_time`           datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效期',
    `scan_code_times_limit` int(11) NOT NULL DEFAULT 0 COMMENT '扫码次数限制',
    `chat_group_name`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群聊名称',
    `scan_code_times`       int(11) NOT NULL DEFAULT 0 COMMENT '扫码次数',
    `del_flag`              tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:正常使用;1:删除;',
    `status`                tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:使用中',
    `update_time`           datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time`           datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `chat_ids`              varchar(255)                                                  NOT NULL DEFAULT '' COMMENT '群ids，逗号分割',
    `scene`                 tinyint(2) NOT NULL DEFAULT '2' COMMENT '场景。1 - 群的小程序插件 2 - 群的二维码插件',
    `remark`                varchar(30)                                                   NOT NULL DEFAULT '' COMMENT '联系方式的备注信息，用于助记',
    `room_base_id`          int(11) NOT NULL DEFAULT '1' COMMENT '起始序号',
    `room_base_name`        varchar(40)                                                   NOT NULL DEFAULT '' COMMENT '群名前缀',
    `auto_create_room`      tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否自动新建群。0-否；1-是。 默认为1',
    `state`                 varchar(30)                                                   NOT NULL DEFAULT '' COMMENT '企业自定义的state参数，用于区分不同的入群渠道',
    `config_id`             varchar(64)                                                   NOT NULL DEFAULT '' COMMENT '加群配置id',
    `sort_no`               int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序字段',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '实际群码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_group_member
-- ----------------------------
DROP TABLE IF EXISTS `we_group_member`;
CREATE TABLE `we_group_member`
(
    `id`         bigint(20) NOT NULL,
    `user_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群成员id',
    `chat_id`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群id',
    `corp_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id',
    `union_id`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '外部联系人在微信开放平台的唯一身份标识',
    `join_time`  timestamp(0)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加群时间',
    `join_scene` tinyint(4) NOT NULL DEFAULT 0 COMMENT '加入方式',
    `type`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '成员类型:1 - 企业成员;2 - 外部联系人',
    `name`       varchar(32)                                                  NOT NULL DEFAULT '' COMMENT '成员名称',
    `invitor`    varchar(64)                                                  NOT NULL DEFAULT '' COMMENT '邀请者userId',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `un_member`(`user_id`, `chat_id`, `corp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业微信群成员' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for we_group_statistic
-- ----------------------------
DROP TABLE IF EXISTS `we_group_statistic`;
CREATE TABLE `we_group_statistic`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`        varchar(64)                                                  NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `chat_id`        varchar(65) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '群ID',
    `stat_time`      datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据日期',
    `new_chat_cnt`   int(10) NOT NULL DEFAULT 0 COMMENT '新增客户群数量',
    `chat_total`     int(10) NOT NULL DEFAULT 0 COMMENT '截至当天客户群总数量',
    `chat_has_msg`   int(10) NOT NULL DEFAULT 0 COMMENT '截至当天有发过消息的客户群数量',
    `new_member_cnt` int(10) NOT NULL DEFAULT 0 COMMENT '客户群新增群人数',
    `member_total`   int(10) NOT NULL DEFAULT 0 COMMENT '截至当天客户群总人数',
    `member_has_msg` int(10) NOT NULL DEFAULT 0 COMMENT '截至当天有发过消息的群成员数',
    `msg_total`      int(10) NOT NULL DEFAULT 0 COMMENT '截至当天客户群消息总数',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `stat_time_index`(`stat_time`, `chat_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1420806332035928142 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '群聊数据统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_material
-- ----------------------------
DROP TABLE IF EXISTS `we_material`;
CREATE TABLE `we_material`
(
    `id`                   bigint(100) NOT NULL COMMENT '主键id',
    `category_id`          bigint(100) NOT NULL DEFAULT 0 COMMENT '分类id',
    `material_url`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '本地资源文件地址',
    `content`              text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文本内容、图片文案',
    `material_name`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '图片名称',
    `digest`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '摘要',
    `create_by`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`          datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`          datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `cover_url`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '封面本地资源文件',
    `audio_time`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '0' COMMENT '音频时长',
    `expire_time`          datetime(0) NOT NULL DEFAULT '2099-01-01 00:00:00' COMMENT '过期时间',
    `show_material`        tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否发布到侧边栏（0否，1是）',
    `temp_flag`            tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为临时素材（0：正常显示的素材，1：临时素材）',
    `is_defined`           TINYINT(1) NOT NULL DEFAULT '0'
        COMMENT '链接时使用(0:默认,1:自定义)',
    `enable_convert_radar` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接时使用(0,不转化为雷达，1：转化为雷达)',
    `radar_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '素材表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_material_config
-- ----------------------------
DROP TABLE IF EXISTS `we_material_config`;
CREATE TABLE `we_material_config`
(
    `corp_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id',
    `del_days` int(3) NOT NULL DEFAULT 0 COMMENT '自动删除天数',
    `is_del`   tinyint(1) NOT NULL DEFAULT 0 COMMENT '过期后是否自动删除（0否，1是）',
    PRIMARY KEY (`corp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业素材配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_material_tag
-- ----------------------------
DROP TABLE IF EXISTS `we_material_tag`;
CREATE TABLE `we_material_tag`
(
    `id`       int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `corp_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '企业id',
    `tag_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签名称',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '素材标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_material_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `we_material_tag_rel`;
CREATE TABLE `we_material_tag_rel`
(
    `id`              int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `material_id`     bigint(100) NOT NULL DEFAULT 0 COMMENT '素材id',
    `material_tag_id` int(10) NOT NULL DEFAULT 0 COMMENT '素材标签id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_material_tag`(`material_id`, `material_tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '素材标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_message_push
-- ----------------------------
DROP TABLE IF EXISTS `we_message_push`;
CREATE TABLE `we_message_push`
(
    `chat_id`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '群聊id',
    `message_push_id` bigint(20) NOT NULL COMMENT '主键id',
    `agent_id`        int(11) NOT NULL DEFAULT 0 COMMENT '企业应用的id',
    `push_type`       int(1) NOT NULL DEFAULT 0 COMMENT '群发类型 0 发给客户 1 发给客户群',
    `message_type`    varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '消息类型 0 文本消息  1 图片消息 2 语音消息  3 视频消息    4 文件消息 5 文本卡片消息 6 图文消息\r\n7 图文消息（mpnews） 8 markdown消息 9 小程序通知消息 10 任务卡片消息',
    `to_user`         varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '指定接收消息的成员',
    `to_party`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '指定接收消息的部门',
    `to_tag`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '指定接收消息的标签',
    `message_json`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息体',
    `push_range`      varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '消息范围 0 全部客户  1 指定客户',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`     datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        int(1) NOT NULL DEFAULT 0 COMMENT '0 未删除 1 已删除',
    `invaliduser`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '无效用户',
    `invalidparty`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '无效单位',
    `invalidtag`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '无效标签',
    PRIMARY KEY (`message_push_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息发送的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_pres_tag_group
-- ----------------------------
DROP TABLE IF EXISTS `we_pres_tag_group`;
CREATE TABLE `we_pres_tag_group`
(
    `task_id`        bigint(20) NOT NULL COMMENT '老客户标签建群任务id',
    `corp_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '企业ID',
    `group_code_id`  bigint(11) NOT NULL DEFAULT 0 COMMENT '群活码id',
    `msgid`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '企业群发消息的id',
    `task_name`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '任务名称',
    `send_type`      tinyint(1) NOT NULL DEFAULT 0 COMMENT '发送方式 0: 企业群发 1：个人群发',
    `create_by`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`    datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `send_scope`     tinyint(1) NOT NULL DEFAULT 0 COMMENT '发送范围 0: 全部客户 1：部分客户',
    `send_gender`    tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '发送性别 0: 全部 1： 男 2： 女 3：未知',
    `cus_begin_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '目标客户被添加起始时间',
    `cus_end_time`   datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '目标客户被添加结束时间',
    `welcome_msg`    varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '加群引导语',
    `del_flag`       tinyint(1) NOT NULL DEFAULT 0 COMMENT '0:未删除、  1:删除;',
    PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '老客户标签建群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_pres_tag_group_scope
-- ----------------------------
DROP TABLE IF EXISTS `we_pres_tag_group_scope`;
CREATE TABLE `we_pres_tag_group_scope`
(
    `task_id`    bigint(20) NOT NULL COMMENT '老客户标签建群任务id',
    `we_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '员工id',
    `is_done`    smallint(1) NOT NULL DEFAULT 0 COMMENT '是否已处理'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '老客标签建群使用范围表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_pres_tag_group_stat
-- ----------------------------
DROP TABLE IF EXISTS `we_pres_tag_group_stat`;
CREATE TABLE `we_pres_tag_group_stat`
(
    `task_id`         bigint(20) NOT NULL COMMENT '老客标签建群任务id',
    `external_userid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户id',
    `user_id`         VARCHAR(64) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci                                                  NOT NULL DEFAULT ''
        COMMENT '员工ID',
    `customer_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    `is_sent`         tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已送达',
    `is_in_group`     tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已经在群'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '老客标签建群客户统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_pres_tag_group_tag
-- ----------------------------
DROP TABLE IF EXISTS `we_pres_tag_group_tag`;
CREATE TABLE `we_pres_tag_group_tag`
(
    `task_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '老客户标签建群任务id',
    `tag_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '老客标签建群标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_sensitive
-- ----------------------------
DROP TABLE IF EXISTS `we_sensitive`;
CREATE TABLE `we_sensitive`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `audit_user_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '0' COMMENT '审计人id',
    `strategy_name`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '策略名称',
    `pattern_words`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '匹配词',
    `audit_user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '审计人',
    `alert_flag`      tinyint(4) NOT NULL DEFAULT 1 COMMENT '消息通知,1 开启 0 关闭',
    `del_flag`        tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识，1 已删除 0 未删除',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`     datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `corp_id`         varchar(64)                                                   NOT NULL DEFAULT '' COMMENT '企业id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_sensitive_act
-- ----------------------------
DROP TABLE IF EXISTS `we_sensitive_act`;
CREATE TABLE `we_sensitive_act`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `act_name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '敏感行为名称',
    `order_num`   int(11) NOT NULL DEFAULT 0 COMMENT '排序字段',
    `enable_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '记录敏感行为,1 开启 0 关闭',
    `del_flag`    tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识，1 已删除 0 未删除',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `corp_id`     varchar(64)                                                   NOT NULL DEFAULT '' COMMENT '企业id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感行为表' ROW_FORMAT = Dynamic;

-- INSERT INTO `we_sensitive_act` VALUES (1, '拉黑/删除客户', 0, 1, 0, 'admin', '2021-08-06 12:20:09', 'admin', '2021-08-06 12:20:09');
-- INSERT INTO `we_sensitive_act` VALUES (5, '互发红包', 0, 1, 0, 'admin', '2021-08-06 16:04:52', 'admin', '2021-08-06 16:04:52');
-- INSERT INTO `we_sensitive_act` VALUES (6, '员工发送名片', 0, 1, 0, 'admin', '2021-08-06 16:05:09', 'admin', '2021-08-06 16:05:09');

-- ----------------------------
-- Table structure for we_sensitive_act_hit
-- ----------------------------
DROP TABLE IF EXISTS `we_sensitive_act_hit`;
CREATE TABLE `we_sensitive_act_hit`
(
    `id`                BIGINT(20) NOT NULL AUTO_INCREMENT
        COMMENT '主键',
    `operator_id`       VARCHAR(64) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '敏感行为操作人id',
    `operator`          VARCHAR(512) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '敏感行为操作人信息',
    `operate_target_id` VARCHAR(64) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '敏感行为操作对象id',
    `operate_target`    VARCHAR(512) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '敏感行为操作对象信息',
    `sensitive_act_id`  BIGINT(20) NOT NULL DEFAULT 0
        COMMENT '敏感行为id',
    `sensitive_act`     VARCHAR(100) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '敏感行为名称',
    `del_flag`          TINYINT(4) NOT NULL DEFAULT 0
        COMMENT '删除标识，1 已删除 0 未删除',
    `create_by`         VARCHAR(64) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '创建人',
    `create_time`       DATETIME(0) NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '创建时间',
    `update_by`         VARCHAR(64) CHARACTER SET utf8mb4
        COLLATE utf8mb4_general_ci  NOT NULL DEFAULT ''
        COMMENT '更新人',
    `update_time`       datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `corp_id`           varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_sensitive_audit_scope
-- ----------------------------
DROP TABLE IF EXISTS `we_sensitive_audit_scope`;
CREATE TABLE `we_sensitive_audit_scope`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `sensitive_id`     bigint(20) NOT NULL DEFAULT 0 COMMENT '敏感词表主键',
    `audit_scope_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审计对象id',
    `scope_type`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '审计范围类型, 1 组织机构 2 成员',
    `audit_scope_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '审计对象名称',
    `corp_id`          varchar(64)                                                  NOT NULL DEFAULT '' COMMENT '企业id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词审计范围' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_tag
-- ----------------------------
DROP TABLE IF EXISTS `we_tag`;
CREATE TABLE `we_tag`
(
    `tag_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '微信端返回的id',
    `group_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '标签组id',
    `corp_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签名',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除）',
    `seq_id`      BIGINT ( 20 ) NOT NULL AUTO_INCREMENT COMMENT '非主键自增序列号',
    PRIMARY KEY (`tag_id`) USING BTREE,
    KEY           `idx_seq_id` ( `seq_id` ) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业微信标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_tag_group
-- ----------------------------
DROP TABLE IF EXISTS `we_tag_group`;
CREATE TABLE `we_tag_group`
(
    `group_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `group_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标签组名',
    `corp_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL DEFAULT '0' COMMENT '帐号状态（0正常 2删除）',
    PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签组' ROW_FORMAT = Dynamic;



-- ----------------------------
-- Table structure for we_user
-- ----------------------------
DROP TABLE IF EXISTS `we_user`;
CREATE TABLE `we_user`
(
    `corp_id`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业id',
    `user_id`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `head_image_url`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '头像地址',
    `user_name`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名称',
    `alias`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
    `gender`            tinyint(1) NOT NULL DEFAULT 1 COMMENT '性别。1表示男性，2表示女性',
    `mobile`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '手机号',
    `email`             varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '邮箱',
    `wx_account`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '个人微信号',
    `main_department`   bigint(20) NOT NULL DEFAULT '0' COMMENT '主部门ID',
    `department`        varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户所属部门,使用逗号隔开,字符串格式存储',
    `position`          varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '职务',
    `is_leader_in_dept` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '1表示为上级,0表示普通成员(非上级)。',
    `join_time`         date                                                          NOT NULL DEFAULT '0000-00-00' COMMENT '入职时间',
    `enable`            tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否启用(1表示启用成员，0表示禁用成员)',
    `id_card`           char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL DEFAULT '' COMMENT '身份证号',
    `qq_account`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT 'QQ号',
    `telephone`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '座机',
    `address`           varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '地址',
    `birthday`          date                                                          NOT NULL DEFAULT '0000-00-00' COMMENT '生日',
    `remark`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
    `customer_tags`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '客户标签,字符串使用逗号隔开',
    `dimission_time`    datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '离职时间',
    `is_allocate`       tinyint(1) NOT NULL DEFAULT 0 COMMENT '离职是否分配(1:已分配;0:未分配;)',
    `is_activate`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除',
    `isOpenChat`        tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否开启会话存档 0：关闭 1：开启',
    `create_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '创建人',
    `update_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '更新人',
    `create_time`       datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
    `update_time`       datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
    `ui_color`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '#6BB4AB' COMMENT '后台界面主题颜色',
    PRIMARY KEY (`corp_id`, `user_id`) USING BTREE,
    KEY                 `idx_is_activate` (`is_activate`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_user_behavior_data
-- ----------------------------
DROP TABLE IF EXISTS `we_user_behavior_data`;
CREATE TABLE `we_user_behavior_data`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `corp_id`               varchar(64)  NOT NULL DEFAULT '' COMMENT '企业ID',
    `user_id`               varchar(128) NOT NULL DEFAULT '' COMMENT '员工id',
    `stat_time`             datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '数据日期，为当日0点的时间戳',
    `new_apply_cnt`         int(10) NOT NULL DEFAULT '0' COMMENT '发起申请数',
    `new_contact_cnt`       int(10) NOT NULL DEFAULT '0' COMMENT '新增客户数，成员新添加的客户数量',
    `chat_cnt`              int(10) NOT NULL DEFAULT '0' COMMENT '聊天总数， 成员有主动发送过消息的单聊总数',
    `message_cnt`           int(10) NOT NULL DEFAULT '0' COMMENT '发送消息数，成员在单聊中发送的消息总数',
    `reply_percentage`      double       NOT NULL DEFAULT '0' COMMENT '已回复聊天占比，浮点型，客户主动发起聊天后，成员在一个自然日内有回复过消息的聊天数/客户主动发起的聊天数比例，不包括群聊，仅在确有聊天时返回',
    `avg_reply_time`        int(10) NOT NULL DEFAULT '0' COMMENT '平均首次回复时长',
    `negative_feedback_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '删除/拉黑成员的客户数，即将成员删除或加入黑名单的客户数',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                   `stat_time_index` (`stat_time`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1420806352176977808 DEFAULT CHARSET=utf8mb4 COMMENT='联系客户统计数据 ';


DROP TABLE IF EXISTS `we_auth_corp_info`;
CREATE TABLE `we_auth_corp_info`
(
    `corp_id`              VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '授权企业ID',
    `suite_id`             VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '第三方应用的SuiteId',
    `permanent_code`       VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '企业微信永久授权码,最长为512字节',
    `corp_name`            VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权方企业名称，即企业简称',
    `corp_type`            VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '授权方企业类型，认证号：verified, 注册号：unverified',
    `corp_square_logo_url` VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '授权方企业方形头像',
    `corp_user_max`        INT(11) NOT NULL DEFAULT '0'
        COMMENT '授权方企业用户规模',
    `corp_agent_max`       INT(11) NOT NULL DEFAULT '0'
        COMMENT '授权方企业应用数上限',
    `corp_full_name`       VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权方企业的主体名称(仅认证或验证过的企业有)，即企业全称。',
    `subject_type`         TINYINT(2) NOT NULL DEFAULT '0'
        COMMENT '企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号',
    `verified_end_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '认证到期时间',
    `corp_wxqrcode`        VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '授权企业在微工作台（原企业号）的二维码，可用于关注微工作台',
    `corp_scale`           VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '企业规模。当企业未设置该属性时，值为空',
    `corp_industry`        VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '企业所属行业。当企业未设置该属性时，值为空',
    `corp_sub_industry`    VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '企业所属子行业。当企业未设置该属性时，值为空',
    `cancel_auth`          TINYINT(1) NOT NULL DEFAULT '0'
        COMMENT '取消授权(0N1Y)',
    PRIMARY KEY (`corp_id`, `suite_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='授权企业信息表';


DROP TABLE IF EXISTS `we_auth_corp_info_extend`;
CREATE TABLE `we_auth_corp_info_extend`
(
    `corp_id`                          VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '授权企业ID',
    `suite_id`                         VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '第三方应用的SuiteId',
    `agentid`                          VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '授权方应用id',
    `name`                             VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权方应用名字',
    `square_logo_url`                  VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权方应用方形头像',
    `round_logo_url`                   VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权方应用圆形头像',
    `auth_mode`                        TINYINT(2) NOT NULL DEFAULT '-1'
        COMMENT '授权模式，0为管理员授权；1为成员授权',
    `is_customized_app`                TINYINT(1) NOT NULL DEFAULT '0'
        COMMENT '是否为代开发自建应用(0N1Y)',
    `auth_user_info_userid`            VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '授权管理员的userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）',
    `auth_user_info_open_userid`       VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '授权管理员的open_userid，可能为空（企业互联由上级企业共享第三方应用给下级时，不返回授权的管理员信息）',
    `auth_user_info_name`              VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权管理员的name，可能为空',
    `auth_user_info_avatar`            VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '授权管理员的头像url，可能为空',
    `dealer_corp_info_corpid`          VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '代理服务商企业微信id',
    `dealer_corp_info_corp_name`       VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '代理服务商企业微信名称',
    `register_code_info_register_code` VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '注册码 , 最长为512个字节',
    `register_code_info_template_id`   VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '推广包ID，最长为128个字节',
    `register_code_info_state`         VARCHAR(128) NOT NULL DEFAULT ''
        COMMENT '用户自定义的状态值。只支持英文字母和数字，最长为128字节。',
    PRIMARY KEY (`corp_id`, `suite_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='授权企业信息扩展表';

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`
(
    `table_id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_name`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
    `table_comment`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
    `class_name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
    `tpl_category`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
    `package_name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
    `module_name`     varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
    `business_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
    `function_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
    `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
    `gen_type`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
    `gen_path`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
    `options`         varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time`     datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time`     datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`
(
    `column_id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
    `table_id`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属表编号',
    `column_name`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
    `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
    `column_type`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
    `java_type`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
    `java_field`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
    `is_pk`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
    `is_increment`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
    `is_required`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
    `is_insert`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
    `is_edit`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
    `is_list`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
    `is_query`       char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
    `query_type`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
    `html_type`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    `dict_type`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
    `sort`           int(11) NULL DEFAULT NULL COMMENT '排序',
    `create_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time`    datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time`    datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 344 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`
(
    `sched_name`    varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_name`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `blob_data`     blob NULL,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
    CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`
(
    `sched_name`    varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `calendar`      blob                                                          NOT NULL,
    PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`
(
    `sched_name`      varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_name`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `time_zone_id`    varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
    CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`
(
    `sched_name`        varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `entry_id`          varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `trigger_name`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `instance_name`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `fired_time`        bigint(13) NOT NULL,
    `sched_time`        bigint(13) NOT NULL,
    `priority`          int(11) NOT NULL,
    `state`             varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `job_name`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `job_group`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `is_nonconcurrent`  varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`
(
    `sched_name`        varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `job_name`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `job_group`         varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `description`       varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `job_class_name`    varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `is_durable`        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL,
    `is_nonconcurrent`  varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL,
    `is_update_data`    varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL,
    `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL,
    `job_data`          blob NULL,
    PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`
(
    `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `lock_name`  varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`
(
    `sched_name`    varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`
(
    `sched_name`        varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `instance_name`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `last_checkin_time` bigint(13) NOT NULL,
    `checkin_interval`  bigint(13) NOT NULL,
    PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`
(
    `sched_name`      varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_name`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `repeat_count`    bigint(7) NOT NULL,
    `repeat_interval` bigint(12) NOT NULL,
    `times_triggered` bigint(10) NOT NULL,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
    CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`
(
    `sched_name`    varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_name`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `str_prop_1`    varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `str_prop_2`    varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `str_prop_3`    varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `int_prop_1`    int(11) NULL DEFAULT NULL,
    `int_prop_2`    int(11) NULL DEFAULT NULL,
    `long_prop_1`   bigint(20) NULL DEFAULT NULL,
    `long_prop_2`   bigint(20) NULL DEFAULT NULL,
    `dec_prop_1`    decimal(13, 4) NULL DEFAULT NULL,
    `dec_prop_2`    decimal(13, 4) NULL DEFAULT NULL,
    `bool_prop_1`   varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `bool_prop_2`   varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
    CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`
(
    `sched_name`     varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_name`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `trigger_group`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `job_name`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `job_group`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `description`    varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `next_fire_time` bigint(13) NULL DEFAULT NULL,
    `prev_fire_time` bigint(13) NULL DEFAULT NULL,
    `priority`       int(11) NULL DEFAULT NULL,
    `trigger_state`  varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `trigger_type`   varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL,
    `start_time`     bigint(13) NOT NULL,
    `end_time`       bigint(13) NULL DEFAULT NULL,
    `calendar_name`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `misfire_instr`  smallint(2) NULL DEFAULT NULL,
    `job_data`       blob NULL,
    PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
    INDEX            `sched_name`(`sched_name`, `job_name`, `job_group`) USING BTREE,
    CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_leave_user
-- ----------------------------
DROP TABLE IF EXISTS `we_leave_user`;
CREATE TABLE `we_leave_user`
(
    `id`                   bigint(20) NOT NULL,
    `user_id`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '用户id',
    `corp_id`              varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业ID',
    `head_image_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '头像地址',
    `user_name`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户名称',
    `alias`                varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
    `main_department`      bigint(20) NOT NULL DEFAULT 0 COMMENT '主部门id，用于数据权限效验',
    `main_department_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户所属主部门名字',
    `is_allocate`          tinyint(1) NOT NULL DEFAULT 0 COMMENT '离职是否分配(1:已分配;0:未分配;)',
    `dimission_time`       datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '离职时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `user_id` (`user_id`,`dimission_time`,`corp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业员工离职表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_application_center
-- ----------------------------
CREATE TABLE `we_application_center`
(
    `appid`                    INT(11) NOT NULL AUTO_INCREMENT
        COMMENT '应用ID',
    `name`                     VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '应用名',
    `description`              VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '应用描述',
    `logo_url`                 VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '应用头像',
    `type`                     TINYINT(2) NOT NULL DEFAULT '0'
        COMMENT '应用类型(1:企业工具，2:客户资源，3:内容资源)',
    `introduction`             LONGTEXT     NOT NULL
        COMMENT '功能介绍',
    `instructions`             LONGTEXT     NOT NULL
        COMMENT '使用说明',
    `consulting_service`       LONGTEXT     NOT NULL
        COMMENT '咨询服务',
    `enable`                   TINYINT(1) NOT NULL DEFAULT '1'
        COMMENT '启用(ON1Y)',
    `create_time`              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '上架时间',
    `development_type`         TINYINT(1) NOT NULL DEFAULT '1'
        COMMENT '开发类型（1自研，2三方）',
    `sidebar_redirect_url`     VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '侧边栏url:自研存储相对路径，三方开发存储完整url',
    `application_entrance_url` VARCHAR(512) NOT NULL DEFAULT ''
        COMMENT '应用入口url',
    PRIMARY KEY (`appid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COMMENT ='应用中心表';



INSERT INTO `we_application_center`
VALUES (2, '青鸾智能工单系统', '企微群聊自动分析反馈内容形成工单，自动根据工单状态变更回复工单内容。',
        '',
        1,
        '群聊关联客户：企微群聊关联工单系统客户关系对应；\r\n自动获取工单信息：根据NLP自动获取信息，形成工单；\r\n自动回复工单信息：工单状态变更，自动群聊@工单反馈人员，自动告知状态；\r\n工单报表：自动发送当日工单报表；',
        '使用该应用需进行初始化操作，进行工单系统注册、客服人员账号注册、客户资料维护；若您有疑问请扫描以下二维码，添加客服，咨询初始化操作事宜',
        'https://wecomsaas-1253559996.cos.ap-guangzhou.myqcloud.com/2021/12/16/壹鸽咨询服务.png',
        1, '2021-12-20 09:59:03', 1, 'workSheetAssistant', 'http://121.37.253.126:8088');


CREATE TABLE `order_user_to_order_account`
(
    `id`              INT(11) NOT NULL AUTO_INCREMENT
        COMMENT '主键',
    `user_id`         VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '企业员工ID',
    `corp_id`         VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '企业ID',
    `network_id`      VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '网点ID',
    `order_user_id`   VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '工单帐号ID',
    `order_user_name` VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '工单帐号名',
    `bind_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_user` (`user_id`, `corp_id`) USING BTREE,
    UNIQUE KEY `uniq_order_user` (`network_id`, `order_user_id`, `corp_id`, `user_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='企业员工与工单帐号绑定关系表';

-- ----------------------------
-- Table structure for we_my_application
-- ----------------------------
CREATE TABLE `we_my_application`
(
    `id`           INT(11) NOT NULL AUTO_INCREMENT
        COMMENT 'ID',
    `corp_id`      VARCHAR(64) NOT NULL DEFAULT ''
        COMMENT '企业ID',
    `appid`        INT(11) NOT NULL DEFAULT '-1'
        COMMENT '应用ID',
    `config`       LONGTEXT    NOT NULL
        COMMENT '应用配置',
    `enable`       TINYINT(1) NOT NULL DEFAULT '1'
        COMMENT '启用(ON1Y)',
    `install_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '安装时间',
    `expire_time`  DATETIME    NOT NULL DEFAULT '2099-01-01 00:00:00'
        COMMENT '过期时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uniq_corpid_appid` (`corp_id`, `appid`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COMMENT ='我的应用';

CREATE TABLE `we_my_application_use_scope`
(
    `id`      INT(11) NOT NULL AUTO_INCREMENT
        COMMENT 'ID',
    `corp_id` VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '企业ID',
    `appid`   INT(11) NOT NULL DEFAULT '-1'
        COMMENT '应用ID',
    `type`    TINYINT(1) NOT NULL DEFAULT '0'
        COMMENT '使用类型(1指定员工，2指定角色)',
    `val`     VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '指定员工存userId,指定角色存角色ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='我的应用使用范围';

CREATE TABLE `order_group_to_order_customer`
(
    `id`                  INT(11) NOT NULL AUTO_INCREMENT
        COMMENT '主键',
    `chat_id`             VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '外部群ID',
    `corp_id`             VARCHAR(64)  NOT NULL DEFAULT ''
        COMMENT '企业ID',
    `network_id`          VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '网点ID',
    `order_customer_id`   VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '工单客户ID',
    `order_customer_name` VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '工单客户名',
    `bind_time`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '绑定时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_chat` (`chat_id`, `corp_id`) USING BTREE,
    UNIQUE KEY `uniq_customer` (`network_id`, `order_customer_id`, `chat_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='企业客户群与工单客户绑定关系';


SET
FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for we_words_category
-- ----------------------------
DROP TABLE IF EXISTS `we_words_category`;
CREATE TABLE `we_words_category`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`   varchar(64)  NOT NULL DEFAULT '' COMMENT '企业ID',
    `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '父分组（0为根节点）',
    `type`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '话术类型（0：企业话术，1：部门话术，2：我的话术）',
    `use_range` varchar(255) NOT NULL DEFAULT '1' COMMENT '使用范围（企业话术：存入根部门1，部门话术：部门id，我的话术：员工id）',
    `name`      varchar(128) NOT NULL DEFAULT '' COMMENT '文件夹名称',
    `sort`      int(11) NOT NULL DEFAULT '0' COMMENT '文件夹排序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_corpid_type_name_userange` (`corp_id`,`type`,`name`,`use_range`) USING BTREE COMMENT '同一类型和权限范围下的文件夹名不能重名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术库文件夹表';

-- ----------------------------
-- Table structure for we_words_group
-- ----------------------------
DROP TABLE IF EXISTS `we_words_group`;
CREATE TABLE `we_words_group`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `category_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件夹ID',
    `title`       varchar(64) NOT NULL DEFAULT '' COMMENT '话术标题',
    `seq`         longtext    NOT NULL COMMENT '附件ID用逗号隔开，从左往右表示先后顺序',
    `is_push`     tinyint(1) NOT NULL COMMENT '是否推送到应用（0：不推送，1推送）',
    `sort`        int(11) NOT NULL DEFAULT '0' COMMENT '排序',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术库主表';


-- ----------------------------
-- Table structure for we_words_detail
-- ----------------------------
DROP TABLE IF EXISTS `we_words_detail`;
CREATE TABLE `we_words_detail`
(
    `id`         bigint(20) NOT NULL DEFAULT '0' COMMENT '话术库附件ID',
    `corp_id`    varchar(64)   NOT NULL DEFAULT '' COMMENT '企业ID',
    `group_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT '话术主表ID',
    `media_type` int(1) NOT NULL DEFAULT '0' COMMENT '0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序',
    `content`    varchar(1500) NOT NULL DEFAULT '' COMMENT '话术详情',
    `title`      varchar(128)  NOT NULL DEFAULT '' COMMENT '标题',
    `url`        varchar(3000) NOT NULL DEFAULT '' COMMENT '链接地址',
    `cover_url`  varchar(255)  NOT NULL DEFAULT '' COMMENT '封面',
    `is_defined` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接时使用：0 默认，1 自定义',
    `size`       bigint(20) NOT NULL DEFAULT '0' COMMENT '视频大小',
    `radar_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话术库附件表';

DROP TABLE IF EXISTS `we_words_last_use`;
CREATE TABLE `we_words_last_use`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `corp_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '企业id',
    `user_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '员工id',
    `type`      tinyint(1) NOT NULL DEFAULT 0 COMMENT '话术类型（0：企业话术，1：部门话术，2：我的话术）',
    `words_ids` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '话术id（用逗号隔开最多5个）',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uniq_corp_user_type`(`corp_id`, `user_id`, `type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '最近使用话术表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for we_emple_code_material
-- ----------------------------
DROP TABLE IF EXISTS `we_emple_code_material`;
CREATE TABLE `we_emple_code_material`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `emple_code_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '员工活码ID',
    `media_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT '素材ID',
    `media_type`    int(1) NOT NULL DEFAULT '0' COMMENT '-1：群活码、0：图片、1：语音、2：视频，3：文件、4：文本、5：图文链接、6：小程序',
    PRIMARY KEY (`id`),
    KEY             `normal_emplecode` (`emple_code_id`) USING BTREE COMMENT '普通索引emple_code_id'
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='员工活码附件表';


-- ----------------------------
-- Table structure for we_emple_code_analyse
-- ----------------------------
DROP TABLE IF EXISTS `we_emple_code_analyse`;
CREATE TABLE `we_emple_code_analyse`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `emple_code_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT '员工活码主键ID',
    `user_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '企业成员userId',
    `external_userid` varchar(32) NOT NULL DEFAULT '' COMMENT '客户ID',
    `time`            date        NOT NULL COMMENT '添加时间',
    `type`            tinyint(1) NOT NULL COMMENT '1:新增，0:流失',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_corpid_codeid_userid_extid_type_time` (`corp_id`,`emple_code_id`,`user_id`,`external_userid`,`type`,`time`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for we_group_tag_category
-- ----------------------------
DROP TABLE IF EXISTS `we_group_tag_category`;
CREATE TABLE `we_group_tag_category`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `name`    varchar(16) NOT NULL COMMENT '群标签组名',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_corpid_name` (`corp_id`,`name`) USING BTREE COMMENT '唯一索引（corp_id、name）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群标签组表';

-- ----------------------------
-- Table structure for we_group_tag
-- ----------------------------
DROP TABLE IF EXISTS `we_group_tag`;
CREATE TABLE `we_group_tag`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `group_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '群标签组ID',
    `name`     varchar(16) NOT NULL COMMENT '群标签名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_groupid_name` (`group_id`,`name`) USING BTREE COMMENT '唯一索引（group_id、name）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群标签表';

-- ----------------------------
-- Table structure for we_group_tag_rel
-- ----------------------------
DROP TABLE IF EXISTS `we_group_tag_rel`;
CREATE TABLE `we_group_tag_rel`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `chat_id` varchar(32) NOT NULL DEFAULT '' COMMENT '群ID',
    `tag_id`  bigint(20) NOT NULL DEFAULT '0' COMMENT '标签ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_corpid_chatid_tagid` (`corp_id`, `chat_id`, `tag_id`) USING BTREE COMMENT '唯一索引（corp_id，chat_id，tag_id）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='客户群和标签关联表';

-- ----------------------------
-- Table structure for we_customer_extend_property
-- ----------------------------
CREATE TABLE `we_customer_extend_property`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`       varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `name`          varchar(64) NOT NULL DEFAULT '' COMMENT '扩展字段名称',
    `type`          int(11) NOT NULL DEFAULT '2' COMMENT '字段类型（1系统默认字段,2单行文本，3多行文本，4单选框，5多选框，6下拉框，7日期，8图片，9文件）',
    `required`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填（1必填0非必填）',
    `property_sort` int(11) NOT NULL DEFAULT '20' COMMENT '字段排序',
    `status`        tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用1启用）',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_corp_id_name` (`corp_id`, `name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='自定义属性关系表';

-- ----------------------------
-- Table structure for extend_property_multiple_option
-- ----------------------------
CREATE TABLE `extend_property_multiple_option`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `extend_property_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '扩展属性ID',
    `multiple_value`     varchar(400) NOT NULL DEFAULT '' COMMENT '多选框.下拉框,单选框可选值',
    `option_sort`        int(11) NOT NULL DEFAULT '1' COMMENT '多选值的排序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_property_id` (`extend_property_id`, `multiple_value`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='扩展属性多选项可选值表';

-- ----------------------------
-- Table structure for we_customer_extend_property_rel
-- ----------------------------
CREATE TABLE `we_customer_extend_property_rel`
(
    `corp_id`            varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `user_id`            varchar(64)  NOT NULL DEFAULT '' COMMENT '跟进人userId',
    `external_userid`    varchar(32)  NOT NULL DEFAULT '' COMMENT '成员客户关系ID',
    `extend_property_id` int(20) NOT NULL DEFAULT '0' COMMENT '扩展属性id',
    `property_value`     varchar(500) NOT NULL DEFAULT '' COMMENT '自定义属性的值',
    PRIMARY KEY (`corp_id`, `user_id`, `external_userid`, `extend_property_id`, `property_value`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='客户-自定义属性关系表';

-- ----------------------------
-- Table structure for we_operations_center_sop
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_sop`;
CREATE TABLE `we_operations_center_sop`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `name`        varchar(32) NOT NULL DEFAULT '' COMMENT 'SOP名称',
    `create_by`   varchar(64) NOT NULL DEFAULT '' COMMENT '创建人.员工userId',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `sop_type`    tinyint(2) NOT NULL DEFAULT '0' COMMENT 'sop类型 0：定时sop，1：循环sop，2：新客sop，3：活动sop，4：生日sop，5：群日历',
    `filter_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '使用群聊类型 0：指定群聊 ,1：筛选群聊 ',
    `is_open`     tinyint(2) NOT NULL DEFAULT '1' COMMENT '启用状态 0：关闭，1：启用',
    PRIMARY KEY (`id`),
    KEY           `index_corpid` (`corp_id`) USING BTREE COMMENT '普通索引(index_corpid)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOP基本信息';

-- ----------------------------
-- Table structure for we_operations_center_sop_scope
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_sop_scope`;
CREATE TABLE `we_operations_center_sop_scope`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `sop_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT 'we_operations_center_sop 主键ID',
    `target_id`   varchar(64) NOT NULL DEFAULT '' COMMENT '当为群sop时，为chatId;当为客户sop时，为userId,传入部门时为partyId',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `type`        tinyint(1) NOT NULL DEFAULT '2' COMMENT '传入员工/部门 2-员工 1部门',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_corpid_sopid_targetid` (`corp_id`,`sop_id`,`target_id`) USING BTREE COMMENT '唯一索引（unique_corpid_sopid_targetid）',
    KEY           `index_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '普通索引（index_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SOP作用范围';

-- ----------------------------
-- Table structure for we_operations_center_group_sop_filter
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_group_sop_filter`;
CREATE TABLE `we_operations_center_group_sop_filter`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `sop_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT 'we_operations_center_sop主键ID',
    `owner`       text        NOT NULL COMMENT '群主( 多个逗号隔开)',
    `tag_id`      text        NOT NULL COMMENT '群标签ID（多个逗号隔开）',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '群创建时间范围',
    `end_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '群创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '唯一索引（unique_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='群SOP筛选群聊条件';

-- ----------------------------
-- Table structure for we_operations_center_customer_sop_filter
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_customer_sop_filter`;
CREATE TABLE `we_operations_center_customer_sop_filter`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`       varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `sop_id`        bigint(20) NOT NULL DEFAULT '0' COMMENT 'we_operations_center_sop主键ID',
    `gender`        tinyint(4) NOT NULL DEFAULT '0' COMMENT '外部联系人性别 0-未知 1-男性 2-女性',
    `users`         text        NOT NULL COMMENT '所属员工（多个逗号隔开 ）',
    `departments`   text        NOT NULL COMMENT '所属部门（多个逗号隔开 ）',
    `tag_id`        text        NOT NULL COMMENT '标签ID（多个逗号隔开 ）',
    `cloumn_info`   text        NOT NULL COMMENT '客户属性名和值，json存储',
    `filter_tag_id` text        NOT NULL COMMENT '标签ID(多个逗号隔开) ',
    `start_time`    datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '客户添加开始时间',
    `end_time`      datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '客户添加截止时间',
    PRIMARY KEY (`id`),
    KEY             `index_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '普通索引（index_corpid_sopid）'
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='客户SOP筛选条件';

-- ----------------------------
-- Table structure for we_operations_center_sop_rules
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_sop_rules`;
CREATE TABLE `we_operations_center_sop_rules`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `sop_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT '群sop的主键id',
    `name`        varchar(32) NOT NULL DEFAULT '' COMMENT '规则名称',
    `alert_type`  tinyint(2) NOT NULL DEFAULT '0' COMMENT '提醒类型\r\n0：xx小时xx分钟提醒，1：xx天xx:xx提醒，2：每天xx:xx提醒，3：每周周x的xx:xx提醒，4：每月x日xx:xx提醒',
    `alert_data1` int(2) NOT NULL DEFAULT '0' COMMENT '提醒时间内容1',
    `alert_data2` varchar(20) NOT NULL DEFAULT '' COMMENT '提醒时间内容2',
    PRIMARY KEY (`id`),
    KEY           `idx_sop` (`sop_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sop规则表';

-- ----------------------------
-- Table structure for we_operations_center_sop_material
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_sop_material`;
CREATE TABLE `we_operations_center_sop_material`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `sop_id`      bigint(20) NOT NULL DEFAULT 0 COMMENT 'sop的主键id',
    `rule_id`     bigint(20) NOT NULL DEFAULT 0 COMMENT '规则id',
    `material_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '素材id',
    `sort`        int(11) NOT NULL DEFAULT 0 COMMENT '素材排序',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_corp_sop`(`corp_id`, `sop_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for we_operations_center_sop_detail
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_sop_detail`;
CREATE TABLE `we_operations_center_sop_detail`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `sop_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT 'sop的主键id',
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `user_id`     varchar(64) NOT NULL COMMENT '操作人/群主',
    `target_id`   varchar(32) NOT NULL DEFAULT '' COMMENT '消息接收者(当为客户时，填写客户userId；当为群时，填写群chatId)',
    `is_finish`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已执行 0：未执行，1：已执行',
    `alert_time`  datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '提醒时间',
    `finish_time` datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '完成时间',
    PRIMARY KEY (`id`),
    KEY           `idx_corp_sop` (`corp_id`,`sop_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for we_operations_center_sop_task
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_sop_task`;
CREATE TABLE `we_operations_center_sop_task`
(
    `id`         bigint(20) NOT NULL COMMENT '主键',
    `corp_id`    varchar(64)   NOT NULL DEFAULT '' COMMENT '企业id',
    `media_type` int(1) NOT NULL DEFAULT '0' COMMENT '0:海报,1:语音,2:视频,3:普通文件,4:文本,5:图文链接,6:小程序',
    `content`    varchar(1500) NOT NULL DEFAULT '' COMMENT '内容详情',
    `title`      varchar(128)  NOT NULL DEFAULT '' COMMENT '标题',
    `url`        varchar(255)  NOT NULL DEFAULT '' COMMENT '链接地址',
    `cover_url`  varchar(255)  NOT NULL DEFAULT '' COMMENT '封面',
    `is_defined` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接时使用：0 默认，1 自定义',
    PRIMARY KEY (`id`),
    KEY          `idx_corp` (`corp_id`) USING BTREE COMMENT '普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='sop待办任务素材表';

-- ----------------------------
-- Table structure for we_operations_center_group_sop_filter_cycle
-- ----------------------------
DROP TABLE IF EXISTS `we_operations_center_group_sop_filter_cycle`;
CREATE TABLE `we_operations_center_group_sop_filter_cycle`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业ID',
    `sop_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT 'sopId',
    `cycle_start` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `cycle_end`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_corpid_sopid` (`corp_id`,`sop_id`) USING BTREE COMMENT '唯一索引（unique_corpid_sopid）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户群SOP-循环SOP的起止时间设置表';
CREATE TABLE `we_external_user_mapping_user`
(
    `external_corp_id` VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '三方应用企业ID',
    `external_user_id` VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '三方应用员工ID',
    `corp_id`          VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '内部应用企业ID',
    `user_id`          VARCHAR(255) NOT NULL DEFAULT ''
        COMMENT '内部应用员工ID',
    PRIMARY KEY (`external_corp_id`, `external_user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='三方应用员工映射表';

-- ----------------------------
-- Table structure for we_customer_transfer_config
-- ----------------------------
CREATE TABLE `we_customer_transfer_config`
(
    `corp_id`              varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `enable_transfer_info` tinyint(1) NOT NULL DEFAULT '1' COMMENT '继承客户信息开关（1：开启，0：关闭）',
    `enable_side_bar`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '侧边栏转接客户开关（1:开启，0:关闭）',
    PRIMARY KEY (`corp_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='继承设置表';


-- ----------------------------
-- Table structure for we_customer_transfer_record
-- ----------------------------
CREATE TABLE `we_customer_transfer_record`
(
    `id`                       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`                  varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `handover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '原跟进成员userid',
    `external_userid`          varchar(32)  NOT NULL DEFAULT '' COMMENT '待分配的外部联系人userid',
    `takeover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '接替成员的userid',
    `hanover_username`         varchar(200) NOT NULL DEFAULT '' COMMENT '原跟进成员名称',
    `takeover_username`        varchar(200) NOT NULL DEFAULT '' COMMENT '跟进成员名称',
    `handover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '原跟进人部门名称',
    `takeover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '接替人部门名称',
    `transfer_time`            datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    `status`                   tinyint(2) NOT NULL DEFAULT '2' COMMENT '接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录',
    `takeover_time`            datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '接替时间',
    `remark`                   varchar(64)  NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY                        `idx_corp_id` (`corp_id`) USING BTREE,
    KEY                        `idx_transfer_time` (`transfer_time`) USING BTREE,
    KEY                        `idx_external_userid` (`external_userid`, `handover_userid`, `takeover_userid`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='在职继承分配记录表';

-- ----------------------------
-- Table structure for we_resigned_transfer_record
-- ----------------------------
CREATE TABLE `we_resigned_transfer_record`
(
    `id`                       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`                  varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `handover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '原跟进离职员工id',
    `takeover_userid`          varchar(64)  NOT NULL DEFAULT '' COMMENT '接替员工id',
    `dimission_time`           datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '离职时间',
    `handover_username`        varchar(200) NOT NULL DEFAULT '' COMMENT '原跟进人用户名',
    `takeover_username`        varchar(200) NOT NULL DEFAULT '' COMMENT '接替人名称',
    `handover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '原跟进人部门名称',
    `takeover_department_name` varchar(100) NOT NULL DEFAULT '' COMMENT '接替人部门名称',
    `transfer_time`            datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `un_corp_handover_takeover_id` (`corp_id`, `handover_userid`, `takeover_userid`, `dimission_time`) USING BTREE,
    KEY                        `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
-- ----------------------------
-- Table structure for we_resigned_customer_transfer_record
-- ----------------------------
CREATE TABLE `we_resigned_customer_transfer_record`
(
    `record_id`       bigint(20) NOT NULL DEFAULT '0' COMMENT '分配记录id',
    `external_userid` varchar(32)  NOT NULL DEFAULT '' COMMENT '外部联系人userId',
    `status`          tinyint(2) NOT NULL DEFAULT '2' COMMENT '接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限',
    `takeover_time`   datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '接替时间',
    `remark`          varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`record_id`, `external_userid`),
    KEY               `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='离职客户继承记录表';
-- ----------------------------
-- Table structure for we_resigned_group_transfer_record
-- ----------------------------
CREATE TABLE `we_resigned_group_transfer_record`
(
    `record_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '分配记录id',
    `chat_id`       varchar(32)  NOT NULL DEFAULT '' COMMENT '群聊id',
    `status`        tinyint(1) NOT NULL DEFAULT '1' COMMENT '接替状态,只有继承成功才会有值（1成功0失败)',
    `takeover_time` datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '接替时间',
    `remark`        varchar(100) NOT NULL DEFAULT '' COMMENT '失败原因',
    PRIMARY KEY (`record_id`, `chat_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='离职客户群继承记录表';

--  欢迎语
CREATE TABLE `we_msg_tlp`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `corp_id`              varchar(64)   NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `default_welcome_msg`  varchar(2000) NOT NULL DEFAULT '' COMMENT '默认欢迎语',
    `welcome_msg_tpl_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '欢迎语适用对象类型:1:员工欢迎语;2:客户群欢迎语',
    `exist_special_flag`   tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否存在有特殊时段欢迎语(存在则有关联rule_id) 0:否 1:是',
    `template_id`          varchar(64)   NOT NULL DEFAULT '' COMMENT '入群欢迎语返回的模板id',
    `notice_flag`          tinyint(1) NOT NULL DEFAULT '0' COMMENT '群素材是否通知员工标识(0: 不通知(默认) 1:通知)',
    `create_by`            varchar(64)   NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`          datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='欢迎语模板表';

CREATE TABLE `we_msg_tlp_material`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '欢迎语素材主键id',
    `default_msg_id`       bigint(20) NOT NULL DEFAULT '0' COMMENT '默认欢迎语模板id',
    `special_msg_id`       bigint(20) NOT NULL DEFAULT '0' COMMENT '特殊规则欢迎语模板id(如果不存在特殊时段欢迎语，且没有素材则该字段为0)',
    `type`                 tinyint(4) NOT NULL DEFAULT '0' COMMENT '素材类型 0:文本 1:图片 2:链接 3:小程序 4:文件 5:视频媒体文件',
    `content`              varchar(255) NOT NULL DEFAULT '' COMMENT '文本内容,链接消息标题,小程序消息标题，(前端: 图片,文件,视频的标题)',
    `pic_url`              varchar(255) NOT NULL DEFAULT '' COMMENT '图片url,链接封面url,小程序picurl,文件url,视频url',
    `description`          varchar(255) NOT NULL DEFAULT '' COMMENT '链接消息描述,小程序appid(前端: 文件大小)',
    `url`                  varchar(255) NOT NULL DEFAULT '' COMMENT '链接url,小程序page',
    `sort_no`              tinyint(2) NOT NULL DEFAULT '0' COMMENT '排序字段',
    `enable_convert_radar` tinyint(1) NOT NULL DEFAULT '0' COMMENT '链接时使用(0,不转化为雷达，1：转化为雷达)',
    `radar_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id，存储雷达时使用',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='欢迎语素材表';
CREATE TABLE `we_msg_tlp_scope`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `msg_tlp_id`  bigint(20) NOT NULL DEFAULT '0' COMMENT '默认欢迎语模板id',
    `use_user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '使用人id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `unique_msgId_ruleId` (`msg_tlp_id`,`use_user_id`) USING BTREE COMMENT '欢迎语id和员工唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='模板使用人员范围';
CREATE TABLE `we_msg_tlp_special_rule`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '欢迎语规则主键id',
    `msg_tlp_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '默认欢迎语模板id',
    `special_welcome_msg` varchar(2000) NOT NULL DEFAULT '' COMMENT '特殊欢迎语模板消息',
    `rule_type`           tinyint(4) NOT NULL DEFAULT '0' COMMENT '特殊欢迎语消息规则类型 1:周策略欢迎语',
    `weekends`            varchar(16)   NOT NULL DEFAULT '0' COMMENT '1-7 周一到周日,多个逗号隔开',
    `weekend_begin_time`  time          NOT NULL DEFAULT '00:00:00' COMMENT '周策略开始时间',
    `weekend_end_time`    time          NOT NULL DEFAULT '00:00:00' COMMENT '周策略结束时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='特殊规则欢迎语表';

-- ----------------------------
-- Table structure for we_moment_detail_rel
-- ----------------------------
CREATE TABLE `we_moment_detail_rel`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `moment_task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '朋友圈任务id',
    `detail_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT '附件id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈任务附件关联表';
-- ----------------------------
-- Table structure for we_moment_task
-- ----------------------------
CREATE TABLE `we_moment_task`
(
    `id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `corp_id`     varchar(64)   NOT NULL DEFAULT '' COMMENT '企业id',
    `moment_id`   varchar(64)   NOT NULL DEFAULT '' COMMENT '朋友圈id',
    `job_id`      varchar(64)   NOT NULL DEFAULT '' COMMENT '企业微信异步任务id 24小时有效',
    `content`     varchar(2000) NOT NULL DEFAULT '' COMMENT '文本内容',
    `type`        tinyint(1) NOT NULL DEFAULT '0' COMMENT '发布类型（0：企业 1：个人）',
    `task_type`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型（0：立即发送 1：定时发送）',
    `push_range`  tinyint(1) NOT NULL DEFAULT '0' COMMENT '可见范围（0：全部客户 1：部分客户）',
    `status`      tinyint(1) NOT NULL DEFAULT '1' COMMENT '任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成',
    `send_time`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `select_user` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否选择员工（0：未选择 1：已选择）',
    `users`       text          NOT NULL COMMENT '所属员工',
    `departments` text          NOT NULL COMMENT '所属部门（多个逗号隔开 ）',
    `tags`        text          NOT NULL COMMENT '客户标签',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(255)  NOT NULL DEFAULT '' COMMENT '创建人',
    PRIMARY KEY (`id`),
    KEY           `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈任务信息表';
-- ----------------------------
-- Table structure for we_moment_task_result
-- ----------------------------
CREATE TABLE `we_moment_task_result`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `moment_task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '朋友圈任务id',
    `user_id`        varchar(64)  NOT NULL DEFAULT '' COMMENT '员工id',
    `publish_time`   datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发布时间',
    `publish_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '成员发表状态。0:待发布 1：已发布 2：已过期 3：不可发布',
    `remark`         varchar(255) NOT NULL DEFAULT '' COMMENT '失败备注',
    PRIMARY KEY (`id`),
    KEY              `idx_moment_task_id` (`moment_task_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈任务执行结果';
-- ----------------------------
-- Table structure for we_moment_user_customer_rel
-- ----------------------------
CREATE TABLE `we_moment_user_customer_rel`
(
    `moment_task_id`  bigint(20) NOT NULL DEFAULT '0' COMMENT '朋友圈任务id',
    `user_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
    `external_userid` varchar(64) NOT NULL DEFAULT '' COMMENT '客户id',
    PRIMARY KEY (`moment_task_id`, `user_id`, `external_userid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='朋友圈客户员工关联表';

--  自动标签  规则设置
CREATE TABLE `we_auto_tag_customer_rule_effect_time`
(
    `rule_id`           bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `effect_begin_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '生效开始时间',
    `effect_end_time`   datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '生效结束时间',
    UNIQUE KEY `idx_rule_id` (`rule_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新客规则生效时间表';
CREATE TABLE `we_auto_tag_customer_scene`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_id`         bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `loop_point`      tinyint(2) NOT NULL DEFAULT '0' COMMENT '指定循环节点 周: 1-7 月: 1-月末',
    `loop_begin_time` time        NOT NULL DEFAULT '00:00:00' COMMENT '循环指定开始时间',
    `loop_end_time`   time        NOT NULL DEFAULT '00:00:00' COMMENT '循环指定结束时间',
    `scene_type`      tinyint(2) NOT NULL DEFAULT '0' COMMENT '场景类型 1:天 2:周 3:月',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新客标签场景表';
CREATE TABLE `we_auto_tag_customer_scene_tag_rel`
(
    `rule_id`           bigint(20) NOT NULL COMMENT '所属规则id',
    `customer_scene_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户场景id',
    `tag_id`            varchar(64) NOT NULL DEFAULT '' COMMENT '标签id',
    PRIMARY KEY (`rule_id`, `customer_scene_id`, `tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新客标签场景与标签关系表';
CREATE TABLE `we_auto_tag_group_scene`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id` varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群标签场景表';
CREATE TABLE `we_auto_tag_group_scene_group_rel`
(
    `rule_id`        bigint(20) NOT NULL COMMENT '所属规则id',
    `group_scene_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键id',
    `group_id`       varchar(32) NOT NULL DEFAULT '' COMMENT '群id',
    PRIMARY KEY (`rule_id`, `group_scene_id`, `group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群标签场景与群关系表';
CREATE TABLE `we_auto_tag_group_scene_tag_rel`
(
    `rule_id`        bigint(20) NOT NULL COMMENT '所属规则id',
    `group_scene_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '群场景id',
    `tag_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '标签id',
    PRIMARY KEY (`rule_id`, `group_scene_id`, `tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群标签场景与标签关系表';
CREATE TABLE `we_auto_tag_keyword`
(
    `rule_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `match_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '匹配规则 1:模糊匹配 2:精确匹配',
    `keyword`    varchar(32) NOT NULL DEFAULT '' COMMENT '关键词',
    PRIMARY KEY (`rule_id`, `match_type`, `keyword`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关键词规则表';
CREATE TABLE `we_auto_tag_keyword_tag_rel`
(
    `rule_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `tag_id`  varchar(64) NOT NULL DEFAULT '' COMMENT '规则名称',
    PRIMARY KEY (`rule_id`, `tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='关键词与标签关系表';
CREATE TABLE `we_auto_tag_rule`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_name`   varchar(32) NOT NULL DEFAULT '' COMMENT '规则名称',
    `label_type`  tinyint(2) NOT NULL DEFAULT '0' COMMENT '规则类型 1:关键词 2:入群 3:新客',
    `status`      tinyint(2) NOT NULL DEFAULT '1' COMMENT '启用禁用状态 0:禁用1:启用',
    `create_time` datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_corp_id` (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='标签规则表';
CREATE TABLE `we_auto_tag_user_rel`
(
    `rule_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `target_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'type:0 表示员工id， type:1 表示部门id',
    `type`      tinyint(1) NOT NULL DEFAULT '2' COMMENT '传入员工/部门 2-员工 1部门',
    PRIMARY KEY (`rule_id`, `target_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='标签与员工使用范围表';


CREATE TABLE `we_auto_tag_rule_hit_customer_record`
(
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `corp_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '企业id',
    `customer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户id',
    `user_id`     varchar(64) NOT NULL DEFAULT '' COMMENT '员工id',
    `add_time`    datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '添加时间',
    PRIMARY KEY (`rule_id`, `corp_id`, `customer_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户打标签记录表';
CREATE TABLE `we_auto_tag_rule_hit_customer_record_tag_rel`
(
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `tag_id`      varchar(64)  NOT NULL DEFAULT '' COMMENT '标签id,去重用',
    `tag_name`    varchar(100) NOT NULL DEFAULT '' COMMENT '标签名',
    `customer_id` varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `user_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '员工id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户标签命中记录';
CREATE TABLE `we_auto_tag_rule_hit_group_record`
(
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `corp_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `customer_id` varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `group_id`    varchar(32)  NOT NULL DEFAULT '' COMMENT '群id',
    `group_name`  varchar(128) NOT NULL DEFAULT '' COMMENT '群名',
    `join_time`   datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '进群时间',
    PRIMARY KEY (`rule_id`, `corp_id`, `customer_id`, `group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户打标签记录表';
CREATE TABLE `we_auto_tag_rule_hit_group_record_tag_rel`
(
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `tag_id`      varchar(64)  NOT NULL DEFAULT '' COMMENT '标签id,去重用',
    `tag_name`    varchar(100) NOT NULL DEFAULT '' COMMENT '标签名',
    `customer_id` varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `group_id`    varchar(32)  NOT NULL DEFAULT '' COMMENT '群id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户标签命中记录';
CREATE TABLE `we_auto_tag_rule_hit_keyword_record`
(
    `corp_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `customer_id` varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `user_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '员工id',
    `keyword`     varchar(255) NOT NULL DEFAULT '' COMMENT '触发的关键词',
    `from_text`   varchar(255) NOT NULL DEFAULT '' COMMENT '触发文本',
    `hit_time`    datetime     NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '命中时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户打标签记录表';
CREATE TABLE `we_auto_tag_rule_hit_keyword_record_tag_rel`
(
    `rule_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '规则id',
    `tag_id`      varchar(64)  NOT NULL COMMENT '标签id,去重用',
    `tag_name`    varchar(100) NOT NULL COMMENT '标签名',
    `customer_id` varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `user_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '员工id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户标签命中记录';

CREATE TABLE `we_redeem_code_activity`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '兑换码活动主键',
    `corp_id`         varchar(64) NOT NULL DEFAULT '' COMMENT '授权企业ID',
    `name`            varchar(32) NOT NULL DEFAULT '' COMMENT '活动名称',
    `start_time`      date        NOT NULL DEFAULT '0000-00-00' COMMENT '活动开始时间',
    `end_time`        date        NOT NULL DEFAULT '0000-00-00' COMMENT '活动结束时间',
    `create_by`       varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `enable_limited`  tinyint(1) NOT NULL DEFAULT '1' COMMENT '客户参与限制，0：可以参与多次，1：只可参与一次',
    `enable_alarm`    tinyint(1) NOT NULL DEFAULT '0' COMMENT '库存告警开关，0：不开启，1：开启',
    `alarm_threshold` int(10) NOT NULL DEFAULT '0' COMMENT '库存告警阈值，告警开启时，库存低于阈值通知员工',
    `del_flag`        tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志，0：未删除，1：已删除',
    PRIMARY KEY (`id`),
    KEY               `index_corpid` (`corp_id`) USING BTREE,
    KEY               `create_time` (`create_time`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='兑换码活动表';

CREATE TABLE `we_redeem_code`
(
    `code`            varchar(20) NOT NULL COMMENT '兑换码',
    `activity_id`     bigint(20) NOT NULL COMMENT '兑换码活动id',
    `status`          tinyint(1) NOT NULL DEFAULT '0' COMMENT '领取状态，0：未领取，1：已领取',
    `effective_time`  date        NOT NULL DEFAULT '0000-00-00' COMMENT '有效期，在该天24点之前可以发送给客户',
    `redeem_time`     datetime    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '兑换码发送给客户的时间',
    `receive_user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '领取人id',
    PRIMARY KEY (`activity_id`, `code`),
    KEY               `status` (`status`) USING BTREE,
    KEY               `redeem_time` (`redeem_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换码库存表';

CREATE TABLE `we_redeem_code_alarm_employee_rel`
(
    `activity_id` bigint(20) NOT NULL COMMENT '活动id',
    `target_id`   varchar(64) NOT NULL COMMENT '员工id,部门id',
    `type`        tinyint(1) NOT NULL DEFAULT '2' COMMENT 'type, 1：存部门，2：存员工',
    PRIMARY KEY (`activity_id`, `target_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兑换码活动，告警员工表';

-- ----------------------------
-- Table structure for we_radar 雷达表
-- ----------------------------
CREATE TABLE `we_radar`
(
    `id`                     bigint(20) NOT NULL,
    `corp_id`                varchar(64)   NOT NULL DEFAULT '' COMMENT '企业ID',
    `radar_title`            varchar(255)  NOT NULL DEFAULT '' COMMENT '雷达标题',
    `url`                    varchar(3000) NOT NULL DEFAULT '' COMMENT '雷达原始路径url',
    `cover_url`              varchar(1200) NOT NULL DEFAULT '' COMMENT '雷达链接封面图',
    `title`                  varchar(255)  NOT NULL DEFAULT '' COMMENT '链接标题',
    `content`                varchar(255)  NOT NULL DEFAULT '' COMMENT '雷达链接摘要',
    `enable_click_notice`    tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启行为通知（1[true]是0[false]否）',
    `enable_behavior_record` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许轨迹记录（1[true]是 0[false]否) ',
    `enable_customer_tag`    tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许打上客户标签（ 1[true]是 0[false]否) ',
    `enable_update_notice`   tinyint(1) NOT NULL DEFAULT '1' COMMENT '更新后是否通知员工（true[1]是 false[0]否) ',
    `create_time`            datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`              varchar(64)   NOT NULL DEFAULT '' COMMENT '创建人',
    `update_time`            datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`              varchar(64)   NOT NULL DEFAULT '' COMMENT '更新人',
    `is_defined`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用自定义链接（1[true]是,0[false]否)',
    `type`                   tinyint(1) NOT NULL DEFAULT '1' COMMENT '雷达类型（1个人雷达，2部门雷达，3企业雷达）',
    PRIMARY KEY (`id`),
    KEY                      `idx_corp_id_type` (`corp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达表';
-- ----------------------------
-- Table structure for we_radar_tag_rel 雷达-标签关系表
-- ----------------------------
CREATE TABLE `we_radar_tag_rel`
(
    `radar_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达ID',
    `tag_id`   varchar(64) NOT NULL DEFAULT '' COMMENT '标签ID',
    PRIMARY KEY (`radar_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达-标签关系表';

-- ----------------------------
-- Table structure for we_radar_channel  渠道表
-- ----------------------------
CREATE TABLE `we_radar_channel`
(
    `id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道id',
    `radar_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id',
    `name`        varchar(32)  NOT NULL DEFAULT '' COMMENT '渠道名称',
    `short_url`   varchar(255) NOT NULL DEFAULT '' COMMENT '渠道的短链url',
    `create_time` datetime     NOT NULL,
    `create_by`   varchar(64)  NOT NULL,
    UNIQUE KEY `uniq_redar_name` (`radar_id`,`name`) USING BTREE,
    KEY           `idx_radar_id_channel_name` (`radar_id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达-渠道表';

-- ----------------------------
-- Table structure for we_radar_click_record  雷达点击记录表
-- ----------------------------
CREATE TABLE `we_radar_click_record`
(
    `id`                       bigint(20) NOT NULL COMMENT '雷达点击记录表ID',
    `radar_id`                 bigint(20) NOT NULL DEFAULT '0' COMMENT '雷达id',
    `user_id`                  varchar(64)  NOT NULL DEFAULT '' COMMENT '发送活码用户id',
    `user_name`                varchar(200) NOT NULL DEFAULT '' COMMENT '发送雷达链接的用户名称',
    `external_user_id`         varchar(32)  NOT NULL DEFAULT '' COMMENT '客户id',
    `external_user_name`       varchar(128) NOT NULL DEFAULT '' COMMENT '客户名称',
    `external_user_head_image` varchar(512) NOT NULL DEFAULT '' COMMENT '客户头像url',
    `channel_type`             tinyint(2) NOT NULL DEFAULT '0' COMMENT '渠道id（0未知渠道,1员工活码，2朋友圈，3群发，4侧边栏,5欢迎语,6 客户SOP,7群SOP，8新客进群，9群日历,10自定义渠道)',
    `channel_name`             varchar(32)  NOT NULL DEFAULT '未知渠道' COMMENT '渠道名',
    `detail`                   varchar(255) NOT NULL DEFAULT '' COMMENT '详情(如果是员工活码,则为员工活码使用场景，如果是新客进群则为新客进群的活码名称,如果是SOP则为SOP名称，如果是群日历，则为日历名称)',
    `union_id`                 varchar(32)  NOT NULL DEFAULT '' COMMENT '外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。',
    `open_id`                  varchar(32)  NOT NULL DEFAULT '' COMMENT '公众号/小程序open_id',
    `create_time`              datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_date`              varchar(32)  NOT NULL DEFAULT '0000-00-00' COMMENT '创建日期（格式yyyy-mm-dd)',
    PRIMARY KEY (`id`),
    KEY                        `idx_corp_date_external` (`radar_id`,`create_date`,`external_user_id`,`channel_name`) USING BTREE,
    KEY                        `idx_corp_channel` (`radar_id`,`channel_name`) USING BTREE,
    KEY                        `idx_corp_customer` (`radar_id`,`external_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='雷达点击记录表';


-- ----------------------------
-- Table structure for we_radar_click_record 长链短链映射表
-- ----------------------------
CREATE TABLE `sys_short_url_mapping`
(
    `id`          bigint(20) NOT NULL COMMENT 'id,短链',
    `short_code`  varchar(32)   NOT NULL DEFAULT '' COMMENT '短链后面的唯一字符串（用于和域名拼接成短链）',
    `long_url`    varchar(1024) NOT NULL COMMENT '原链接（长链接）',
    `append_info` varchar(512)  NOT NULL DEFAULT '' COMMENT '附加信息Json(user_id,radar_id,channel_id,detail)',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   varchar(64)   NOT NULL DEFAULT '' COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_short_code` (`short_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='长链-短链映射表';
-- ----------------------------
-- Table structure for we_radar_click_record
-- ----------------------------

CREATE TABLE `we_open_config`
(
    `corp_id`                     varchar(64)  NOT NULL DEFAULT '' COMMENT '企业id',
    `official_account_app_id`     varchar(64)  NOT NULL DEFAULT '' COMMENT '公众号appid',
    `official_account_app_secret` varchar(128) NOT NULL DEFAULT '' COMMENT '公众号secret',
    `official_account_domain`     varchar(255) NOT NULL DEFAULT '' COMMENT '公众号域名',
    `create_by`                   varchar(64)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`                 datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                   varchar(64)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`                 datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='企业公众号配置表';



