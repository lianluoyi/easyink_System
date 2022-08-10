package com.easyink.web.controller.wecom.radar;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.radar.*;
import com.easyink.wecom.domain.vo.radar.*;
import com.easyink.wecom.service.radar.WeRadarClickRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName： WeRadarClickRecordController
 *
 * @author wx
 * @date 2022/7/19 19:54
 */
@Api(tags = "雷达点击记录Controller")
@RestController
@RequestMapping("/wecom/radar/record")
public class WeRadarClickRecordController extends BaseController {

    private final WeRadarClickRecordService radarClickRecordService;


    @Autowired
    public WeRadarClickRecordController(WeRadarClickRecordService radarClickRecordService) {
        this.radarClickRecordService = radarClickRecordService;
    }

    @Log(title = "新增雷达点击记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增雷达点击记录")
//    @PreAuthorize("@ss.hasPermi('redeeomCode:activity:add')")
    public <T> AjaxResult add(@RequestBody @Validated RadarClickRecordDTO clickRecordDTO) {
        radarClickRecordService.saveClickRecord(clickRecordDTO);
        return AjaxResult.success();
    }

    @GetMapping("/getTotal")
    @ApiOperation("雷达详情数据总览")
    public AjaxResult<RadarRecordTotalVO> getTotal(@RequestParam("radarId") Long radarId) {
        return AjaxResult.success(radarClickRecordService.getTotal(radarId));
    }

    @GetMapping("/getTimeRangeAnalyseCount")
    @ApiOperation("时间段内雷达点击数据统计")
    public AjaxResult<RadarAnalyseVO> getTimeRangeAnalyseCount(SearchRadarAnalyseDTO radarAnalyseDTO) {
        return AjaxResult.success(radarClickRecordService.getTimeRangeAnalyseCount(radarAnalyseDTO));
    }

    @GetMapping("/getChannelSort")
    @ApiOperation("获得渠道排序")
    public AjaxResult<RadarChannelSortVO> getChannelSort(@RequestParam("radarId") Long radarId) {
        return AjaxResult.success(radarClickRecordService.getChannelSort(radarId));
    }


    @GetMapping("/getCustomerClickRecord")
    @ApiOperation("获取客户点击记录")
    public TableDataInfo<RadarCustomerRecordVO> getCustomerClickRecord(SearchCustomerRecordDTO customerRecordDTO) {
        startPage();
        return getDataTable(radarClickRecordService.getCustomerClickRecord(customerRecordDTO));
    }

    @GetMapping("/getCustomerClickRecordDetail")
    @ApiOperation("获取客户点击记录详情")
    public AjaxResult<RadarCustomerClickRecordDetailVO> getCustomerClickRecordDetail(SearchCustomerRecordDetailDTO customerRecordDTO) {
        return AjaxResult.success(radarClickRecordService.getCustomerClickRecordDetail(customerRecordDTO));
    }


    @GetMapping("/getChannelClickRecord")
    @ApiOperation("获取渠道点击记录")
    public TableDataInfo<RadarChannelRecordVO> getChannelClickRecord(SearchChannelRecordDTO channelRecordDTO) {
        startPage();
        return getDataTable(radarClickRecordService.getChannelClickRecord(channelRecordDTO));
    }

    @GetMapping("/getChannelClickRecordDetail")
    @ApiOperation("获取渠道点击记录详情")
    public TableDataInfo<RadarCustomerRecordVO> getChannelClickRecordDetail(SearchChannelRecordDetailDTO channelRecordDetailDTO) {
        startPage();
        return getDataTable(radarClickRecordService.getChannelClickRecordDetail(channelRecordDetailDTO));
    }

}
