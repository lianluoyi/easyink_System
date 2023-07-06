package com.easyink.common.constant.autotag;


/**
 * 自动标签常量类
 *
 * @author lichaoyu
 * @date 2023/6/5 13:33
 */
public class AutoTagConstants {

    /**
     * 第一行第一列字段属性值
     */
    public static final String FIRST_FIELD_VALUE = "external_userid";

    /**
     * 第一行第二列字段属性值
     */
    public static final String SECOND_FIELD_VALUE = "unionid";

    /**
     * 第一行第三列字段属性值
     */
    public static final String THIRD_FIELD_VALUE = "手机号";

    /**
     * 最大的处理数据条数
     */
    public static final Integer MAX_TASK_SIZE = 2000;

    /**
     * 导入失败原因
     */
    public static final String EXTERNAL_USER_ID_NOT_BE_REPEATED = "externalUserid不能重复";

    /**
     * 导入失败原因
     */
    public static final String UNION_ID_NOT_BE_REPEATED = "unionid不能重复";

    /**
     * 导入失败原因
     */
    public static final String MOBILE_NOT_BE_REPEATED = "手机号不能重复";

    /**
     * 导入失败报告文件名
     */
    public static final String EXPORT_FAIL_FILE_NAME = "导入失败报告";

    /**
     * 导入失败报告文件类型
     */
    public static final String EXPORT_FAIL_FILE_TYPE = ".txt";

    /**
     * 导入失败报告文件后缀
     */
    public static final String URL_FILE_SUFFIX = "txt";

    /**
     * 批量标签任务删除状态
     */
    public static final Integer TASK_IS_DEL = 1;

    /**
     * 导入详情报表名称
     */
    public static final String EXPORT_TASK_DETAIL_NAME = "导入详情报表";
    /**
     * 不是企业客户失败备注
     */
    public static final String NOT_CUSTOMER = "Ta不是企业客户";
    /**
     * 服务异常的失败备注
     */
    public static final String SYS_ERROR = "系统服务异常";
    /**
     * 任务被删除而终止的失败备注
     */
    public static final String TASK_STOPPER_BY_DEL = "任务被删除,停止执行";
    /**
     * 导入字段超出限制长度提示内容
     */
    public static final String COLUMN_EXCEED_64_LENGTH = "字段长度不能超过64字符";
    /**
     * 3个字段的长度校验
     */
    public static final int MAX_COLUMN_LENGTH = 64;


}
