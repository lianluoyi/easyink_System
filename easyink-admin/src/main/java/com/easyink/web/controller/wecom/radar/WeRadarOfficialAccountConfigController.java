package com.easyink.web.controller.wecom.radar;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.dto.radar.WeRadarOfficialAccountConfigDTO;
import com.easyink.wecom.domain.vo.WeOpenConfigVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.radar.WeRadarOfficialAccountConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 雷达公众号配置Controller
 *
 * @author wx
 * 2023/1/11 16:58
 **/
@Api("雷达公众号配置Controller")
@RestController
@RequestMapping("/wecom/radar/config")
@RequiredArgsConstructor
public class WeRadarOfficialAccountConfigController {

    private final WeRadarOfficialAccountConfigService weRadarOfficialAccountConfigService;

    @PostMapping
    @ApiOperation("设置雷达配置公众号")
    public AjaxResult setRadarOfficialAccountConfig(@RequestBody @Validated WeRadarOfficialAccountConfigDTO dto) {
        weRadarOfficialAccountConfigService.setRadarOfficialAccountConfig(dto, LoginTokenService.getLoginUser());
        return AjaxResult.success();
    }

    @GetMapping
    @ApiOperation("获取雷达配置公众号")
    public AjaxResult getRadarOfficialAccountConfig() {
        return AjaxResult.success(WeOpenConfigVO.covert2WeOpenConfigVO(weRadarOfficialAccountConfigService.getRadarOfficialAccountConfig(LoginTokenService.getLoginUser().getCorpId())));
    }

}
