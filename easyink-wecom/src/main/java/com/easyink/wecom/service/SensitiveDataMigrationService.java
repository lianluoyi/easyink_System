package com.easyink.wecom.service;

import java.util.Map;

/**
 * 敏感数据迁移服务接口
 *
 * @author java-backend-expert
 * @date 2024-12-19
 */
public interface SensitiveDataMigrationService {

    /**
     * 迁移所有表的敏感数据
     */
    Map<String, Object> migrateAllSensitiveData();

    /**
     * 迁移指定表的敏感数据
     *
     * @param tableName 表名
     * @return 迁移的记录数
     */
    int migrateSensitiveDataByTable(String tableName);

    /**
     * 获取迁移进度
     *
     * @param tableName 表名
     * @return 迁移进度信息
     */
    Map<String, Object> getMigrationProgress(String tableName);

    /**
     * 验证迁移结果
     *
     * @param tableName 表名
     * @return 验证结果
     */
    Map<String, Object> verifyMigrationResult(String tableName);


    /**
     * 重新对地址字段进行脱敏处理
     * 用于修改脱敏方法后，重新处理之前已加密但脱敏不完整的地址字段
     * 
     * @return 处理结果信息
     */
    Map<String, Object> reDesensitizeAddressFields();

}