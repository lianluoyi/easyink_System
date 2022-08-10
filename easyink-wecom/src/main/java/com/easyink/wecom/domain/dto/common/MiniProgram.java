package com.easyink.wecom.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序素材/消息
 *
 * @author tigger
 * 2022/1/18 16:43
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MiniProgram implements Attachment{
    /**
     * 小程序消息标题，最长为64字节
     */
    private String title;
    /**
     * 小程序消息封面的mediaid，封面图建议尺寸为520*416
     */
    private String pic_media_id;
    /**
     * 小程序appid，必须是关联到企业的小程序应用
     */
    private String appid;
    /**
     * 小程序page路径
     */
    private String page;
}
