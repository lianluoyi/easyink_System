package com.easywecom.wecom.domain.dto.welcomemsg;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 入群欢迎语素材修改DTO
 *
 * @author tigger
 * 2022/1/6 21:39
 **/
@AllArgsConstructor
@Data
public class GroupWelcomeMsgDeleteDTO {
    private String template_id;

    private Integer agentid;
}
