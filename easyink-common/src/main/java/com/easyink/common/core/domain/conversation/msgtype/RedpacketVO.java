package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

/**
 * 红包VO
 *
 * @author tigger
 * 2022/1/26 15:57
 **/
@Data
public class RedpacketVO extends AttachmentBaseVO{
    /**	红包消息类型。1 普通红包、2 拼手气群红包、3 激励群红包。Uint32类型*/
    private Integer type;
    /**红包祝福语。String类型*/
    private String wish;
    /**红包总个数。Uint32类型*/
    private Integer totalcnt;
    /**红包总金额。Uint32类型，单位为分。*/
    private Integer totalamount;
}
