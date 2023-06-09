package com.easyink.web.controller.monitor;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.system.SysUserOnline;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.LogoutReasonEnum;
import com.easyink.common.service.ISysUserOnlineService;
import com.easyink.wecom.login.util.LoginTokenService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * 在线用户监控
 *
 * @author admin
 */
@RestController
@RequestMapping("/monitor/online")
@ApiSupport(order = 6, author = "1*+")
@Api(value = "SysUserOnlineController", tags = "系统在线用户接口")
public class SysUserOnlineController extends BaseController {
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisCache redisCache;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    @ApiOperation("在线用户列表")
    public TableDataInfo<SysUserOnline> list(@ApiParam("Ip地址") String ipaddr, @ApiParam("用户昵称") String userName) {
        String currentCorpId = LoginTokenService.getLoginUser().getCorpId();
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            SysUserOnline sysUserOnline = null;
            if (StringUtils.isNotBlank(ipaddr) && StringUtils.isNotBlank(userName)) {
                sysUserOnline = userOnlineService.selectOnlineByInfo(ipaddr, userName, user);
            } else if (StringUtils.isNotBlank(ipaddr)){
                sysUserOnline = userOnlineService.selectOnlineByIpaddr(ipaddr, user);
            } else if (StringUtils.isNotBlank(userName)) {
                sysUserOnline = userOnlineService.selectOnlineByUserName(userName, user);
            } else {
                sysUserOnline = userOnlineService.loginUserToUserOnline(user);
            }
            if (sysUserOnline != null) {
                userOnlineList.add(sysUserOnline);
            }
        }
        // 根据登录用户名+ip地址 按登录时间倒序排序后 去重,保留最新登录的信息(同一ip如果没有通过后台登出而是直接关闭浏览器,下次打开浏览器登录后redis若token还没过期,则会有该ip的两个同样登录用户)
        List<SysUserOnline> list = userOnlineList.stream().filter(user->currentCorpId.equals(user.getCorpId()))
                .sorted(Comparator.comparing(SysUserOnline::getLoginTime).reversed()).collect(
                collectingAndThen(
                        toCollection(() ->
                                new TreeSet<>(
                                        Comparator.comparing(o -> o.getUserName() + ";" + o.getIpaddr()))
                        ), ArrayList::new));
        list.removeAll(Collections.singleton(null));
        return getDataTable(list);
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    @ApiOperation("强退用户列表")
    public AjaxResult forceLogout(@ApiParam("tokenId") @PathVariable String tokenId) {
        redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
        // 设置登出原因缓存
        redisCache.setCacheObject(RedisKeyConstants.ACCOUNT_LOGOUT_REASON_KEY + tokenId, LogoutReasonEnum.FORCED.getCode()
                , 30, TimeUnit.MINUTES);
        return AjaxResult.success();
    }
}
