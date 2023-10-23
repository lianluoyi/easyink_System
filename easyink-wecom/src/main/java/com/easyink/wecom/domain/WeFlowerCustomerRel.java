package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.Constants;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.dto.customer.ExternalContact;
import com.easyink.wecom.domain.dto.customer.FollowInfo;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import com.easyink.wecom.domain.vo.WeCustomerExportVO;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 具有外部联系人功能企业员工也客户的关系对象 we_flower_customer_rel
 *
 * @author 佚名
 * @date 2021-7-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_flower_customer_rel")
@ApiModel("员工客户关系对象")
public class WeFlowerCustomerRel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    @TableField("id")
    private Long id = SnowFlakeUtil.nextId();

    @ApiModelProperty(value = "添加了此外部联系人的企业成员userid")
    @TableField(value = "user_id")
    private String userId;

    /**
     * 企业id
     */
    @TableField("corp_id")
    private String corpId;

    /**
     * 外部联系人名称
     */
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty(value = "该成员对此外部联系人的备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "该成员对此外部联系人的描述")
    @TableField("description")
    @Excel(name = "描述",sort = 11)
    private String description;

    @ApiModelProperty(value = "该成员添加此外部联系人的时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "添加时间",sort = 7)
    private Date createTime;

    @ApiModelProperty(value = "此外部联系人删除成员的时间(流失时间)")
    @TableField("delete_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deleteTime;

    @ApiModelProperty(value = "此外部联系人被成员删除的时间")
    @TableField("del_by_user_time")
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date delByUserTime;

    @ApiModelProperty(value = "该成员对此客户备注的企业名称 ")
    @TableField("remark_corp_name")
    private String remarkCorpName;

    @ApiModelProperty(value = "该成员对此客户备注的手机号码")
    @TableField("remark_mobiles")
    @Excel(name = "电话",sort = 6)
    private String remarkMobiles;

    @ApiModelProperty(value = "发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid")
    @TableField("oper_userid")
    private String operUserid;

    @ApiModelProperty(value = "客户QQ")
    @TableField("qq")
    private String qq;

    @ApiModelProperty(value = "客户地址")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "邮件")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "该成员添加此客户的来源，")
    @TableField("add_way")
    @Excel(name = "来源",sort =5)
    private String addWay;

    @ApiModelProperty(value = "企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定")
    @TableField("state")
    private String state;

    @ApiModelProperty(value = "客户id")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty(value = "状态（0正常 1已流失 2员工删除客户 3待继承 4转接中）")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "该成员添加此客户的来源add_way为10时，对应的视频号信息")
    @TableField("wechat_channel")
    private String wechatChannel;


    /**
     * 微信用户添加的标签
     */
    @TableField(exist = false)
    private List<WeFlowerCustomerTagRel> weFlowerCustomerTagRels;


    @TableField(exist = false)
    private String department;
    /**
     * 客户的unionId
     */
    @TableField(exist = false)
    private String unionId;


    @TableField(exist = false)
    private String beginTime;


    @TableField(exist = false)
    private String endTime;

    /**
     * 根据分配记录实体构建 员工客户关系实体
     *
     * @param weCustomerTransferRecord {@link WeCustomerTransferRecord }
     */
    public WeFlowerCustomerRel(WeCustomerTransferRecord weCustomerTransferRecord) {
        this.corpId = weCustomerTransferRecord.getCorpId();
        this.externalUserid = weCustomerTransferRecord.getExternalUserid();
        this.userId = weCustomerTransferRecord.getHandoverUserid();
    }

    /**
     * 根据客户vo构建 员工客户关系实体
     *
     * @param vo {@link WeCustomerVO}
     */
    public WeFlowerCustomerRel(WeCustomerVO vo) {
        this.corpId = vo.getCorpId();
        this.externalUserid = vo.getExternalUserid();
        this.userId = vo.getUserId();
    }


    public void setBeginTime(String beginTime) {
        this.beginTime = DateUtils.parseBeginDay(beginTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = DateUtils.parseEndDay(endTime);
    }

    /**
     * 根据API获取的客户信息 构建 成员-客户实体
     *
     * @param detail {@link GetByUserResp.ExternalContactDetail}
     * @param corpId 企业id
     * @return {@link WeFlowerCustomerRel}
     */
    public WeFlowerCustomerRel(GetByUserResp.ExternalContactDetail detail, String corpId) {
        this(detail.getExternal_contact(), detail.getFollow_info(), corpId);
    }

    /**
     * 根据API获取的客户信息 构建 成员-客户实体
     *
     * @param externalContact {@link ExternalContact}
     * @param followInfo      {@link FollowInfo}
     * @param corpId          企业id
     * @return {@link WeFlowerCustomerRel}
     */
    public WeFlowerCustomerRel(ExternalContact externalContact, FollowInfo followInfo, String corpId) {
        this.id = SnowFlakeUtil.nextId();
        this.userId = followInfo.getUserId();
        this.description = followInfo.getDescription();
        this.remarkCorpName = followInfo.getRemark_company();
        this.remarkMobiles = String.join(",", followInfo.getRemark_mobiles());
        this.remark = followInfo.getRemark();
        this.corpId = corpId;
        this.operUserid = followInfo.getOper_userid();
        this.addWay = followInfo.getAdd_way();
        this.state = followInfo.getState();
        this.externalUserid = externalContact.getExternalUserid();
        this.createTime = DateUtils.unix2Date(followInfo.getCreatetime());
        // 增加来源的视频号名称
        if (followInfo.getWechat_channels() == null || StringUtils.isBlank(followInfo.getWechat_channels().getNickname())) {
            this.wechatChannel = StringUtils.EMPTY;
        } else {
            this.wechatChannel = followInfo.getWechat_channels().getNickname();
        }
    }


    /**
     * 通过数据库查出来的weCustomer构建关系实体
     *
     * @param weCustomer {@link WeCustomer}
     */
    public WeFlowerCustomerRel(WeCustomer weCustomer) {
        this.externalUserid = weCustomer.getExternalUserid();
        this.corpId = weCustomer.getCorpId();
        if (CollectionUtils.isNotEmpty(weCustomer.getWeFlowerCustomerRels())) {
            this.userId = weCustomer.getWeFlowerCustomerRels().get(0).getUserId();
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
        WeFlowerCustomerRel that = (WeFlowerCustomerRel) o;
        return Objects.equals(externalUserid, that.externalUserid) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(corpId, that.corpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalUserid, userId, corpId);
    }

    public WeCustomerExportVO transferToExportVO() {
        WeCustomerExportVO vo = new WeCustomerExportVO();
        BeanUtils.copyProperties(this, vo);
        vo.setPhone(this.remarkMobiles);
        return vo;
    }
}
