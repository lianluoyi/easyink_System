package com.easyink.wecom.domain.dto;

import com.easyink.common.core.domain.RootEntity;
import lombok.Data;

/**
 * 类名： WeCustomerMessageDTO
 *
 * @author 佚名
 * @date 2021/8/31 21:51
 */
@Data
public class WeCustomerMessageDTO extends RootEntity {
    /**
     * 是否是admin
     */
    private boolean adminFlag;
    /**
     * 部门
     */
    private String[] departments;
    /**
     * 发送人
     */
    private String sender;
    /**
     * 内容
     */
    private String content;
    /**
     * 发送方式
     */
    private String pushType;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 企业Id
     */
    private String corpId;

    public WeCustomerMessageDTO(boolean adminFlag, String sender, String content, String pushType, String beginTime, String endTime) {
        this.adminFlag = adminFlag;
        this.sender = sender;
        this.content = content;
        this.pushType = pushType;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
}
