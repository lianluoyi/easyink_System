package com.easyink.web.controller.monitor;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.framework.web.domain.Server;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: ServerController
 *
 * @author: 1*+
 * @date: 2021-08-27 16:30
 */
@RestController
@RequestMapping("/monitor/server")
@ApiSupport(order = 3, author = "1*+")
@Api(value = "ServerController", tags = "服务器监控接口")
public class ServerController extends BaseController {


    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    @ApiOperation("获取服务器监控信息")
    public AjaxResult getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }
}
