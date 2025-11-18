package com.easyink.wecom.service.impl;

import com.alibaba.fastjson.JSON;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.encrypt.StrategyCryptoUtil;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskDetail;
import com.easyink.wecom.domain.entity.WeMapConfig;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easyink.wecom.domain.entity.wechatopen.WeOpenConfig;
import com.easyink.wecom.domain.model.customer.AddressModel;
import com.easyink.wecom.mapper.SensitiveDataMigrationMapper;
import com.easyink.wecom.service.SensitiveDataMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 敏感数据迁移服务实现类
 *
 * @author admin
 * @date 2024-01-01
 */
@Slf4j
@Service
public class SensitiveDataMigrationServiceImpl implements SensitiveDataMigrationService {

    // 批处理大小
    private static final int BATCH_SIZE = 1000;
    // 迁移进度缓存
    private final Map<String, Map<String, Object>> migrationProgress = new ConcurrentHashMap<>();
    @Autowired
    private SensitiveDataMigrationMapper migrationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> migrateAllSensitiveData() {
        log.info("开始执行全量敏感数据迁移");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tableResults = new HashMap<>();

        String[] tables = {
                "we_user", "we_flower_customer_rel", "we_customer_extend_property_rel",
                "we_batch_tag_task_detail", "we_corp_account", "we_map_config", "we_open_config"
        };

        int totalTables = tables.length;
        int successTables = 0;

        for (String tableName : tables) {
            try {
                Map<String, Object> tableResult = migrateTableSensitiveData(tableName);
                tableResults.put(tableName, tableResult);

                if ("success".equals(tableResult.get("status"))) {
                    successTables++;
                }
            } catch (Exception e) {
                log.error("迁移表 {} 时发生异常", tableName, e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("status", "error");
                errorResult.put("message", e.getMessage());
                errorResult.put("processedRecords", 0);
                tableResults.put(tableName, errorResult);
            }
        }

        result.put("totalTables", totalTables);
        result.put("successTables", successTables);
        result.put("failedTables", totalTables - successTables);
        result.put("tableResults", tableResults);
        result.put("status", successTables == totalTables ? "success" : "partial_success");

        log.info("全量敏感数据迁移完成，成功: {}/{}", successTables, totalTables);
        return result;
    }

    @Override
    public int migrateSensitiveDataByTable(String tableName) {
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> migrateTableSensitiveData(String tableName) {
        log.info("开始迁移表: {}", tableName);

        Map<String, Object> result = new HashMap<>();
        AtomicLong processedRecords = new AtomicLong(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        try {
            switch (tableName.toLowerCase()) {
                case "we_user":
                    processedRecords.set(migrateWeUser());
                    break;
                case "we_flower_customer_rel":
                    processedRecords.set(migrateWeFlowerCustomerRel());
                    break;
                case "we_customer_extend_property_rel":
                    processedRecords.set(migrateWeCustomerExtendPropertyRel());
                    break;
                case "we_batch_tag_task_detail":
                    processedRecords.set(migrateWeBatchTagTaskDetail());
                    break;
                case "we_corp_account":
                    processedRecords.set(migrateWeCorpAccount());
                    break;
                case "we_map_config":
                    processedRecords.set(migrateWeMapConfig());
                    break;
                case "we_open_config":
                    processedRecords.set(migrateWeOpenConfig());
                    break;
                default:
                    throw new IllegalArgumentException("不支持的表名: " + tableName);
            }

            result.put("status", "success");
            result.put("message", "迁移完成");
            result.put("processedRecords", processedRecords.get());
            result.put("errorCount", errorCount.get());

        } catch (Exception e) {
            log.error("迁移表 {} 失败", tableName, e);
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("processedRecords", processedRecords.get());
            result.put("errorCount", errorCount.get());
        }

        log.info("表 {} 迁移完成，处理记录数: {}", tableName, processedRecords.get());
        return result;
    }

    @Override
    public Map<String, Object> getMigrationProgress(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            // 返回所有表的进度
            Map<String, Object> allProgress = new HashMap<>();
            String[] tables = {
                    "we_user", "we_flower_customer_rel", "we_customer_extend_property_rel",
                    "we_batch_tag_task_detail", "we_corp_account", "we_map_config", "we_open_config"
            };

            for (String table : tables) {
                allProgress.put(table, getTableProgress(table));
            }
            return allProgress;
        } else {
            return getTableProgress(tableName);
        }
    }

    @Override
    public Map<String, Object> verifyMigrationResult(String tableName) {
        log.info("开始验证表 {} 的迁移结果", tableName);

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> stats = migrationMapper.getTableMigrationStats(tableName);

            if (stats != null && !stats.isEmpty()) {
                int totalRecords = ((Number) stats.get("total_records")).intValue();
                int migratedRecords = ((Number) stats.get("migrated_records")).intValue();

                result.put("tableName", tableName);
                result.put("totalRecords", totalRecords);
                result.put("migratedRecords", migratedRecords);
                result.put("pendingRecords", totalRecords - migratedRecords);
                result.put("migrationRate", totalRecords > 0 ? (double) migratedRecords / totalRecords * 100 : 0);
                result.put("isComplete", totalRecords == migratedRecords);
                result.put("status", "success");
            } else {
                result.put("status", "error");
                result.put("message", "无法获取表统计信息");
            }

        } catch (Exception e) {
            log.error("验证表 {} 迁移结果失败", tableName, e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reDesensitizeAddressFields() {
        log.info("开始重新对地址字段进行脱敏处理");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tableResults = new HashMap<>();

        // 包含地址字段的表
        String[] addressTables = {
                "we_user", "we_flower_customer_rel", "we_customer_extend_property_rel"
        };

        int totalTables = addressTables.length;
        int successTables = 0;
        long totalProcessedRecords = 0;

        for (String tableName : addressTables) {
            try {
                Map<String, Object> tableResult = reDesensitizeTableAddressData(tableName);
                tableResults.put(tableName, tableResult);

                if ("success".equals(tableResult.get("status"))) {
                    successTables++;
                    totalProcessedRecords += (Long) tableResult.get("processedRecords");
                }
            } catch (Exception e) {
                log.error("重新脱敏表 {} 地址字段时发生异常", tableName, e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("status", "error");
                errorResult.put("message", e.getMessage());
                errorResult.put("processedRecords", 0L);
                tableResults.put(tableName, errorResult);
            }
        }

        result.put("totalTables", totalTables);
        result.put("successTables", successTables);
        result.put("failedTables", totalTables - successTables);
        result.put("totalProcessedRecords", totalProcessedRecords);
        result.put("tableResults", tableResults);
        result.put("status", successTables == totalTables ? "success" : "partial_success");

        log.info("地址字段重新脱敏完成，成功: {}/{}, 总处理记录数: {}", successTables, totalTables, totalProcessedRecords);
        return result;
    }

    /**
     * 重新脱敏指定表的地址数据
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reDesensitizeTableAddressData(String tableName) {
        log.info("开始重新脱敏表: {} 的地址字段", tableName);

        Map<String, Object> result = new HashMap<>();
        long processedRecords = 0;

        try {
            switch (tableName.toLowerCase()) {
                case "we_user":
                    processedRecords = reDesensitizeWeUserAddress();
                    break;
                case "we_flower_customer_rel":
                    processedRecords = reDesensitizeWeFlowerCustomerRelAddress();
                    break;
                case "we_customer_extend_property_rel":
                    processedRecords = reDesensitizeWeCustomerExtendPropertyRelAddress();
                    break;
                default:
                    throw new IllegalArgumentException("不支持的表名: " + tableName);
            }

            result.put("status", "success");
            result.put("message", "重新脱敏完成");
            result.put("processedRecords", processedRecords);

        } catch (Exception e) {
            log.error("重新脱敏表 {} 地址字段失败", tableName, e);
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("processedRecords", processedRecords);
        }

        log.info("表 {} 地址字段重新脱敏完成，处理记录数: {}", tableName, processedRecords);
        return result;
    }

    /**
     * 重新脱敏 WeUser 表的地址字段
     */
    private long reDesensitizeWeUserAddress() {
        log.info("开始重新脱敏 we_user 表的地址字段");
        long processedCount = 0;
        int offset = 0;

        while (true) {
            List<WeUser> users = migrationMapper.selectWeUserAddressByPage(offset, BATCH_SIZE);
            if (users.isEmpty()) {
                break;
            }

            if (!users.isEmpty()) {
                // 直接对地址字段重新脱敏处理
                for (WeUser user : users) {
                    try {
                        // 重新进行脱敏处理
                        SensitiveFieldProcessor.processForSave(user);
                    } catch (Exception e) {
                        log.warn("处理用户 {} 地址字段时出错: {}", user.getUserId(), e.getMessage());
                    }
                }

                // 批量更新
                migrationMapper.batchUpdateWeUserAddress(users);
                processedCount += users.size();
            }

            offset += BATCH_SIZE;
            log.info("已处理 we_user 地址字段记录: {}", processedCount);
        }

        log.info("we_user 表地址字段重新脱敏完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 重新脱敏 WeFlowerCustomerRel 表的地址字段
     */
    private long reDesensitizeWeFlowerCustomerRelAddress() {
        log.info("开始重新脱敏 we_flower_customer_rel 表的地址字段");
        long processedCount = 0;
        Long lastId = 0L;

        while (true) {
            List<WeFlowerCustomerRel> relations = migrationMapper.selectWeFlowerCustomerRelAddressByPage(lastId, BATCH_SIZE);
            if (relations.isEmpty()) {
                break;
            }


            if (!relations.isEmpty()) {
                // 直接对地址字段重新脱敏处理
                for (WeFlowerCustomerRel relation : relations) {
                    try {
                        // 重新进行脱敏处理
                        SensitiveFieldProcessor.processForSave(relation);
                    } catch (Exception e) {
                        log.warn("处理客户关系 {} 地址字段时出错: {}", relation.getId(), e.getMessage());
                    }
                    lastId = relation.getId();
                }

                // 批量更新
                migrationMapper.batchUpdateWeFlowerCustomerRelAddress(relations);
                processedCount += relations.size();
            } else {
                // 更新lastId以继续分页
                lastId = relations.get(relations.size() - 1).getId();
            }

            log.info("已处理 we_flower_customer_rel 地址字段记录: {}", processedCount);
        }

        log.info("we_flower_customer_rel 表地址字段重新脱敏完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 重新脱敏 WeCustomerExtendPropertyRel 表的地址字段
     */
    private long reDesensitizeWeCustomerExtendPropertyRelAddress() {
        log.info("开始重新脱敏 we_customer_extend_property_rel 表的地址字段");
        long processedCount = 0;
        int offset = 0;

        while (true) {
            List<WeCustomerExtendPropertyRel> properties = migrationMapper.selectWeCustomerExtendPropertyRelAddressByPage(offset, BATCH_SIZE);
            if (properties.isEmpty()) {
                break;
            }

            for (WeCustomerExtendPropertyRel property : properties) {
                property.setOriginalPropertyValue(property.getPropertyValue());
                AddressModel addressModel = JSON.parseObject(property.getPropertyValue(), AddressModel.class);
                addressModel.setDetailAddress(StrategyCryptoUtil.esensitizationAllAddress(addressModel.getDetailAddress()));
                property.setPropertyValue(JSON.toJSONString(addressModel));
                // 处理AddressModel中的detailAddress字段加密和脱敏
                SensitiveFieldProcessor.processForSave(property);
            }

            // 批量更新
            migrationMapper.batchUpdateWeCustomerExtendPropertyRelAddress(properties);
            processedCount += properties.size();
            offset += BATCH_SIZE;

            log.info("已处理 we_customer_extend_property_rel 地址字段记录: {}", processedCount);
        }

        log.info("we_customer_extend_property_rel 表地址字段重新脱敏完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeUser 表数据
     */
    private long migrateWeUser() {
        log.info("开始迁移 we_user 表");
        long processedCount = 0;
        int offset = 0;

        while (true) {
            List<WeUser> users = migrationMapper.selectWeUserByPage(offset, BATCH_SIZE);
            if (users.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeUser user : users) {
                // 使用SensitiveFieldProcessor.processForSave()同时处理：
                // 1. 将明文字段加密后存储到对应的加密字段（如mobile_encrypt）
                // 2. 将原字段的值脱敏后设置回原字段（如mobile）
                SensitiveFieldProcessor.processForSave(user);
            }

            // 批量更新
            migrationMapper.batchUpdateWeUser(users);
            processedCount += users.size();
            offset += BATCH_SIZE;

            log.info("已处理 we_user 记录: {}", processedCount);
        }

        log.info("we_user 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeFlowerCustomerRel 表数据
     */
    private long migrateWeFlowerCustomerRel() {
        log.info("开始迁移 we_flower_customer_rel 表");
        long processedCount = 0;
        Long lastId = 0L;

        while (true) {
            List<WeFlowerCustomerRel> relations = migrationMapper.selectWeFlowerCustomerRelByPage(lastId, BATCH_SIZE);
            if (relations.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeFlowerCustomerRel relation : relations) {
                // 同时处理remarkMobiles和address字段的加密和脱敏
                SensitiveFieldProcessor.processForSave(relation);
                lastId = relation.getId();
            }

            // 批量更新
            migrationMapper.batchUpdateWeFlowerCustomerRel(relations);
            processedCount += relations.size();

            log.info("已处理 we_flower_customer_rel 记录: {}", processedCount);
        }

        log.info("we_flower_customer_rel 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeCustomerExtendPropertyRel 表数据
     */
    private long migrateWeCustomerExtendPropertyRel() {
        log.info("开始迁移 we_customer_extend_property_rel 表");
        long processedCount = 0;
        int offset = 0;

        while (true) {
            List<WeCustomerExtendPropertyRel> properties = migrationMapper.selectWeCustomerExtendPropertyRelByPage(offset, BATCH_SIZE);
            if (properties.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeCustomerExtendPropertyRel property : properties) {
                property.setOriginalPropertyValue(property.getPropertyValue());
                AddressModel addressModel = JSON.parseObject(property.getPropertyValue(), AddressModel.class);
                SensitiveFieldProcessor.processForSave(addressModel);
                property.setPropertyValue(JSON.toJSONString(addressModel));
                // 处理AddressModel中的detailAddress字段加密和脱敏
                SensitiveFieldProcessor.processForSave(property);
            }

            // 批量更新
            migrationMapper.batchUpdateWeCustomerExtendPropertyRel(properties);
            processedCount += properties.size();
            offset += BATCH_SIZE;

            log.info("已处理 we_customer_extend_property_rel 记录: {}", processedCount);
        }

        log.info("we_customer_extend_property_rel 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeBatchTagTaskDetail 表数据
     */
    private long migrateWeBatchTagTaskDetail() {
        log.info("开始迁移 we_batch_tag_task_detail 表");
        long processedCount = 0;
        Long lastId = 0L;

        while (true) {
            List<WeBatchTagTaskDetail> details = migrationMapper.selectWeBatchTagTaskDetailByPage(lastId, BATCH_SIZE);
            if (details.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeBatchTagTaskDetail detail : details) {
                // 处理importMobile字段的加密和脱敏
                SensitiveFieldProcessor.processForSave(detail);
                lastId = detail.getId();
            }

            // 批量更新
            migrationMapper.batchUpdateWeBatchTagTaskDetail(details);
            processedCount += details.size();

            log.info("已处理 we_batch_tag_task_detail 记录: {}", processedCount);
        }

        log.info("we_batch_tag_task_detail 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeCorpAccount 表数据
     */
    private long migrateWeCorpAccount() {
        log.info("开始迁移 we_corp_account 表");
        long processedCount = 0;
        Long lastId = 0L;

        while (true) {
            List<WeCorpAccount> accounts = migrationMapper.selectWeCorpAccountByPage(lastId, BATCH_SIZE);
            if (accounts.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeCorpAccount account : accounts) {
                // 处理多个secret字段的加密和脱敏
                SensitiveFieldProcessor.processForSave(account);
                lastId = account.getId();
            }

            // 批量更新
            migrationMapper.batchUpdateWeCorpAccount(accounts);
            processedCount += accounts.size();

            log.info("已处理 we_corp_account 记录: {}", processedCount);
        }

        log.info("we_corp_account 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeMapConfig 表数据
     */
    private long migrateWeMapConfig() {
        log.info("开始迁移 we_map_config 表");
        long processedCount = 0;
        Long lastId = 0L;

        while (true) {
            List<WeMapConfig> configs = migrationMapper.selectWeMapConfigByPage(lastId, BATCH_SIZE);
            if (configs.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeMapConfig config : configs) {
                // 处理apiKey和iframeApiKey字段的加密和脱敏
                SensitiveFieldProcessor.processForSave(config);
                lastId = config.getId();
            }

            // 批量更新
            migrationMapper.batchUpdateWeMapConfig(configs);
            processedCount += configs.size();

            log.info("已处理 we_map_config 记录: {}", processedCount);
        }

        log.info("we_map_config 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 迁移 WeOpenConfig 表数据
     */
    private long migrateWeOpenConfig() {
        log.info("开始迁移 we_open_config 表");
        long processedCount = 0;
        int offset = 0;

        while (true) {
            List<WeOpenConfig> configs = migrationMapper.selectWeOpenConfigByPage(offset, BATCH_SIZE);
            if (configs.isEmpty()) {
                break;
            }

            // 处理敏感字段加密和脱敏
            for (WeOpenConfig config : configs) {
                // 处理officialAccountAppSecret字段的加密和脱敏
                SensitiveFieldProcessor.processForSave(config);
            }

            // 批量更新
            migrationMapper.batchUpdateWeOpenConfig(configs);
            processedCount += configs.size();
            offset += BATCH_SIZE;

            log.info("已处理 we_open_config 记录: {}", processedCount);
        }

        log.info("we_open_config 表迁移完成，共处理 {} 条记录", processedCount);
        return processedCount;
    }

    /**
     * 获取单个表的迁移进度
     */
    private Map<String, Object> getTableProgress(String tableName) {
        Map<String, Object> progress = new HashMap<>();

        try {
            Map<String, Object> stats = migrationMapper.getTableMigrationStats(tableName);

            if (stats != null && !stats.isEmpty()) {
                int totalRecords = ((Number) stats.get("total_records")).intValue();
                int migratedRecords = ((Number) stats.get("migrated_records")).intValue();

                progress.put("tableName", tableName);
                progress.put("totalRecords", totalRecords);
                progress.put("migratedRecords", migratedRecords);
                progress.put("pendingRecords", totalRecords - migratedRecords);
                progress.put("progressPercentage", totalRecords > 0 ? (double) migratedRecords / totalRecords * 100 : 100);
                progress.put("isComplete", totalRecords == migratedRecords);
                progress.put("status", "success");
            } else {
                progress.put("status", "error");
                progress.put("message", "无法获取表统计信息");
            }

        } catch (Exception e) {
            log.error("获取表 {} 迁移进度失败", tableName, e);
            progress.put("status", "error");
            progress.put("message", e.getMessage());
        }

        return progress;
    }
}