package com.easyink.common.shorturl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 类名: 雷达短链附加信息
 *
 * @author : silver_chariot
 * @date : 2022/7/21 11:40
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RadarShortUrlAppendInfo extends BaseShortUrlAppendInfo{

    /**
     * 雷达id
     */
    private Long radarId;
    /**
     * 渠道id ,如果是系统默认的渠道使用{@link com.easyink.common.enums.radar.RadarChannelEnum}
     */
    private Integer channelType;

    /**
     * 详情(如果是员工活码,则为员工活码使用场景，如果是新客进群则为新客进群的活码名称,如果是SOP则为SOP名称，如果是群日历，则为日历名称，如果是自定义渠道则为自定义渠道的渠道名)
     */
    private String detail;
    /**
     * 企业id ( 主要用于获取appid配置,如果后续改成后端直接跳转则可去除)
     */
    private String corpId;


}
