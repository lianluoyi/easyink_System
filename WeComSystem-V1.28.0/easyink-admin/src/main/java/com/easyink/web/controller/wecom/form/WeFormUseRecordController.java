package com.easyink.web.controller.wecom.form;


import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.vo.form.FormSimpleInfoVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.form.WeFormUseRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 表单使用记录表(WeFormUseRecord)表控制层
 *
 * @author tigger
 * @since 2023-01-13 10:12:29
 */
@RestController
@RequestMapping("/wecom/form/use/record")
public class WeFormUseRecordController extends BaseController {

    private final WeFormUseRecordService weFormUseRecordService;

    @Lazy
    public WeFormUseRecordController(WeFormUseRecordService weFormUseRecordService) {
        this.weFormUseRecordService = weFormUseRecordService;
    }

    @ApiOperation("表单使用记录分页")
    @GetMapping("/page/record")
    public TableDataInfo<FormSimpleInfoVO> page(@ApiParam("表单所属类别") @RequestParam(value = "sourceType") Integer sourceType) {
        return getDataTable(this.weFormUseRecordService.pageRecord(sourceType, LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation("新增侧边栏表单使用记录")
    @GetMapping("/add")
    public AjaxResult add(
            @ApiParam("表单id") @RequestParam(value = "formId") Long formId,
            @ApiParam("员工id") @RequestParam(value = "userId") String userId,
            @ApiParam("客户id") @RequestParam(value = "externalUserId") String externalUserId,
            @ApiParam("企业id") @RequestParam(value = "corpId") String corpId) {
        weFormUseRecordService.saveRecord(formId, userId, externalUserId, corpId);
        return AjaxResult.success();
    }
}

