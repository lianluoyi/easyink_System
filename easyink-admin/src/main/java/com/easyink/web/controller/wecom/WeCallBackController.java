package com.easyink.web.controller.wecom;

import com.alibaba.fastjson.JSON;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.WeCrypt;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.Threads;
import com.easyink.common.utils.wecom.WxCryptUtil;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeCallBackEventFactory;
import com.easyink.wecom.factory.WeEventHandle;
import com.easyink.wecom.openapi.service.AppCallbackSettingService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.cp.bean.WxCpTpXmlPackage;
import me.chanjar.weixin.cp.bean.WxCpXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 类名: 企业微信回调接口
 *
 * @author: 1*+
 * @date: 2021-08-06 16:52
 */
@Api(value = "WeCallBackController", tags = "企业微信回调接口")
@Slf4j
@RestController
@RequestMapping("/wecom/callback")
public class WeCallBackController {
    private final WeEventHandle weEventHandle;
    private final RuoYiConfig ruoYiConfig;
    private final AppCallbackSettingService appCallbackSettingService;
    @Resource(name = "sendCallbackExecutor")
    private ThreadPoolTaskExecutor sendCallbackExecutor;


    @Autowired
    public WeCallBackController(WeEventHandle weEventHandle, RuoYiConfig ruoYiConfig,  AppCallbackSettingService appCallbackSettingService) {
        this.weEventHandle = weEventHandle;
        this.ruoYiConfig = ruoYiConfig;
        this.appCallbackSettingService = appCallbackSettingService;
    }

    /**
     * 企业微信官方回调入口 请求接收业务数据
     * (5秒内需要响应给企业微信，否则企业微信端会进行重试推送)
     *
     * @param msg       bodymsg
     * @param signature 企业微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数。与timestamp结合使用，用于防止请求重放攻击。
     * @return 当接收成功后，http头部返回200表示接收ok，其他错误码企业微信后台会一律当做失败并发起重试
     */
    @ApiOperation("企业微信官方回调入口")
    @PostMapping(value = "/recive")
    public String receive(@RequestBody String msg, @RequestParam(name = "msg_signature") String signature,
                          String timestamp, String nonce) {
        try {
            WeCrypt weCrypt = ruoYiConfig.getSelfBuild();
            WxCryptUtil wxCryptUtil = new WxCryptUtil(weCrypt.getToken(), weCrypt.getEncodingAesKey());

            String decrypt = wxCryptUtil.decrypt(signature, timestamp, nonce, msg);
            WxCpXmlMessageVO wxCpXmlMessage = strXmlToBean(decrypt);
            log.info("企微回调通知接口 wxCpXmlMessage:{}", JSON.toJSONString(wxCpXmlMessage));
            WeCallBackEventFactory factory = weEventHandle.factory(wxCpXmlMessage.getEvent());
            if (factory != null) {
                Threads.SINGLE_THREAD_POOL.submit(() -> factory.eventHandle(wxCpXmlMessage));
            } else {
                log.info("[企微回调通知接口]该回调事件不存在对应的处理,{}", wxCpXmlMessage.getEvent());
            }
            sendCallbackExecutor.execute(()->appCallbackSettingService.sendCallback(wxCpXmlMessage.getToUserName(),msg, signature,timestamp, nonce));
        } catch (Exception e) {
            log.error("企微回调异常:{}", ExceptionUtils.getStackTrace(e));
        }
        return "success";
    }




