package com.easyink.wecom.domain.entity.customer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.enums.CustomerExtendPropertyEnum;
import com.easyink.wecom.domain.model.customer.AddressModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 类名: 客户-自定义属性关系表
 *
 * @author : silver_chariot
 * @date : 2021/11/10 17:47
 */
@EqualsAndHashCode(callSuper = true)
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

    @TableField(exist = false)
    private String originalPropertyValue;

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
        setPropertyType(source.getPropertyType());
        setPropertyValue(source.getPropertyValue());

        if(CustomerExtendPropertyEnum.LOCATION.getType().equals(source.getPropertyType())){
            AddressModel addressModel = JSON.parseObject(source.getPropertyValue(), AddressModel.class);
            if(addressModel != null){
                SensitiveFieldProcessor.processForSave(addressModel);
                this.setProvince(addressModel.getProvince());
                this.setCity(addressModel.getCity());
                this.setArea(addressModel.getArea());
                this.setTown(addressModel.getTown());
                this.setDetailAddress(addressModel.getDetailAddress());
                this.setDetailAddressEncrypt(addressModel.getDetailAddressEncrypt());
                setPropertyValue(JSON.toJSONString(addressModel));
            }
        }
        this.corpId = corpId;
        this.externalUserid = externalUserId;
        this.userId = userId;

    }
}
