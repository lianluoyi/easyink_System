package com.easyink.web.controller.wecom.radar;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.radar.RadarChannelEnum;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.dto.radar.DeleteRadarDTO;
import com.easyink.wecom.domain.dto.radar.GetRadarShortUrlDTO;
import com.easyink.wecom.domain.dto.radar.RadarDTO;
import com.easyink.wecom.domain.dto.radar.SearchRadarDTO;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.radar.WeRadarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName： WeRadarController
 *
 * @author wx
 * @date 2022/7/18 14:57
 */

@Api(tags = "雷达Controller")
@RestController
@RequestMapping("/wecom/radar")
public class WeRadarController extends BaseController {

    private final WeRadarService weRadarService;

    @Autowired
    public WeRadarController(WeRadarService weRadarService) {
        this.weRadarService = weRadarService;
    }

    @Log(title = "新增雷达", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增雷达")
    //@PreAuthorize("@ss.hasPermi('redeeomCode:activity:add')")
    public AjaxResult add(@RequestBody @Validated RadarDTO radarDTO) {
        radarDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weRadarService.saveRadar(radarDTO);
        return AjaxResult.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询雷达列表")
    public TableDataInfo<WeRadarVO> list(SearchRadarDTO radarDTO) {
        radarDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeRadarVO> list = weRadarService.getRadarList(radarDTO);
        return getDataTable(list);
    }

    @GetMapping("/get")
    @ApiOperation("获取雷达详情")
    public AjaxResult<WeRadarVO> get(@RequestParam("id") Long id) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        WeRadarVO weRadarVO = weRadarService.getRadar(corpId, id);
        return AjaxResult.success(weRadarVO);
    }

    @Log(title = "更新雷达", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("修改雷达")
    //@PreAuthorize("@ss.hasPermi('redeeomCode:activity:edit')")
    public AjaxResult edit(@RequestBody @Validated RadarDTO radarDTO) {
        radarDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weRadarService.updateRadar(radarDTO);
        return AjaxResult.success();
    }

    @Log(title = "删除雷达", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @ApiOperation("删除雷达")
    //@PreAuthorize("@ss.hasPermi('redeeomCode:activity:del')")
    public <T> AjaxResult<T> remove(@Validated @RequestBody DeleteRadarDTO deleteDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(weRadarService.batchRemoveRadar(corpId, deleteDTO));
    }

    @GetMapping("/getRadarShortUrl")
    @ApiOperation("侧边栏获取雷达短链")
    public AjaxResult<AttachmentParam> getRadarShortUrl(GetRadarShortUrlDTO radarShortUrlDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weRadarService.getRadarShortUrl(radarShortUrlDTO.getRadarId(), RadarChannelEnum.SIDE_BAR.getTYPE(), radarShortUrlDTO.getUserId(), corpId, RadarConstants.RadarCustomerClickRecord.EMPTY_SCENARIO));
    }

}
