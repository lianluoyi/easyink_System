package com.easywecom.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.utils.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 客户标签关系对象 we_flower_customer_tag_rel
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@TableName("we_flower_customer_tag_rel")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeFlowerCustomerTagRel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id;

    @ApiModelProperty(value = "添加客户的企业微信用户")
    @TableField("flower_customer_rel_id")
    private Long flowerCustomerRelId;

    @ApiModelProperty(value = "标签id")
    @TableField("tag_id")
    private String tagId;

    @ApiModelProperty(value = "")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 标签组ID
     */
    @TableField(exist = false)
    private String groupId;

    /**
     * 标签名
     */
    @TableField(exist = false)
    private String tagName;

    @ApiModelProperty(value = "")
    @TableField("external_userid")
    private String externalUserid;

    public WeFlowerCustomerTagRel(Long flowerCustomerRelId, String tagId, String externalUserid) {
        id = SnowFlakeUtil.nextId();
        this.flowerCustomerRelId = flowerCustomerRelId;
        this.tagId = tagId;
        this.externalUserid = externalUserid;
    }
}
