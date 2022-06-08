package com.easywecom.common.core.domain.conversation.msgtype;

/**
 * 消息类型枚举
 *
 * @author tigger
 * 2022/1/28 14:00
 **/
public enum MsgTypeEnum {

    TEXT("text"),
    IMAGE("image"),
    REVOKE("revoke"),
    VOICE("voice"),
    VIDEO("video"),
    EMOTION("emotion"),
    FILE("file"),
    CARD("card"),
    MIXED("mixed"),
    MEETING_VOICE_CALL("meeting_voice_call"),
    VOIP_DOC_SHARE("voip_doc_share"),
    CHATRECORD("chatrecord"),
    CHATRECORD_TEXT("ChatRecordText"),
    CHATRECORD_FILE("ChatRecordFile"),
    CHATRECORD_IMAGE("ChatRecordImage"),
    CHATRECORD_VIDEO("ChatRecordVideo"),
    CHATRECORD_LINK("ChatRecordLink"),
    CHATRECORD_LOCATION("ChatRecordLocation"),
    CHATRECORD_MIXED("ChatRecordMixed"),

    /**
     * 以下消息类型未实现
     */
    AGREE("agree"),
    DISAGREE("disagree"),
    LINK("link"),
    WEAPP("weapp"),
    VOTE("vote"),
    COLLECT("collect"),
    REDPACKET("redpacket"),
    MEETING("meeting"),
    DOCMSG("docmsg"),
    MARKDOWN("markdown"),
    NEWS("news"),
    CALENDAR("calendar"),
    EXTERNAL_REDPACKET("external_redpacket"),
    SPHFEED("sphfeed"),
    ;

    private String type;

    MsgTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
