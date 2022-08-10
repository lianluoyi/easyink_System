package com.easyink.wecom.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 类名: 查询企微PRO的客户资料返回实体
 *
 * @author : silver_chariot
 * @date : 2021/11/2 16:12
 */
@Data
@ApiModel("查询企微PRO的客户资料返回实体")
public class QueryCustomerFromPlusVO {

    @ApiModelProperty(value = "外部联系人的userid")
    private String externalUserid;

    @ApiModelProperty(value = "外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。")
    private String unionId;

    @ApiModelProperty(value = "外部联系人名称")
    private String name;

    @ApiModelProperty(value = "外部联系人头像")
    private String avatar;

    @ApiModelProperty(value = "外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户")
    private Integer type;

    @ApiModelProperty(value = "外部联系人性别 0-未知 1-男性 2-女性")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    private Date birthday;

    @ApiModelProperty(value = "企业ID")
    private String corpId;

    @ApiModelProperty(value = "客户企业简称")
    private String corpName;

    @ApiModelProperty(value = "客户企业全称")
    private String corpFullName;

    @ApiModelProperty(value = "客户职位")
    private String position;

    @ApiModelProperty(value = "跟进人和备注详情")
    private FollowUserInfo followUserInfo;

    @Data
    public class FollowUserInfo {
        @ApiModelProperty(value = "企微成员userid")
        private String userId;

        @ApiModelProperty(value = "发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid")
        private String operUserid;

        @ApiModelProperty(value = "该成员对此外部联系人的备注")
        private String remark;

        @ApiModelProperty(value = "该成员对此外部联系人的描述")
        private String description;

        @ApiModelProperty(value = "该成员添加此外部联系人的时间")
        private Date creatTime;

        @ApiModelProperty(value = "该成员对此客户备注的企业名称")
        private String remarkCorpName;

        @ApiModelProperty(value = "该成员对此客户备注的手机号码")
        private String remarkMobiles;

        @ApiModelProperty(value = "客户QQ")
        private String qq;

        @ApiModelProperty(value = "客户地址")
        private String address;

        @ApiModelProperty(value = "该成员添加此客户的来源，")
        private String addWay;

        @ApiModelProperty(value = "状态（0正常 1删除流失 2员工删除用户）")
        private String status;

    }
}
