package com.easyink.wecom.domain.model.third;

import com.easyink.common.annotation.Excel;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.wecom.domain.enums.third.HaiderbaoMsgTypeEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

/**
 * 导入会话存档model
 * @author tigger
 * 2024/11/5 15:12
 **/
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportSessionArchiveModel {
    @Excel(name = "acmsgid")
    private String msgId;
    @Excel(name = "orgid")
    private String orgId;
    @Excel(name = "wechat_account")
    private String userId;
    @Excel(name = "fromid")
    private String from;
    @Excel(name = "toid")
    private String to;
    @Excel(name = "friendname")
    private String externalUserNick;
    @Excel(name = "followusername")
    private String userNick;
    @Excel(name = "wechattime", dateFormat = "yyyy/MM/dd HH:mm:ss")
    private Date msgTime;
    @Excel(name = "chat_msgtype")
    private Integer msgType;
    @Excel(name = "content")
    private String content;

    public ChatInfoVO toChatInfoVO(Map<String, String> dataMapping) {
        ChatInfoVO chatInfoVO = new ChatInfoVO();
        chatInfoVO.setMsgid(this.msgId);
        chatInfoVO.setMsgtime(this.msgTime.getTime());
        chatInfoVO.setAction("send");
        HaiderbaoMsgTypeEnum typeEnum = HaiderbaoMsgTypeEnum.getEnumByOriginType(msgType);
        chatInfoVO.setMsgtype(typeEnum == null ? null : typeEnum.getTarget());
        chatInfoVO.setSeq(0L);
        chatInfoVO.setIsRevoke(false);

        chatInfoVO.setFrom(dataMapping.getOrDefault(this.from, ""));
        chatInfoVO.setFromInfo(null);
        chatInfoVO.setTolist(Lists.newArrayList(dataMapping.getOrDefault(this.to, "")));
        chatInfoVO.setToListInfo(null);
        chatInfoVO.setRoomid("");
        chatInfoVO.setRoomInfo(null);
        return chatInfoVO;
    }


}
