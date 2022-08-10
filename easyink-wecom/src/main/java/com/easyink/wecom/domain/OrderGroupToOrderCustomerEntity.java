package com.easyink.wecom.domain;

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
 * 类名： 企业客户群与工单客户绑定关系
 *
 * @author 佚名
 * @date 2021-12-13 18:43:30
 */
@Data
@TableName("order_group_to_order_customer")
@ApiModel("企业客户群与工单客户绑定关系实体")
public class OrderGroupToOrderCustomerEntity {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 外部群ID
     */
    @ApiModelProperty(value = "外部群ID", required = true)
    @TableField("chat_id")
    @NotBlank
    private String chatId;
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
    @JsonIgnore
    private String networkId;
    /**
     * 工单客户ID
     */
    @ApiModelProperty(value = "工单客户ID", required = true)
    @TableField("order_customer_id")
    private String orderCustomerId;
    /**
     * 工单客户名
     */
    @ApiModelProperty(value = "工单客户名", required = true)
    @TableField("order_customer_name")
    private String orderCustomerName;
    /**
     * 绑定时间
     */
    @ApiModelProperty(value = "绑定时间")
    @TableField("bind_time")
    private Date bindTime;

}
