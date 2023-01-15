package com.easyink.wecom.domain.vo.unionid;

import com.alibaba.fastjson.annotation.JSONField;
import com.easyink.wecom.domain.dto.customer.ExternalContact;
import lombok.Data;

/**
 * 类名: 根据external_user_id获取unionId的返回结果
 *
 * @author : silver_chariot
 * @date : 2023/1/5 9:28
 **/
@Data
public class GetUnionIdVO {

    /**
     * 外部联系人userid
     */
    private String externalUserId;
    /**
     * 名称
     */
    private String name ;
    /**
     * 头像url
     */
    private String avatar ;
    /**
     * 公众平台unionId
     */
    private String unionId;
    /**
     * 外部联系人性别 0-未知 1-男性 2-女性性别 1 为男
     */
    private Integer gender ;

    public GetUnionIdVO(ExternalContact externalContact) {
        this.externalUserId = externalContact.getExternalUserid();
        this.avatar = externalContact.getAvatar();
        this.name = externalContact.getName() ;
        this.gender = externalContact.getGender();
        this.unionId = externalContact.getUnionid() ;
    }
}
