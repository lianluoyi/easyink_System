package com.easyink.wecom.domain.dto;

import lombok.Data;

/**
 * @description: 发送应用消息响应应用结果
 * @author admin
 * @create: 2020-10-27 22:46
 **/
@Data
public class WeMessagePushResultDTO extends WeResultDTO {

    /**
     * 无效用户
     */
    private String invaliduser;

    /**
     * 无效单位
     */
    private String invalidparty;

    /**
     * 无效标签
     */
    private String invalidtag;

}
