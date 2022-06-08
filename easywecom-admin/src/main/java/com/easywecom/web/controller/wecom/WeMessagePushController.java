package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.utils.poi.ExcelUtil;
import com.easywecom.wecom.domain.WeMessagePush;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeMessagePushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息发送的Controller
 *
 * @author admin
 * @date 2020-10-28
 */
@RestController
@RequestMapping("/wecom/push")
@Api(tags = "消息发送的Controller")
public class WeMessagePushController extends BaseController {

    @Autowired
    private WeMessagePushService weMessagePushService;

    //  @PreAuthorize("@ss.hasPermi('system:push:list')")
    @GetMapping("/list")
    @ApiOperation("查询消息发送的列表")
    public TableDataInfo list(WeMessagePush weMessagePush) {
        startPage();
        List<WeMessagePush> list = weMessagePushService.selectWeMessagePushList(weMessagePush);
        return getDataTable(list);
    }

    // @PreAuthorize("@ss.hasPermi('system:push:export')")
    @Log(title = "消息发送的", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation("导出消息发送的列表")
    public AjaxResult export(WeMessagePush weMessagePush) {
        List<WeMessagePush> list = weMessagePushService.selectWeMessagePushList(weMessagePush);
        ExcelUtil<WeMessagePush> util = new ExcelUtil<>(WeMessagePush.class);
        return util.exportExcel(list, "push");
    }

    //  @PreAuthorize("@ss.hasPermi('system:push:query')")
    @GetMapping(value = "/{messagePushId}")
    @ApiOperation("获取消息发送的详细信息")
    public AjaxResult getInfo(@PathVariable("messagePushId") Long messagePushId) {
        return AjaxResult.success(weMessagePushService.selectWeMessagePushById(messagePushId));
    }

    //  @PreAuthorize("@ss.hasPermi('system:push:add')")
    @Log(title = "消息发送的", businessType = BusinessType.INSERT)
    @PostMapping(value = "add")
    @ApiOperation("新增消息发送")
    public AjaxResult add(@RequestBody WeMessagePush weMessagePush) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        weMessagePushService.insertWeMessagePush(weMessagePush, corpId);
        return AjaxResult.success();
    }


    //  @PreAuthorize("@ss.hasPermi('system:push:remove')")
    @Log(title = "消息发送的", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messagePushIds}")
    @ApiOperation("删除消息发送")
    public AjaxResult remove(@PathVariable Long[] messagePushIds) {
        return toAjax(weMessagePushService.deleteWeMessagePushByIds(messagePushIds));
    }
}
