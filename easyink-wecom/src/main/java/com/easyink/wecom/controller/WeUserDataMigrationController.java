package com.easyink.wecom.controller;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.service.impl.WeUserDataMigrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * WeUser数据迁移管理Controller
 * 用于管理员手动触发敏感字段数据迁移
 * 
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@Api(tags = "WeUser数据迁移管理")
@RestController
@RequestMapping("/wecom/migration")
public class WeUserDataMigrationController extends BaseController {

    @Autowired
    private WeUserDataMigrationService migrationService;

    /**
     * 执行WeUser敏感字段数据迁移
     * 将现有明文敏感数据加密并填充到对应的加密字段
     */
    @ApiOperation("执行WeUser敏感字段数据迁移")
    @Log(title = "WeUser数据迁移", businessType = BusinessType.UPDATE)
    @PostMapping("/weuser/sensitive-fields")
    @PreAuthorize("@ss.hasPermi('wecom:migration:execute')")
    public AjaxResult migrateWeUserSensitiveData() {
        try {
            log.info("开始执行WeUser敏感字段数据迁移");
            
            WeUserDataMigrationService.MigrationResult result = migrationService.migrateWeUserSensitiveData();
            
            if (result.success) {
                log.info("WeUser敏感字段数据迁移成功完成，结果: {}", result);
                return AjaxResult.success("数据迁移成功完成", result);
            } else {
                log.error("WeUser敏感字段数据迁移失败: {}", result.errorMessage);
                return AjaxResult.error("数据迁移失败: " + result.errorMessage);
            }
            
        } catch (Exception e) {
            log.error("执行WeUser敏感字段数据迁移时发生异常", e);
            return AjaxResult.error("数据迁移执行异常: " + e.getMessage());
        }
    }

    /**
     * 验证WeUser敏感字段迁移结果
     * 检查是否还有明文数据但缺少加密数据的记录
     */
    @ApiOperation("验证WeUser敏感字段迁移结果")
    @GetMapping("/weuser/sensitive-fields/validate")
    @PreAuthorize("@ss.hasPermi('wecom:migration:validate')")
    public AjaxResult validateMigrationResult() {
        try {
            log.info("管理员{}开始验证WeUser敏感字段迁移结果");
            
            WeUserDataMigrationService.ValidationResult result = migrationService.validateMigrationResult();
            
            if (result.isComplete) {
                log.info("迁移验证通过，所有敏感字段都已正确加密");
                return AjaxResult.success("迁移验证通过，所有敏感字段都已正确加密", result);
            } else {
                log.warn("迁移验证发现问题，还有{}条记录的敏感字段未加密", result.missedCount);
                return AjaxResult.error("迁移验证发现问题，还有" + result.missedCount + "条记录的敏感字段未加密", result);
            }
            
        } catch (Exception e) {
            log.error("验证WeUser敏感字段迁移结果时发生异常", e);
            return AjaxResult.error("迁移验证执行异常: " + e.getMessage());
        }
    }

    /**
     * 获取WeUser敏感字段迁移统计信息
     * 显示当前的迁移状态和统计数据
     */
    @ApiOperation("获取WeUser敏感字段迁移统计信息")
    @GetMapping("/weuser/sensitive-fields/statistics")
    @PreAuthorize("@ss.hasPermi('wecom:migration:view')")
    public AjaxResult getMigrationStatistics() {
        try {
            log.info("管理员{}查询WeUser敏感字段迁移统计信息");
            
            WeUserDataMigrationService.ValidationResult result = migrationService.validateMigrationResult();
            
            return AjaxResult.success("获取迁移统计信息成功", result.statistics);
            
        } catch (Exception e) {
            log.error("获取WeUser敏感字段迁移统计信息时发生异常", e);
            return AjaxResult.error("获取统计信息异常: " + e.getMessage());
        }
    }

    /**
     * 检查是否需要执行数据迁移
     * 用于系统启动时自动检查
     */
    @ApiOperation("检查是否需要执行数据迁移")
    @GetMapping("/weuser/sensitive-fields/check")
    @PreAuthorize("@ss.hasPermi('wecom:migration:view')")
    public AjaxResult checkMigrationNeeded() {
        try {
            log.info("检查是否需要执行WeUser敏感字段数据迁移");
            
            WeUserDataMigrationService.ValidationResult result = migrationService.validateMigrationResult();
            
            boolean migrationNeeded = !result.isComplete;
            
            if (migrationNeeded) {
                log.info("检测到需要执行数据迁移，遗漏记录数: {}", result.missedCount);
                return AjaxResult.success("需要执行数据迁移", 
                    new MigrationCheckResult(true, result.missedCount, "检测到" + result.missedCount + "条记录需要迁移"));
            } else {
                log.info("所有敏感字段都已正确加密，无需执行迁移");
                return AjaxResult.success("无需执行数据迁移", 
                    new MigrationCheckResult(false, 0, "所有敏感字段都已正确加密"));
            }
            
        } catch (Exception e) {
            log.error("检查是否需要执行数据迁移时发生异常", e);
            return AjaxResult.error("检查迁移需求异常: " + e.getMessage());
        }
    }

    /**
     * 迁移检查结果
     */
    public static class MigrationCheckResult {
        /** 是否需要迁移 */
        public boolean migrationNeeded;
        /** 需要迁移的记录数 */
        public int recordCount;
        /** 描述信息 */
        public String description;

        public MigrationCheckResult(boolean migrationNeeded, int recordCount, String description) {
            this.migrationNeeded = migrationNeeded;
            this.recordCount = recordCount;
            this.description = description;
        }

        @Override
        public String toString() {
            return String.format("MigrationCheckResult{migrationNeeded=%s, recordCount=%d, description='%s'}", 
                migrationNeeded, recordCount, description);
        }
    }
}