package com.easyink.web.controller.openapi;


import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.handler.third.SessionArchiveHandler;
import com.easyink.wecom.service.SensitiveDataMigrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * 类名: 对外开放的api接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 15:44
 */
@RestController
@RequestMapping("/transfer")
@Api(tags = {"迁移接口"})
@AllArgsConstructor
@Slf4j
public class TransferApiController {

    private final SessionArchiveHandler sessionArchiveHandler;

    private final SensitiveDataMigrationService sensitiveDataMigrationService;

    @PostMapping("/sync/mapping")
    @ApiOperation("保存saas企业服务商员工和外部联系人映射关系")
    public AjaxResult<String> syncMapping(@RequestParam String corpId) {
        try {
            sessionArchiveHandler.syncMapping(corpId);
        } catch (Exception e) {
            log.error("[保存saas企业服务商员工和外部联系人映射关系] 异常: {}", ExceptionUtils.getStackTrace(e));
            return AjaxResult.error("保存saas企业服务商员工和外部联系人映射关系失败");
        }
        return AjaxResult.success();
    }


    /**
     * @param file excel文件
     * @param corpId saas服务的对应的企业id密文
     * @param serviceAgentId 第三方服务商的agentId
     * @return
     */
    @PostMapping("/import/sessionArchive")
    @ApiOperation("导入会话存档")
    public AjaxResult<String> importSessionArchive(MultipartFile file, @RequestParam String corpId, @RequestParam String serviceAgentId) {
        try (InputStream is = file.getInputStream()) {
            sessionArchiveHandler.importSessionArchive(is, corpId, serviceAgentId);
        } catch (Exception e) {
            log.error("[导入会话存档] 失败: {}", ExceptionUtils.getStackTrace(e));
            return AjaxResult.error("导入会话存档失败");
        }
        return AjaxResult.success();
    }





    @ApiOperation("迁移所有表的敏感数据")
    @PostMapping("/migrateAll")
    public AjaxResult migrateAllTables() {
        try {
            log.info("开始执行所有表的敏感数据迁移");
            sensitiveDataMigrationService.migrateAllSensitiveData();
            log.info("所有表的敏感数据迁移完成");
            return AjaxResult.success("所有表的敏感数据迁移完成");
        } catch (Exception e) {
            log.error("敏感数据迁移失败", e);
            return AjaxResult.error("敏感数据迁移失败: " + e.getMessage());
        }
    }

    @ApiOperation("迁移指定表的敏感数据")
    @PostMapping("/migrateTable/{tableName}")
    public AjaxResult migrateTable(
            @ApiParam(value = "表名", required = true)
            @PathVariable String tableName) {
        try {
            log.info("开始执行表 {} 的敏感数据迁移", tableName);
            int migratedCount = sensitiveDataMigrationService.migrateSensitiveDataByTable(tableName);
            log.info("表 {} 的敏感数据迁移完成，共迁移 {} 条记录", tableName, migratedCount);
            return AjaxResult.success("表 " + tableName + " 的敏感数据迁移完成，共迁移 " + migratedCount + " 条记录");
        } catch (Exception e) {
            log.error("表 {} 的敏感数据迁移失败", tableName, e);
            return AjaxResult.error("表 " + tableName + " 的敏感数据迁移失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取迁移进度")
    @GetMapping("/progress/{tableName}")
    public AjaxResult getMigrationProgress(
            @ApiParam(value = "表名", required = true)
            @PathVariable String tableName) {
        try {
            return AjaxResult.success(sensitiveDataMigrationService.getMigrationProgress(tableName));
        } catch (Exception e) {
            log.error("获取表 {} 的迁移进度失败", tableName, e);
            return AjaxResult.error("获取迁移进度失败: " + e.getMessage());
        }
    }

    @ApiOperation("验证迁移结果")
    @GetMapping("/verify/{tableName}")
    public AjaxResult verifyMigration(
            @ApiParam(value = "表名", required = true)
            @PathVariable String tableName) {
        try {
            return AjaxResult.success(sensitiveDataMigrationService.verifyMigrationResult(tableName));
        } catch (Exception e) {
            log.error("验证表 {} 的迁移结果失败", tableName, e);
            return AjaxResult.error("验证迁移结果失败: " + e.getMessage());
        }
    }


    @ApiOperation("重新脱敏地址字段")
    @PostMapping("/reDesensitizeAddress")
    public AjaxResult<Map<String, Object>> reDesensitizeAddressFields() {
        try {
            log.info("[重新脱敏地址字段]开始执行地址字段重新脱敏处理");
            Map<String, Object> result = sensitiveDataMigrationService.reDesensitizeAddressFields();
            log.info("[重新脱敏地址字段]地址字段重新脱敏处理完成");
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("重新脱敏地址字段失败", e);
            return AjaxResult.error("重新脱敏地址字段失败: " + e.getMessage());
        }
    }

}
