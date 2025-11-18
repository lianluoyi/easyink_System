package com.easyink.wecom.service.impl;

import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.encrypt.StrategyCryptoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.mapper.WeUserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * WeUser敏感字段数据迁移服务
 * 用于将现有明文敏感数据加密并填充到对应的加密字段
 * 
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@Service
@AllArgsConstructor
public class WeUserDataMigrationService {

    private final WeUserMapper weUserMapper;


    /**
     * 批处理大小
     */
    private static final int BATCH_SIZE = 1000;

    /**
     * 执行WeUser敏感字段数据迁移
     * 将现有的明文手机号和地址加密后填充到对应的加密字段
     * 
     * @return 迁移结果统计
     */
    @Transactional(rollbackFor = Exception.class)
    public MigrationResult migrateWeUserSensitiveData() {
        log.info("开始执行WeUser敏感字段数据迁移...");
        
        MigrationResult result = new MigrationResult();
        int offset = 0;
        int totalProcessed = 0;
        int totalUpdated = 0;
        
        try {
            while (true) {
                // 分批查询需要迁移的用户数据
                List<WeUser> users = selectUsersForMigration(offset, BATCH_SIZE);
                
                if (users.isEmpty()) {
                    log.info("没有更多需要迁移的数据，迁移完成");
                    break;
                }
                
                log.info("正在处理第{}批数据，共{}条记录", (offset / BATCH_SIZE) + 1, users.size());
                
                List<WeUser> usersToUpdate = new ArrayList<>();
                
                for (WeUser user : users) {
                    boolean needUpdate = false;
                    
                    try {
                        // 处理手机号加密
                        if (StringUtils.isNotBlank(user.getMobile()) && 
                            StringUtils.isBlank(user.getMobileEncrypt())) {
                            String encryptedMobile = StrategyCryptoUtil.encrypt(user.getMobile());
                            user.setMobileEncrypt(encryptedMobile);
                            needUpdate = true;
                            result.mobileEncrypted++;
                        }
                        
                        // 处理地址加密
                        if (StringUtils.isNotBlank(user.getAddress()) && 
                            StringUtils.isBlank(user.getAddressEncrypt())) {
                            String encryptedAddress = StrategyCryptoUtil.encrypt(user.getAddress());
                            user.setAddressEncrypt(encryptedAddress);
                            needUpdate = true;
                            result.addressEncrypted++;
                        }
                        
                        if (needUpdate) {
                            usersToUpdate.add(user);
                        }
                        
                    } catch (Exception e) {
                        log.error("处理用户{}的敏感字段时发生错误: {}", user.getUserId(), e.getMessage(), e);
                        result.errorCount++;
                        // 继续处理其他用户，不中断整个迁移过程
                    }
                }
                
                // 批量更新
                if (!usersToUpdate.isEmpty()) {
                    int updated = batchUpdateEncryptFields(usersToUpdate);
                    totalUpdated += updated;
                    log.info("成功更新{}条用户记录的加密字段", updated);
                }
                
                totalProcessed += users.size();
                offset += BATCH_SIZE;
                
                // 每处理1万条记录输出一次进度
                if (totalProcessed % 10000 == 0) {
                    log.info("已处理{}条记录，已更新{}条记录", totalProcessed, totalUpdated);
                }
            }
            
            result.totalProcessed = totalProcessed;
            result.totalUpdated = totalUpdated;
            result.success = true;
            
            log.info("WeUser敏感字段数据迁移完成！统计信息: {}", result);
            
        } catch (Exception e) {
            log.error("WeUser敏感字段数据迁移失败", e);
            result.success = false;
            result.errorMessage = e.getMessage();
            throw e;
        }
        
        return result;
    }

    /**
     * 分批查询需要迁移的用户数据
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    private List<WeUser> selectUsersForMigration(int offset, int limit) {
        try {
            return weUserMapper.selectUsersForMigration(offset, limit);
        } catch (Exception e) {
            log.error("查询需要迁移的用户数据失败，offset: {}, limit: {}", offset, limit, e);
            throw new RuntimeException("查询迁移数据失败", e);
        }
    }

    /**
     * 批量更新用户的加密字段
     * 
     * @param users 用户列表
     * @return 更新的记录数
     */
    private int batchUpdateEncryptFields(List<WeUser> users) {
        try {
            return weUserMapper.batchUpdateEncryptFields(users);
        } catch (Exception e) {
            log.error("批量更新用户加密字段失败，用户数量: {}", users.size(), e);
            throw new RuntimeException("批量更新加密字段失败", e);
        }
    }

