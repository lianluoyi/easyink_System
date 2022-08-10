package com.easyink.wecom.domain.dto.welcomemsg;

import com.easyink.wecom.domain.dto.common.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 入群欢迎语素材添加DTO
 *
 * @author admin
 * @date 2020-11-18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupWelcomeMsgAddDTO extends Messages {


    /**
     * 授权方安装的应用agentid。仅旧的第三方多应用套件需要填此参数
     */
    private Integer agentid;
    /**
     * 是否通知成员将这条入群欢迎语应用到客户群中，0-不通知，1-通知， 不填则通知
     */
    private Integer notify;




}
