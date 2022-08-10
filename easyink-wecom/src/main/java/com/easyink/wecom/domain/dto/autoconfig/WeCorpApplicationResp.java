package com.easyink.wecom.domain.dto.autoconfig;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.List;

/**
 * 类名: 企业应用返回
 *
 * @author: 1*+
 * @date: 2021-08-24$ 13:33$
 */
@Data
public class WeCorpApplicationResp {


    private JSONArray custom_app;
    private JSONArray default_app;
    private List<OpenapiApp> openapi_app;
    private boolean testUser;

    @Data
    public static class OpenapiApp {
        private String aes_app_id;
        private String app_id;
        private Integer app_open;
        private Integer app_flag;
        private Integer app_open_id;
        private Integer arch_rw_flag;
        private AppPerm app_perm;
        private String corp_id;
        private String name;
        private Boolean callback_open;
        private String redirect_domain;
        private String redirect_domain2;
    }


    @Data
    public static class AppPerm {
        private Integer app_perm_flag;
        private List<String> partyids;
    }

}
