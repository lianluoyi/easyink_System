package com.easyink.common.core.domain.conversation.msgtype;

import lombok.Data;

import java.util.List;

/**
 * 日程VO
 *
 * @author tigger
 * 2022/2/8 14:50
 **/
@Data
public class CalendarVO {
    /**	日程主题*/
    private String title;
    /**	日程组织者*/
    private String creatorname;
    /**日程参与人*/
    private List<String> attendeename;
    /**日程开始时间。Utc时间，单位秒*/
    private Long starttime;
    /**日程结束时间。Utc时间，单位秒*/
    private Long endtime;
    /**	日程地点*/
    private String place;
    /**日程备注*/
    private String remarks;

}
