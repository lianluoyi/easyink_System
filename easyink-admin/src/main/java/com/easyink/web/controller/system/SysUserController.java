package com.easyink.web.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import com.easyink.common.annotation.Log;
import com.easyink.common.constant.UserConstants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.SysRole;
import com.easyink.common.core.domain.entity.SysUser;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.mapper.SysUserMapper;
import com.easyink.common.service.ISysRoleService;
import com.easyink.common.service.ISysUserService;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.SecurityUtils;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.service.WeUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

import static com.easyink.common.constant.WeConstans.USER_ID_PREFIX;

/**
 * 用户信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/user")
@Api(tags = "用户信息")
@Validated
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private WeUserService weUserService;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private WeUserMapper weUserMapper;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public TableDataInfo list(SysUser user) {
        startPage();

        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping("/export")
    @ApiOperation("导出数据")
    public AjaxResult export(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    @ApiOperation("导入数据")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @GetMapping("/importTemplate")
    @ApiOperation("导入模板")
    public AjaxResult importTemplate() {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation("根据用户编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll(LoginTokenService.getLoginUser().getCorpId());
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        if (StringUtils.isNotNull(userId)) {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(LoginTokenService.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改用户")
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(LoginTokenService.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }


    @ApiOperation("查找当前登录用户")
    @GetMapping("/findCurrentLoginUser")
    public AjaxResult findCurrentLoginUser(HttpServletRequest request) {
        String userId = "";
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (null != loginUser) {
            SysUser user = loginUser.getUser();
            if (null != user) {
                List<WeUser> weUsers = weUserService.selectWeUserList(WeUser.builder()
                        .mobile(user.getPhonenumber())
                        .build());
                if (CollectionUtil.isNotEmpty(weUsers)) {
                    userId = weUsers.get(0).getUserId();

                }
            }

        }
        return AjaxResult.success(userId);
    }

    @ApiOperation(("修改当前用户的UI主题颜色"))
    @GetMapping("/changeUiColor")
    public AjaxResult changeUiColor(@ApiParam(value = "颜色(需要urlEncode后传入)") @NotBlank(message = "颜色不能为空") String color) {
        try {
            color = URLDecoder.decode(color,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return AjaxResult.error("请传入正确的颜色格式");
        }
        LoginUser loginUser = LoginTokenService.getLoginUser();
        // 账密登录管理员更换主题颜色
        if (loginUser.isSuperAdmin()) {
            sysUserMapper.updateUser(
                    SysUser.builder()
                            .userId(loginUser.getUser().getUserId())
                            .uiColor(color)
                            .build()
            );
        } else if (!loginUser.isSuperAdmin() && loginUser.getWeUser() != null) {
            // 扫码用户更换主题颜色
            WeUser weUser = loginUser.getWeUser();
            weUser.setUiColor(color);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(weUser.getName()) && weUser.getName().startsWith(USER_ID_PREFIX)) {
                weUser.setName(null);
            }
            weUserMapper.updateWeUser(weUser);
        }
        // 刷新缓存
        LoginTokenService.refreshDataScope();
        return AjaxResult.success();
    }



}
