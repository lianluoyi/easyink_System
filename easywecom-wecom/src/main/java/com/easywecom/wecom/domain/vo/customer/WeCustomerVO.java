package com.easywecom.wecom.domain.vo.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.easywecom.common.core.domain.wecom.BaseExtendPropertyRel;
import com.easywecom.wecom.domain.WeFlowerCustomerTagRel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 类名: 客户详情实体
 *
 * @author : silver_chariot
 * @date : 2021/12/1 20:33
 */
@Data
@NoArgsConstructor
public class WeCustomerVO {
    // 客户信息

    @ApiModelProperty(value = "企业id")
    private String corpId;
    @ApiModelProperty(value = "外部联系人userId")
    private String externalUserid;
    @ApiModelProperty(value = "外部联系人姓名")
    private String name;
    @ApiModelProperty(value = "外部联系人头像")
    private String avatar;
    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    private Integer type;
    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性")
    private Integer gender;
    @ApiModelProperty(value = "客户企业简称")
    private String corpName;
    @ApiModelProperty(value = "客户企业全称")
    private String corpFullName;
    @ApiModelProperty(value = "客户职位")
    private String position;
    @ApiModelProperty(value = "是否开启会话存档")
    private Integer isOpenChat;
    @ApiModelProperty(value = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    // 跟进人备注信息

    @ApiModelProperty(value = "跟进人关系id")
    private Long relId;
    @ApiModelProperty(value = "跟进人userId")
    private String userId;
    @ApiModelProperty(value = "跟进人对客户的备注")
    private String remark;
    @ApiModelProperty(value = "跟进人对客户的描述")
    private String description;
    @ApiModelProperty(value = "客户创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @ApiModelProperty(value = "跟进人对客户的备注公司名称")
    private String remarkCorpName;
    @ApiModelProperty(value = "跟进人对客户的备注电话")
    private String remarkMobiles;
    @ApiModelProperty(value = "跟进人对客户备注的qq")
    private String qq;
    @ApiModelProperty(value = "跟进人对客户备注的地址")
    private String address;
    @ApiModelProperty(value = "跟进人对客户备注的email")
    private String email;
    @ApiModelProperty(value = "该成员添加此客户的来源")
    private String addWay;
    @ApiModelProperty(value = "客户的状态,0:正常,1:已流失,2:员工删除客户,3:待继承,4:转接中")
    private String status;

    @ApiModelProperty(value = "该成员添加此客户的来源add_way为10时，对应的视频号信息")
    @TableField("wechat_channel")
    private String wechatChannel;

    @ApiModelProperty(value = "跟进人名称")
    private String userName;
    @ApiModelProperty(value = "跟进人部门名称")
    private String department;

    // 标签信息

    @ApiModelProperty(value = "跟进人对客户打的标签集合")
    private List<WeFlowerCustomerTagRel> weFlowerCustomerTagRels;

    // 扩展信息

    @ApiModelProperty(value = "跟进人对客户备注的自定义字段信息")
    private List<BaseExtendPropertyRel> extendProperties;

    @ApiModelProperty(value = "接替状态，接替状态,1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录")
    private Integer transferStatus;


}
