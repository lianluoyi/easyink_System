package com.easyink.web.controller.openapi;


import com.easyink.common.annotation.Log;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.openapi.aop.ValidateSign;
import com.easyink.wecom.openapi.domain.resp.BaseOpenApiResp;
import com.easyink.wecom.openapi.domain.vo.GetWeCustomerByUnionIdVO;
import com.easyink.wecom.openapi.dto.ConfigCorpSelfBuildDTO;
import com.easyink.wecom.openapi.dto.GetWeCustomerByUnionIdDTO;
import com.easyink.wecom.openapi.service.AppIdInfoService;
import com.easyink.wecom.openapi.service.LockSelfBuildService;
import com.easyink.wecom.service.WeCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: 对外开放的api接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 15:44
 */
@RestController
@RequestMapping("/open_api")
@Api(tags = {"对外开放的api接口"})
@AllArgsConstructor
public class OpenApiController {

    private final AppIdInfoService appIdInfoService;
    private final WeCustomerService weChatCustomerService;
    private final LockSelfBuildService lockSelfBuildService;
    private final WeCustomerService weCustomerService;


    @GetMapping("/getTicket")
    @ApiOperation("获取票据")
    public BaseOpenApiResp<String> getTicket(String appId, String appSecret) {
        return BaseOpenApiResp.success(appIdInfoService.getTicket(appId, appSecret));
    }

    @ValidateSign
    @PostMapping("/externalcontact/unionid_to_external_userid")
    @ApiOperation("根据unionId获取联系人externalUserid")
    public BaseOpenApiResp<GetWeCustomerByUnionIdVO> unionIdToExternalUserid(@Validated @RequestBody GetWeCustomerByUnionIdDTO dto) {
        WeCustomer weCustomer = weChatCustomerService.getCustomerByUnionId(dto);
        GetWeCustomerByUnionIdVO vo = new GetWeCustomerByUnionIdVO();
        if (weCustomer != null) {
            vo.initByWeCustomer(weCustomer);
        }
        return BaseOpenApiResp.success(vo);
    }


    /**
     * 配置待开发对应的自建应用的服务器地址配置信息
     * @param selfBuildDTO
     * @return
     */
    @PostMapping("/selfbuild/config")
    @ApiOperation("设置指定企业的自建应用配置信息")
    public BaseOpenApiResp<GetWeCustomerByUnionIdVO> configSelfBuildByCorp(@Validated @RequestBody ConfigCorpSelfBuildDTO selfBuildDTO) {
        lockSelfBuildService.config(selfBuildDTO);
        return BaseOpenApiResp.success();
    }

    /**
     * 内部用的同步客户接口
     */
    @GetMapping("/synchWeCustomer")
    @ApiOperation("手动同步客户接口")
    public BaseOpenApiResp<String> manualSyncWeCustomer(@RequestParam String corpId) {
        weCustomerService.syncWeCustomerV2(corpId);
        return BaseOpenApiResp.success();
    }



}
