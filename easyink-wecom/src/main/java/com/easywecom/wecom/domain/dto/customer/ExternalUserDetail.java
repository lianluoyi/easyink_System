package com.easywecom.wecom.domain.dto.customer;

import com.easywecom.wecom.domain.dto.WeResultDTO;
import lombok.Data;

import java.util.List;

/**
 * @description: 获取客户详情
 * @author admin
 * @create: 2020-10-19 22:33
 **/
@Data
public class ExternalUserDetail extends WeResultDTO {

    /**
     * 客户详情
     */
    private ExternalContact external_contact;


    /**
     * 客户联系人
     */
    private List<FollowUser> follow_user;

    private FollowInfo follow_info;

    private String external_userid;

}
