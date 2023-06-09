package com.easyink.common.core.domain.conversation;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 返回结果VO
 *
 * @author tigger
 * 2022/1/25 16:55
 **/
@Data
public class FinanceResVO {

    private Integer errcode;

    private String errmsg;

    private List<EncryptVO> chatdata;




    @Data
    @Getter
    public static class EncryptVO{
        private Integer publickey_ver;

        private String encrypt_chat_msg;

        private String msgid;

        private String encrypt_random_key;

        private Integer seq;

    }
}
