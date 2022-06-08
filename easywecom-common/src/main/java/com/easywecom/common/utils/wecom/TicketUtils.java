package com.easywecom.common.utils.wecom;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;

/**
 * @author admin
 * @description
 * @date 2021/1/6 12:00
 **/
@Slf4j
public class TicketUtils {

    public static HashMap getSignatureMap(String ticketVaule, String url) {
        String nonceStr = RandomStringUtils.randomAlphanumeric(10);
        Long timestamp = System.currentTimeMillis() / 1000;
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("jsapi_ticket=").append(ticketVaule)
                .append("&noncestr=").append(nonceStr)
                .append("&timestamp=").append(timestamp)
                .append("&url=").append(url);
        log.info("H5加密串：{}", strBuild.toString());
        String signature = DigestUtils.sha1Hex(strBuild.toString());
        return new HashMap(16) {{
            put("nonceStr", nonceStr);
            put("timestamp", timestamp);
            put("signature", signature);
        }};
    }

}
