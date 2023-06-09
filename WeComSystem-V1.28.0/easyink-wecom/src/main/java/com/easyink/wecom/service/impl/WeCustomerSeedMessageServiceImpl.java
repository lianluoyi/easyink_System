package com.easyink.wecom.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.GroupMessageType;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomerSeedMessage;
import com.easyink.wecom.domain.dto.message.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerSeedMessageMapper;
import com.easyink.wecom.service.WeCustomerSeedMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 群发消息  子消息表(包括 文本消息、图片消息、链接消息、小程序消息) we_customer_seedMessage
 *
 * @author admin
 * @date 2020-12-28
 */
@Service
public class WeCustomerSeedMessageServiceImpl extends ServiceImpl<WeCustomerSeedMessageMapper, WeCustomerSeedMessage> implements WeCustomerSeedMessageService {
    private final WeCustomerSeedMessageMapper weCustomerSeedMessageMapper;

    @Autowired
    public WeCustomerSeedMessageServiceImpl(WeCustomerSeedMessageMapper weCustomerSeedMessageMapper) {
        this.weCustomerSeedMessageMapper = weCustomerSeedMessageMapper;
    }

    @Override
    public void saveSeedMessage(CustomerMessagePushDTO customerMessagePushDTO, long messageId) {
        List<WeCustomerSeedMessage> customerSeedMessages = new ArrayList<>();
        List<Attachment> attachments = customerMessagePushDTO.getAttachments();
        //保存文本
        if (customerMessagePushDTO.getTextMessage() != null) {
            WeCustomerSeedMessage textMessage = new WeCustomerSeedMessage();
            buildCommonMessage(textMessage, messageId);
            String content = customerMessagePushDTO.getTextMessage().getContent();
            if (StringUtils.isNotBlank(content)) {
                textMessage.setContent(content);
            }
            textMessage.setMessageType(GroupMessageType.TEXT.getType());
            customerSeedMessages.add(textMessage);
        }
        //保存附件
        attachments.forEach(attachment -> {
            WeCustomerSeedMessage customerSeedMessage = new WeCustomerSeedMessage();
            buildCommonMessage(customerSeedMessage, messageId);
            buildSeedMessage(attachment, customerSeedMessage);
            customerSeedMessages.add(customerSeedMessage);
        });
        this.saveBatch(customerSeedMessages);
    }

    @Override
    public int deleteByMessageId(Long messageId, String corpId) {
        return weCustomerSeedMessageMapper.deleteByMessageId(messageId, corpId);
    }

    /**
     * 构造通用实体
     *
     * @param customerSeedMessage 子消息实体
     * @param messageId           消息id
     */
    private void buildCommonMessage(WeCustomerSeedMessage customerSeedMessage, Long messageId) {
        customerSeedMessage.setContent(StrUtil.EMPTY);
        customerSeedMessage.setSeedMessageId(SnowFlakeUtil.nextId());
        customerSeedMessage.setMessageId(messageId);
        customerSeedMessage.setDelFlag(WeConstans.WE_CUSTOMER_MSG_RESULT_NO_DEFALE);
        customerSeedMessage.setUpdateBy(LoginTokenService.getUsername());
        customerSeedMessage.setCreateBy(LoginTokenService.getUsername());
        customerSeedMessage.setIsDefined(Boolean.FALSE);
    }

