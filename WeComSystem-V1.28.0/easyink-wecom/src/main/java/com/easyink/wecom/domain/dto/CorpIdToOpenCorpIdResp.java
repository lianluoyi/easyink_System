package com.easyink.wecom.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * ClassName： CorpidToOpencorpid
 * 明文ID到密文ID的响应
 *
 * @author wx
 * @date 2022/8/22 18:09
 */
@Data
public class CorpIdToOpenCorpIdResp extends WeResultDTO{

    /**
     * 用来接收接口转换升级后的密文corpid
     * 用open_corpid 替换 corpid
     */
    private String open_corpid;

    /**
     * 用来接收转化后新的external_useridList
     * 用new_external_userid 替换 external_userid
     */
    private List<ExternalUserMapping> items;

    /**
     * 用来接收转化后新的useridList
     * 用open_userid 替换 userid
     */
    private List<UserIdMapping> open_userid_list;

    /**
     *  转换external_userid返回结果,明文external_userid与密文new_external_userid一对一映射关系
     */
    @Data
    public class ExternalUserMapping {
        /**
         * external_userid 未升级之前的外部联系人id
         */
        private String external_userid;

        /**
         * new_external_userid 新外部联系人id
         */
        private String new_external_userid;
    }

    /**
     * 转换userid返回结果,明文userid与密文open_userid一对一映射关系
     */
    @Data
    public class UserIdMapping{

        /**
         * 转换成功的userid
         */
        private String userid;

        /**
         * 转换成功的userid对应的该服务商应用下的成员ID
         */
        private String open_userid;
    }



}
