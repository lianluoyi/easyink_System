package com.easyink.wecom.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.constant.redeemcode.RedeemCodeConstants;
import com.easyink.common.enums.MediaType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeWelcomeMsgClient;
import com.easyink.wecom.domain.*;
import com.easyink.wecom.domain.dto.WeMediaDTO;
import com.easyink.wecom.domain.dto.WeWelcomeMsg;
import com.easyink.wecom.domain.dto.common.*;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgAddDTO;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgDeleteDTO;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgResult;
import com.easyink.wecom.domain.dto.welcomemsg.GroupWelcomeMsgUpdateDTO;
import com.easyink.wecom.domain.vo.WeUserVO;
import com.easyink.wecom.mapper.WeMsgTlpMaterialMapper;
import com.easyink.wecom.service.*;
import com.easyink.wecom.utils.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 模板使用人员范围Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Slf4j
@Service
public class WeMsgTlpMaterialServiceImpl extends ServiceImpl<WeMsgTlpMaterialMapper, WeMsgTlpMaterial> implements WeMsgTlpMaterialService {

    private WeMsgTlpMaterialService weMsgTlpMaterialService;
    private WeMaterialService weMaterialService;
    private WeWelcomeMsgClient weWelcomeMsgClient;
    private WeFlowerCustomerRelService weFlowerCustomerRelService;
    private WeCustomerService weCustomerService;
    private WeUserService weUserService;
    private WeMsgTlpService weMsgTlpService;
    private AttachmentService attachmentService;

    @Lazy
    @Autowired
    public WeMsgTlpMaterialServiceImpl(WeMsgTlpMaterialService weMsgTlpMaterialService, WeMaterialService weMaterialService, WeWelcomeMsgClient weWelcomeMsgClient, WeFlowerCustomerRelService weFlowerCustomerRelService, WeCustomerService weCustomerService, WeUserService weUserService, WeMsgTlpService weMsgTlpService, AttachmentService attachmentService) {
        this.weMsgTlpMaterialService = weMsgTlpMaterialService;
        this.weMaterialService = weMaterialService;
        this.weWelcomeMsgClient = weWelcomeMsgClient;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
        this.weCustomerService = weCustomerService;
        this.weUserService = weUserService;
        this.weMsgTlpService = weMsgTlpService;
        this.attachmentService = attachmentService;
    }

