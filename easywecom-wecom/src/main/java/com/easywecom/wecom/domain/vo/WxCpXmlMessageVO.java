package com.easywecom.wecom.domain.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.XmlUtils;
import me.chanjar.weixin.common.util.xml.XStreamCDataConverter;
import me.chanjar.weixin.cp.bean.WxCpXmlMessage;
import me.chanjar.weixin.cp.util.xml.XStreamTransformer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 类名： WxCpXmlMessageVO
 * 微信推送过来的消息，也是同步回复给用户的消息，xml格式
 *
 * @author 佚名
 * @date 2021/8/31 14:18
 */
@Data
@Slf4j
@XStreamAlias("xml")
public class WxCpXmlMessageVO extends WxCpXmlMessage {
    @XStreamAlias("Alias")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String alias;

    @XStreamAlias("BatchJob")
    private BatchJob batchJob = new BatchJob();

    @XStreamAlias("FailReason")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String failReason;

    @XStreamAlias("TagType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String tagType;

    @XStreamAlias("Id")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String tagId;

    @XStreamAlias("UpdateDetail")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String updateDetail;

    @XStreamAlias("JoinScene")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String joinScene;

    @XStreamAlias("QuitScene")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String quitScene;

    @XStreamAlias("MemChangeCnt")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String memberChangeCnt;

    @XStreamAlias("Source")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String source;

    @XStreamAlias("MainDepartment")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String mainDepartment;

    /********************************************************
     * 以下字段为三方应用增加字段
     ********************************************************/
    @XStreamAlias("SuiteId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String suiteId;

    @XStreamAlias("InfoType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String infoType;

    @XStreamAlias("TimeStamp")
    private Long timeStamp;
    /**
     * 推送三方应用suiteticket
     */
    @XStreamAlias("SuiteTicket")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String suiteTicket;
    /**
     * 授权成功回调
     */
    @XStreamAlias("AuthCode")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authCode;
    /**
     * 授权变更通知
     */
    @XStreamAlias("AuthCorpId")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String authCorpId;

    /******************************************************
     * 以上字段为三方应用增加字段
     *******************************************************/


    @Data
    public static class BatchJob implements Serializable {
        private static final long serialVersionUID = -3418685294606228837L;
        @XStreamAlias("JobId")
        @XStreamConverter(value = XStreamCDataConverter.class)
        private String jobId;

        @XStreamAlias("JobType")
        @XStreamConverter(value = XStreamCDataConverter.class)
        private String jobType;

        @XStreamAlias("ErrCode")
        private Integer errCode;

        @XStreamAlias("ErrMsg")
        @XStreamConverter(value = XStreamCDataConverter.class)
        private String errMsg;
    }

    public static WxCpXmlMessageVO fromXml(String xml) {
        //修改微信变态的消息内容格式，方便解析
        final WxCpXmlMessageVO xmlPackage = XStreamTransformer.fromXml(WxCpXmlMessageVO.class, xml);
        xmlPackage.setAllFieldsMap(XmlUtils.xml2Map(xml));
        return xmlPackage;
    }

    @Override
    public String getToUserName() {
        if (StringUtils.isBlank(super.getToUserName())) {
            return this.authCorpId;
        }
        return super.getToUserName();
    }

    public String getAuthCorpId() {
        if (StringUtils.isBlank(super.getToUserName())) {
            return this.authCorpId;
        }
        return super.getToUserName();
    }

    @Override
    public Long getCreateTime() {
        if (ObjectUtils.isEmpty(super.getCreateTime())) {
            return this.timeStamp;
        }
        return super.getCreateTime();
    }

    public Long getTimeStamp() {
        if (ObjectUtils.isEmpty(super.getCreateTime())) {
            return this.timeStamp;
        }
        return super.getCreateTime();
    }

    /**
     * 生成企微回调事件的唯一标识
     *
     * @param key 拼接唯一标识的参数,如外部联系人事件传外部联系人id,群聊事件传chatId
     * @return 唯一标识
     */
    public String getUniqueKey(String key) {
        if (StringUtils.isAnyBlank(this.getEvent(), this.getChangeType(), this.getToUserName()) || this.getCreateTime() == null) {
            return "";
        }
        return this.getToUserName() + ":" + this.getEvent() + ":" + this.getChangeType() + ":" + this.getCreateTime() + ":" + key;
    }
}
