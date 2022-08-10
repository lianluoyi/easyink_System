package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.wecom.domain.WeGroupCodeActual;
import com.easywecom.wecom.domain.dto.groupcode.AddCorpCodeDTO;
import com.easywecom.wecom.domain.dto.groupcode.EditCorpCodeDTO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeGroupCodeActualService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业微信活码Controller
 *
 * @author tigger
 * 2022/2/9 16:14
 **/
@RestController
@RequestMapping("/wecom/corp/actual")
@Api(tags = "企业微信活码Controller")
public class WeGroupCodeCorpActualController extends BaseController {

    private WeGroupCodeActualService weGroupCodeActualService;


    @Autowired
    public WeGroupCodeCorpActualController(WeGroupCodeActualService weGroupCodeActualService) {
        this.weGroupCodeActualService = weGroupCodeActualService;
    }


    @Log(title = "新增企业微信活码", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增企业微信活码")
    public AjaxResult<List<WeGroupCodeActual>> addCorpCode(@Validated @RequestBody AddCorpCodeDTO addCorpCodeDTO) {
        return AjaxResult.success(weGroupCodeActualService.addBatch(addCorpCodeDTO.getWeGroupCodeCorpActualList(), addCorpCodeDTO.getGroupCodeId(), LoginTokenService.getLoginUser().getCorpId()));
    }


    @Log(title = "修改企业微信活码", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改企业微信活码")
    public AjaxResult editCorpCpde(@Validated @RequestBody EditCorpCodeDTO editCorpCodeDTO) {
        return toAjax(weGroupCodeActualService.editBatch(editCorpCodeDTO.getWeGroupCodeCorpActualList(), editCorpCodeDTO.getGroupCodeId(), LoginTokenService.getLoginUser().getCorpId()));
    }

    @Log(title = "删除企业微信活码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除企业微信活码")
    public AjaxResult removeCorpCode(@PathVariable(value = "ids") List<Long> removeIds) {

        return toAjax(weGroupCodeActualService.removeBatch(removeIds, LoginTokenService.getLoginUser().getCorpId()));
    }

}
