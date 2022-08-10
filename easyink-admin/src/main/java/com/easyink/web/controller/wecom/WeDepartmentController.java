package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.service.ISysDeptService;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeDepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业微信组织架构相关Controller
 *
 * @author admin
 * @date 2020-09-01
 */
@Api(value = "WeDepartmentController",tags = "微信部门相关接口")
@RestController
@RequestMapping("/wecom/department")
public class WeDepartmentController extends BaseController {
    @Autowired
    private WeDepartmentService weDepartmentService;
    @Autowired
    private ISysDeptService sysDeptService;


    /**
     * 查询企业微信组织架构相关列表
     */
    //  @PreAuthorize("@ss.hasPermi('contacts:organization:list')")
    @GetMapping("/list")
    @ApiOperation("获取部门列表")
    public AjaxResult list(Integer isActivate) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        List<WeDepartment> list = weDepartmentService.selectWeDepartmentList(loginUser.getCorpId(), isActivate, loginUser);
        return AjaxResult.success(list);
    }


    /**
     * 新增企业微信组织架构相关
     */
    // @PreAuthorize("@ss.hasPermi('contacts:organization:addMember')")
    @Log(title = "企业微信组织架构相关", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加部门")
    public AjaxResult add(@Validated @RequestBody WeDepartment weDepartment) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        weDepartment.setCorpId(loginUser.getCorpId());
        if (weDepartmentService.insertWeDepartment(weDepartment)) {
            // 刷新loginUser中的部门数据权限信息
            LoginTokenService.refreshDataScope();
            return AjaxResult.success();
        }
        return AjaxResult.error();

    }

    /**
     * 修改企业微信组织架构相关
     */
    //  @PreAuthorize("@ss.hasPermi('contacts:organization:editDep')")
    @Log(title = "企业微信组织架构相关", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("更新部门")
    public AjaxResult edit(@Validated @RequestBody WeDepartment weDepartment) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        weDepartment.setCorpId(loginUser.getCorpId());
        weDepartmentService.updateWeDepartment(weDepartment);
        // 修改员工部门后刷新 登录用户的数据权限
        LoginTokenService.refreshDataScope();
        return AjaxResult.success();
    }

    /**
     * 删除企业微信组织架构相关
     */
    //  @PreAuthorize("@ss.hasPermi('wecom:department:remove')")
    @Log(title = "企业微信组织架构相关", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除企业微信组织架构相关")
    public AjaxResult remove(@PathVariable String[] ids) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        weDepartmentService.deleteWeDepartmentByIds(loginUser.getCorpId(), ids);
        LoginTokenService.refreshDataScope();
        return AjaxResult.success();
    }

}
