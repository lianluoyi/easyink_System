package com.easyink.wecom.domain.dto;

import com.easyink.wecom.domain.dto.common.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 发送好友欢迎语DTO
 *
 * @author admin
 * @date 2020-11-18
 */
@Data
@Builder
public class WeWelcomeMsg {
    /**
     * 必填
     */
    private String welcome_code;

    private Text text;

    private List<Attachment> attachments;

}