    /**
     * 验证迁移结果
     * 检查是否还有明文数据但缺少加密数据的记录
     * 
     * @return 验证结果
     */
    public ValidationResult validateMigrationResult() {
        log.info("开始验证WeUser敏感字段迁移结果...");
        
        ValidationResult result = new ValidationResult();
        
        try {
            // 统计总体数据
            MigrationStatistics stats = weUserMapper.getMigrationStatistics();
            result.statistics = stats;
            
            // 检查遗漏的记录
            List<WeUser> missedRecords = weUserMapper.selectMissedEncryptionRecords();
            result.missedRecords = missedRecords;
            result.missedCount = missedRecords.size();
            
            // 判断迁移是否完整
            result.isComplete = (result.missedCount == 0);
            
            if (result.isComplete) {
                log.info("迁移验证通过！所有敏感字段都已正确加密");
            } else {
                log.warn("迁移验证发现问题！还有{}条记录的敏感字段未加密", result.missedCount);
                for (WeUser user : missedRecords) {
                    log.warn("用户{}({})的敏感字段未完全加密", user.getUserId(), user.getName());
                }
            }
            
            log.info("迁移验证统计: {}", stats);
            
        } catch (Exception e) {
            log.error("验证迁移结果时发生错误", e);
            result.errorMessage = e.getMessage();
        }
        
        return result;
    }

    /**
     * 迁移结果统计
     */
    public static class MigrationResult {
        /** 是否成功 */
        public boolean success = false;
        /** 总处理记录数 */
        public int totalProcessed = 0;
        /** 总更新记录数 */
        public int totalUpdated = 0;
        /** 手机号加密数量 */
        public int mobileEncrypted = 0;
        /** 地址加密数量 */
        public int addressEncrypted = 0;
        /** 错误数量 */
        public int errorCount = 0;
        /** 错误信息 */
        public String errorMessage;

        @Override
        public String toString() {
            return String.format(
                "MigrationResult{success=%s, totalProcessed=%d, totalUpdated=%d, " +
                "mobileEncrypted=%d, addressEncrypted=%d, errorCount=%d, errorMessage='%s'}",
                success, totalProcessed, totalUpdated, mobileEncrypted, 
                addressEncrypted, errorCount, errorMessage
            );
        }
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        /** 是否完整 */
        public boolean isComplete = false;
        /** 遗漏记录数 */
        public int missedCount = 0;
        /** 遗漏的记录 */
        public List<WeUser> missedRecords;
        /** 统计信息 */
        public MigrationStatistics statistics;
        /** 错误信息 */
        public String errorMessage;

        @Override
        public String toString() {
            return String.format(
                "ValidationResult{isComplete=%s, missedCount=%d, statistics=%s, errorMessage='%s'}",
                isComplete, missedCount, statistics, errorMessage
            );
        }
    }

    /**
     * 迁移统计信息
     */
    public static class MigrationStatistics {
        /** 总用户数 */
        public int totalUsers = 0;
        /** 有手机号的用户数 */
        public int usersWithMobile = 0;
        /** 有加密手机号的用户数 */
        public int usersWithMobileEncrypt = 0;
        /** 有地址的用户数 */
        public int usersWithAddress = 0;
        /** 有加密地址的用户数 */
        public int usersWithAddressEncrypt = 0;
        /** 手机号缺少加密的用户数 */
        public int mobileMissingEncrypt = 0;
        /** 地址缺少加密的用户数 */
        public int addressMissingEncrypt = 0;

        @Override
        public String toString() {
            return String.format(
                "MigrationStatistics{totalUsers=%d, usersWithMobile=%d, usersWithMobileEncrypt=%d, " +
                "usersWithAddress=%d, usersWithAddressEncrypt=%d, mobileMissingEncrypt=%d, addressMissingEncrypt=%d}",
                totalUsers, usersWithMobile, usersWithMobileEncrypt, 
                usersWithAddress, usersWithAddressEncrypt, mobileMissingEncrypt, addressMissingEncrypt
            );
        }
    }
}