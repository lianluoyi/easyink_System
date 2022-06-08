package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 视频VO
 *
 * @author tigger
 * 2022/1/25 18:25
 **/
@Data
public class VideoVO extends AttachmentBaseVO {
    private Integer play_length;

    private String md5sum;

    private String sdkfileid;

    private Integer filesize;


}
