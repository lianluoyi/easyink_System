package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

import java.util.List;

/**
 * meetingVoiceCallVO
 *
 * @author tigger
 * 2022/1/26 17:31
 **/
@Data
public class MeetingVoiceCallVO extends AttachmentBaseVO{

    private Integer endtime;
    private String sdkfileid;
    private List<DemofiledataVO> demofiledata;
    private List<Sharescreendata> sharescreendata;

    /***/
    private String filename;

    @Data
    public static class DemofiledataVO extends AttachmentBaseVO{
        private String filename;
        private String demooperator;
        private Integer starttime;
        private Integer endtime;
    }


    @Data
    public static class Sharescreendata{


        private String share;
        private Integer starttime;
        private Integer endtime;

    }




}
