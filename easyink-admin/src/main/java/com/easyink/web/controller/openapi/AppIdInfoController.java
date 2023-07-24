package com.easyink.web.controller.openapi;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.utils.ServletUtils;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.openapi.constant.AppInfoConst;
import com.easyink.wecom.openapi.domain.entity.AppCallbackSetting;
import com.easyink.wecom.openapi.domain.resp.BaseOpenApiResp;
import com.easyink.wecom.openapi.domain.vo.AppIdGenVO;
import com.easyink.wecom.openapi.dto.AddCallbackDTO;
import com.easyink.wecom.openapi.dto.EditCallbackDTO;
import com.easyink.wecom.openapi.service.AppCallbackSettingService;
import com.easyink.wecom.openapi.service.AppIdInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: 开发参数生成接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 11:39
 */
@RestController
@RequestMapping("/wecom/openapi")
@Api(tags = {"开发参数生成接口"})
@Slf4j
@RequiredArgsConstructor
public class AppIdInfoController {

    private final AppIdInfoService appIdGenService;
    private final AppCallbackSettingService appCallbackSettingService;


    @PostMapping("/appInfo/create")
    @ApiOperation("生成开发账号")
    public AjaxResult<AppIdGenVO> create() {
        return AjaxResult.success(appIdGenService.create(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/appInfo/get")
    @ApiOperation("获取开发参数")
    public AjaxResult<AppIdGenVO> get() {
        return AjaxResult.success(appIdGenService.getVO(LoginTokenService.getLoginUser().getCorpId()));
    }

    @PostMapping("/appInfo/refreshSecret")
    @ApiOperation("刷新appSecret")
    public AjaxResult<AppIdGenVO> refreshSecret() {
        return AjaxResult.success(appIdGenService.refreshSecret(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/callback/list")
    @ApiOperation("获取消息订阅的回调地址")
    public AjaxResult<List<AppCallbackSetting>> getCallbackList() {
        return AjaxResult.success(appCallbackSettingService.list(new LambdaQueryWrapper<AppCallbackSetting>()
                .eq(AppCallbackSetting::getCorpId, LoginTokenService.getLoginUser().getCorpId())
        ));
    }

    @PostMapping("/callback")
    @ApiOperation("新增消息订阅的回调地址")
    public AjaxResult<Void> addCallbackUrl(@RequestBody AddCallbackDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(appCallbackSettingService.addUrl(dto));
    }
    @PutMapping("/callback")
    @ApiOperation("编辑消息订阅的回调地址")
    public AjaxResult<Void> editCallbackUrl(@RequestBody EditCallbackDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        appCallbackSettingService.editUrl(dto);
        return AjaxResult.success();
    }

    @DeleteMapping("/callback/{id}")
    @ApiOperation("删除消息订阅的回调地址")
    public AjaxResult<Void> delCallbackUrl( @PathVariable("id")Long id) {
        appCallbackSettingService.deleteUrl(id);
        return AjaxResult.success();
    }





    @GetMapping("/validate")
    @ApiOperation("校验签名")
    public BaseOpenApiResp<Integer> validate() {
        log.info("[openApi]收到校验签名请求,{}", ServletUtils.getRequest());
        return BaseOpenApiResp.success(AppInfoConst.TICKET_EXPIRE_TIME);
    }


}
