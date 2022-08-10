package com.easywecom.common.utils.ip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.constant.Constants;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.http.HttpUtils;
import com.easywecom.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取地址类
 *
 * @author admin
 */
@Slf4j
public class AddressUtils {

    // IP地址查询
    private static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    private static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip) {
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        RuoYiConfig ruoYiConfig = SpringUtils.getBean(RuoYiConfig.class);
        if (ruoYiConfig.isAddressEnabled()) {
            try {
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSON.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
                return String.format("%s %s", region, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN;
    }
}
