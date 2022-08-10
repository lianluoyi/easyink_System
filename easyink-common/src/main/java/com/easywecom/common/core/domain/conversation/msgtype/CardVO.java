package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 卡片VO
 *
 * @author tigger
 * 2022/1/26 15:37
 **/
@Data
public class CardVO extends AttachmentBaseVO {
    private String corpname;
    private String userid;

    /**
     * 自定义数据
     */
    private String userName;
    private String imageUrl;
}
