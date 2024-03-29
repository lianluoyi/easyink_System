package com.easyink.wecom.domain.dto.message;

import com.easyink.wecom.domain.dto.common.Attachment;
import com.easyink.wecom.domain.dto.common.Text;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeCustomerMessagePushDTO {

    /**
     * 群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群
     */
    private String chat_type;

    /**
     * 客户的外部联系人id列表，仅在chat_type为single时有效，不可与sender同时为空，最多可传入1万个客户
     */
    private List<String> external_userid;

    /**
     * 发送企业群发消息的成员userid，当类型为发送给客户群时必填
     */
    private String sender;

    /**
     * 文本消息
     */
    private Text text;

    /**
     * 附件
     */
    private List<Attachment> attachments;

    /**
     * 图片消息
     */
    //private ImageMessageDTO image;

    /**
     * 链接消息
     */
    //private LinkMessageDTO link;

    /**
     * 小程序消息
     */
    //private MiniprogramMessageDTO miniprogram;
}
