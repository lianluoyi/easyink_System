package com.easyink.common.shorturl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 表单短链附加信息
 *
 * @author wx
 * 2023/1/15 16:57
 **/
@Data
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class FormShortUrlAppendInfo extends BaseShortUrlAppendInfo{

    /**
     * 表单id
     */
    private Long formId;

    /**
     * 渠道 {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     */
    private Integer channelType;

    /**
     * 公众号appId
     */
    private String appId;

}
