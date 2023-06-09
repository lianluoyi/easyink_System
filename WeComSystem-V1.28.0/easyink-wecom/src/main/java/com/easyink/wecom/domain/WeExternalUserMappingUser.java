package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: WeExternalUserMappingUser
 *
 * @author: 1*+
 * @date: 2021-11-30 14:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("we_external_user_mapping_user")
public class WeExternalUserMappingUser {


    @TableField("external_corp_id")
    private String externalCorpId;

    @TableField("external_user_id")
    private String externalUserId;

    @TableField("corp_id")
    private String corpId;

    @TableField("user_id")
    private String userId;


}
