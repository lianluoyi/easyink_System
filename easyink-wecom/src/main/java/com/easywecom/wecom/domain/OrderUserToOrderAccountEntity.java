package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * 类名： 企业员工与工单帐号绑定关系表
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
@Data
@TableName("order_user_to_order_account")
@ApiModel("企业员工与工单帐号绑定关系表实体")
public class OrderUserToOrderAccountEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 企业员工ID
     */
    @ApiModelProperty(value = "企业员工ID", required = true)
    @TableField("user_id")
    @NotBlank
    private String userId;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID", hidden = true)
    @TableField("corp_id")
    @JsonIgnore
    private String corpId;
    /**
     * 网点ID
     */
    @ApiModelProperty(value = "网点ID", hidden = true)
    @TableField("network_id")
    private String networkId;
    /**
     * 工单帐号ID
     */
    @ApiModelProperty(value = "工单帐号ID", required = true)
    @TableField("order_user_id")
    @NotBlank
    private String orderUserId;
    /**
     * 工单帐号名
     */
    @ApiModelProperty(value = "工单帐号名", required = true)
    @TableField("order_user_name")
    @NotBlank
    private String orderUserName;
    /**
     * 绑定时间
     */
    @ApiModelProperty(value = "绑定时间")
    @TableField("bind_time")
    private Date bindTime;

}
