package com.easyink.wecom.domain.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 附件
 *
 * @author tigger
 * 2022/1/18 17:26
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Attachments extends Messages implements Attachment {

    @ApiModelProperty("消息类型")
    private String msgtype;
}
