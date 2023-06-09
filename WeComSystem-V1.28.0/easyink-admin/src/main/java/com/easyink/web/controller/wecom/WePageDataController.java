package com.easyink.web.controller.wecom;

import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.wecom.domain.dto.WePageStaticDataDTO;
import com.easyink.wecom.login.util.LoginTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 * @description 首页统计
 * @date 2021/2/23 15:30
 **/
@Api(tags = "首页统计controller")
@Slf4j
@RestController
@RequestMapping("wecom/page/")
public class WePageDataController {
    private final RedisCache redisCache;

    @Autowired
    public WePageDataController(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /**
     *
     */
    @ApiOperation(value = "数据总览controller", httpMethod = "GET")
    @GetMapping("/getCorpBasicData")
    public AjaxResult getCorpBasicData() {
        return AjaxResult.success(redisCache.getCacheMap(RedisKeyConstants.CORP_BASIC_DATA + LoginTokenService.getLoginUser().getCorpId()));
    }

    @ApiOperation(value = "实时数据controller", httpMethod = "GET")
    @GetMapping("/getCorpRealTimeData")
    public AjaxResult getCorpRealTimeData() {
        WePageStaticDataDTO wePageStaticDataDto = redisCache.getCacheObject(RedisKeyConstants.CORP_REAL_TIME + LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(wePageStaticDataDto);
    }
}