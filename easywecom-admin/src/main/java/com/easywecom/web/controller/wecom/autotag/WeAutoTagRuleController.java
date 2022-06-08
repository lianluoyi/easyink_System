package com.easywecom.web.controller.wecom.autotag;


import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.wecom.domain.dto.autotag.TagRuleBatchStatusDTO;
import com.easywecom.wecom.domain.dto.autotag.TagRuleDeleteDTO;
import com.easywecom.wecom.domain.dto.autotag.customer.AddCustomerTagRuleDTO;
import com.easywecom.wecom.domain.dto.autotag.customer.UpdateCustomerTagRuleDTO;
import com.easywecom.wecom.domain.dto.autotag.group.AddGroupTagRuleDTO;
import com.easywecom.wecom.domain.dto.autotag.group.UpdateGroupTagRuleDTO;
import com.easywecom.wecom.domain.dto.autotag.keyword.AddKeywordTagRuleDTO;
import com.easywecom.wecom.domain.dto.autotag.keyword.UpdateKeywordTagRuleDTO;
import com.easywecom.wecom.domain.query.autotag.RuleInfoQuery;
import com.easywecom.wecom.domain.query.autotag.TagRuleQuery;
import com.easywecom.wecom.domain.vo.autotag.TagRuleListVO;
import com.easywecom.wecom.domain.vo.autotag.customer.TagRuleCustomerInfoVO;
import com.easywecom.wecom.domain.vo.autotag.group.TagRuleGroupInfoVO;
import com.easywecom.wecom.domain.vo.autotag.keyword.TagRuleKeywordInfoVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 标签规则表(WeAutoTagRule)表控制层
 *
 * @author tigger
 * @since 2022-02-27 15:52:39
 */
@Api(tags = "自动标签规则")
@RestController
@RequestMapping("wecom/auto/tag/rule")
public class WeAutoTagRuleController extends BaseController {

    @Autowired
    private WeAutoTagRuleService weAutoTagRuleService;

    @PreAuthorize("@ss.hasPermi('customerManage:autoLabel:list')")
    @ApiOperation("关键词规则列表")
    @PostMapping("keyword/list")
    public TableDataInfo<TagRuleListVO> listKeyword(@RequestBody TagRuleQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<TagRuleListVO> list = weAutoTagRuleService.listKeyword(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('customerManage:autoLabel:list')")
    @ApiOperation("群规则列表")
    @PostMapping("group/list")
    public TableDataInfo<TagRuleListVO> listGroup(@RequestBody TagRuleQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<TagRuleListVO> list = weAutoTagRuleService.listGroup(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('customerManage:autoLabel:list')")
    @ApiOperation("新客规则列表")
    @PostMapping("customer/list")
    public TableDataInfo<TagRuleListVO> listCustomer(@RequestBody TagRuleQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<TagRuleListVO> list = weAutoTagRuleService.listCustomer(query);
        return getDataTable(list);
    }

    /**
     * 详情
     */
    @ApiOperation("关键词规则详情")
    @GetMapping("keyword/info")
    public AjaxResult<TagRuleKeywordInfoVO> keywordInfo(RuleInfoQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weAutoTagRuleService.keywordInfo(query));
    }

    @ApiOperation("群规则详情")
    @GetMapping("group/info")
    public AjaxResult<TagRuleGroupInfoVO> groupInfo(RuleInfoQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weAutoTagRuleService.groupInfo(query));
    }

    @ApiOperation("新客规则详情")
    @GetMapping("customer/info")
    public AjaxResult<TagRuleCustomerInfoVO> customerInfo(RuleInfoQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weAutoTagRuleService.customerInfo(query));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:add')")
    @ApiOperation("关键词规则新增")
    @PostMapping("keyword/add")
    public AjaxResult keywordAdd(@RequestBody AddKeywordTagRuleDTO addKeywordTagRuleDTO) {
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            addKeywordTagRuleDTO.setCreateBy(LoginTokenService.getUsername());
        } else {
            addKeywordTagRuleDTO.setCreateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        addKeywordTagRuleDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weAutoTagRuleService.keywordAdd(addKeywordTagRuleDTO));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:add')")
    @ApiOperation("入群规则新增")
    @PostMapping("group/add")
    public AjaxResult groupAdd(@RequestBody AddGroupTagRuleDTO addGroupTagRuleDTO) {
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            addGroupTagRuleDTO.setCreateBy(LoginTokenService.getUsername());
        } else {
            addGroupTagRuleDTO.setCreateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        addGroupTagRuleDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weAutoTagRuleService.groupAdd(addGroupTagRuleDTO));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:add')")
    @ApiOperation("新客规则新增")
    @PostMapping("customer/add")
    public AjaxResult customerAdd(@RequestBody AddCustomerTagRuleDTO addCustomerTagRuleDTO) {
        addCustomerTagRuleDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            addCustomerTagRuleDTO.setCreateBy(LoginTokenService.getUsername());
        } else {
            addCustomerTagRuleDTO.setCreateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        return toAjax(weAutoTagRuleService.customerAdd(addCustomerTagRuleDTO));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:edit')")
    @ApiOperation("关键词规则修改")
    @PutMapping("keyword/edit")
    public AjaxResult keywordEdit(@RequestBody UpdateKeywordTagRuleDTO updateKeywordTagRuleDTO) {
        return toAjax(weAutoTagRuleService.keywordEdit(updateKeywordTagRuleDTO, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:edit')")
    @ApiOperation("入群规则修改")
    @PutMapping("group/edit")
    public AjaxResult groupEdit(@RequestBody UpdateGroupTagRuleDTO updateGroupTagRuleDTO) {
        return toAjax(weAutoTagRuleService.groupEdit(updateGroupTagRuleDTO, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:edit')")
    @ApiOperation("新客规则修改")
    @PutMapping("customer/edit")
    public AjaxResult customerEdit(@RequestBody UpdateCustomerTagRuleDTO updateCustomerTagRuleDTO) {
        return toAjax(weAutoTagRuleService.customerEdit(updateCustomerTagRuleDTO, LoginTokenService.getLoginUser().getCorpId()));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('wecom:autotag:del')")
    @ApiOperation("关键词规则删除")
    @DeleteMapping("keyword/remove")
    public AjaxResult keywordDelete(@Validated @RequestBody TagRuleDeleteDTO deleteDTO) {
        return toAjax(weAutoTagRuleService.keywordDelete(deleteDTO, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:del')")
    @ApiOperation("入群规则删除")
    @DeleteMapping("group/remove")
    public AjaxResult groupDelete(@Validated @RequestBody TagRuleDeleteDTO deleteDTO) {
        return toAjax(weAutoTagRuleService.groupDelete(deleteDTO, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:del')")
    @ApiOperation("新客规则删除")
    @DeleteMapping("customer/remove")
    public AjaxResult customerDelete(@Validated @RequestBody TagRuleDeleteDTO deleteDTO) {
        return toAjax(weAutoTagRuleService.customerDelete(deleteDTO, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:autotag:enable')")
    @ApiOperation("批量启用禁用")
    @PostMapping("batch/status")
    public AjaxResult batchStatus(@Validated @RequestBody TagRuleBatchStatusDTO tagRuleBatchStatusDTO) {
        tagRuleBatchStatusDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weAutoTagRuleService.batchStatus(tagRuleBatchStatusDTO);
        return AjaxResult.success();
    }


}

