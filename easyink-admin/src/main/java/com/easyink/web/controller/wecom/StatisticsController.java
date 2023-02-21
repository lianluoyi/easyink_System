package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.vo.statistics.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeUserCustomerMessageStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计Controller
 *
 * @author wx
 * 2023/2/14 13:58
 **/
@RestController
@Api(value = "StatisticsController", tags = "数据统计接口")
@RequestMapping("/wecom/statistics")
@RequiredArgsConstructor
public class StatisticsController extends BaseController {

    private final WeUserCustomerMessageStatisticsService weUserCustomerMessageStatisticsService;

    @PostMapping("/getCustomerOverViewOfTotal")
    @ApiOperation("获取客户概况-数据总览")
    public AjaxResult getCustomerOverViewOfTotal(@RequestBody @Validated StatisticsDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.getCustomerOverViewOfTotal(dto));
    }

    @PostMapping("/getCustomerOverViewOfUser")
    @ApiOperation("获取客户概况-数据总览-员工维度")
    public TableDataInfo<CustomerOverviewVO> getCustomerOverViewOfUser(@RequestBody @Validated CustomerOverviewDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getCustomerOverViewOfUser(dto, true));
    }

    @PreAuthorize("@ss.hasPermi('statistic:customerContact:export')")
    @PostMapping("/exportCustomerOverViewOfUser")
    @ApiOperation("导出客户概况-数据总览-员工维度")
    public AjaxResult<CustomerOverviewVO> exportCustomerOverViewOfUser(@RequestBody @Validated CustomerOverviewDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportCustomerOverViewOfUser(dto));
    }

    @PostMapping("/getCustomerActivityOfDateTrend")
    @ApiOperation("获取客户活跃度-趋势图")
    public AjaxResult getCustomerActivityOfDateTrend(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.getCustomerActivityOfDateTrend(dto));
    }

    @PostMapping("/getCustomerActivityOfDate")
    @ApiOperation("获取客户活跃度-日期维度")
    public TableDataInfo<CustomerActivityOfDateVO> getCustomerActivityOfDate(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getCustomerActivityOfDate(dto, true));
    }

    @PreAuthorize("@ss.hasPermi('statistic:customerContact:export')")
    @PostMapping("/exportCustomerActivityOfDate")
    @ApiOperation("导出客户活跃度-日期维度")
    public AjaxResult exportCustomerActivityOfDate(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportCustomerActivityOfDate(dto));
    }

    @PostMapping("/getCustomerActivityOfUser")
    @ApiOperation("获取客户活跃度-员工维度")
    public TableDataInfo<CustomerActivityOfUserVO> getCustomerActivityOfUser(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getCustomerActivityOfUser(dto, true));
    }

    @PreAuthorize("@ss.hasPermi('statistic:customerContact:export')")
    @PostMapping("/exportCustomerActivityOfUser")
    @ApiOperation("导出客户活跃度-员工维度")
    public AjaxResult exportCustomerActivityOfUser(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportCustomerActivityOfUser(dto));
    }

    @PostMapping("/getCustomerActivityOfUserDetail")
    @ApiOperation("获取客户活跃度-员工维度-详情")
    public TableDataInfo<CustomerActivityOfCustomerVO> getCustomerActivityOfUserDetail(@RequestBody @Validated CustomerActivityUserDetailDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getCustomerActivityOfUserDetail(dto));
    }

    @PostMapping("/getCustomerActivityOfCustomer")
    @ApiOperation("获取客户活跃度-客户维度")
    public TableDataInfo<CustomerActivityOfCustomerVO> getCustomerActivityOfCustomer(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getCustomerActivityOfCustomer(dto, true));
    }

    @PreAuthorize("@ss.hasPermi('statistic:customerContact:export')")
    @PostMapping("/exportCustomerActivityOfCustomer")
    @ApiOperation("导出客户活跃度-客户维度")
    public AjaxResult exportCustomerActivityOfCustomer(@RequestBody @Validated CustomerActivityDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportCustomerActivityOfCustomer(dto));
    }

    @PostMapping("/getUserServiceOfTotal")
    @ApiOperation("获取员工服务-数据总览")
    public AjaxResult getUserServiceOfTotal(@RequestBody @Validated StatisticsDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.getUserServiceOfTotal(dto));
    }

    @PostMapping("/getUserServiceOfUser")
    @ApiOperation("获取员工服务-数据总览-员工维度")
    public TableDataInfo<UserServiceVO> getUserServiceOfUser(@RequestBody @Validated UserServiceDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getUserServiceOfUser(dto, true));
    }

    @PreAuthorize("@ss.hasPermi('statistic:employeeService:export')")
    @PostMapping("/exportUserServiceOfUser")
    @ApiOperation("导出员工服务-数据总览-员工维度")
    public AjaxResult exportUserServiceOfUser(@RequestBody @Validated UserServiceDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportUserServiceOfUser(dto));
    }


}
