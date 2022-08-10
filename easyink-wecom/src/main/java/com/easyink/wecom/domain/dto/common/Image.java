package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片素材/消息
 *
 * @author tigger
 * 2022/1/18 16:42
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Image implements Attachment{
    /**
     * 图片的media_id
     */
    private String media_id;
    /**
     * 图片的链接，仅可使用上传图片接口得到的链接
     */
    private String pic_url;
}
