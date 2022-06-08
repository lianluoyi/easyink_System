package com.easywecom.wecom.domain.dto.moment;

import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.GroupMessageType;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.file.FileUtils;
import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.easywecom.wecom.domain.dto.message.ImageMessageDTO;
import com.easywecom.wecom.domain.dto.message.LinkMessageDTO;
import com.easywecom.wecom.domain.dto.message.VideoDTO;
import com.easywecom.wecom.service.WeCustomerMessageService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import ws.schild.jave.EncoderException;

import java.io.File;
import java.io.IOException;

/**
 * 类名： MomentAttachment
 *
 * @author 佚名
 * @date 2022/1/11 15:10
 */
@ApiModel("朋友圈附件")
@Data
@NoArgsConstructor
@Slf4j
public class MomentAttachment {
    @ApiModelProperty("消息类型")
    private String msgtype;

    @ApiModelProperty("图片/海报消息")
    private ImageMessageDTO image;

    @ApiModelProperty("链接消息")
    private LinkMessageDTO link;

    @ApiModelProperty("视频")
    private VideoDTO video;

    private MomentAttachment(VideoDTO videoDTO) {
        this.video = videoDTO;
        this.setMsgtype(GroupMessageType.VIDEO.getMessageType());
    }

    public MomentAttachment(LinkMessageDTO linkMessageDTO) {
        this.link = linkMessageDTO;
        this.setMsgtype(GroupMessageType.LINK.getMessageType());
    }

    public MomentAttachment(ImageMessageDTO imageMessageDTO) {
        this.image = imageMessageDTO;
        this.setMsgtype(GroupMessageType.IMAGE.getMessageType());
    }

    public static MomentAttachment buildMomentVideo(WeWordsDetailEntity detailEntity, String corpId) {
        WeCustomerMessageService weCustomerMessageService = SpringUtils.getBean(WeCustomerMessageService.class);
        String coverUrl = StringUtils.isEmpty(detailEntity.getCoverUrl())?WeConstans.VIDEO_2_LINK_DEFAULT_URL:detailEntity.getCoverUrl();
        String coverMediaId = weCustomerMessageService.buildMediaId(coverUrl, GroupMessageType.IMAGE.getMessageType(), 1, detailEntity.getTitle(), corpId);
        if (detailEntity.getSize() != null && detailEntity.getSize() < WeConstans.DEFAULT_MAX_VIDEO_SIZE && checkVideo(detailEntity)) {
            //获取mediaId
            String mediaId = weCustomerMessageService.buildMediaId(detailEntity.getUrl(), GroupMessageType.VIDEO.getMessageType(), 1, detailEntity.getTitle(), corpId);
            return new MomentAttachment(new VideoDTO(mediaId, coverMediaId, detailEntity.getTitle()));
        } else {
            //大于10M 30s转链接
            return new MomentAttachment(new LinkMessageDTO(coverMediaId, detailEntity.getTitle(), detailEntity.getUrl(), "点击查看视频"));
        }
    }

    /**
     * 校验视频的长度
     * @param detailEntity
     * @return
     */
    private static boolean checkVideo(WeWordsDetailEntity detailEntity){
        if (!GroupMessageType.VIDEO.getType().equals(detailEntity.getMediaType().toString())){
            return false;
        }
        try {
            File file = FileUtils.getFileByUrl(detailEntity.getUrl());
            return FileUtils.getDuration(file) <= 30;
        } catch (IOException | EncoderException e) {
            log.error("朋友圈获取视频临时文件失败 e:{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}
