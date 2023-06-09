package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.domain.dto.statistics.*;
import com.easyink.wecom.domain.vo.statistics.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.*;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final WeUserService weUserService;
    private final PageHomeService pageHomeService;
    private final WeTagService weTagService;

    private final WeGroupTagService weGroupTagService;

    @GetMapping("/data")
    @ApiOperation("执行对应日期的数据统计任务，执行前需先将we_user_customer_message_statisticsService表中对应日期的数据删除。")
    public AjaxResult getData(String time) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        weUserService.getUserBehaviorDataByCorpId(corpId, time);
        pageHomeService.doSystemCustomStat(corpId, false, time);
        weUserCustomerMessageStatisticsService.getMessageStatistics(corpId,time);
        return AjaxResult.success();
    }

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

    @PostMapping("/getCustomerTagTableView")
    @ApiOperation("获取标签统计-客户标签-表格视图")
    public TableDataInfo<WeTagCustomerStatisticsVO> getCustomerTagTableView(@RequestBody @Validated WeTagStatisticsDTO dto){
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return weTagService.selectTagStatistics(dto);
    }

    @PostMapping("/getCustomerTagChartView")
    @ApiOperation("获取标签统计-客户标签-图表视图")
    public TableDataInfo<WeTagCustomerStatisticsChartVO> getCustomerTagChartView(@RequestBody @Validated WeTagStatisticsDTO dto){
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return weTagService.getCustomerTagTableChartView(dto);
    }

    @PreAuthorize("@ss.hasPermi('statistic:labelStatistics:export')")
    @PostMapping("/exportCustomerTagsView")
    @ApiOperation("导出标签统计-客户标签")
    public AjaxResult<CustomerOverviewDateVO> exportCustomerTagsView(@RequestBody @Validated WeTagStatisticsDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weTagService.exportCustomerTagsView(dto));
    }

    @PostMapping("/getCustomerOverViewOfDate")
    @ApiOperation("获取客户概况-数据总览-日期维度")
    public TableDataInfo<CustomerOverviewDateVO> getCustomerOverViewOfDate(@RequestBody @Validated CustomerOverviewDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getCustomerOverViewOfDate(dto));
    }

    @PreAuthorize("@ss.hasPermi('statistic:customerContact:export')")
    @PostMapping("/exportCustomerOverViewOfDate")
    @ApiOperation("导出客户概况-数据总览-日期维度")
    public AjaxResult<CustomerOverviewDateVO> exportCustomerOverViewOfDare(@RequestBody @Validated CustomerOverviewDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportCustomerOverViewOfDate(dto));
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
        //构建返回条件
        TableDataInfo tableDataInfo=new TableDataInfo();
        PageInfo<UserServiceVO> pageInfo=weUserCustomerMessageStatisticsService.getUserServiceOfUser(dto);
        tableDataInfo.setTotal((int) pageInfo.getTotal());
        tableDataInfo.setRows(pageInfo.getList());
        tableDataInfo.setCode(ResultTip.TIP_GENERAL_SUCCESS.getCode());
        tableDataInfo.setMsg("查询成功");
        return tableDataInfo;
    }

    @PreAuthorize("@ss.hasPermi('statistic:employeeService:export')")
    @PostMapping("/exportUserServiceOfUser")
    @ApiOperation("导出员工服务-数据总览-员工维度")
    public AjaxResult exportUserServiceOfUser(@RequestBody @Validated UserServiceDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportUserServiceOfUser(dto));
    }

    @PostMapping("/getUserServiceOfTime")
    @ApiOperation("获取员工服务-数据总览-时间维度")
    public TableDataInfo<UserServiceTimeVO> getUserServiceOfTime(@RequestBody @Validated UserServiceDTO dto){
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weUserCustomerMessageStatisticsService.getUserServiceOfTime(dto));
    }

    @PreAuthorize("@ss.hasPermi('statistic:employeeService:export')")
    @PostMapping("/exportUserServiceOfTime")
    @ApiOperation("导出员工服务-数据总览-时间维度")
    public AjaxResult exportUserServiceOfTime(@RequestBody @Validated UserServiceDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weUserCustomerMessageStatisticsService.exportUserServiceOfTime(dto));
    }

    @PostMapping("/getGroupTagTableView")
    @ApiOperation("获取群标签-表格视图")
    public TableDataInfo<WeTagGroupStatisticsVO> getGroupTagTableView(@RequestBody @Validated WeTagStatisticsDTO weTagStatisticsDTO) {
        weTagStatisticsDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeTagGroupStatisticsVO> weTagGroupStatisticsVOS = weGroupTagService.groupTagTableView(weTagStatisticsDTO);
        return PageInfoUtil.getDataTable(weTagGroupStatisticsVOS, weTagStatisticsDTO.getPageNum(),weTagStatisticsDTO.getPageSize());
    }

    @PostMapping("/getGroupTagChartView")
    @ApiOperation("获取群标签-图表视图")
    public TableDataInfo<WeTagGroupStatisticChartVO> getGroupTagChartView(@RequestBody @Validated WeTagStatisticsDTO weTagStatisticsDTO) {
        weTagStatisticsDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeTagGroupStatisticChartVO> weTagStatisticsChartVOList = weGroupTagService.groupTagChartView(weTagStatisticsDTO);
        return PageInfoUtil.getDataTable(weTagStatisticsChartVOList, weTagStatisticsDTO.getPageNum(),weTagStatisticsDTO.getPageSize());
    }

    @PostMapping("/exportGroupTagsView")
    @ApiOperation("导出群标签表格")
    public AjaxResult exportGroupTagsView(@RequestBody @Validated WeTagStatisticsDTO weTagStatisticsDTO) {
        weTagStatisticsDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weGroupTagService.exportGroupTags(weTagStatisticsDTO));
    }

}
