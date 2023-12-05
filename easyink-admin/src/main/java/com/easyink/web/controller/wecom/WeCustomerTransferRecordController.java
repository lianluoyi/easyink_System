package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.transfer.TransferCustomerDTO;
import com.easyink.wecom.domain.dto.transfer.TransferRecordPageDTO;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferConfig;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.transfer.WeCustomerTransferRecordVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCustomerTransferConfigService;
import com.easyink.wecom.service.WeCustomerTransferRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: 在职继承分配记录表控制层接口
 *
 * @author : silver_chariot
 * @date : 2021/11/29 17:56
 */
@RestController
@RequestMapping("/wecom/transfer")
@Api(tags = "在职继承接口")
public class WeCustomerTransferRecordController extends BaseController {

    private final WeCustomerTransferRecordService weCustomerTransferRecordService;
    private final WeCustomerTransferConfigService weCustomerTransferConfigService;

    @Autowired
    public WeCustomerTransferRecordController(@NotNull WeCustomerTransferRecordService weCustomerTransferRecordService, @NotNull WeCustomerTransferConfigService weCustomerTransferConfigService) {
        this.weCustomerTransferRecordService = weCustomerTransferRecordService;
        this.weCustomerTransferConfigService = weCustomerTransferConfigService;
    }

    @PostMapping
    @ApiOperation("在职继承")
    @PreAuthorize("@ss.hasPermi('customerManage:active:transfer')")
    public AjaxResult transfer(@Validated @RequestBody TransferCustomerDTO dto) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        weCustomerTransferRecordService.transfer(corpId, dto.getCustomerList(), dto.getTakeoverUserid(), dto.getTransferSuccessMsg());
        return AjaxResult.success();

    }

    @GetMapping("/recordList")
    @ApiOperation("在职继承记录列表")
    @PreAuthorize("@ss.hasPermi('customerManage:transfer:record')")
    public TableDataInfo<WeCustomerTransferRecordVO> recordList(@Validated TransferRecordPageDTO dto) {
        startPage();
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weCustomerTransferRecordService.getList(dto));
    }

    @GetMapping("/customerList")
    @ApiOperation("在职继承客户列表")
    public TableDataInfo<WeCustomerVO> transferCustomerList(WeCustomer weCustomer) {
        PageInfoUtil.setPage();
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeCustomerVO> list = weCustomerTransferRecordService.transferCustomerList(weCustomer);
        return getDataTable(list);
    }

    @GetMapping("/config")
    @ApiOperation("获取继承设置")
    public AjaxResult<WeCustomerTransferConfig> getConfig() {
        return AjaxResult.success(weCustomerTransferConfigService.getById(LoginTokenService.getLoginUser().getCorpId()));
    }

    @PutMapping("/editConfig")
    @ApiOperation("修改继承设置")
    @PreAuthorize("@ss.hasPermi('customerManage:transfer:config')")
    public AjaxResult editConfig(@RequestBody WeCustomerTransferConfig weCustomerTransferConfig) {
        weCustomerTransferConfig.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCustomerTransferConfigService.editConfig(weCustomerTransferConfig);
        return AjaxResult.success();
    }

}
