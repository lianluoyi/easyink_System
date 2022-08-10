package com.easywecom.wecom.domain.dto.welcomemsg;

import com.easywecom.wecom.domain.dto.common.Messages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群欢迎语更新DTO
 *
 * @author tigger
 * 2022/1/17 19:15
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupWelcomeMsgUpdateDTO extends Messages {

    /**
     * 模板id
     */
    private String template_id;
    /**
     * 授权方安装的应用agentid。仅旧的第三方多应用套件需要填此参数
     */
    private Integer agentid;
}
