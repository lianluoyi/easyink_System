package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

import java.util.List;

/**
 * 投票VO
 *
 * @author tigger
 * 2022/2/8 14:35
 **/
@Data
public class VoteVO extends AttachmentBaseVO{
    /**投票主题*/
    private String votetitle;
    /**投票选项*/
    private List<String> voteitem;
    /**投票类型*/
    private Integer votetype;
    /**投票id*/
    private String voteid;
}
