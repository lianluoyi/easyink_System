package com.easyink.common.enums.wechatopen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;

/**
 * 微信开放平台枚举
 *
 * @author wx
 * 2023/1/10 19:43
 **/
@Data
public class WechatOpenEnum {


    @AllArgsConstructor
    public enum ServiceType {
        UNKNOWN(-1, "未知"),
        SUBSCRIBE(0, "订阅号"),
        UPDATED_SUBSCRIBE(1, "由历史老帐号升级后的订阅号"),
        SERVICE(2, "服务号"),
        ;
        @Getter
        private Integer code;
        @Getter
        private String desc;

        /**
         * 根据CODE获取公众号类型
         *
         * @param code code
         * @return 公众号类型
         */
        public static ServiceType getByCode(Integer code) {
            if (code == null) {
                return UNKNOWN;
            }
            return Arrays.stream(values()).filter(obj -> obj.getCode().equals(code)).findFirst().orElse(UNKNOWN);
        }
    }

    /**
     * 使用公众号的类型
     */
    @AllArgsConstructor
    public enum UseOfficeAccountType {
        UNKNOWN(-1, "未知"),
        RADAR(1, "雷达"),
        FORM(2, "智能表单"),
        EMPLE_CODE(3, "员工活码"),
        GROUP_CODE(4, "群活码"),
        ;
        @Getter
        private Integer code;
        @Getter
        private String desc;

        /**
         * 根据CODE使用公众号的类型
         *
         * @param code code
         * @return 公众号类型
         */
        public static UseOfficeAccountType getByCode(Integer code) {
            if (code == null) {
                return UNKNOWN;
            }
            return Arrays.stream(values()).filter(obj -> obj.getCode().equals(code)).findFirst().orElse(UNKNOWN);
        }
    }
}
