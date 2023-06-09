package com.easyink.web.controller.wecom;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.vo.ReleaseNotesVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.PageHomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: PageHomeController
 *
 * @author: 1*+
 * @date: 2021-08-27 17:16
 */
@Api(value = "PageHomeController", tags = "首页数据接口")
@RestController
@RequestMapping("/wecom/pagehome")
public class PageHomeController {


    private final PageHomeService pageHomeService;

    private final RuoYiConfig ruoYiConfig;

    @Autowired
    public PageHomeController(PageHomeService pageHomeService, RuoYiConfig ruoYiConfig) {
        this.pageHomeService = pageHomeService;
        this.ruoYiConfig = ruoYiConfig;
    }

    /**
     * 刷新统计数据缓存
     */
    @ApiOperation("刷新统计数据缓存")
    @GetMapping("/reloadredis")
    public AjaxResult reloadRedis() {
        pageHomeService.reloadPageHome(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @ApiOperation("获取当前版本")
    @GetMapping("/getCurrVersion")
    public AjaxResult<ReleaseNotesVO> getCurrVersion() {
        ReleaseNotesVO notesVO = ReleaseNotesVO.builder().version(ruoYiConfig.getVersion())
                .notes(ruoYiConfig.getReleaseNotes()).build();
        return AjaxResult.success(notesVO);
    }
}