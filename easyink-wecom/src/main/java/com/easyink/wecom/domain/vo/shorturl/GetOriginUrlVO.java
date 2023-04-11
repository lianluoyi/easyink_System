package com.easyink.wecom.domain.vo.shorturl;

import lombok.Data;

/**
 * 类名: 获取原链接和类型返回参数
 *
 * @author : silver_chariot
 * @date : 2023/3/13 20:07
 **/
@Data
public class GetOriginUrlVO {
    /**
     * 需要转发的链接
     */
    private String redirectUrl;
    /**
     * 短链类型  {@link com.easyink.common.shorturl.enums.ShortUrlTypeEnum }
     */
    private Integer type;


    public GetOriginUrlVO(Integer type, String redirectUrl) {
        setType(type);
        setRedirectUrl(redirectUrl);
    }
}