    /**
     * 保存好友默认欢迎语素材
     *
     * @param defaultMsgId        默认欢迎语id
     * @param defaultMaterialList 欢迎语素材
     */
    @Override
    public void saveDefaultMaterial(Long defaultMsgId, List<WeMsgTlpMaterial> defaultMaterialList) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 校验素材类型和内容是否合理
        if (CollectionUtils.isNotEmpty(defaultMaterialList)) {
            checkMaterialContent(defaultMaterialList);

            List<WeMsgTlpMaterial> batchList = new ArrayList<>();
            for (WeMsgTlpMaterial weMsgTlpMaterial : defaultMaterialList) {
                weMsgTlpMaterial.setDefaultMsgId(defaultMsgId);
                batchList.add(weMsgTlpMaterial);
            }
            weMsgTlpMaterialService.saveBatch(batchList);
        }
    }

    /**
     * 保存特殊欢迎语素材
     *
     * @param defaultMsgId
     * @param weMsgTlpSpecialRules
     */
    @Override
    public void saveSpecialMaterial(Long defaultMsgId, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        List<WeMsgTlpMaterial> specialMaterialBatchList = new ArrayList<>();
        for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : weMsgTlpSpecialRules) {
            // 校验素材类型和内容是否合理
            if (CollectionUtils.isNotEmpty(weMsgTlpSpecialRule.getSpecialMaterialList())) {
                checkMaterialContent(weMsgTlpSpecialRule.getSpecialMaterialList());
                for (WeMsgTlpMaterial weMsgTlpMaterial : weMsgTlpSpecialRule.getSpecialMaterialList()) {
                    weMsgTlpMaterial.setDefaultMsgId(defaultMsgId);
                    weMsgTlpMaterial.setSpecialMsgId(weMsgTlpSpecialRule.getId());
                    specialMaterialBatchList.add(weMsgTlpMaterial);
                }
            }
        }
        weMsgTlpMaterialService.saveBatch(specialMaterialBatchList);
    }

    /**
     * 校验素材类型和内容是否合理
     *
     * @param specialMaterialList
     */
    private void checkMaterialContent(List<WeMsgTlpMaterial> specialMaterialList) {
        for (WeMsgTlpMaterial weMsgTlpMaterial : specialMaterialList) {
            weMsgTlpMaterial.checkContent();
        }
    }

    /**
     * 保存群欢迎语素材
     *
     * @param defaultMsgId        默认欢迎语id
     * @param defaultWelcomeMsg   默认欢迎语
     * @param corpId              企业id
     * @param defaultMaterialList 欢迎语素材
     */
    @Override
    public void saveGroupMaterial(Long defaultMsgId, String defaultWelcomeMsg, String corpId, List<WeMsgTlpMaterial> defaultMaterialList, Boolean noticeFlag) {
        if (StringUtils.isEmpty(corpId) && defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        // 处理文本消息
        GroupWelcomeMsgAddDTO groupWelcomeMsgAddDTO = new GroupWelcomeMsgAddDTO();
        groupWelcomeMsgAddDTO.setNotify(noticeFlag ? 1 : 0);
        if (StringUtils.isNotEmpty(defaultWelcomeMsg)) {
            handleText(defaultWelcomeMsg, groupWelcomeMsgAddDTO);
        }
        // 存在素材，则构建素材
        WeMsgTlpMaterial weMsgTlpMaterial = null;
        if (CollectionUtils.isNotEmpty(defaultMaterialList)) {
            // 这里最多只存在一个素材
            weMsgTlpMaterial = defaultMaterialList.get(0);
            // 根据媒体类型构建DTO调用企业微信接口
            groupWelcomeMsgAddDTO = handleByMaterialType(weMsgTlpMaterial.getContent(), weMsgTlpMaterial.getPicUrl(), weMsgTlpMaterial.getDescription(), weMsgTlpMaterial.getUrl(), AttachmentTypeEnum.getByMessageType(weMsgTlpMaterial.getType()), groupWelcomeMsgAddDTO, corpId);
        }
        // 2.1.调用企微接口同步素材
        log.info("入群欢迎语素材数据，json: {}", JSONObject.toJSONString(groupWelcomeMsgAddDTO));
        GroupWelcomeMsgResult result = weWelcomeMsgClient.add(groupWelcomeMsgAddDTO, corpId);

        if (weMsgTlpMaterial != null) {
            weMsgTlpMaterial.setDefaultMsgId(defaultMsgId);
            weMsgTlpMaterialService.saveOrUpdate(weMsgTlpMaterial);
        }

        // 保存模板id到欢迎语表
        WeMsgTlp weMsgTlp = weMsgTlpService.getById(defaultMsgId);
        weMsgTlp.setTemplateId(result.getTemplate_id());
        weMsgTlpService.updateById(weMsgTlp);

    }

    /**
     * 修改默认好友欢迎语附件
     *
     * @param removeMaterialIds 需要删除的素材ids
     * @param defaultMaterials  新增或修改的素材
     * @param defaultMsgId      默认欢迎语id
     */
    @Override
    public void updateDefaultEmployMaterial(List<Long> removeMaterialIds, List<WeMsgTlpMaterial> defaultMaterials, Long defaultMsgId) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 1 删除需要删除的集合
        if (CollectionUtils.isNotEmpty(removeMaterialIds)) {
            weMsgTlpMaterialService.removeByIds(removeMaterialIds);
        }
        // 2 新增或修改的数据
        if (CollectionUtils.isNotEmpty(defaultMaterials)) {
            int sortNo = 0;
            checkMaterialContent(defaultMaterials);
            for (WeMsgTlpMaterial defaultMaterial : defaultMaterials) {
                defaultMaterial.setSortNo(++sortNo);
                defaultMaterial.setDefaultMsgId(defaultMsgId);
            }
            weMsgTlpMaterialService.saveOrUpdateBatch(defaultMaterials);
        }
    }


    /**
     * 修改特殊欢迎语附件
     *
     * @param removeSpecialRuleMaterialIds 需要删除的素材ids
     * @param weMsgTlpSpecialRules         新增或修改的素材
     * @param defaultMsgId                 默认欢迎语id
     */
    @Override
    public void updateSpecialMaterial(List<Long> removeSpecialRuleMaterialIds, List<WeMsgTlpSpecialRule> weMsgTlpSpecialRules, Long defaultMsgId) {
        if (defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 3.2.1 删除需要删除的特殊欢迎语附件
        if (CollectionUtils.isNotEmpty(removeSpecialRuleMaterialIds)) {
            weMsgTlpMaterialService.removeByIds(removeSpecialRuleMaterialIds);
        }
        // 3.2.2 修改或新政特殊欢迎语附件
        List<WeMsgTlpMaterial> batchList = new ArrayList<>();
        for (WeMsgTlpSpecialRule weMsgTlpSpecialRule : weMsgTlpSpecialRules) {
            int sortNo = 0;
            for (WeMsgTlpMaterial weMsgTlpMaterial : weMsgTlpSpecialRule.getSpecialMaterialList()) {
                weMsgTlpMaterial.setSortNo(++sortNo);
                weMsgTlpMaterial.setDefaultMsgId(defaultMsgId);
                weMsgTlpMaterial.setSpecialMsgId(weMsgTlpSpecialRule.getId());
                batchList.add(weMsgTlpMaterial);
            }
        }
        weMsgTlpMaterialService.saveOrUpdateBatch(batchList);
    }

    /**
     * 修改群欢迎语附件
     *
     * @param defaultWelcomeMsg 默认欢迎语
     * @param removeMaterialIds 需要删除的素材ids
     * @param defaultMaterials  添加的附件素材
     * @param defaultMsgId      默认欢迎语
     * @param templateId        群素材模板id
     * @param corpId            企业id
     */
    @Override
    public void updateDefaultGroupMaterial(String defaultWelcomeMsg, List<Long> removeMaterialIds, List<WeMsgTlpMaterial> defaultMaterials, Long defaultMsgId, String templateId, String corpId) {
        if (StringUtils.isEmpty(corpId) && StringUtils.isEmpty(templateId) && defaultMsgId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        // 1. 删除需要删除的附件素材
        if (CollectionUtils.isNotEmpty(removeMaterialIds)) {
            weMsgTlpMaterialService.removeByIds(removeMaterialIds);
        }
        // 2. 更新素材
        weMsgTlpMaterialService.updateGroupMaterial(templateId, defaultMsgId, defaultWelcomeMsg, corpId, defaultMaterials);
    }


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
    @Override
    public WeWelcomeMsg buildWeWelcomeMsg(String defaultMsg, List<WeMsgTlpMaterial> materialList, WeWelcomeMsg.WeWelcomeMsgBuilder weWelcomeMsgBuilder, String userId, String externalUserId, String corpId, String remark) {
        if (StringUtils.isEmpty(defaultMsg) && CollectionUtils.isEmpty(materialList)) {
            log.error("欢迎语和素材都为空，欢迎语发送失败");
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(externalUserId) || StringUtils.isEmpty(corpId)) {
            log.error("params missing, userid:{}, externalUserId:{}, corpId: {}", userId, externalUserId, corpId);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        // 处理欢迎语
        // 兑换码
        String redeemCode = StringUtils.EMPTY;

        String replyText = replyTextIfNecessary(defaultMsg, remark, redeemCode, externalUserId, userId, corpId);
        Optional.ofNullable(replyText).ifPresent(text -> weWelcomeMsgBuilder.text(Text.builder().content(text).build()));
        // 处理素材
        List<Attachment> attachmentList = new ArrayList<>();
        Attachments attachments;
        AttachmentTypeEnum type;
        for (WeMsgTlpMaterial weMsgTlpMaterial : materialList) {
            type = AttachmentTypeEnum.getByMessageType(weMsgTlpMaterial.getType());
            if (type == null) {
                log.error("type is error !!, type: {}", weMsgTlpMaterial.getType());
                continue;
            }
            AttachmentParam param = AttachmentParam.costFromWeMsgTlpMaterial(weMsgTlpMaterial.getRadarId(), userId, corpId, weMsgTlpMaterial, type);
            attachments = attachmentService.buildAttachment(param, corpId);
//            attachments = this.buildByWelcomeMsgType(param.getContent(), param.getPicUrl(), param.getDescription(), param.getUrl(), param.getTypeEnum(), corpId);
            if (attachments != null) {
                attachmentList.add(attachments);
            }
        }
        WeWelcomeMsg welcomeMsg = weWelcomeMsgBuilder.attachments(attachmentList).build();
        return welcomeMsg;
    }

    /**
     * 同步企业微信接口删除所有素材
     *
     * @param ids
     * @param corpId
     */
    @Override
    public void removeGroupMaterial(List<Long> ids, String corpId) {
        if (StringUtils.isEmpty(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        // 1.同步删除企业微信管理的素材
        List<WeMsgTlp> weMsgTlps = weMsgTlpService.listByIds(ids);
        for (WeMsgTlp weMsgTlp : weMsgTlps) {
            weWelcomeMsgClient.del(new GroupWelcomeMsgDeleteDTO(weMsgTlp.getTemplateId(), null), weMsgTlp.getCorpId());
        }
        // 4.删除所有素材
        weMsgTlpMaterialService.remove(new LambdaQueryWrapper<WeMsgTlpMaterial>()
                .in(WeMsgTlpMaterial::getDefaultMsgId, ids));
    }

    /**
     * 更新群素材
     *
     * @param templateId        素材模板id
     * @param defaultMsgId      默认欢迎语id
     * @param defaultWelcomeMsg 默认欢迎语
     * @param corpId            企业id
     * @param defaultMaterials  素材
     */
    @Override
    public void updateGroupMaterial(String templateId, Long defaultMsgId, String defaultWelcomeMsg, String corpId, List<WeMsgTlpMaterial> defaultMaterials) {
        if (StringUtils.isEmpty(corpId) && StringUtils.isEmpty(templateId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        GroupWelcomeMsgUpdateDTO groupWelcomeMsgUpdateDTO = new GroupWelcomeMsgUpdateDTO();
        if (StringUtils.isNotEmpty(defaultWelcomeMsg)) {
            handleText(defaultWelcomeMsg, groupWelcomeMsgUpdateDTO);
        }
        // 存在素材，则构建素材
        WeMsgTlpMaterial weMsgTlpMaterial = null;
        if (CollectionUtils.isNotEmpty(defaultMaterials)) {
            // 这里最多只存在一个素材
            weMsgTlpMaterial = defaultMaterials.get(0);
            // 根据媒体类型构建DTO调用企业微信接口
            groupWelcomeMsgUpdateDTO = handleByMaterialType(weMsgTlpMaterial.getContent(), weMsgTlpMaterial.getPicUrl(), weMsgTlpMaterial.getDescription(), weMsgTlpMaterial.getUrl(), AttachmentTypeEnum.getByMessageType(weMsgTlpMaterial.getType()), groupWelcomeMsgUpdateDTO, corpId);
        }
        groupWelcomeMsgUpdateDTO.setTemplate_id(templateId);

        // 2.1.调用企微接口同步素材
        log.info("更新入群欢迎语素材数据，json: {}", JSONObject.toJSONString(groupWelcomeMsgUpdateDTO));
        weWelcomeMsgClient.edit(groupWelcomeMsgUpdateDTO, corpId);

        if (weMsgTlpMaterial != null) {
            weMsgTlpMaterial.setDefaultMsgId(defaultMsgId);
            weMsgTlpMaterialService.saveOrUpdate(weMsgTlpMaterial);
        }

    }

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
    @Override
    public String replyTextIfNecessary(final String welcomeMsg, final String remark, final String redeemCode, String externalUserId, String userId, String corpId) {
        String customerNickName = WeConstans.CUSTOMER_NICKNAME;
        String employeeName = WeConstans.EMPLOYEE_NAME;

        if (StringUtils.isNotEmpty(welcomeMsg)) {
            String replyText = welcomeMsg;
            //替换#客户昵称#
            if (replyText.contains(customerNickName)) {
                if (StringUtils.isNotEmpty(remark)) {

                    replyText = replyText.replaceAll(customerNickName, remark);
                } else {
                    WeCustomer weCustomer = weCustomerService.selectWeCustomerById(externalUserId, corpId);
                    replyText = replyText.replaceAll(customerNickName, weCustomer.getName());
                }
            }
            //替换#员工姓名#
            if (replyText.contains(employeeName)) {
                WeUserVO user = weUserService.getUser(corpId, userId);
                if (user == null) {
                    log.error("sendMessageToNewExternalUserId user is null!! corpId={},userId={}", corpId, userId);
                } else {
                    replyText = replyText.replaceAll(employeeName, user.getUserName());
                }
            }
            //替换#兑换码#
            if (replyText.contains(RedeemCodeConstants.REDEEM_CODE)) {
                if (StringUtils.isNotEmpty(redeemCode)) {
                    replyText = replyText.replaceAll(RedeemCodeConstants.REDEEM_CODE, redeemCode);
                } else {
                    replyText = replyText.replaceAll(RedeemCodeConstants.REDEEM_CODE, StringUtils.EMPTY);
                }
            }
            return replyText;
        }
        return null;
    }

//    /**
//     * 根据枚举类型构建数据
//     *
//     * @param content     数据1
//     * @param picUrl      数据2
//     * @param description 数据3
//     * @param url         数据4
//     * @param type        枚举
//     * @param corpId      企业id
//     * @return
//     */
//    @Override
//    public Attachments buildByWelcomeMsgType(String content, String picUrl, String description, String url, AttachmentTypeEnum type, String corpId) {
//        StringUtils.checkCorpId(corpId);
//        switch (type) {
//            case IMAGE:
//                return buildImage(picUrl, type, corpId);
//            case LINK:
//                return buildLink(content, picUrl, description, url, type);
//            case MINIPROGRAM:
//                return buildMiniprogram(content, picUrl, description, url, type, corpId);
//            case FILE:
//                return buildFile(picUrl, type, corpId);
//            case VIDEO:
//                return buildVideo(content, picUrl, description, url, type, corpId);
//            default:
//                log.error("type error !!!");
//                return null;
//        }
//    }
//
//    /**
//     * 构建视频
//     */
//    private Attachments buildVideo(String content, String picUrl, String fileSize, String url, AttachmentTypeEnum type, String corpId) {
//        Attachments attachments;
//        // 大于10M，需要发送为link形式
//        if (Integer.parseInt(fileSize) >= 0 && Integer.parseInt(fileSize) > WeConstans.DEFAULT_MAX_VIDEO_SIZE) {
//            // 标题
//            content = StringUtils.isNotEmpty(content) ? content : FileUtil.getName(picUrl);
//            // 链接地址
//            url = picUrl;
//            // 封面图片
//            picUrl = WeConstans.DEFAULT_VIDEO_COVER_URL;
//            // 描述信息
//            String desc = WeConstans.CLICK_SEE_VIDEO;
//            attachments = buildLink(content, picUrl, desc, url, AttachmentTypeEnum.LINK);
//            return attachments;
//        }
//        attachments = new Attachments();
//        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, type.getTypeStr(), FileUtil.getName(picUrl), corpId);
//        attachments.setMsgtype(type.getTypeStr());
//        attachments.setVideo(Video.builder().media_id(weMediaDto.getMedia_id()).build());
//        return attachments;
//    }
//
//    /**
//     * 构建文件
//     */
//    private Attachments buildFile(String picUrl, AttachmentTypeEnum type, String corpId) {
//        Attachments attachments = new Attachments();
//        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, type.getTypeStr(), FileUtil.getName(picUrl), corpId);
//        attachments.setMsgtype(type.getTypeStr());
//        attachments.setFile(File.builder().media_id(weMediaDto.getMedia_id()).build());
//        return attachments;
//    }
//
//    /**
//     * 构建小程序
//     */
//    private Attachments buildMiniprogram(String content, String picUrl, String description, String url, AttachmentTypeEnum type, String corpId) {
//        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, AttachmentTypeEnum.IMAGE.getTypeStr(), FileUtil.getName(picUrl), corpId);
//        Attachments attachments = new Attachments();
//        attachments.setMsgtype(type.getTypeStr());
//        attachments.setMiniprogram(MiniProgram.builder()
//                .title(content)
//                .pic_media_id(weMediaDto.getMedia_id())
//                .appid(description)
//                .page(url)
//                .build());
//        return attachments;
//    }
//
//    /**
//     * 构建链接
//     */
//    private Attachments buildLink(String content, String picUrl, String description, String url, AttachmentTypeEnum type) {
//        Attachments attachments = new Attachments();
//        attachments.setMsgtype(type.getTypeStr());
//        attachments.setLink(Link.builder()
//                .title(content)
//                .picurl(picUrl)
//                .desc(description)
//                .url(url)
//                .build());
//        return attachments;
//    }
//
//    /**
//     * 构建图片
//     */
//    private Attachments buildImage(String picUrl, AttachmentTypeEnum type, String corpId) {
//        Attachments attachments = new Attachments();
//        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, type.getTypeStr(), FileUtil.getName(picUrl), corpId);
//        attachments.setMsgtype(type.getTypeStr());
//        attachments.setImage(Image.builder().media_id(weMediaDto.getMedia_id()).build());
//        return attachments;
//    }

    /**
     * 将数据构建成企业微信接口DTO
     */
    private <T extends Messages> T handleByMaterialType(String content, String picUrl, String description, String url, AttachmentTypeEnum type, T messages, String corpId) {
        // 根据url调用上传素材接口获取media_id
        switch (type) {
            case IMAGE:
                handleImage(picUrl, messages, corpId);
                break;
            case LINK:
            case RADAR:
                handleLink(content, picUrl, description, url, messages);
                break;
            case MINIPROGRAM:
                handleMiniprogram(content, picUrl, description, url, messages, corpId);
                break;
            case FILE:
                handleFile(picUrl, messages, corpId);
                break;
            case VIDEO:
                handleVideo(content, picUrl, description, url, messages, corpId);
                break;
            default:
                log.error("媒体类型类型异常type: {}", type);
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return messages;
    }

    /**
     * 构建视频文件
     *
     * @param content  视频标题
     * @param picUrl   视频url
     * @param fileSize 视频文件大小，对应link的图文消息描述
     * @param url      图文消息的链接
     * @param messages 素材
     * @param corpId   企业id
     */
    private void handleVideo(String content, String picUrl, String fileSize, String url, Messages messages, String corpId) {
        // 大于10M, 以链接形式发送
        if (StringUtils.isNotEmpty(fileSize) && Integer.parseInt(fileSize) > WeConstans.DEFAULT_MAX_VIDEO_SIZE) {
            // 标题
            content = StringUtils.isNotEmpty(content) ? content : FileUtil.getName(picUrl);
            // 默认描述
            String desc = WeConstans.CLICK_SEE_VIDEO;
            // 封面图
            url = StringUtils.isNotEmpty(url) ? url : WeConstans.DEFAULT_VIDEO_COVER_URL;
            handleLink(content, url, desc, picUrl, messages);
            return;
        }
        Video video = new Video();
        WeMediaDTO weMediaDTO = weMaterialService.uploadTemporaryMaterial(picUrl, MediaType.VIDEO.getMediaType(), FileUtil.getName(picUrl), corpId);
        if (weMediaDTO.getErrcode() != 0) {
            log.error("接口调用异常，errormsg: {}", weMediaDTO.getErrmsg());
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        video.setMedia_id(weMediaDTO.getMedia_id());
        messages.setVideo(video);
    }

    /**
     * @param picUrl   图片url
     * @param messages 素材
     * @param corpId   企业id
     */
    private void handleFile(String picUrl, Messages messages, String corpId) {
        File file = new File();
        WeMediaDTO weMediaDTO = weMaterialService.uploadTemporaryMaterial(picUrl, MediaType.FILE.getMediaType(), FileUtil.getName(picUrl), corpId);
        if (weMediaDTO.getErrcode() != 0) {
            log.error("接口调用异常，errormsg: {}", weMediaDTO.getErrmsg());
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        file.setMedia_id(weMediaDTO.getMedia_id());
        messages.setFile(file);
    }

    /**
     * 构建小程序
     *
     * @param title    小程序消息标题，最长为64字节
     * @param picUrl   小程序消息封面的mediaid，封面图建议尺寸为520*416
     * @param appid    小程序appid
     * @param page     小程序page路径
     * @param messages 素材
     * @param corpId
     */
    private void handleMiniprogram(String title, String picUrl, String appid, String page, Messages messages, String corpId) {
        MiniProgram miniProgram = new MiniProgram();
        miniProgram.setTitle(title);
        miniProgram.setAppid(appid);
        miniProgram.setPage(page);
        // 调用素材上传获取media_id
        WeMediaDTO weMediaDTO = weMaterialService.uploadTemporaryMaterial(picUrl, MediaType.IMAGE.getMediaType(), FileUtil.getName(picUrl), corpId);
        if (weMediaDTO.getErrcode() != 0) {
            log.error("接口调用异常，errormsg: {}", weMediaDTO.getErrmsg());
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        miniProgram.setPic_media_id(weMediaDTO.getMedia_id());
        messages.setMiniprogram(miniProgram);
    }

    /**
     * 构建链接
     *
     * @param content     图文消息标题
     * @param picUrl      图文消息封面的url
     * @param description 图文消息的描述
     * @param url         图文消息的链接
     * @param messages    素材
     */
    private void handleLink(String content, String picUrl, String description, String url, Messages messages) {
        Link link = new Link();
        link.setTitle(content);
        link.setPicurl(picUrl);
        link.setDesc(description);
        link.setUrl(url);
        messages.setLink(link);
    }

    /**
     * 构建图片
     *
     * @param picUrl   图片url
     * @param messages 素材
     * @param corpId
     */
    private void handleImage(String picUrl, Messages messages, String corpId) {
        Image image = new Image();
        // 调用素材上传获取media_id
        WeMediaDTO weMediaDTO = weMaterialService.uploadTemporaryMaterial(picUrl, MediaType.IMAGE.getMediaType(), FileUtil.getName(picUrl), corpId);
        if (weMediaDTO.getErrcode() != 0) {
            log.error("接口调用异常，errormsg: {}", weMediaDTO.getErrmsg());
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        image.setMedia_id(weMediaDTO.getMedia_id());
        messages.setImage(image);
    }

    /**
     * 构建文本
     *
     * @param defaultWelcomeMsg 文本消息
     * @param messages          素材
     */
    private void handleText(String defaultWelcomeMsg, Messages messages) {
        // 群欢迎语必须有欢迎语，需要设置text类型数据
        Text text = new Text();

        String realMsg = defaultWelcomeMsg;
        // 如果存在替换现有的客户昵称占位符 #客户昵称# -> %NICKNAME%
        if (defaultWelcomeMsg.contains(WeConstans.CUSTOMER_NICKNAME)) {
            realMsg = defaultWelcomeMsg.replaceAll(WeConstans.CUSTOMER_NICKNAME, WeConstans.GROUP_CUSTOMER_NICKNAME);
        }
        text.setContent(realMsg);
        messages.setText(text);
    }
}
