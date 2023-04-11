package com.easyink.wecom.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外部联系人externalUserId明密文映射表(WeExternalUseridMapping)实体类
 *
 * @author wx
 * @since 2023-03-14 18:01:42
 */
@Data
@NoArgsConstructor
@TableName("we_external_userid_mapping")
@AllArgsConstructor
public class WeExternalUseridMapping implements Serializable {

    private static final long serialVersionUID = 845683925684050552L;

    @ApiModelProperty("密文corpId")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("明文externalUserId")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty("密文externalUserId")
    @TableField("open_external_userid")
    private String openExternalUserid;

}

