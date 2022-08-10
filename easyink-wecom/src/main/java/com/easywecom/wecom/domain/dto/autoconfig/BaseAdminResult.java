package com.easywecom.wecom.domain.dto.autoconfig;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 类名: 基础admin返回结果
 *
 * @author: 1*+
 * @date: 2021-08-24$ 10:38$
 */
@Data
public class BaseAdminResult<T> {

    private T data;

    private ErrResult result;


    @Data
    public static class ErrResult {
        private Integer errCode;
        private String message;
        private String humanMessage;

        public String getMessage() {
            if (StringUtils.isBlank(message)) {
                return humanMessage;
            }
            return message;
        }

    }
}
