package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频素材/消息
 *
 * @author tigger
 * 2022/1/18 16:44
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Video implements Attachment {
    /**
     * 视频的media_id
     */
    private String media_id;
}
