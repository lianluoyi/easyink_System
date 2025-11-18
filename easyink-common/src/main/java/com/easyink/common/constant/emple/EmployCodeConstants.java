package com.easyink.common.constant.emple;

import cn.hutool.core.util.StrUtil;
import com.easyink.common.shorturl.enums.ShortUrlTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 员工活码常量类
 * @author tigger
 * 2025/1/13 10:18
 **/
@Data
public class EmployCodeConstants {

    /**
     * 专属活码过期时间, 单位: 小时
     */
    public static final Integer CUSTOMER_EMPLOY_EXPIRE_HOURS = 24;
    /**
     * 专属活码企微state前缀
     */
    public static final String CUSTOMER_EMPLOY_STATE_PREFIX = "ce" + StrUtil.UNDERLINE;
    /**
     * 客户专属活码链接（长链）中域名占位符
     */
    public static final String DOMAIN_PLACEHOLDER = "{domain}";
    /**
     * 客户专属活码链接（长链）中employCodeId占位符
     */
    public static final String EMPLOY_CODE_ID_PLACEHOLDER = "{employCodeId}";
    /**
     * 客户专属活码链接（长链）中corpId占位符
     */
    public static final String CORP_ID_PLACEHOLDER = "{corpId}";
    /**
     * 客户专属活码链接（长链）中userId占位符
     */
    public static final String USER_ID_PLACEHOLDER = "{userId}";
    /**
     * 客户专属活码链接（长链）中shortUrlType占位符
     */
    public static final String SHORT_URL_TYPE_PLACEHOLDER = "{shortUrlType}";
    /**
     * 客户专属活码长链
     */
    public static final String CUSTOMER_EMPLOY_CODE_URL = DOMAIN_PLACEHOLDER + "?"
            + "employCodeId=" + EMPLOY_CODE_ID_PLACEHOLDER
            + "&" + "corpId=" + CORP_ID_PLACEHOLDER
            + "&" + "shortUrlType=" + SHORT_URL_TYPE_PLACEHOLDER;

    private EmployCodeConstants() {
    }

    /**
     * 生成客户专属活码长链接
     *
     * @param domain       h5中间也地址
     * @param employCodeId 员工活码id
     * @param corpId
     * @return
     */
    public static String genCustomerEmployLongUrl(String domain, Long employCodeId, String corpId) {
        if (employCodeId == null || StringUtils.isAnyBlank(domain)) {
            return null;
        }
        return CUSTOMER_EMPLOY_CODE_URL.replace(DOMAIN_PLACEHOLDER, domain)
                .replace(EMPLOY_CODE_ID_PLACEHOLDER, String.valueOf(employCodeId))
                .replace(CORP_ID_PLACEHOLDER, corpId)
                .replace(SHORT_URL_TYPE_PLACEHOLDER, ShortUrlTypeEnum.CUSTOMER_USER_CODE.getType().toString());
    }
}
