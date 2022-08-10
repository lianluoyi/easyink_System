package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 在线文档VO
 *
 * @author tigger
 * 2022/2/8 14:40
 **/
@Data
public class DocmsgVO extends AttachmentBaseVO{
    /**在线文档名称*/
    private String title;
    /**在线文档链接*/
    private String link_url;
    /**在线文档创建者。本企业成员创建为userid；外部企业成员创建为external_userid*/
    private String doc_creator;


}
