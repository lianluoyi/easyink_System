package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 音频共享文档VO
 *
 * @author tigger
 * 2022/2/7 11:25
 **/
@Data
public class VoipDocShareVO extends AttachmentBaseVO{

    private String filename;
    private String md5sum;
    private Integer filesize;
    private String sdkfileid;
}
