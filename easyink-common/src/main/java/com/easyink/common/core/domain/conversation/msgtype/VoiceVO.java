package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 音频VO
 *
 * @author tigger
 * 2022/1/25 18:23
 **/
@Data
public class VoiceVO extends AttachmentBaseVO{

    private Integer play_length;

    private String md5sum;

    private String sdkfileid;

    private Integer voice_size;
}
