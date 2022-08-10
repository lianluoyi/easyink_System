package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 自定义表情VO
 *
 * @author tigger
 * 2022/1/25 18:48
 **/
@Data
public class EmotionVO extends AttachmentBaseVO{

    private String md5sum;
    private String sdkfileid;
    private Integer width;
    private Integer imagesize;
    private Integer type;
    private Integer height;

}
