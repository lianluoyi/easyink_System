package com.easyink.wecom.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @description: 离职成员客户列表
 * @author admin
 * @create: 2020-10-25 16:07
 **/
@Data
public class LeaveWeUserListsDTO extends WeResultDTO {


    private List<LeaveWeUser> info;
    private String next_cursor;

    @Data
    public class LeaveWeUser {

        /**
         * 离职成员的userid
         */
        private String handover_userid;

        /**
         * 外部联系人userid
         */
        private String external_userid;

        /**
         * 离职时间
         */
        private Long dimission_time;

    }
}
