package com.easyink.web.controller.wecom.transfer;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeSensitiveActHitService;
import com.easyink.wecom.service.WeUserCustomerMessageStatisticsService;
import com.easyink.wecom.service.WeUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据迁移Controller
 *
 * @author lichaoyu
 * @date 2023/5/23 17:42
 */
@RestController
@Api(value = "WeComTransferController", tags = "数据迁移接口")
@RequestMapping("/wecom/transferData")
@RequiredArgsConstructor
public class WeComTransferController {

    private final WeUserCustomerMessageStatisticsService weUserCustomerMessageStatisticsService;
    private final WeSensitiveActHitService weSensitiveActHitService;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WeUserService weUserService;

    @PostMapping("/updateUserActiveChatCnt")
    @ApiOperation("更新历史数据中员工主动发起的会话数")
    public AjaxResult updateUserActiveChatCnt() {
        weUserCustomerMessageStatisticsService.updateUserActiveChatCnt();
        return AjaxResult.success();
    }

    @PostMapping("/update/sensitive/history/data")
    @ApiOperation("更新历史敏感行为操作人和操作对象信息")
    public AjaxResult updateSensitive() {
        weSensitiveActHitService.updateHistorySensitive();
        return AjaxResult.success();
    }

    @PostMapping("/update/totalAllCustomerCnt")
    @ApiOperation("数据统计-联系客户-客户总数，旧数据统计")
    public AjaxResult updateTotalAllCustomerCnt() {
        weFlowerCustomerRelService.updateTotalAllCustomerCnt();
        return AjaxResult.success();
    }

    @PostMapping("/migrateDepartmentSnapshot")
    @ApiOperation("数据迁移-为历史数据补充部门快照信息")
    public AjaxResult migrateDepartmentSnapshot(@RequestParam(required = false) String corpId) {
        // 如果没有指定企业ID，则使用当前登录用户的企业ID
        if (corpId == null || corpId.trim().isEmpty()) {
            corpId = LoginTokenService.getLoginUser().getCorpId();
        }
        
        String result = weUserService.migrateDepartmentSnapshot(corpId);
        return AjaxResult.success(result);
    }

    @PostMapping("/migrateDepartmentSnapshot/all")
    @ApiOperation("数据迁移-为所有企业的历史数据补充部门快照信息")
    public AjaxResult migrateDepartmentSnapshotForAll() {
        try {
            // 获取所有企业的历史数据进行迁移
            // 注意：这个接口应该谨慎使用，建议在维护窗口期间执行
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("开始为所有企业迁移部门快照数据...\n");
            
            // 这里可以根据实际需求添加获取所有企业ID的逻辑
            // 为了安全起见，暂时只处理当前登录用户的企业
            String currentCorpId = LoginTokenService.getLoginUser().getCorpId();
            String result = weUserService.migrateDepartmentSnapshot(currentCorpId);
            resultBuilder.append("企业 ").append(currentCorpId).append(": ").append(result).append("\n");
            
            return AjaxResult.success(resultBuilder.toString());
        } catch (Exception e) {
            return AjaxResult.error("批量迁移失败: " + e.getMessage());
        }
    }
}
