package com.easywecom.wecom.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easywecom.common.core.domain.wecom.BaseExtendPropertyRel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 客户-自定义属性关系表
 *
 * @author : silver_chariot
 * @date : 2021/11/10 17:47
 */
@Data
@ApiModel("客户-自定义属性关系")
@AllArgsConstructor
@NoArgsConstructor
public class WeCustomerExtendPropertyRel extends BaseExtendPropertyRel {
    /**
     * 企业id
     */
    @TableField("corp_id")
    @ApiModelProperty(value = "企业id")
    private String corpId;

    @TableField("user_id")
    @ApiModelProperty(value = "跟进人userId")
    private String userId;
    /**
     * 成员客户关系ID
     */
    @TableField("external_userid")
    @ApiModelProperty(value = "成员客户关系ID")
    private String externalUserid;

    /**
     * 构造方法
     *
     * @param source         {@link BaseExtendPropertyRel}
     * @param corpId         企业id
     * @param externalUserId 客户userId
     * @param userId         跟进人userId
     */
    public WeCustomerExtendPropertyRel(BaseExtendPropertyRel source, String corpId, String externalUserId, String userId) {
        setExtendPropertyId(source.getExtendPropertyId());
        setPropertyValue(source.getPropertyValue());
        this.corpId = corpId;
        this.externalUserid = externalUserId;
        this.userId = userId;
    }
}
