package com.easywecom.common.core.domain.conversation;

import com.easywecom.common.core.domain.conversation.msgtype.*;
import lombok.Data;

/**
 * 基础消息VO
 *
 * @author tigger
 * 2022/1/26 10:22
 **/
@Data
public class ChatBodyVO {

    /**
     * 组合消息类型实体
     */
    private TextVO text;
    private ImageVO image;
    private VideoVO video;
    private VoiceVO voice;
    private FileVO file;
    private EmotionVO emotion;
    private MixedVO mixed;
    private RevokeVO revoke;
    private CardVO card;
    private MeetingVoiceCallVO meetingVoiceCall;
    private ChatRecordVO chatRecord;
    /**
     * 内嵌消息的消息体(混合消息和会话记录)
     * 可对应各种类型消息体数据，如text,image等等
     */
    private Object content;


    /**
     * 默认实现或则为实现的
     */
    private LinkVO link;
    private AgreeVO agree;
    private DisagreeVO disagree;
    private CalendarVO calendar;
    private CollectVO collect;
    private LocationVO location;
    private RedpacketVO redpacket;
    private DocmsgVO docmsg;
    private MarkdownVO markdown;
    private MeetingVO meeting;
    private NewsVO news;
    private SphFeedVO sphfeed;
    private TodoVO todo;
    private VoipDocShareVO voip_doc_share;
    private VoteVO vote;
    private WeappVO weapp;
}
