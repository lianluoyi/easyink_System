package com.easyink.wecom.domain.dto.customer;

import cn.hutool.core.date.DateUtil;
import com.easyink.common.annotation.SysProperty;
import com.easyink.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 类名: 编辑客户参数
 *
 * @author : silver_chariot
 * @date : 2021/11/18 14:10
 */
@Data
public class EditCustomerDTO {

    //*** 客户基本属性

    @ApiModelProperty(value = "企业id,不必传")
    private String corpId;
    @ApiModelProperty(value = "外部联系人的userid", required = true)
    @NotBlank(message = "外部联系人的id不可为空")
    private String externalUserid;

    @ApiModelProperty(value = "跟进人id", required = true)
    @NotBlank(message = "跟进人userId不能为空")
    private String userId;

    @ApiModelProperty(value = "生日")
    @SysProperty(name = "出生日期", dateFormat = "yyyy-MM-dd")
    private String birthday;


    //*** 跟进人对客户的备注属性

    @ApiModelProperty(value = "跟进人对客户的备注")
    @SysProperty(name = "备注")
    private String remark;

    @ApiModelProperty(value = "跟进人对客户的描述")
    @SysProperty(name = "备注")
    private String description;

    @ApiModelProperty(value = "跟进人对客户的备注公司名字")
    private String remarkCorpName;

    @ApiModelProperty(value = "跟进人对客户的备注电话")
    @SysProperty(name = "电话")
    private String remarkMobiles;

    @ApiModelProperty(value = "跟进人对客户的备注地址")
    @SysProperty(name = "地址")
    private String address;

    @ApiModelProperty(value = "跟进人对客户的备注邮箱")
    @SysProperty(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "客户扩展属性集合")
    private List<BaseExtendPropertyRel> extendProperties;

    // *** 跟进人对客户所打的标签

    @ApiModelProperty(value = "要给客户编辑的标签,列表内传参格式与‘批量打标签'一样")
    private List<WeTag> editTag;

    @ApiModelProperty(value = "操作人,不必传")
    private String updateBy;


    /**
     * 转换成 weCustomer 客户实体
     *
     * @return {@link WeCustomer}
     */
    public WeCustomer transferToCustomer() {
        WeCustomer weCustomer = new WeCustomer();
        if (StringUtils.isBlank(birthday)) {
            weCustomer.setBirthday(null);
        }else {
            weCustomer.setBirthday(DateUtil.parse(birthday, DateUtils.YYYY_MM_DD));
        }
        weCustomer.setExtendProperties(this.extendProperties);
        weCustomer.setCorpId(this.corpId);
        weCustomer.setExternalUserid(this.externalUserid);
        weCustomer.setUserId(this.userId);
        return weCustomer;
    }

    /**
     * 转换成 WeFlowerCustomerRel 跟进人与客户关系实体
     *
     * @return {@link WeFlowerCustomerRel}
     */
    public WeFlowerCustomerRel transferToCustomerRel() {
        WeFlowerCustomerRel rel = new WeFlowerCustomerRel();
        rel.setCorpId(this.corpId);
        rel.setUserId(this.userId);
        rel.setExternalUserid(this.externalUserid);

        rel.setDescription(this.description);
        rel.setRemark(this.remark);
        rel.setRemarkMobiles(this.remarkMobiles);
        rel.setRemarkCorpName(this.remarkCorpName);
        rel.setEmail(this.email);
        rel.setAddress(this.address);
        return rel;

    }
}
