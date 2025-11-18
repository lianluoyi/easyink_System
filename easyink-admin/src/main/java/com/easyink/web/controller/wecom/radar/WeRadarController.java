package com.easyink.web.controller.wecom.radar;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.radar.RadarChannelEnum;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.ip.IpUtils;
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
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
@Slf4j
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

    @GetMapping("/clickRadar")
    @ApiOperation("根据短链记录雷达点击记录")
    public AjaxResult radar(@ApiParam("短链后缀的code") String shortCode, @ApiParam("用户的公众号openid") String openId) {
//        String serverIp = "";
//        try {
//            serverIp = IpUtils.getOutIp();
//        } catch (Exception e) {
//            log.error("[雷达]获取服务器ip异常.e:{}", ExceptionUtils.getStackTrace(e));
//        }
//        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
//        log.info("[雷达]有人点击了短链,shortCode:{},openId:{},ip:{},serverIp:{}", shortCode, openId, ip, serverIp);
//        if (serverIp.equals(ip)) {
//            log.info("[雷达]ip与服务器ip一样,不处理,ip:{}", ip);
//            return AjaxResult.success();
//        }
        return AjaxResult.success("success", weRadarService.recordRadar(shortCode, openId));
    }


}
