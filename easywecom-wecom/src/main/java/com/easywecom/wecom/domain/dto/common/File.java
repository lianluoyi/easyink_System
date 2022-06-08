package com.easywecom.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件素材/消息
 *
 * @author tigger
 * 2022/1/18 16:44
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class File implements Attachment{
    /**
     * 视频的media_id
     */
    private String media_id;
}
