package com.easyink.web.controller.system;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.SysMenu;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginBody;
import com.easyink.common.core.domain.model.LoginResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.model.LoginUserVO;
import com.easyink.common.core.domain.system.RouterVo;
import com.easyink.common.enums.LoginTypeEnum;
import com.easyink.common.service.ISysMenuService;
import com.easyink.common.token.SysPermissionService;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.ServletUtils;
import com.easyink.wecom.domain.vo.WeInternalPreLoginParamVO;
import com.easyink.wecom.login.service.SysLoginService;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCorpAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 类名: SysLoginController
 *
 * @author: 1*+
 * @date: 2021-09-14 10:32
 */
@Api(tags = "登录接口")
@RestController
public class SysLoginController {

    private final SysLoginService loginService;
    private final ISysMenuService menuService;
    private final SysPermissionService permissionService;
    private final TokenService tokenService;
    private final WeCorpAccountService iWxCorpAccountService;

    @Autowired
    public SysLoginController(SysLoginService loginService, ISysMenuService menuService, SysPermissionService permissionService, TokenService tokenService, WeCorpAccountService iWxCorpAccountService) {
        this.loginService = loginService;
        this.menuService = menuService;
        this.permissionService = permissionService;
        this.tokenService = tokenService;
        this.iWxCorpAccountService = iWxCorpAccountService;
    }

    @ApiOperation("登录方法")
    @PostMapping("/login")
    public AjaxResult<LoginResult> login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        return AjaxResult.success(new LoginResult(token));
    }

    @ApiOperation("络客SRCM获取token")
    @GetMapping("/lockToken")
    public AjaxResult<LoginResult> lockToken(@RequestParam("corpId") String corpId, @RequestParam("userId") String userId) {
        return AjaxResult.success(loginService.getLoginToken(corpId, userId));
    }

    @ApiOperation("内部扫码登录")
    @GetMapping("/qrCodeLogin")
    public AjaxResult<LoginResult> qrCodeLogin(@ApiParam("扫码登录返回的授权码") @RequestParam("code") String code) {
        LoginResult loginResult = loginService.loginHandler(code, LoginTypeEnum.BY_SCAN.getState());
        if (StringUtils.isNotBlank(loginResult.getErrorMsg())) {
            return AjaxResult.error(loginResult.getErrorMsg());
        }
        return AjaxResult.success(loginResult);
    }

    @ApiOperation("网页登录")
    @GetMapping("/webLogin")
    public AjaxResult<LoginResult> webLogin(@ApiParam("网页登录返回的授权码") @RequestParam("code") String code) {
        LoginResult loginResult = loginService.loginHandler(code, LoginTypeEnum.BY_WEB.getState());
        if (StringUtils.isNotBlank(loginResult.getErrorMsg())) {
            return AjaxResult.error(loginResult.getErrorMsg());
        }
        return AjaxResult.success(loginResult);
    }

    @ApiOperation("三方扫码登录")
    @GetMapping("/qrCodeLogin3rd")
    public AjaxResult<LoginResult> qrCodeLogin3rd(@ApiParam("扫码登录返回的授权码") @RequestParam("authCode") String authCode) {
        LoginResult loginResult = loginService.loginHandler(authCode, LoginTypeEnum.BY_THIRD_SCAN.getState());
        loginResult.setLoginUser(null);
        if (StringUtils.isNotBlank(loginResult.getErrorMsg())) {
            return AjaxResult.error(loginResult.getErrorMsg());
        }
        return AjaxResult.success(loginResult);
    }

    @ApiOperation("登录处理器（扫码、网页登录）")
    @GetMapping("/loginHandler")
    public AjaxResult<LoginResult> loginHandler(@ApiParam("登录返回的授权码") @RequestParam("code") String code,
                                                @ApiParam("登录返回的自定义state 内部扫码：INTERNAL_SCAN_LOGIN 三方扫码：THIRD_SCAN_LOGIN 网页登录：WEB_LOGIN ") @RequestParam("state") String state) {
        LoginResult loginResult = loginService.loginHandler(code, state);
        if (StringUtils.isNotBlank(loginResult.getErrorMsg())) {
            return AjaxResult.error(loginResult.getErrorMsg());
        }
        return AjaxResult.success(loginResult);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("getInfo")
    public AjaxResult<LoginUserVO> getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(loginUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(loginUser);
        // 刷新TOKEN
        LoginTokenService.refreshDataScope();
        return AjaxResult.success(new LoginUserVO(loginUser, (HashSet) roles, (HashSet) permissions));
    }

    @ApiOperation("获取路由信息")
    @GetMapping("getRouters")
    public AjaxResult<RouterVo> getRouters() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        List<SysMenu> menus = menuService.selectMenuTreeByLoginUser(loginUser);
        List<RouterVo> routerVos = menuService.buildMenus(menus);
        // delMenuWithNoChild(routerVos);
        return AjaxResult.success(routerVos);
    }

    private void delMenuWithNoChild(List<RouterVo> routerVos) {
        for (Iterator<RouterVo> it = routerVos.iterator(); it.hasNext(); ) {
            RouterVo vo = it.next();
            if (CollectionUtils.isEmpty(vo.getChildren()) || isAllPageChildren(vo.getChildren())) {
                // 判断是否有孩子节点 且不是横栏的目录（比如客户中心）
                if (isMenu(vo) &&  !Boolean.TRUE.equals(vo.getIsFrameMenu())) {
                    // 删除该节点
                    it.remove();
                }
                if(CollectionUtils.isNotEmpty(vo.getChildren())) {
                    delMenuWithNoChild(vo.getChildren());
                }
            } else {
                delMenuWithNoChild(vo.getChildren());
            }
        }
    }

    /**
     * 子节点全部是页面
     *
     * @param children 子节点列表
     * @return true or false
     */
    private boolean isAllPageChildren(List<RouterVo> children) {
        if(CollectionUtils.isEmpty(children)) {
            return false ;
        }
        boolean isAllPageChildren = true;
        for(RouterVo vo : children) {
            if(Boolean.FALSE.equals(vo.getIsPage()) ) {
                return false ;
            }
        }
        return isAllPageChildren;
    }

    /**
     * 判断是否是菜单节点
     *
     * @param vo vo
     * @return true or false
     */
    private boolean isMenu(RouterVo vo) {
        String noDirect = "noRedirect";
        return noDirect.equals(vo.getRedirect());
    }

    @ApiOperation("获取内部应用登录二维码构造相关参数")
    @GetMapping("/findWxQrLoginInfo")
    public AjaxResult<WeInternalPreLoginParamVO> findWxQrLoginInfo() {
        WeCorpAccount validWeCorpAccount = iWxCorpAccountService.findValidWeCorpAccount();
        return AjaxResult.success(new WeInternalPreLoginParamVO(validWeCorpAccount));
    }


}
