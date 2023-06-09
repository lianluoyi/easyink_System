package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.system.SysNotice;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 公告 信息操作处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/notice")
@Api(tags = "信息操作处理")
@Deprecated
public class SysNoticeController extends BaseController {


    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    @ApiOperation("删除通知公告")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        return AjaxResult.success();
    }
}
