package com.easyink.web.controller.wecom;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.wecom.TicketUtils;
import com.easyink.wecom.client.WeTicketClient;
import com.easyink.wecom.domain.WeH5TicketDto;
import com.easyink.wecom.login.util.LoginTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @description h5 ticket校验接口
 * @date 2021/1/6 11:23
 **/
@Slf4j
@RequestMapping(value = "/wecom/ticket/")
@Api(tags = "h5 ticket校验接口")
@RestController
public class WeTicketController extends BaseController {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private WeTicketClient weTicketClient;


    /**
     * 获取企业的jsapi_ticket
     *
     * @param url JS接口页面的完整URL
     * @return
     */
    @ApiOperation("获取企业的jsapi_ticket")
    @GetMapping("/getAppTicket")
    public AjaxResult getAppTicket(String url, String agentId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        String key = WeConstans.APP_TICKET_KEY + ":" + corpId + ":" + agentId;
        String ticketVaule = redisCache.getCacheObject(key);
        if (StringUtils.isEmpty(ticketVaule)) {
            WeH5TicketDto ticketRes = weTicketClient.getJsapiTicket(agentId, LoginTokenService.getLoginUser().getCorpId());
            if (ticketRes != null && StringUtils.isNotEmpty(ticketRes.getTicket())) {
                redisCache.setCacheObject(key, ticketRes.getTicket(), ticketRes.getExpiresIn(), TimeUnit.SECONDS);
                ticketVaule = ticketRes.getTicket();
            }
        }
        return AjaxResult.success(TicketUtils.getSignatureMap(ticketVaule, url));
    }

    /**
     * 获取应用的jsapi_ticket
     *
     * @param url JS接口页面的完整URL
     * @return
     */
    @ApiOperation("获取应用的jsapi_ticket")
    @GetMapping("/getAgentTicket")
    public AjaxResult getAgentTicket(String url, String agentId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        String key = WeConstans.AGENT_TICKET_KEY + ":" + corpId + ":" + agentId;
        String ticketVaule = redisCache.getCacheObject(key);
        if (StringUtils.isEmpty(ticketVaule)) {
            WeH5TicketDto ticketRes = weTicketClient.getTicket("agent_config", agentId, LoginTokenService.getLoginUser().getCorpId());
            if (ticketRes != null && StringUtils.isNotEmpty(ticketRes.getTicket())) {
                redisCache.setCacheObject(key, ticketRes.getTicket(), ticketRes.getExpiresIn(), TimeUnit.SECONDS);
                ticketVaule = ticketRes.getTicket();
            }
        }
        return AjaxResult.success(TicketUtils.getSignatureMap(ticketVaule, url));
    }


}
