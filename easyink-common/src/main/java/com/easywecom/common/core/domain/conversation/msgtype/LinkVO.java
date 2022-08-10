package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 链接VO
 *
 * @author tigger
 * 2022/1/26 15:52
 **/
@Data
public class LinkVO extends AttachmentBaseVO{

    private String image_url;
    private String description;
    private String link_url;
    private String title;

}
