package com.easyink.web.controller.wecom.radar;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.radar.DeleteRadarChannelDTO;
import com.easyink.wecom.domain.dto.radar.RadarChannelDTO;
import com.easyink.wecom.domain.dto.radar.SearchRadarChannelDTO;
import com.easyink.wecom.domain.vo.radar.WeRadarChannelVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.radar.WeRadarChannelService;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName： weRadarChannelController
 *
 * @author wx
 * @date 2022/7/19 14:48
 */
@Api(tags = "雷达渠道Controller")
@RestController
@RequestMapping("/wecom/radar/channel")
public class WeRadarChannelController extends BaseController {

    private final WeRadarChannelService radarChannelService;

    @Autowired
    public WeRadarChannelController(WeRadarChannelService radarChannelService) {
        this.radarChannelService = radarChannelService;
    }

    @Log(title = "新增雷达渠道", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增雷达渠道")
//    @PreAuthorize("@ss.hasPermi('redeeomCode:activity:add')")
    public <T> AjaxResult add(@RequestBody @Validated RadarChannelDTO radarChannelDTO) {
        radarChannelService.saveRadarChannel(radarChannelDTO);
        return AjaxResult.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询雷达渠道序列")
    public TableDataInfo<WeRadarChannelVO> list(SearchRadarChannelDTO radarChannelDTO) {
        startPage();
        List<WeRadarChannelVO> list = radarChannelService.getRadarChannelList(radarChannelDTO);
        return getDataTable(list);
    }

    //@PreAuthorize("@ss.hasPermi('wecom:redeemcode:remove')")
    @Log(title = "删除雷达渠道", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @ApiOperation("删除雷达渠道")
    public <T> AjaxResult remove(@Validated @RequestBody DeleteRadarChannelDTO deleteDTO) {
        radarChannelService.batchRemoveRadarChannel(deleteDTO);
        return AjaxResult.success();
    }


    @GetMapping("/get")
    @ApiOperation("获取雷达渠道详情")
    public AjaxResult<WeRadarChannelVO> get(@RequestParam("id") Long id) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(radarChannelService.getRadarChannel(corpId, id));
    }

    //@PreAuthorize("@ss.hasPermi('wecom:redeemcode:edit')")
    @Log(title = "修改雷达渠道", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("修改雷达渠道")
    //  @PreAuthorize("@ss.hasPermi('redeeomCode:activity:edit')")
    public <T> AjaxResult<T> edit(@Validated @RequestBody RadarChannelDTO radarChannelDTO) {
        radarChannelService.updateRadarChannel(radarChannelDTO);
        return AjaxResult.success();
    }

}
