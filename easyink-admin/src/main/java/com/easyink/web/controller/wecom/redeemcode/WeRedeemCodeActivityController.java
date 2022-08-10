package com.easyink.web.controller.wecom.redeemcode;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeActivityDeleteDTO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeActivityService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 兑换码活动Controller
 *
 * @author wx
 * @date 2022/7/4 14:09
 */
@Api(tags = "兑换码活动Controller")
@RestController
@RequestMapping("/wecom/redeem")
public class WeRedeemCodeActivityController extends BaseController {


    private final WeRedeemCodeActivityService weRedeemCodeActivityService;
    private final WeRedeemCodeService weRedeemCodeService;

    @Autowired
    public WeRedeemCodeActivityController(WeRedeemCodeActivityService weRedeemCodeActivityService, WeRedeemCodeService weRedeemCodeService) {
        this.weRedeemCodeActivityService = weRedeemCodeActivityService;
        this.weRedeemCodeService = weRedeemCodeService;
    }

    //@PreAuthorize("@ss.hasPermi('wecom:redeemcode:add')")
    @Log(title = "兑换码活动", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增兑换码活动")
    @PreAuthorize("@ss.hasPermi('redeeomCode:activity:add')")
    public <T> AjaxResult<T> add(@RequestBody @Validated WeRedeemCodeActivityDTO weRedeemCodeActivityDTO) {
        weRedeemCodeActivityDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weRedeemCodeActivityService.saveReemCodeActivity(weRedeemCodeActivityDTO));
    }

    @GetMapping("/list")
    @ApiOperation("查询兑换码活动列表")
    public TableDataInfo<WeRedeemCodeActivityVO> list(WeRedeemCodeActivityDTO weRedeemCodeActivityDTO) {
        weRedeemCodeActivityDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        startPage();
        List<WeRedeemCodeActivityVO> list = weRedeemCodeActivityService.getReemCodeActivityList(weRedeemCodeActivityDTO);
        return getDataTable(list);
    }

    //@PreAuthorize("@ss.hasPermi('wecom:redeemcode:edit')")
    @Log(title = "兑换码活动", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("修改兑换码活动")
    @PreAuthorize("@ss.hasPermi('redeeomCode:activity:edit')")
    public <T> AjaxResult<T> edit(@Validated @RequestBody WeRedeemCodeActivityDTO weRedeemCodeActivityDTO) {
        weRedeemCodeActivityDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weRedeemCodeActivityService.updateWeRedeemCodeActivity(weRedeemCodeActivityDTO);
        return AjaxResult.success();
    }

    //@PreAuthorize("@ss.hasPermi('wecom:redeemcode:remove')")
    @Log(title = "兑换码活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @ApiOperation("删除兑换码活动")
    @PreAuthorize("@ss.hasPermi('redeeomCode:activity:del')")
    public <T> AjaxResult<T> remove(@Validated @RequestBody WeRedeemCodeActivityDeleteDTO deleteDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(weRedeemCodeActivityService.batchRemoveRedeemCodeActivity(corpId, deleteDTO));
    }

    @GetMapping("/get")
    @ApiOperation("获取兑换码活动详情")
    public AjaxResult<WeRedeemCodeActivityVO> get(@RequestParam("id") Long id) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weRedeemCodeActivityService.getRedeemCodeActivity(corpId, id));
    }

}