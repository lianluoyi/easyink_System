package com.easyink.wecom.domain.dto.customer;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 类名: 企微API跟进成员详情实体
 *
 * @author : silver_chariot
 * @date : 2021/11/1 11:01
 */
@Data
public class FollowInfo {
    /**
     * 添加了此外部联系人的企业成员userid
     */
    private String userId;
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
    private String remark_corp_name;
    /**
     * 该成员对此客户备注的手机号码
     */
    private String[] remark_mobiles;
    /**
     * 该成员添加此客户的来源
     */
    private String add_way;
    /**
     * 该成员添加此客户的来源add_way为10时，对应的视频号信息
     */
    private WechatChannel wechat_channels;
    /**
     * 发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid
     */
    private String oper_userid;
    /**
     * 标签,调用get_by_user时返回,调用get接口时不返回
     **/
    private String[] tag_id;
    /**
     * 企业自定义的state参数，用于区分客户具体是通过哪个「联系我」添加，由企业通过创建「联系我」方式指定
     */
    private String state;
    /**
     * 标签详情集合,get_by_user批量获取客户信息时不返回,调用get接口单个获取客户信息时会返回
     */
    private List<Tags> tags;

    @Data
    public class Tags {
        /**
         * 标签组名称
         */
        private String group_name;
        /**
         * 标签名称
         */
        private String tag_name;
        /**
         * 标签id
         */
        private String tag_id;
        /**
         * 标签类型
         */
        private Integer type;
    }

    /**
     * 微信视频号信息
     */
    @Data
    public class WechatChannel {
        /**
         * 视频号名称
         */
        private String nickname;

    }


    public String getRemark() {
        if (remark == null) {
            return StringUtils.EMPTY;
        }
        return remark;
    }

    public String getDescription() {
        if (description == null) {
            return StringUtils.EMPTY;
        }
        return description;
    }

    public String getRemark_company() {
        if (remark_corp_name == null) {
            return StringUtils.EMPTY;
        }
        return remark_corp_name;
    }


    public String getOper_userid() {
        if (oper_userid == null) {
            return StringUtils.EMPTY;
        }
        return oper_userid;
    }


    public String getState() {
        if (state == null) {
            return StringUtils.EMPTY;
        }
        return state;
    }

    public String getAdd_way() {
        if (add_way == null) {
            // 如果没返回添加方式 则默认添加方式为0:未知来源
            return "0";
        }
        return add_way;
    }
}
