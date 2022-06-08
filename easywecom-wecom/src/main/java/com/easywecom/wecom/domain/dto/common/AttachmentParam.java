package com.easywecom.wecom.domain.dto.common;

import com.easywecom.common.enums.AttachmentTypeEnum;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.WeMaterial;
import com.easywecom.wecom.domain.WeMsgTlpMaterial;
import com.easywecom.wecom.domain.dto.message.Attachment;
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
    public static AttachmentParam costFromWeMsgTlpMaterial(WeMsgTlpMaterial weMsgTlpMaterial, AttachmentTypeEnum typeEnum) {
        if (weMsgTlpMaterial == null) {
            return null;
        }
        AttachmentParam attachmentParam = new AttachmentParam();
        BeanUtils.copyProperties(weMsgTlpMaterial, attachmentParam);
        attachmentParam.setTypeEnum(typeEnum);
        return attachmentParam;

    }

    /**
     * 将WeMaterial对象转化为该对象
     */
    public static AttachmentParam costFromWeMaterialByType(WeMaterial weMaterial, AttachmentTypeEnum typeEnum) {
        if (weMaterial == null || typeEnum == null) {
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
            case MINIPROGRAM:
                return builder.content(weMaterial.getMaterialName())
                        .picUrl(weMaterial.getCoverUrl())
                        .description(weMaterial.getContent())
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
    public static AttachmentParam costFromAttachment(Attachment attachment) {
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

