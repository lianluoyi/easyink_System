package com.easywecom.web.controller.wecom;

import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.wecom.domain.dto.BaseApplicationDTO;
import com.easywecom.wecom.domain.dto.QueryApplicationDTO;
import com.easywecom.wecom.domain.dto.SetApplicationUseScopeDTO;
import com.easywecom.wecom.domain.dto.UpdateApplicationDTO;
import com.easywecom.wecom.domain.vo.ApplicationIntroductionVO;
import com.easywecom.wecom.domain.vo.MyApplicationIntroductionVO;
import com.easywecom.wecom.domain.vo.WeApplicationDetailVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeApplicationCenterService;
import com.easywecom.wecom.service.WeMyApplicationService;
import com.easywecom.wecom.service.WeMyApplicationUseScopeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 类名: WeApplicationManagerController
 *
 * @author: 1*+
 * @date: 2021-10-15 17:17
 */
@Api(value = "WeApplicationManagerController", tags = "应用管理接口")
@RestController
@RequestMapping("/wecom/application")
public class WeApplicationManagerController {

    private final WeApplicationCenterService weApplicationCenterService;
    private final WeMyApplicationService weMyApplicationService;
    private final WeMyApplicationUseScopeService weMyApplicationUseScopeService;

    @Autowired
    public WeApplicationManagerController(WeApplicationCenterService weApplicationCenterService, WeMyApplicationService weMyApplicationService, WeMyApplicationUseScopeService weMyApplicationUseScopeService) {
        this.weApplicationCenterService = weApplicationCenterService;
        this.weMyApplicationService = weMyApplicationService;
        this.weMyApplicationUseScopeService = weMyApplicationUseScopeService;
    }


    @ApiOperation("获取应用中心列表")
    @GetMapping("/getApplicationList")
    public AjaxResult<List<ApplicationIntroductionVO>> getApplicationList(@ApiParam @Valid QueryApplicationDTO queryApplicationDTO) {
        return AjaxResult.success(weApplicationCenterService.listOfEnableApplication(queryApplicationDTO.getType(), queryApplicationDTO.getName()));
    }


    @ApiOperation("获取应用中心应用详情")
    @GetMapping("/getApplicationDetail")
    public AjaxResult<WeApplicationDetailVO> getApplicationList(BaseApplicationDTO baseApplicationDTO) {
        return AjaxResult.success(weApplicationCenterService.getApplicationDetail(baseApplicationDTO.getAppid(), LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:application:install')")
    @ApiOperation("安装应用")
    @PostMapping("/installApplication")
    public AjaxResult installApplication(@RequestBody BaseApplicationDTO baseApplicationDTO) {
        weMyApplicationService.installApplication(LoginTokenService.getLoginUser().getCorpId(), baseApplicationDTO.getAppid());
        return AjaxResult.success();
    }

    @ApiOperation("获取我的应用列表")
    @GetMapping("/getMyApplicationList")
    public AjaxResult<List<MyApplicationIntroductionVO>> getMyApplicationList() {
        return AjaxResult.success(weMyApplicationService.listOfMyApplication(LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("获取我的应用列表-侧边栏")
    @GetMapping("/getMyApplicationList2Sidebar")
    public AjaxResult<List<MyApplicationIntroductionVO>> getMyApplicationList2Sidebar() {
        return AjaxResult.success(weMyApplicationService.listOfMyApplication2Sidebar(LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("移除我的应用")
    @PostMapping("/deleteMyApplication")
    public AjaxResult<List<MyApplicationIntroductionVO>> deleteMyApplication(@RequestBody BaseApplicationDTO baseApplicationDTO) {
        weMyApplicationService.deleteMyApplication(LoginTokenService.getLoginUser().getCorpId(), baseApplicationDTO.getAppid());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:myApplication:update')")
    @ApiOperation("更新我的应用配置")
    @PostMapping("/updateMyApplicationConfig")
    public AjaxResult<List<MyApplicationIntroductionVO>> updateMyApplicationConfig(@RequestBody UpdateApplicationDTO updateApplicationDTO) {
        weMyApplicationService.updateMyApplicationConfig(LoginTokenService.getLoginUser().getCorpId(), updateApplicationDTO.getAppid(), updateApplicationDTO.getConfig());
        return AjaxResult.success();
    }

    @ApiOperation("我的应用使用范围")
    @PostMapping("/setMyApplicationUseScope")
    public AjaxResult setMyApplicationUseScope(@Valid @RequestBody SetApplicationUseScopeDTO setApplicationUseScopeDTO) {
        weMyApplicationUseScopeService.setMyApplicationUseScope(LoginTokenService.getLoginUser().getCorpId(), setApplicationUseScopeDTO.getAppid(), setApplicationUseScopeDTO.getUseScopeList());
        return AjaxResult.success();
    }

}
