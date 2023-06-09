package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本素材/消息
 *
 * @author tigger
 * 2022/1/18 16:41
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Text implements Attachment{

    /**
     * 消息文本内容,最长为4000字节
     */
    private String content;
}
