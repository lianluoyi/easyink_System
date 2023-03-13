package com.easyink.wecom.domain.resp;

import lombok.Data;

import java.util.List;

/**
 * 类名: 企微后台获取企业成员列表的响应
 * 由企微官方后台接口https://work.weixin.qq.com/wework_admin/contacts/getDepartmentMember返回的数据
 * 由于企微官方现在没有返回员工数据，
 * 需要员工授权才能获取，所以easyink的解决方案是让管理员扫码登录后台后，
 * easyink 基于其登录session调用以上获取成员信息的接口 , 自动并保存这些消息
 * 因为不是openapi开放平台的接口
 * 所以字段如果没有明确的值， 大多数都是靠猜测其含义
 *
 * @author : silver_chariot
 * @date : 2023/2/27 14:48
 **/
@Data
public class GetDepartMemberResp {


    /**
     * 企业员工列表
     */
    private ContactList contact_list;

    /**
     * 员工总数
     */
    private Integer member_count;
    /**
     * 下一页的企业员工列表
     */
    private ContactList next_page_contact_list;


    /**
     * 页数
     */
    private Integer page_count;

    @Data
    public static class ContactList {
        /**
         * 企业员工列表
         */
        private List<MemberInfo> list;

    }


    /**
     * 企业成员信息
     */
    @Data
    public static class MemberInfo {
        /**
         * 加入状态，常见值：MMOCBIZ_CONTACT_JOIN
         */
        private String JoinStatus;
        /**
         * 未知字段，
         */
        private Integer acctid_stat;
        /**
         * 类似于open api 返回的userId
         */
        private String acctid;
        /**
         * 成员头像
         */
        private String imgid;
        /**
         * 成员名字
         */
        private String name;
        /**
         * 手机号
         */
        private String mobile;
        /**
         * 邮箱
         */
        private String email;
        /**
         * 地址
         */
        private String xcx_corp_address;
    }
}
