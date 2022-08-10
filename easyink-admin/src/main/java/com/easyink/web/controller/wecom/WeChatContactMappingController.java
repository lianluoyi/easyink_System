package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.WeChatContactMapping;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeChatContactMappingService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天关系映射Controller
 *
 * @author admin
 * @date 2020-12-27
 */
@Api(tags = "聊天关系映射Controller")
@RestController
@RequestMapping("/chat/mapping")
public class WeChatContactMappingController extends BaseController {
    @Autowired
    private WeChatContactMappingService weChatContactMappingService;

    /**
     * 查询聊天关系映射列表
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:list')")
    @ApiOperation(value = "查询聊天关系映射列表", httpMethod = "GET")
    @GetMapping("/list")
    public TableDataInfo<WeChatContactMapping> list(WeChatContactMapping weChatContactMapping) {
        startPage();
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        if (StringUtils.isBlank(corpId)) {
            return getDataTable(new ArrayList<>());
        }
        weChatContactMapping.setCorpId(corpId);
        List<WeChatContactMapping> list = weChatContactMappingService.selectWeChatContactMappingList(weChatContactMapping);
        return getDataTable(list);
    }

    /**
     * 按客户查询关系映射列表
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:listByCustomer')")
    @Deprecated
    @ApiOperation(value = "按客户查询关系映射列表", httpMethod = "GET")
    @GetMapping("/listByCustomer")
    public TableDataInfo<PageInfo<WeCustomer>> listByCustomer() {
        return getDataTable(new ArrayList<>());
    }

    /**
     * 导出聊天关系映射列表
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:export')")
    @Deprecated
    @Log(title = "聊天关系映射", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation("导出聊天关系映射列表")
    public AjaxResult export(WeChatContactMapping weChatContactMapping) {
        weChatContactMapping.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeChatContactMapping> list = weChatContactMappingService.selectWeChatContactMappingList(weChatContactMapping);
        ExcelUtil<WeChatContactMapping> util = new ExcelUtil<>(WeChatContactMapping.class);
        return util.exportExcel(list, "mapping");
    }

    /**
     * 获取聊天关系映射详细信息
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:query')")
    @Deprecated
    @GetMapping(value = "/{id}")
    @ApiOperation("获取聊天关系映射详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(weChatContactMappingService.selectWeChatContactMappingById(id));
    }

    /**
     * 新增聊天关系映射
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:add')")
    @Deprecated
    @Log(title = "聊天关系映射", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增聊天关系映射")
    public AjaxResult add(@RequestBody WeChatContactMapping weChatContactMapping) {
        return toAjax(weChatContactMappingService.insertWeChatContactMapping(weChatContactMapping));
    }

    /**
     * 修改聊天关系映射
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:edit')")
    @Deprecated
    @Log(title = "聊天关系映射", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改聊天关系映射")
    public AjaxResult edit(@RequestBody WeChatContactMapping weChatContactMapping) {
        return toAjax(weChatContactMappingService.updateWeChatContactMapping(weChatContactMapping));
    }

    /**
     * 删除聊天关系映射
     */
//    @PreAuthorize("@ss.hasPermi('chat:mapping:remove')")
    @Deprecated
    @Log(title = "聊天关系映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除聊天关系映射")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(weChatContactMappingService.deleteWeChatContactMappingByIds(ids));
    }
}