    /**
     * 回调配置校验接口 请求验证URL有效性
     *
     * @param request request
     * @return 在1秒内响应GET请求，响应内容为上一步得到的明文消息内容（不能加引号，不能带bom头，不能带换行符）
     */
    @ApiOperation("回调配置校验接口")
    @GetMapping(value = "/recive")
    public String recive(HttpServletRequest request) {
        log.info("回调配置校验接口开始");
        // 微信加密签名
        String sVerifyMsgSig = request.getParameter("msg_signature");
        // 时间戳
        String sVerifyTimeStamp = request.getParameter("timestamp");
        // 随机数
        String sVerifyNonce = request.getParameter("nonce");
        // 随机字符串
        String sVerifyEchoStr = request.getParameter("echostr");

        try {
            WeCrypt weCrypt = ruoYiConfig.getSelfBuild();
            WxCryptUtil wxCryptUtil = new WxCryptUtil(weCrypt.getToken(), weCrypt.getEncodingAesKey());
            return wxCryptUtil.verifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);
        } catch (Exception e) {
            log.error("回调配置校验接口：{}", ExceptionUtils.getStackTrace(e));
            return "error";
        }
    }

    /**
     * 企业微信官方回调入口 请求接收业务数据
     * (5秒内需要响应给企业微信，否则企业微信端会进行重试推送)
     *
     * @param msg       bodymsg
     * @param signature 企业微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数。与timestamp结合使用，用于防止请求重放攻击。
     * @return 当接收成功后，http头部返回200表示接收ok，其他错误码企业微信后台会一律当做失败并发起重试
     */
    @ApiOperation("企业微信官方回调入口")
    @PostMapping(value = "/recive3rdapp")
    public String receive3rdapp(@RequestBody String msg, @RequestParam(name = "msg_signature") String signature,
                                String timestamp, String nonce) {
        try {
            WxCpTpXmlPackage wxCpTpXmlPackage = WxCpTpXmlPackage.fromXml(msg);
            WeCrypt weCrypt = ruoYiConfig.getProvider().getCryptById(wxCpTpXmlPackage.getToUserName());
            WxCryptUtil wxCryptUtil = new WxCryptUtil(weCrypt.getToken(), weCrypt.getEncodingAesKey());

            String decrypt = wxCryptUtil.decrypt(signature, timestamp, nonce, msg);
            log.info("企微三方应用回调通知接口,转换前的xml :{}", decrypt);
            WxCpXmlMessageVO wxCpXmlMessage = strXmlToBean(decrypt);
            log.info("企微三方应用回调通知接口 wxCpXmlMessage:{}", JSON.toJSONString(wxCpXmlMessage));
            String event = StringUtils.isNotBlank(wxCpXmlMessage.getInfoType()) ? wxCpXmlMessage.getInfoType() : wxCpXmlMessage.getEvent();
            WeCallBackEventFactory factory = weEventHandle.factory(event);
            if (factory != null) {
                Threads.SINGLE_THREAD_POOL.submit(() -> factory.eventHandle(wxCpXmlMessage));
            } else {
                throw new CustomException(ResultTip.TIP_STRATEGY_IS_EMPTY);
            }
            sendCallbackExecutor.execute(()->appCallbackSettingService.sendCallback(wxCpXmlMessage.getToUserName(),msg, signature,timestamp, nonce));
        } catch (Exception e) {
            log.error("企微三方应用回调通知接口异常:{}", ExceptionUtils.getStackTrace(e));
        }
        return "success";
    }

    /**
     * （三方应用）回调配置校验接口 请求验证URL有效性
     *
     * @param msgSignature 企业微信加密签名，msg_signature结合了企业填写的token、请求中的timestamp、nonce参数、加密的消息体
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param echostr      加密的字符串。需要解密得到消息内容明文，解密后有random、msg_len、msg、receiveid四个字段，其中msg即为消息内容明文
     * @return 在1秒内响应GET请求，响应内容为上一步得到的明文消息内容（不能加引号，不能带bom头，不能带换行符）
     */
    @ApiOperation("三方应用回调配置校验接口")
    @GetMapping(value = "/recive3rdapp")
    public String check3rdapp(@RequestParam("msg_signature") String msgSignature,
                              @RequestParam("timestamp") String timestamp,
                              @RequestParam("nonce") String nonce,
                              @RequestParam("echostr") String echostr) {
        log.info("三方应用回调配置校验接口");
        try {
            WeCrypt weCrypt = ruoYiConfig.getProvider().getCryptById("");
            WxCryptUtil wxCryptUtil = new WxCryptUtil(weCrypt.getToken(), weCrypt.getEncodingAesKey());
            return wxCryptUtil.verifyURL(msgSignature, timestamp, nonce, echostr);
        } catch (Exception e) {
            log.error("回调配置校验接口：{}", ExceptionUtils.getStackTrace(e));
            return "error";
        }
    }

    private WxCpXmlMessageVO strXmlToBean(String xmlStr) {
        XStream xstream = XStreamInitializer.getInstance();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.processAnnotations(WxCpXmlMessage.class);
        xstream.processAnnotations(WxCpXmlMessageVO.class);
        xstream.processAnnotations(WxCpXmlMessageVO.ScanCodeInfo.class);
        xstream.processAnnotations(WxCpXmlMessageVO.SendPicsInfo.class);
        xstream.processAnnotations(WxCpXmlMessageVO.SendPicsInfo.Item.class);
        xstream.processAnnotations(WxCpXmlMessageVO.SendLocationInfo.class);
        xstream.processAnnotations(WxCpXmlMessageVO.BatchJob.class);
        return (WxCpXmlMessageVO) xstream.fromXML(xmlStr);
    }


}
