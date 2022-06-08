package com.easywecom.wecom.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeGroupExtendDTO extends WeResultDTO {
    private List<FailedChatList> failed_chat_list;

    @Data
    public class FailedChatList {
        private String chat_id;
        private Integer errcode;
        private String errmsg;
    }

}