    /**
     * 构造消息内容实体
     *
     * @param attachment          附件
     * @param customerSeedMessage 子消息实体
     */
    private void buildSeedMessage(Attachment attachment, WeCustomerSeedMessage customerSeedMessage) {
        String msgtype = attachment.getMsgtype();
        //图片
        if (GroupMessageType.IMAGE.getType().equals(msgtype) && attachment.getImageMessage() != null) {
            ImageMessageDTO imageMessage = attachment.getImageMessage();
            String name = StringUtils.isNotBlank(imageMessage.getTitle()) ? imageMessage.getTitle() : FileUtil.getName(imageMessage.getPic_url());
            customerSeedMessage.setPicName(name);
            customerSeedMessage.setMediaId(Optional.ofNullable(imageMessage.getMedia_id()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setPicUrl(Optional.ofNullable(imageMessage.getPic_url()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setMessageType(GroupMessageType.IMAGE.getType());
        }
        //小程序
        else if (GroupMessageType.MINIPROGRAM.getType().equals(msgtype) && attachment.getMiniprogramMessage() != null) {
            MiniprogramMessageDTO miniprogramMessage = attachment.getMiniprogramMessage();
            customerSeedMessage.setMiniprogramTitle(Optional.ofNullable(miniprogramMessage.getTitle()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setMiniprogramMediaId(Optional.ofNullable(miniprogramMessage.getPic_media_id()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setAppid(Optional.ofNullable(miniprogramMessage.getAppid()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setAccountOriginalId(Optional.ofNullable(miniprogramMessage.getAccountOriginalId()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setPage(Optional.ofNullable(miniprogramMessage.getPage()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setMessageType(GroupMessageType.MINIPROGRAM.getType());
        }
        //链接
        else if (GroupMessageType.LINK.getType().equals(msgtype) && attachment.getLinkMessage() != null) {
            LinkMessageDTO linkMessage = attachment.getLinkMessage();
            customerSeedMessage.setLinkUrl(Optional.ofNullable(linkMessage.getUrl()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setLinkTitle(Optional.ofNullable(linkMessage.getTitle()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setLinkPicurl(Optional.ofNullable(linkMessage.getPicurl()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setLinDesc(Optional.ofNullable(linkMessage.getDesc()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setIsDefined(Optional.ofNullable(linkMessage.getIsDefined()).orElse(Boolean.FALSE));
            customerSeedMessage.setMessageType(GroupMessageType.LINK.getType());
        // 雷达
        } else if (GroupMessageType.RADAR.getType().equals(msgtype) && attachment.getRadarMessage() != null) {
            RadarMessageDTO radarMessage = attachment.getRadarMessage();
            customerSeedMessage.setExtraId(radarMessage.getRadarId());
            customerSeedMessage.setLinkTitle(radarMessage.getLinkTitle());
            customerSeedMessage.setMessageType(GroupMessageType.RADAR.getType());
        } else if (GroupMessageType.FORM.getType().equals(msgtype) && attachment.getFormMessage() != null) {
            FormMessageDTO formMessage = attachment.getFormMessage();
            customerSeedMessage.setExtraId(formMessage.getFormId());
            customerSeedMessage.setLinkTitle(formMessage.getLinkTitle());
            customerSeedMessage.setMessageType(GroupMessageType.FORM.getType());
        }
        //视频
        else if (GroupMessageType.VIDEO.getType().equals(msgtype) && attachment.getVideoDTO() != null) {
            VideoDTO videoDTO = attachment.getVideoDTO();
            String name = StringUtils.isNotBlank(videoDTO.getTitle()) ? videoDTO.getTitle() : FileUtil.getName(videoDTO.getVideoUrl());
            customerSeedMessage.setVideoName(name);
            customerSeedMessage.setVideoUrl(Optional.ofNullable(videoDTO.getVideoUrl()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setMessageType(GroupMessageType.VIDEO.getType());
            customerSeedMessage.setPicUrl(videoDTO.getCoverUrl());
            customerSeedMessage.setSize(videoDTO.getSize());
        }
        //文件
        else if (GroupMessageType.FILE.getType().equals(msgtype) && attachment.getFileDTO() != null) {
            FileDTO fileDTO = attachment.getFileDTO();
            String name = StringUtils.isNotBlank(fileDTO.getTitle()) ? fileDTO.getTitle() : FileUtil.getName(fileDTO.getFileUrl());
            customerSeedMessage.setFileName(name);
            customerSeedMessage.setFileUrl(Optional.ofNullable(fileDTO.getFileUrl()).orElse(StrUtil.EMPTY));
            customerSeedMessage.setMessageType(GroupMessageType.FILE.getType());
        }
    }

}
