package com.easyink.wecom.domain.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.xml.XStreamCDataConverter;
import me.chanjar.weixin.cp.bean.WxCpXmlMessage;

/**
 * 微信开放平台-第三方平台推送过来的消息,xml格式
 * https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/2.0/api/Before_Develop/authorize_event.html
 *
 *
 * @author wx
 * 2023/1/12 19:05
 **/
@Data
@Slf4j
@XStreamAlias("xml")
public class WeOpenXmlMessageVO extends WxCpXmlMessage {

    @XStreamAlias("InfoType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String infoType;

    @XStreamAlias("CreateTime")
    private Long createTime;


    /**
     * 第三方平台 appid
     */
    @XStreamAlias("AppId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String appId;

    /**
     * "Ticket 内容"
     */
    @XStreamAlias("ComponentVerifyTicket")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String componentVerifyTicket;

    /**
     * 公众号appid
     */
    @XStreamAlias("AuthorizerAppid")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authorizerAppid;

    /**
     * 授权码
     */
    @XStreamAlias("AuthorizationCode")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authorizationCode;

    /**
     * 授权码过期时间
     */
    @XStreamAlias("AuthorizationCodeExpiredTime")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authorizationCodeExpiredTime;

    /**
     * 预授权码
     */
    @XStreamAlias("PreAuthCode")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String preAuthCode;
}
