package com.easywecom.wecom.domain.dto.customer;

import lombok.Data;

import java.util.List;

/**
 * 类名: 企微API跟进成员实体
 *
 * @author : silver_chariot
 * @date : 2021/11/1 10:58
 */
@Data
public class FollowUser {
    /**
     * 添加了此外部联系人的企业成员userid
     */
    private String userid;
    /**
     * 该成员对此外部联系人的备注
     */
    private String remark;
    /**
     * 该成员对此外部联系人的描述
     */
    private String description;
    /**
     * 该成员添加此外部联系人的时间
     */
    private long createtime;
    /**
     * 该成员对此客户备注的企业名称
     */
    private String remarkCorpName;
    /**
     * 该成员对此客户备注的手机号码
     */
    private String[] remarkMobiles;
    /**
     * 该成员添加此客户的来源
     */
    private String addWay;
    /**
     * 发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid
     */
    private String operUserid;
    /**
     * 企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定
     */
    private String state;
    /**
     * 标签
     **/
    private List<ExternalUserTag> tags;
}
