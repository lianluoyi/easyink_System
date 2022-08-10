package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户和角色关联
 *
 * @author : silver_chariot
 * @date : 2021/8/18 15:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("we_user_role")
@ApiModel
@Builder
public class WeUserRole {

    @TableField("corp_id")
    @ApiModelProperty(name = "企业ID")
    private String corpId;

    @TableField("user_id")
    @ApiModelProperty(name = "企微用户ID")
    private String userId;

    @TableField("role_id")
    @ApiModelProperty(name = "角色ID")
    private Long roleId;
}
