package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

import java.util.List;

/**
 * 图文VO
 *
 * @author tigger
 * 2022/2/8 14:42
 **/
@Data
public class NewsVO extends AttachmentBaseVO {
    /**图文消息的内容*/
    private Info info;


    @Data
    public static class Info{
        /**图文消息数组，每个item结构包含title、description、url、picurl等结构*/
        private List<NewsItem> item;
    }


    @Data
    public static class NewsItem {
        /**图文消息标题*/
        private String title;
        /**图文消息描述*/
        private String description;
        /**图文消息点击跳转地址*/
        private String url;
        /**图文消息配图的url*/
        private String picurl;
    }
}
