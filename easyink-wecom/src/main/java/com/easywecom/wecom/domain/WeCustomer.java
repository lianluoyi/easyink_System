package com.easywecom.wecom.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easywecom.common.annotation.Excel;
import com.easywecom.common.core.domain.BaseEntity;
import com.easywecom.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.dto.customer.ExternalContact;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 企业微信客户对象 we_customer
 *
 * @author 佚名
 * @date 2021-7-28
 */
@ApiModel("企业微信客户对象")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_customer")
@Slf4j
public class WeCustomer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "外部联系人的userid")
    @TableId
    @NotBlank(message = "外部联系人的id不可为空")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty(value = "外部联系人名称")
    @TableField("name")
    @Excel(name = "客户",sort = 1)
    private String name;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty(value = "外部联系人头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。")
    @TableField("unionid")
    private String unionid;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", dateFormat = "yyyy-MM-dd" ,sort = 9)
    private Date birthday;

    @ApiModelProperty(value = "客户企业简称")
    @TableField("corp_name")
    @Excel(name = "公司",sort = 3)
    private String corpName;

    @ApiModelProperty(value = "客户企业全称")
    @TableField("corp_full_name")
    private String corpFullName;

    @ApiModelProperty(value = "客户职位")
    @TableField("position")
    private String position;

    @ApiModelProperty(value = "是否开启会话存档 0：关闭 1：开启")
    @TableField("is_open_chat")
    private Integer isOpenChat;

    /**
     * 添加人员
     */
    @TableField(exist = false)
    private List<WeFlowerCustomerRel> weFlowerCustomerRels;

    /**
     * 添加人id
     */
    @TableField(exist = false)
    private String userIds;

    /**
     * 标签
     */
    @TableField(exist = false)
    private String tagIds;

    @TableField(exist = false)
    @JSONField(defaultValue = "0")
    private Integer status;

    /**
     * 部门
     */
    @TableField(exist = false)
    private String departmentIds;

    /**
     * 添加人名称
     */
    @TableField(exist = false)
    @Excel(name = "所属员工",sort = 5)
    private String userName;

    /**
     * 员工id
     */
    @TableField(exist = false)
    private String userId;


    /**
     * 创建者
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String updateBy;

    /**
     * 备注
     */
    @TableField(exist = false)
    @Excel(name = "备注",sort = 2)
    private String remark;
    /**
     * 手机号
     */
    @TableField(exist = false)
    @Excel(name = "电话",sort = 10)
    private String phone;
    /**
     * 描述
     */
    @TableField(exist = false)
    private String desc;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询过滤类型：1：根据客户id：external_userid过滤，2：根据员工id：user_id和客户id：external_userid")
    private String queryType;


    @ApiModelProperty(value = "部门")
    @Excel(name = "所属部门", sort = 6)
    @TableField(exist = false)
    private String departmentName;

    @ApiModelProperty(value = "跟进人离职时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dimissionTime;

    @ApiModelProperty(value = "客户自定义字段")
    @TableField(exist = false)
    private List<BaseExtendPropertyRel> extendProperties = new ArrayList<>();

    @ApiModelProperty(value = "接替状态,1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录")
    @TableField(exist = false)
    private Integer transferStatus;


    /**
     * 根据API返回的客户详情实体 构建数据交互的企微客户实体
     *
     * @param externalContact {@link ExternalContact}
     * @param corpId          企业ID
     */
    public WeCustomer(ExternalContact externalContact, String corpId) {
        BeanUtils.copyPropertiesignoreOther(externalContact, this);
        this.corpId = corpId;
        if (StringUtils.isBlank(externalContact.getUnionid())) {
            this.unionid = StringUtils.EMPTY;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeCustomer weCustomer = (WeCustomer) o;
        return Objects.equals(corpId, weCustomer.getCorpId())
                && Objects.equals(externalUserid, weCustomer.externalUserid);

    }

    @Override
    public int hashCode() {
        return Objects.hash(corpId, externalUserid);
    }
}
