package com.easyink.web.controller.wecom.transfer;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.service.WeSensitiveActHitService;
import com.easyink.wecom.service.WeUserCustomerMessageStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
