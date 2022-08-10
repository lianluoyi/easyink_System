package com.easyink.wecom.domain.dto.welcomemsg;

import lombok.Data;

/**
 * 入群欢迎语素材管理返回实体
 *
 * @author tigger
 * 2022/1/5 15:59
 **/
@Data
public class GroupWelcomeMsgResult {

    private Integer errcode;

    private String errmsg;

    private String template_id;
}
