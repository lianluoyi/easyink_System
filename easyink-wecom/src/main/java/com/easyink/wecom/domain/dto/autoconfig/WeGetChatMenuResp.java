package com.easyink.wecom.domain.dto.autoconfig;

import lombok.Data;

import java.util.List;

/**
 * 类名: 获取所有的侧边栏菜单
 *
 * @author: 1*+
 * @date: 2021-08-25$ 11:12$
 */
@Data
public class WeGetChatMenuResp {


    private List<ChatMenu> edit_banner_block;

    private List<ChatMenu> publish_banner_block;

    @Data
    public static class ChatMenu {
        private String item_id;
        private String item_info;
        private String item_name;
        private Integer item_type;
        private CorpApp corp_app;
    }

    @Data
    public static class CorpApp {
        private String app_id;
    }
}
