package com.easyink.web.controller.wecom.redeemcode;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDeleteDTO;
import com.easyink.wecom.domain.vo.redeemcode.ImportRedeemCodeVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ClassName： WeRedeemCode
 *
 * @author wx
 * @date 2022/7/6 10:43
 */

@Api(tags = "兑换码Controller")
@RestController
@RequestMapping("/wecom/redeemcode")
public class WeRedeemCodeController extends BaseController {

    private final WeRedeemCodeService weRedeemCodeService;

    private final WeCustomerService weCustomerService;

    @Autowired
    public WeRedeemCodeController(WeRedeemCodeService weRedeemCodeService, WeCustomerService weCustomerService) {
        this.weRedeemCodeService = weRedeemCodeService;
        this.weCustomerService = weCustomerService;
    }


    @ApiOperation(value = "导入兑换码的excel", httpMethod = "POST")
    @PostMapping("/importRedeemCode/{id}")
    public <T> AjaxResult<T> importRedeemCode(@RequestParam("file") MultipartFile file, @PathVariable String id) throws Exception {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        ImportRedeemCodeVO result = weRedeemCodeService.importRedeemCode(corpId, file, id);
        return AjaxResult.success(result);
    }

    @Log(title = "新增兑换码", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增兑换码")
    public <T> AjaxResult<T> add(@RequestBody @Validated WeRedeemCodeDTO weRedeemCodeDTO) {
        weRedeemCodeService.saveRedeemCode(weRedeemCodeDTO);
        return AjaxResult.success();
    }

    @Log(title = "编辑修改兑换码", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("编辑修改兑换码")
    public <T> AjaxResult<T> edit(@RequestBody @Validated WeRedeemCodeDTO weRedeemCodeDTO) {
        weRedeemCodeDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weRedeemCodeService.updateRedeemCode(weRedeemCodeDTO);
        return AjaxResult.success();
    }

    @Log(title = "删除兑换码", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @ApiOperation("删除兑换码活动")
    public <T> AjaxResult<T> remove(@Validated @RequestBody WeRedeemCodeDeleteDTO deleteDTO) {
        return toAjax(weRedeemCodeService.batchRemoveRedeemCode(deleteDTO));
    }

    @GetMapping("/list")
    @ApiOperation("查询兑换码列表")
    public TableDataInfo<WeRedeemCodeVO> list(WeRedeemCodeDTO weRedeemCodeDTO) {
        weRedeemCodeDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeRedeemCodeVO> list = weRedeemCodeService.getReemCodeList(weRedeemCodeDTO);
        return getDataTable(list);
    }

    @GetMapping("/customerList")
    @ApiOperation("查询联系人")
    public AjaxResult<WeRedeemCodeActivityVO> getCustomerList(String customerName) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weCustomerService.getCustomer(corpId, customerName));
    }

}



















