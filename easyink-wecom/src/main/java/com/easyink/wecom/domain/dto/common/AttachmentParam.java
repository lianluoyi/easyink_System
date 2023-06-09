package com.easyink.wecom.domain.dto.common;

import com.easyink.common.constant.radar.RadarConstants;
import com.easyink.common.enums.AttachmentTypeEnum;
import com.easyink.common.enums.EmployCodeSourceEnum;
import com.easyink.common.enums.code.WelcomeMsgTypeEnum;
import com.easyink.common.enums.radar.RadarChannelEnum;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.WeMaterial;
import com.easyink.wecom.domain.WeMsgTlpMaterial;
import com.easyink.wecom.domain.dto.message.Attachment;
import com.easyink.wecom.domain.enums.form.FormChannelEnum;
import com.easyink.wecom.service.radar.WeRadarService;
import com.easyink.wecom.utils.ExtraMaterialUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;


/**
 * 公共素材参数
 *
 * @author tigger
 * 2022/1/20 16:20
 **/
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AttachmentParam {

    @ApiModelProperty(value = "文本内容,链接消息标题,小程序消息标题，(前端: 图片,文件,视频的标题)")
    private String content;

    @ApiModelProperty(value = "图片url,链接封面url,小程序picurl,文件url,视频url")
    private String picUrl;

    @ApiModelProperty(value = "链接消息描述,小程序appid(前端: 文件大小)")
    private String description;

    @ApiModelProperty(value = "链接url,小程序page")
    private String url;

    /**
     * {@link AttachmentTypeEnum}
     */
    private AttachmentTypeEnum typeEnum;

    /**
     * 将WeMsgTlpMaterial对象转化为该对象
     */
    public static AttachmentParam costFromWeMsgTlpMaterial(Long extraId, String userId, String corpId, WeMsgTlpMaterial weMsgTlpMaterial, AttachmentTypeEnum typeEnum) {
        if (weMsgTlpMaterial == null) {
            return null;
        }
        AttachmentParam attachmentParam = new AttachmentParam();
        BeanUtils.copyProperties(weMsgTlpMaterial, attachmentParam);
        attachmentParam.setTypeEnum(typeEnum);
        if (AttachmentTypeEnum.RADAR.equals(attachmentParam.getTypeEnum())) {
            return buildRadarAttachment(extraId, RadarChannelEnum.WELCOME_MSG.getTYPE(), userId, corpId, RadarConstants.RadarCustomerClickRecord.COMMON_MSG);
        } else if(AttachmentTypeEnum.FORM.equals(attachmentParam.getTypeEnum())) {
            return ExtraMaterialUtils.getFormAttachment(extraId, FormChannelEnum.WELCOME_MSG.getCode(), corpId, userId);
        }
        return attachmentParam;
    }

    /**
     * 获取短链id
     *
     * @param radarId
     * @param channelType
     * @param userId
     * @param corpId
     * @param scenario    场景名称
     * @return
     */
    private static AttachmentParam buildRadarAttachment(Long radarId, Integer channelType, String userId, String corpId, String scenario) {
        final WeRadarService radarService = SpringUtils.getBean(WeRadarService.class);
        return radarService.getRadarShortUrl(radarId, channelType, userId, corpId, scenario);
    }

    /**
     * 将WeMaterial对象转化为该对象
     */
    public static AttachmentParam costFromWeMaterialByType(Integer source, String scenario, String userId, String corpId, WeMaterial weMaterial, AttachmentTypeEnum typeEnum) {
        if (source == null || weMaterial == null || typeEnum == null) {
            return null;
        }
        AttachmentParam.AttachmentParamBuilder builder = AttachmentParam.builder();
        switch (typeEnum) {
            case IMAGE:
            case FILE:
                return builder.picUrl(weMaterial.getMaterialUrl()).typeEnum(typeEnum).build();
            case LINK:
                return builder.content(weMaterial.getMaterialName())
                        .picUrl(weMaterial.getCoverUrl())
                        .description(weMaterial.getDigest())
                        .url(weMaterial.getMaterialUrl()).typeEnum(typeEnum).build();
            case RADAR:
                Integer radarChannelType = EmployCodeSourceEnum.NEW_GROUP.getSource().equals(source) ? RadarChannelEnum.NEW_IN_GROUP.getTYPE() : RadarChannelEnum.EMPLE_CODE.getTYPE();
                return buildRadarAttachment(weMaterial.getExtraId(), radarChannelType, userId, corpId, scenario);
            case FORM:
                Integer formChannelType = EmployCodeSourceEnum.NEW_GROUP.getSource().equals(source) ? FormChannelEnum.NEW_IN_GROUP.getCode() : FormChannelEnum.EMPLE_CODE.getCode();
                return ExtraMaterialUtils.getFormAttachment(weMaterial.getExtraId(), formChannelType, corpId, userId);
            case MINIPROGRAM:
                return builder.content(weMaterial.getMaterialName())
                        .picUrl(weMaterial.getCoverUrl())
                        .description(weMaterial.getAppid())
                        .url(weMaterial.getMaterialUrl()).typeEnum(typeEnum).build();
            case VIDEO:
                return builder.picUrl(weMaterial.getMaterialUrl()).description(weMaterial.getContent()).typeEnum(typeEnum).build();
            default:
                return null;
        }
    }

    /**
     * 将{@link Attachment}对象转化为该对象
     */
    public static AttachmentParam costFromAttachment(String sender, String corpId, String taskName, Attachment attachment) {
        if (attachment == null || StringUtils.isEmpty(attachment.getMsgtype())) {
            return null;
        }
        AttachmentParamBuilder builder = AttachmentParam.builder();
        Integer num;
        try {
            num = Integer.valueOf(attachment.getMsgtype());
        } catch (NumberFormatException e) {
            return null;
        }
        AttachmentTypeEnum typeEnum = AttachmentTypeEnum.mappingFromGroupMessageType(num);
        if (typeEnum == null) {
            return null;
        }
        switch (typeEnum) {
            case IMAGE:
                return builder.picUrl(attachment.getImageMessage().getPic_url()).typeEnum(typeEnum).build();
            case FILE:
                return builder.picUrl(attachment.getFileDTO().getFileUrl()).typeEnum(typeEnum).build();
            case LINK:
                return builder.content(attachment.getLinkMessage().getTitle())
                        .picUrl(attachment.getLinkMessage().getPicurl())
                        .description(attachment.getLinkMessage().getDesc())
                        .url(attachment.getLinkMessage().getUrl()).typeEnum(typeEnum).build();
            case RADAR:
                return buildRadarAttachment(attachment.getRadarMessage().getRadarId(), RadarChannelEnum.GROUP_TASK.getTYPE(), sender, corpId, taskName);
            case FORM:
                return ExtraMaterialUtils.getFormAttachment(attachment.getFormMessage().getFormId(), FormChannelEnum.GROUP_TASK.getCode(), corpId, sender);
            case MINIPROGRAM:
                return builder.picUrl(attachment.getMiniprogramMessage().getPicUrl())
                        .content(attachment.getMiniprogramMessage().getTitle())
                        .description(attachment.getMiniprogramMessage().getAppid())
                        .url(attachment.getMiniprogramMessage().getPage()).typeEnum(typeEnum).build();
            case VIDEO:
                return builder.content(attachment.getVideoDTO().getTitle())
                        .picUrl(attachment.getVideoDTO().getVideoUrl())
                        .description(String.valueOf(attachment.getVideoDTO().getSize()))
                        .url(attachment.getVideoDTO().getCoverUrl()).typeEnum(typeEnum).build();
            default:
                return null;
        }
    }

}

