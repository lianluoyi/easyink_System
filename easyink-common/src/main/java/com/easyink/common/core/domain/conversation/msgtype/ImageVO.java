package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 图片VO
 *
 * @author tigger
 * 2022/1/25 17:50
 **/
@Data
public class ImageVO extends AttachmentBaseVO{
    private String md5sum;

    private String sdkfileid;

    private String filesize;

}
