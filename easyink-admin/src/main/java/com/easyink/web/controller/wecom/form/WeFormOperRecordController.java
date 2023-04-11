package com.easyink.web.controller.wecom.form;


import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.vo.form.FormCustomerOperRecordVO;
import com.easyink.wecom.domain.vo.form.FormOperRecordDetailVO;
import com.easyink.wecom.domain.vo.form.FormUserSendRecordVO;
import com.easyink.wecom.service.form.WeFormOperRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 智能表单操作记录表(WeFormOperRecord)表控制层
 *
 * @author wx
 * @since 2023-01-13 11:49:44
 */
@Api(value = "WeFormOperRecordController", tags = "智能表单操作记录Controller")
@RestController
@RequestMapping("/wecom/form/record")
@RequiredArgsConstructor
public class WeFormOperRecordController extends BaseController {

    private final WeFormOperRecordService weFormOperRecordService;

    @ApiOperation("客户操作记录")
    @GetMapping("/getCustomerOperRecord")
    public TableDataInfo<FormCustomerOperRecordVO> getCustomerOperRecord(@ApiParam("表单id") @RequestParam("formId") @NotNull Long formId,
                                                                         @ApiParam("时间类型，1：点击时间，2：提交时间") Integer timeType,
                                                                         @ApiParam("开始时间") Date beginTime,
                                                                         @ApiParam("结束时间") Date endTime,
                                                                         @ApiParam("客户名称") String customerName,
                                                                         @ApiParam("点击渠道") Integer channelType) {
        startPage();
        return getDataTable(weFormOperRecordService.getCustomerOperRecord(formId, timeType, beginTime, endTime, customerName, channelType));
    }

    @ApiOperation("员工发送记录")
    @GetMapping("/getUserSendRecord")
    public TableDataInfo<FormUserSendRecordVO> getUserSendRecord(@ApiParam("表单id") @RequestParam("formId") @NotNull Long formId,
                                                                 @ApiParam("时间类型，1：点击时间，2：提交时间") Integer timeType,
                                                                 @ApiParam("开始时间") Date beginTime,
                                                                 @ApiParam("结束时间") Date endTime,
                                                                 @ApiParam("员工姓名") String userName) {
        startPage();
        return getDataTable(weFormOperRecordService.getUserSendRecord(formId, timeType, beginTime, endTime, userName));
    }

    @ApiOperation("获取表单详情")
    @GetMapping("/getFormResult")
    public AjaxResult<FormOperRecordDetailVO> getFormResult(@ApiParam("表单id") @RequestParam("formId") @NotNull Long formId) {
        return AjaxResult.success(weFormOperRecordService.getFormResult(formId));
    }

    @ApiOperation("导出客户操作记录")
    @GetMapping("/exportCustomerOperRecord")
    public AjaxResult exportCustomerOperRecord(@ApiParam("表单id") @RequestParam("formId") @NotNull Long formId,
                                               @ApiParam("时间类型，1：点击时间，2：提交时间") Integer timeType,
                                               @ApiParam("开始时间") Date beginTime,
                                               @ApiParam("结束时间") Date endTime,
                                               @ApiParam("客户名称") String customerName,
                                               @ApiParam("点击渠道") Integer channelType) {
        return AjaxResult.success(weFormOperRecordService.exportCustomerOperRecord(formId, timeType, beginTime, endTime, customerName, channelType));
    }

    @ApiOperation("导出员工发送记录")
    @GetMapping("/exportUserSendRecord")
    public AjaxResult exportUserSendRecord(@ApiParam("表单id") @RequestParam("formId") @NotNull Long formId,
                                           @ApiParam("时间类型，1：点击时间，2：提交时间") Integer timeType,
                                           @ApiParam("开始时间") Date beginTime,
                                           @ApiParam("结束时间") Date endTime,
                                           @ApiParam("员工姓名") String userName) {
        return AjaxResult.success(weFormOperRecordService.exportUserSendRecord(formId, timeType, beginTime, endTime, userName));
    }

}

