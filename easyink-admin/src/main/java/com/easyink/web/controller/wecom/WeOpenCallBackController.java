package com.easyink.web.controller.wecom;

import com.alibaba.fastjson.JSON;
import com.easyink.common.config.WechatOpenConfig;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.Threads;
import com.easyink.common.utils.WXBizMsgCrypt;
import com.easyink.wecom.domain.vo.WeOpenXmlMessageVO;
import com.easyink.wecom.factory.WeOpenCallBackEventFactory;
import com.easyink.wecom.factory.WeOpenEventHandle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信开放平台回调接口
 *
 * @author wx
 * 2023/1/12 18:10
 **/
@Api(value = "WeOpenCallBackController", tags = "企业微信回调接口")
@Slf4j
@RestController
@RequestMapping("/wecom/weopen/callback")
@RequiredArgsConstructor
public class WeOpenCallBackController {
    private final WeOpenEventHandle weOpenEventHandle;
    private final WechatOpenConfig wechatOpenConfig;

    @ApiOperation(value = "微信开放平台-第三方平台授权事件接收接口(授权事件通知、component_verify_ticket)")
    @PostMapping(value = "/${wechatopen.platform3rdAccount.appId}/receive3rdPlatform")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String receive3rdPlatform(@RequestParam("timestamp") String timeStamp,
                                     @RequestParam("nonce") String nonce,
                                     @RequestParam("msg_signature") String msgSignature,
                                     @RequestBody String postData) {
        try {
            //这个类是微信官网提供的解密类,需要用到消息校验Token 消息加密Key和服务平台appid
            WechatOpenConfig.Platform3rdAccount platform3rdAccount = wechatOpenConfig.getPlatform3rdAccount();
            WXBizMsgCrypt weCrypt = new WXBizMsgCrypt(platform3rdAccount.getComponentToken(), platform3rdAccount.getAesKey(), platform3rdAccount.getAppId());
            String decrypt = weCrypt.decryptMsg(msgSignature, timeStamp, nonce, postData);
            log.info("微信第三方平台回调通知接口,转换前的xml :{}", decrypt);
            WeOpenXmlMessageVO wxCpXmlMessage = strXmlToBean(decrypt);
            log.info("微信第三方平台回调通知接口,转换后的wxCpXmlMessage:{}", JSON.toJSONString(wxCpXmlMessage));
            String event = StringUtils.isNotBlank(wxCpXmlMessage.getInfoType()) ? wxCpXmlMessage.getInfoType() : wxCpXmlMessage.getEvent();
            WeOpenCallBackEventFactory factory = weOpenEventHandle.factory(event);
            if (factory != null) {
                Threads.WE_OPEN_THREAD_POOL.submit(() -> factory.eventHandle(wxCpXmlMessage));
            } else {
                throw new CustomException(ResultTip.TIP_STRATEGY_IS_EMPTY);
            }
        } catch (Exception e) {
            log.error("微信第三方平台回调异常, e:{}", ExceptionUtils.getStackTrace(e));
            return "error";
        }
        return "success";
    }

    /**
     * 安全的XML反序列化方法
     * 使用严格的白名单机制控制反序列化范围，防止XXE攻击和反序列化攻击
     *
     * @param xmlStr XML字符串
     * @return 反序列化后的WeOpenXmlMessageVO对象
     * @throws CustomException 当XML解析失败或包含非法内容时抛出
     */
    private WeOpenXmlMessageVO strXmlToBean(String xmlStr) {

            List<Class<?>> objects = new ArrayList<>();
            objects.add(WxCpXmlMessage.class);
            objects.add(WeOpenXmlMessageVO.class);
            WeOpenXmlMessageVO messageVO = com.xmlly.util.XmlUtil.strXmlToBean(xmlStr, objects, WeOpenXmlMessageVO.class);
            return messageVO;

    }

}
