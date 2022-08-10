package com.easyink.wecom.domain.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.easyink.wecom.domain.dto.WeResultDTO;
import lombok.Data;

import java.util.List;

/**
 * 类名: 获取指定的应用详情 响应实体
 *
 * @author : silver_chariot
 * @date : 2022/7/4 11:37
 * @see : https://developer.work.weixin.qq.com/document/path/90227
 **/
@Data
public class GetAgentResp extends WeResultDTO {
    /**
     * 企业应用id
     */
    @JSONField(name = "agentid")
    private Integer agentId;
    /**
     * 企业应用名称
     */
    private String name;
    /**
     * 企业应用方形头像
     */
    private String square_logo_url;
    /**
     * 企业应用详情
     */
    private String description;
    /**
     * 企业应用可见范围（人员），其中包括userid
     */
    private AllowUserInfo allow_userinfos;
    /**
     * 企业应用可见范围（部门）
     */
    private AllowParty allow_partys;
    /**
     * 企业应用可见范围（标签）
     */
    private AllowTag allow_tags;
    /**
     * 企业应用是否被停用
     */
    private Integer close;
    /**
     * 企业应用可信域名
     */
    private String  redirect_domain;
    /**
     * 企业应用可信域名
     */
    private Integer report_location_flag;
    /**
     * 企业应用是否打开地理位置上报 0：不上报；1：进入会话上报；
     */
    private Integer isreportenter;
    /**
     * 应用主页url
     */
    private String home_url;
    /**
     * 代开发自建应用返回该字段，表示代开发发布状态。0：待开发（企业已授权，服务商未创建应用）；1：开发中（服务商已创建应用，未上线）；2：已上线（服务商已上线应用且不存在未上线版本）；3：存在未上线版本（服务商已上线应用但存在未上线版本）
     */
    private Integer customized_publish_status;
    /**
     * 可见员工信息
     */
    @Data
    public static class AllowUserInfo {
        /**
         * 可见员工列表
         */
        private List<User> user;
    }

    /**
     * 员工信息
     */
    @Data
    public static class User {
        /**
         * 员工id
         */
        private String userid;
    }

    /**
     * 可见部门信息
     */
    @Data
    public class AllowParty {
        /**
         * 可见部门数组
         */
        private List<Integer> partyid;

    }

    /**
     * 可见标签详情
     */
    @Data
    public class AllowTag {
        /**
         * 可见标签
         */
        private List<Integer> tagid;
    }
}
