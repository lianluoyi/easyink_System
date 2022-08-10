package com.easyink.wecom.domain.dto.autoconfig;

import lombok.Data;

import java.util.List;

/**
 * 类名: 获取可调用应用列表
 * {"auth_list":{"appid_list":["5629500240553829","5629502523554136"]}}
 *
 * @author: 1*+
 * @date: 2021-08-24$ 14:42$
 */
@Data
public class WeApiAccessibleAppsResp {


    private AuthList auth_list;

    @Data
    public static class AuthList {
        private List<String> appid_list;
    }

}
