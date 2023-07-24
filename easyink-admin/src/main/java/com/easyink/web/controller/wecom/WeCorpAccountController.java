package com.easyink.web.controller.wecom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.annotation.Log;
import com.easyink.common.config.ChatRsaKeyConfig;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.WeCrypt;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.user.NoLoginTokenException;
import com.easyink.common.exception.user.UserNoCorpException;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossSwitchVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeAuthCorpInfoExtendService;
import com.easyink.wecom.service.WeCorpAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业id相关配置Controller
 *
 * @author admin
 * @date 2020-08-24
 */
@RestController
@RequestMapping("/wecom/corp")
@Api(tags = "企业id配置接口")
public class WeCorpAccountController extends BaseController {
    private final WeCorpAccountService weCorpAccountService;
    private final RuoYiConfig ruoYiConfig;
    private final WeAuthCorpInfoExtendService weAuthCorpInfoExtendService;
    private final ChatRsaKeyConfig chatRsaKeyConfig;

    @Autowired
    public WeCorpAccountController(WeCorpAccountService weCorpAccountService, RuoYiConfig ruoYiConfig, WeAuthCorpInfoExtendService weAuthCorpInfoExtendService, ChatRsaKeyConfig chatRsaKeyConfig) {
        this.weCorpAccountService = weCorpAccountService;
        this.ruoYiConfig = ruoYiConfig;
        this.weAuthCorpInfoExtendService = weAuthCorpInfoExtendService;
        this.chatRsaKeyConfig = chatRsaKeyConfig;
    }


    //  @PreAuthorize("@ss.hasPermi('wechat:corp:list')")
    @GetMapping("/list")
    @Deprecated
    @ApiOperation("查询企业id相关配置列表")
    public TableDataInfo<WeCorpAccount> list(WeCorpAccount weCorpAccount) {
        startPage();
        List<WeCorpAccount> list = weCorpAccountService.list(new LambdaQueryWrapper<WeCorpAccount>()
                .eq(WeCorpAccount::getDelFlag, Constants.NORMAL_CODE)
                .like(WeCorpAccount::getCompanyName, weCorpAccount.getCompanyName()));
        list.forEach(corpAccount -> corpAccount.setIsCustomizedApp(weAuthCorpInfoExtendService.isCustomizedApp(corpAccount.getCorpId())));
        return getDataTable(list);
    }

    @GetMapping("/get")
    @ApiOperation("查询当前企业微信基础配置")
    @ApiResponses({
            @ApiResponse(code = 503, message = "没有可用的企业微信配置，请联系管理员"),
            @ApiResponse(code = 1017, message = "当前企业已授权但未启用待开发应用，无法使用该功能")
    })
    public AjaxResult<WeCorpAccount> get(){
        WeCorpAccount account = null;
        try {
            //是内部应用服务器
            if (ruoYiConfig.isInternalServer()) {
                account = weCorpAccountService.findValidWeCorpAccount();
            } else {
                account = weCorpAccountService.findValidWeCorpAccount(LoginTokenService.getLoginUser().getCorpId());
            }
        } catch (NoLoginTokenException | UserNoCorpException e) {
            if (ruoYiConfig.isInternalServer()) {
                account = weCorpAccountService.findValidWeCorpAccount();
            }
        }
        if(account == null) {
            throw new CustomException(ResultTip.TIP_NOT_AVAILABLE_CONFIG_FOUND);
        }
        // 获取公司名称
        account.setCompanyName(weCorpAccountService.getCorpName(account.getCorpId()));
        account.setIsCustomizedApp(weAuthCorpInfoExtendService.isCustomizedApp(account.getCorpId()));
        return AjaxResult.success(account);
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:corp:add')")
    @Log(title = "新增企业id相关配置", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增企业id相关配置")
    public AjaxResult<WeCorpAccount> add(@Validated @RequestBody WeCorpAccount weCorpAccount) {
        weCorpAccountService.saveCorpConfig(weCorpAccount, LoginTokenService.getLoginUser());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wechat:corp:edit')")
    @Log(title = "修改企业id相关配置", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改企业id相关配置")
    public AjaxResult<WeCorpAccount> edit(@Validated @RequestBody WeCorpAccount weCorpAccount) {
        weCorpAccountService.updateWeCorpAccount(weCorpAccount, LoginTokenService.getLoginUser());
        return AjaxResult.success();
    }



    @PreAuthorize("@ss.hasPermi('wechat:corp:startCustomerChurnNoticeSwitch')")
    @Log(title = "客户流失通知开关", businessType = BusinessType.UPDATE)
    @PutMapping("/startCustomerChurnNoticeSwitch/{status}")
    @ApiOperation("客户流失通知开关")
    @Deprecated
    public AjaxResult<WeCorpAccount> startCustomerChurnNoticeSwitch(@PathVariable String status) {
        weCorpAccountService.startCustomerChurnNoticeSwitch(LoginTokenService.getLoginUser().getCorpId(), status);
        return AjaxResult.success();
    }
    //   @PreAuthorize("@ss.hasPermi('wechat:corp:getCustomerChurnNoticeSwitch')")
    @Log(title = "客户流失通知开关查询", businessType = BusinessType.OTHER)
    @GetMapping("/getCustomerChurnNoticeSwitch")
    @ApiOperation("客户流失通知开关查询")
    @Deprecated
    public AjaxResult<String> getCustomerChurnNoticeSwitch() {
        return AjaxResult.success("操作成功", weCorpAccountService.getCustomerChurnNoticeSwitch(LoginTokenService.getLoginUser().getCorpId()));
    }

    @Log(title = "流失设置开关查询", businessType = BusinessType.OTHER)
    @GetMapping("/getCustomerLossSwitch")
    @ApiOperation("流失设置开关查询")
    public AjaxResult<CustomerLossSwitchVO> getCustomerChurnSwitch() {
        return AjaxResult.success("操作成功", weCorpAccountService.getCustomerLossSwitch(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/getChatPublicKey")
    @ApiOperation("获取会话存档加解密的公钥")
    public AjaxResult<String> getChatKey() {
        return AjaxResult.success("操作成功", chatRsaKeyConfig.getPublicKey());
    }

}
