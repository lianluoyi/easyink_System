package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeGroupCodeActual;
import com.easywecom.wecom.service.WeGroupCodeActualService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实际群码接口
 * 类名： WeGroupCodeActualController
 *
 * @author 佚名
 * @date 2021/9/30 16:05
 */
@RestController
@RequestMapping("/wecom/actual")
@Api(tags = "实际群码Controller")
public class WeGroupCodeActualController extends BaseController {
    @Autowired
    private WeGroupCodeActualService weGroupCodeActualService;

    //  @PreAuthorize("@ss.hasPermi('wecom:actual:list')")
    @GetMapping("/list")
    @ApiOperation("查询实际群码列表")
    public TableDataInfo list(WeGroupCodeActual weGroupCodeActual) {
        startPage();
        List<WeGroupCodeActual> list = weGroupCodeActualService.selectWeGroupCodeActualList(weGroupCodeActual);
        list.forEach(groupCode -> {
            //如果为2099年 显示空值给前端
            if (DateUtils.isSameDay(groupCode.getEffectTime(), DateUtils.dateTime(com.easywecom.common.utils.DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE))) {
                groupCode.setEffectTime(null);
            }
        });
        return getDataTable(list);
    }

    //   @PreAuthorize("@ss.hasPermi('wecom:actual:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取实际群码详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        WeGroupCodeActual weGroupCodeActual = weGroupCodeActualService.selectWeGroupCodeActualById(id);
        if (StringUtils.isNull(weGroupCodeActual)) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, "数据不存在");
        }
        return AjaxResult.success(weGroupCodeActual);
    }

    //  @PreAuthorize("@ss.hasPermi('wecom:actual:add')")
    @Log(title = "实际群码", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增实际群码查询")
    @Deprecated
    public AjaxResult add(@Validated @RequestBody WeGroupCodeActual weGroupCodeActual) {
        return toAjax(weGroupCodeActualService.insertWeGroupCodeActual(weGroupCodeActual));
    }

    //  @PreAuthorize("@ss.hasPermi('wecom:actual:edit')")
    @Log(title = "实际群码", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改实际群码")
    public AjaxResult edit(@RequestBody WeGroupCodeActual weGroupCodeActual) {
        WeGroupCodeActual original = weGroupCodeActualService.selectWeGroupCodeActualById(weGroupCodeActual.getId());
        if (StringUtils.isNull(original)) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, "数据不存在");
        }
        return toAjax(weGroupCodeActualService.updateWeGroupCodeActual(weGroupCodeActual));
    }

    //   @PreAuthorize("@ss.hasPermi('wecom:actual:remove')")
    @Log(title = "实际群码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除实际群码")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(weGroupCodeActualService.deleteWeGroupCodeActualByIds(ids));
    }


}
