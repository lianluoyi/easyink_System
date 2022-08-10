package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.ResultTip;
import com.easyink.wecom.domain.WeSensitive;
import com.easyink.wecom.domain.query.WeSensitiveHitQuery;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeSensitiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感词Controller
 *
 * @author admin
 * @date 2020-12-29
 */
@RestController
@RequestMapping("/wecom/sensitive")
@Api(tags = "敏感词管理")
public class WeSensitiveController extends BaseController {
    @Autowired
    private WeSensitiveService weSensitiveService;

    /**
     * 查询敏感词设置列表
     */
    @GetMapping("/list")
    @ApiOperation("查询敏感词列表")
    public TableDataInfo list(WeSensitive weSensitive) {
        startPage();
        weSensitive.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeSensitive> list = weSensitiveService.selectWeSensitiveList(weSensitive);
        return getDataTable(list);
    }

    /**
     * 获取敏感词设置详细信息
     */
    @GetMapping(value = "/{id}")
    @ApiOperation("查询敏感词详情")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(weSensitiveService.selectWeSensitiveById(id));
    }

    /**
     * 新增敏感词设置
     */
    @PreAuthorize("@ss.hasPermi('wecom:sensitive:add')")
    @Log(title = "敏感词设置", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加敏感词")
    public AjaxResult add(@Valid @RequestBody WeSensitive weSensitive) {
        weSensitive.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weSensitiveService.insertWeSensitive(weSensitive));
    }

    /**
     * 修改敏感词设置
     */
    @PreAuthorize("@ss.hasPermi('wecom:sensitive:edit')")
    @Log(title = "敏感词设置", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改敏感词")
    public AjaxResult edit(@Valid @RequestBody WeSensitive weSensitive) {
        Long id = weSensitive.getId();
        WeSensitive originData = weSensitiveService.selectWeSensitiveById(id);
        if (originData == null) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, "数据不存在");
        }
        weSensitive.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weSensitiveService.updateWeSensitive(weSensitive));
    }

    /**
     * 删除敏感词设置
     */
    @PreAuthorize("@ss.hasPermi('wecom:sensitive:remove')")
    @Log(title = "敏感词设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除敏感词")
    public AjaxResult remove(@PathVariable("ids") String ids) {
        String[] id = ids.split(",");
        Long[] idArray = new Long[id.length];
        Arrays.stream(id).map(Long::parseLong).collect(Collectors.toList()).toArray(idArray);
        return toAjax(weSensitiveService.destroyWeSensitiveByIds(idArray));
    }

    /**
     * 敏感词命中查询
     */
    @PreAuthorize("@ss.hasPermi('wecom:sensitive:list')")
    @GetMapping("/hit/list")
    @ApiOperation("敏感词命中查询")
    public TableDataInfo hitList(WeSensitiveHitQuery query) {
        return getDataTable(weSensitiveService.getHitSensitiveList(query, LoginTokenService.getLoginUser()));
    }
}
