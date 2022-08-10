package com.easyink.wecom.domain.dto;

import lombok.Data;

/**
 * @author admin
 * @version 1.0
 * @date 2021/2/9 14:58
 */
@Data
public class WeChatUserDTO {
    private String userid;
    private String unionid;
    private String name;
    private String avatar;
}
