package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 链接素材/消息
 *
 * @author tigger
 * 2022/1/18 16:42
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Link implements Attachment{
    /**
     * 图文消息标题，最长为128字节
     */
    private String title;
    /**
     * 图文消息封面的url
     */
    private String picurl;
    /**
     * 图文消息的描述，最长为512字节
     */
    private String desc;
    /**
     * 图文消息的链接
     */
    private String url;
}
