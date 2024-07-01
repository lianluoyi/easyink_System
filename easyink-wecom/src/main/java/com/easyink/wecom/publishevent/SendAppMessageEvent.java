package com.easyink.wecom.publishevent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发送应用app消息事件
 * @author tigger
 * 2024/2/19 16:08
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendAppMessageEvent {

    /**
     * 企业id
     */
    private String corpId;

    /**
     * 发送的员工id列表
     */
    private List<String> userIdList;

    /**
     * 消息模板
     */
    private String msgContentTemplate;
    /**
     * 替换消息模板的参数
     */
    private String[] msgParams;


}
