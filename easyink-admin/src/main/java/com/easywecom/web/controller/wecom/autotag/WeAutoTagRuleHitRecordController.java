package com.easywecom.web.controller.wecom.autotag;


import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.wecom.domain.query.autotag.CustomerTagRuleRecordQuery;
import com.easywecom.wecom.domain.query.autotag.GroupTagRuleRecordQuery;
import com.easywecom.wecom.domain.query.autotag.TagRuleRecordKeywordDetailQuery;
import com.easywecom.wecom.domain.query.autotag.TagRuleRecordQuery;
import com.easywecom.wecom.domain.vo.autotag.record.CustomerCountVO;
import com.easywecom.wecom.domain.vo.autotag.record.customer.CustomerTagRuleRecordVO;
import com.easywecom.wecom.domain.vo.autotag.record.group.GroupTagRuleRecordVO;
import com.easywecom.wecom.domain.vo.autotag.record.keyword.KeywordRecordDetailVO;
import com.easywecom.wecom.domain.vo.autotag.record.keyword.KeywordTagRuleRecordVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitCustomerRecordService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitGroupRecordService;
import com.easywecom.wecom.service.autotag.WeAutoTagRuleHitKeywordRecordService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 客户打标签记录表(WeAutoTagRuleHitRecord)表控制层
 *
 * @author tigger
 * @since 2022-02-27 15:52:40
 */
@Api(tags = "自动标签记录")
@RestController
@RequestMapping("wecom/auto/tag/hit/record")
public class WeAutoTagRuleHitRecordController extends BaseController {

    @Autowired
    private WeAutoTagRuleHitKeywordRecordService weAutoTagRuleHitKeywordRecordService;
    @Autowired
    private WeAutoTagRuleHitGroupRecordService weAutoTagRuleHitGroupRecordService;
    @Autowired
    private WeAutoTagRuleHitCustomerRecordService weAutoTagRuleHitCustomerRecordService;


    /**
     * 列表
     */
    @ApiOperation("关键词规则记录列表")
    @PostMapping("keyword/list")
    public TableDataInfo<KeywordTagRuleRecordVO> listKeywordRecord(@Validated @RequestBody TagRuleRecordQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<KeywordTagRuleRecordVO> list = weAutoTagRuleHitKeywordRecordService.listKeywordRecord(query);
        return getDataTable(list);
    }

    /**
     * 列表
     */
    @ApiOperation("触发关键词详情列表")
    @PostMapping("keyword/detail")
    public TableDataInfo<KeywordRecordDetailVO> listKeywordDetail(@Validated @RequestBody TagRuleRecordKeywordDetailQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<KeywordRecordDetailVO> list = weAutoTagRuleHitKeywordRecordService.listKeywordDetail(query);
        return getDataTable(list);
    }


    /**
     * 列表
     */
    @ApiOperation("群规则记录列表")
    @PostMapping("group/list")
    public TableDataInfo<GroupTagRuleRecordVO> listGroupRecord(@Validated @RequestBody GroupTagRuleRecordQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<GroupTagRuleRecordVO> list = weAutoTagRuleHitGroupRecordService.listGroupRecord(query);
        return getDataTable(list);
    }


    @ApiOperation("新客规则记录列表")
    @PostMapping("customer/list")
    public TableDataInfo<CustomerTagRuleRecordVO> listCustomerRecord(@RequestBody CustomerTagRuleRecordQuery query) {
        query.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageHelper.startPage(query.getPageNum(), query.getPageSize(), null);
        List<CustomerTagRuleRecordVO> list = weAutoTagRuleHitCustomerRecordService.listCustomerRecord(query);
        return getDataTable(list);
    }

    @ApiOperation("关键词记录统计")
    @GetMapping("keyword/count/{id}")
    public AjaxResult<CustomerCountVO> keywordCount(@PathVariable("id") Long ruleId) {
        CustomerCountVO countVO = weAutoTagRuleHitKeywordRecordService.keywordCustomerCount(ruleId, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(countVO);
    }

    @ApiOperation("群记录统计")
    @GetMapping("group/count/{id}")
    public AjaxResult<CustomerCountVO> groupCount(@PathVariable("id") Long ruleId) {
        CustomerCountVO countVO = weAutoTagRuleHitGroupRecordService.groupCustomerCount(ruleId, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(countVO);
    }

    @ApiOperation("新客记录统计")
    @GetMapping("customer/count/{id}")
    public AjaxResult<CustomerCountVO> customerCount(@PathVariable("id") Long ruleId) {
        CustomerCountVO countVO = weAutoTagRuleHitCustomerRecordService.customerCustomerCount(ruleId, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(countVO);
    }

}

