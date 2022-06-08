package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.common.enums.AttachmentTypeEnum;
import com.easywecom.wecom.domain.WeMsgTlpMaterial;
import com.easywecom.wecom.domain.WeMsgTlpSpecialRule;
import com.easywecom.wecom.domain.dto.WeWelcomeMsg;
import com.easywecom.wecom.domain.dto.common.Attachments;

import java.util.List;

/**
 * 模板使用人员范围Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
public interface WeMsgTlpMaterialService extends IService<WeMsgTlpMaterial> {
    /**
     * 保存好友默认欢迎语素材
     *
     * @param defaultMsgId        默认欢迎语id
     * @param defaultMaterialList 欢迎语素材
     */
    void saveDefaultMaterial(Long defaultMsgId, List<WeMsgTlpMaterial> defaultMaterialList);

    /**
     * 保存特殊欢迎语素材
     *
     * @param defaultMsgId
     * @param weMsgTlpSpecialRules
     */
    void saveSpecialMaterial(Long defaultMsgId, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules);

    /**
     * 保存群欢迎语素材
     *
     * @param defaultMsgId        默认欢迎语id
     * @param defaultWelcomeMsg   默认欢迎语
     * @param corpId              企业id
     * @param defaultMaterialList 欢迎语素材
     */
    void saveGroupMaterial(Long defaultMsgId, String defaultWelcomeMsg, String corpId, List<WeMsgTlpMaterial> defaultMaterialList, Boolean noticeFlag);

    /**
     * 修改默认好友欢迎语附件
     *
     * @param removeMaterialIds 需要删除的素材ids
     * @param defaultMaterials  新增或修改的素材
     * @param defaultMsgId      默认欢迎语id
     */
    void updateDefaultEmployMaterial(List<Long> removeMaterialIds, List<WeMsgTlpMaterial> defaultMaterials, Long defaultMsgId);

    /**
     * 修改特殊欢迎语附件
     *
     * @param removeSpecialRuleMaterialIds 需要删除的素材ids
     * @param weMsgTlpSpecialRules         新增或修改的素材
     * @param defaultMsgId                 默认欢迎语id
     */
    void updateSpecialMaterial(List<Long> removeSpecialRuleMaterialIds, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules, Long defaultMsgId);

    /**
     * 修改群欢迎语附件
     *
     * @param defaultWelcomeMsg 默认欢迎语
     * @param removeMaterialIds 需要删除的素材ids
     * @param defaultMaterials  添加的附件素材
     * @param defaultMsgId      默认欢迎语id
     * @param templateId        群素材模板id
     * @param corpId            企业id
     */
    void updateDefaultGroupMaterial(String defaultWelcomeMsg, List<Long> removeMaterialIds, List<WeMsgTlpMaterial> defaultMaterials, Long defaultMsgId, String templateId, String corpId);

    /**
     * 构建发送好友欢迎语DTO
     *
     * @param defaultMsg          默认欢迎语
     * @param materialList        欢迎语素材
     * @param weWelcomeMsgBuilder 发送欢迎语DTO
     * @param userId              员工id
     * @param externalUserId      客户id
     * @param corpId              企业id
     * @param remark              备注
     * @return 欢迎语实体DTO
     */
    WeWelcomeMsg buildWeWelcomeMsg(String defaultMsg, List<WeMsgTlpMaterial> materialList, WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder, String userId, String externalUserId, String corpId, String remark);

    /**
     * 同步企业微信接口删除所有素材
     *
     * @param ids    欢迎语ids
     * @param corpId 企业id
     */
    void removeGroupMaterial(List<Long> ids, String corpId);

    /**
     * 更新群素材
     *
     * @param templateId        素材模板id
     * @param defaultMsgId      默认欢迎语id
     * @param defaultWelcomeMsg 默认欢迎语
     * @param corpId            企业id
     * @param defaultMaterials  素材
     */
    void updateGroupMaterial(String templateId, Long defaultMsgId, String defaultWelcomeMsg, String corpId, List<WeMsgTlpMaterial> defaultMaterials);

    /**
     * 返回替换后的文本有需要的话
     *
     * @param welcomeMsg     文本消息
     * @param remark         给客户的备注
     * @param externalUserId 外部联系人id
     * @param userId         员工id
     * @param corpId         企业id
     * @return 替换后的文本，为null返回null
     */
    String replyTextIfNecessary(String welcomeMsg, String remark, String externalUserId, String userId, String corpId);

//    /**
//     * 构建欢迎语数据
//     *
//     * @param content
//     * @param picUrl
//     * @param description
//     * @param url
//     * @param type
//     * @param corpId
//     */
//    Attachments buildByWelcomeMsgType(String content, String picUrl, String description, String url, AttachmentTypeEnum type, String corpId);
}
