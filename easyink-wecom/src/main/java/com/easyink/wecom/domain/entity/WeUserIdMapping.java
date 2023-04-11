package com.easyink.wecom.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工userId明密文映射表(WeUserIdMapping)实体类
 *
 * @author wx
 * @since 2023-03-14 18:02:22
 */
@NoArgsConstructor
@Data
@TableName("we_user_id_mapping")
@AllArgsConstructor
public class WeUserIdMapping implements Serializable {

    private static final long serialVersionUID = -65143439989430863L;

    @ApiModelProperty("密文企业id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("明文userId")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("密文userId")
    @TableField("open_user_id")
    private String openUserId;


}

