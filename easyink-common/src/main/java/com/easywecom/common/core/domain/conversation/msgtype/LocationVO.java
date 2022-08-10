package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 地理位置VO
 *
 * @author tigger
 * 2022/1/26 15:50
 **/
@Data
public class LocationVO extends AttachmentBaseVO{
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer zoom;
    private String title;

}
