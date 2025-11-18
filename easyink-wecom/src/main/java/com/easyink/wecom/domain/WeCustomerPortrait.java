package com.easyink.wecom.domain;

import com.easyink.common.annotation.EncryptField;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 * @description: 客户画像实体VO
 * @create: 2021-03-03 11:49
 **/
@Data
public class WeCustomerPortrait {

    @ApiModelProperty(value = "外部联系人id")
    private String externalUserid;

    @ApiModelProperty(value = "企业员工id")
    private String userId;

    @ApiModelProperty(value = "客户与企业员工关系id")
    private String flowerCustomerRelId;

    @ApiModelProperty(value = "公司id")
    private String corpId;

    @ApiModelProperty(value = "客户昵称")
    private String name;

    @ApiModelProperty(value = "客户性别(0-未知 1-男性 2-女性)")
    private Integer gender;

    @ApiModelProperty(value = "客户备注")
    private String remark;

    @ApiModelProperty(value = "备注客户手机号")
    @EncryptField(EncryptField.FieldType.MOBILE)
    private String remarkMobiles;
    private String remarkMobilesEncrypt;

    @ApiModelProperty(value = "客户生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @ApiModelProperty(value = "添加客户的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty(value = "该成员添加此客户的来源，")
    private String addWay;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "地址")
    @EncryptField(EncryptField.FieldType.ADDRESS)
    private String address;
    private String addressEncrypt;
    /**
     * qq
     */
    private String qq;
    /**
     * 职业
     */
    private String position;
    /**
     * 公司
     */
    private String remarkCorpName;
    /**
     * 描述
     */
    private String description;
    /**
     * 年纪
     */
    private int age;
    /**
     * 客户头像
     */
    private String avatar;

    /**
     * 客户标签
     */
    private List<WeTagGroup> weTagGroupList;

    @ApiModelProperty(value = "客户社交关系")
    private WeCustomerSocialConn socialConn;

    @ApiModelProperty(value = "'外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户'")
    private Integer type;

    @ApiModelProperty(value = "公司名称")
    private String corpName;

    @ApiModelProperty(value = "公司全称")
    private String corpFullName;

    @ApiModelProperty(value = "扩展字段属性集合")
    private List<BaseExtendPropertyRel> extendProperties = new ArrayList<>();

    @ApiModelProperty(value = "状态（0正常 1已流失 2员工删除客户 3待继承 4转接中）")
    private String status;

    /**
     * 客户实体转换成客户画像实体
     *
     * @param customer {@link WeCustomerVO}
     */
    public WeCustomerPortrait(WeCustomerVO customer) {
        BeanUtils.copyPropertiesASM(customer, this);
        this.flowerCustomerRelId = customer.getRelId().toString();
    }
}
