package com.easyink.web.controller.wecom.form;


import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.form.*;
import com.easyink.wecom.domain.query.form.FormQuery;
import com.easyink.wecom.domain.vo.form.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.form.WeFormService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 表单表(WeForm)表控制层
 *
 * @author tigger
 * @since 2023-01-09 15:00:44
 */
@Validated
@RestController
@RequestMapping("/wecom/form")
@Slf4j
public class WeFormController extends BaseController {

    @Autowired
    private WeFormService weFormService;

    @ApiOperation("新增表单")
    @PostMapping("/add")
    @Log(title = "智能表单", businessType = BusinessType.INSERT)
    public AjaxResult insert(@Validated @RequestBody FormAddRequestDTO addDTO) {
        this.weFormService.saveForm(addDTO, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }


    @ApiOperation("编辑表单")
    @PostMapping("/edit")
    @Log(title = "智能表单", businessType = BusinessType.UPDATE)
    public AjaxResult update(@Validated @RequestBody FormUpdateRequestDTO updateDTO) {
        this.weFormService.updateForm(updateDTO, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }


    @ApiOperation("删除表单")
    @PostMapping("/remove")
    @Log(title = "智能表单", businessType = BusinessType.DELETE)
    public AjaxResult delete(@Validated @RequestBody DeleteFormDTO deleteDTO) {
        this.weFormService.deleteForm(deleteDTO.getIdList(), LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @ApiOperation("表单分页")
    @GetMapping("/page")
    public TableDataInfo<FormPageVO> page(@Valid FormQuery formQuery) {
        return getDataTable(this.weFormService.getPage(formQuery, LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("表单编辑详情")
    @GetMapping("/edit/detail")
    public AjaxResult<WeFormEditDetailVO> editDetail(@ApiParam(value = "表单id") @RequestParam(value = "id") Long id) {
        return AjaxResult.success(this.weFormService.getEditDetail(id, LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("表单详情")
    @GetMapping("/detail")
    public AjaxResult<FormDetailViewVO> detail(@ApiParam(value = "表单id") @RequestParam(value = "id") Long id) {
        return AjaxResult.success(this.weFormService.getDetail(id, LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("表单数据总览")
    @GetMapping("/total/view")
    public AjaxResult<FormTotalView> totalView(@ApiParam(value = "表单id") @RequestParam(value = "id") Long id) {
        return AjaxResult.success(this.weFormService.totalView(id, LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("批量修改分组")
    @PostMapping("/updateBatch/group")
    @Log(title = "智能表单", businessType = BusinessType.UPDATE)
    public AjaxResult updateBatchGroup(@Validated @RequestBody BatchUpdateGroupDTO batchDTO) {
        weFormService.updateBatchGroup(batchDTO, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @ApiOperation("推广")
    @GetMapping("/promotional")
    public AjaxResult<PromotionalVO> promotional(@ApiParam(value = "表单id") @RequestParam("id") Long id, HttpServletResponse response) {
        return AjaxResult.success(weFormService.promotion(id, response)
);
    }

    @ApiOperation("启用禁用表单")
    @PostMapping("/enableForm")
    @Log(title = "智能表单", businessType = BusinessType.UPDATE)
    public AjaxResult<PromotionalVO> enableForm(
            @ApiParam(value = "表单id") @RequestParam("id") Long id,
            @ApiParam(value = "启用禁用状态") @RequestParam("enableFlag") Boolean enableFlag
    ) {
        weFormService.enableForm(id, enableFlag, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @ApiOperation("中间页获取表单内容，同时异步记录点击事件")
    @GetMapping("/getContent")
    public AjaxResult getContent(@ApiParam("表单id") Long formId,
                                 @ApiParam("员工id") String userId,
                                 @ApiParam("用户的公众号openid") String openId,
                                 @ApiParam("表单渠道id") Integer channelType) {
        log.info("[点击表单] formId:{}, userId:{}, openId:{}, channelType:{}", formId, userId, openId, channelType);
        return AjaxResult.success(weFormService.getContent(formId, userId, openId, channelType));
    }

    @ApiOperation("中间页提交表单结果")
    @PostMapping("/commit")
    @Log(title = "智能表单", businessType = BusinessType.UPDATE)
    public AjaxResult commit(@RequestBody @Validated FormCommitDTO formCommitDTO) {
        weFormService.commit(formCommitDTO);
        return AjaxResult.success("success");
    }

    @ApiOperation("侧边栏获取表单URL")
    @GetMapping("/genFormUrl")
    public AjaxResult<String> genFormUrl(
            @ApiParam("表单id") @RequestParam("formId") Long formId,
            @ApiParam("员工id") @RequestParam("userId") String userId,
            @ApiParam("渠道类型") @RequestParam("channelType") Integer channelType
    ) {
        return AjaxResult.success("success", weFormService.sideBarGenFormUrl(formId, userId, channelType));
    }

}

