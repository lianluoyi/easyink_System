package com.easyink.web.controller.common;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.exception.CustomException;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.common.shorturl.service.ShortUrlService;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.ip.IpUtils;
import com.easyink.wecom.domain.vo.shorturl.GetOriginUrlVO;
import com.easyink.wecom.handler.shorturl.AbstractShortUrlHandler;
import com.easyink.wecom.handler.shorturl.ShortUrlHandlerFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.easyink.common.enums.ResultTip.TIP_CANNOT_FIND_PAGE;

/**
 * 类名: 短链相关接口
 *
 * @author : silver_chariot
 * @date : 2022/7/18 17:08
 **/
@RestController
@RequestMapping("/url")
@Api(value = "ShortUrlController", tags = "短链相关")
@Slf4j
@AllArgsConstructor
public class ShortUrlController {
    /**
     * 短链长度
     */
    public static final int SHORT_CODE_LENGTH = 6;
    private final ShortUrlService shortUrlService;
    private final ShortUrlHandlerFactory shortUrlHandlerFactory;



    @GetMapping("/{shortCode}")
    @ApiOperation("根据短链获取原链接和短链的类型")
    public AjaxResult getUrlAndHandle(@ApiParam("短链后缀的code")@PathVariable String shortCode) {
        // 校验短链code
        validateCode(shortCode);
//        // 校验访问Ip
//        String serverIp = "";
//        try {
//            serverIp = IpUtils.getOutIp();
//        } catch (Exception e) {
//            log.error("[短链]获取服务器ip异常.e:{}", ExceptionUtils.getStackTrace(e));
//        }
//        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
//        log.info("[短链]有人点击了短链,shortCode:{},ip:{},serverIp:{}", shortCode, ip, serverIp);
//        if (serverIp.equals(ip)) {
//            log.info("[短链]ip与服务器ip一样,不处理,ip:{}", ip);
//            return AjaxResult.success();
//        }
        // 获取短链接到原链接的映射
        SysShortUrlMapping urlMapping = shortUrlService.getUrlByMapping(shortCode);
        AbstractShortUrlHandler handler = shortUrlHandlerFactory.getByType(urlMapping.getType());
        if (handler == null) {
            throw new CustomException(TIP_CANNOT_FIND_PAGE);
        }
        // 进行业务处理并返回 需要重定向的链接给前端
        return AjaxResult.success(new GetOriginUrlVO(urlMapping.getType(), handler.handleAndGetRedirectUrl(urlMapping)));
    }

    /**
     * 校验短链的code
     *
     * @param shortCode 短链6位的code
     */
    private void validateCode(String shortCode) {
        if (StringUtils.isBlank(shortCode) || shortCode.length() != SHORT_CODE_LENGTH) {
            throw new CustomException(TIP_CANNOT_FIND_PAGE);
        }
    }




}
