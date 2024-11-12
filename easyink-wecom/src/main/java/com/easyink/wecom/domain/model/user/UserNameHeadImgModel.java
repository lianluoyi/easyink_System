package com.easyink.wecom.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工昵称头像model
 * @author tigger
 * 2023/12/26 16:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNameHeadImgModel {

    /**
     * 员工id
     */
    private String userId;
    /**
     * 员工昵称
     */
    private String userName;
    /**
     * 头像
     */
    private String headImg;
}
