package com.easyink.web.controller.global;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.framework.config.SysConfig;
import com.easyink.wecom.domain.vo.global.GlobalSettingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局设置
 *
 * @author tigger
 * 2025/7/15 15:05
 **/
@RestController
@RequestMapping("/wecom/setting")
@Api(tags = "全局设置接口controller")
@Slf4j
public class SettingController extends BaseController {

    @ApiOperation("获取全局配置")
    @ApiResponses({
    })
    @GetMapping("/get")
    public AjaxResult<GlobalSettingVO> getGlobalSetting() {
        GlobalSettingVO globalSettingVO = new GlobalSettingVO();
        globalSettingVO.setSelectMonthLimit(SysConfig.getInstance().getSelectMonthLimit());
        return AjaxResult.success(globalSettingVO);
    }
}
