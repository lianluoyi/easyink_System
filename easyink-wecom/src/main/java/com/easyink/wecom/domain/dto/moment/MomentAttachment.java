package com.easyink.wecom.domain.dto.moment;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.GroupMessageType;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.dto.message.ImageMessageDTO;
import com.easyink.wecom.domain.dto.message.LinkMessageDTO;
import com.easyink.wecom.domain.dto.message.RadarMessageDTO;
import com.easyink.wecom.domain.dto.message.VideoDTO;
import com.easyink.wecom.service.WeCustomerMessageService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import ws.schild.jave.EncoderException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    @ApiModelProperty("链接消息")
    private RadarMessageDTO radar;

    @ApiModelProperty("视频")
    private VideoDTO video;

    private MomentAttachment(RadarMessageDTO radarMessageDTO) {
        this.radar = radarMessageDTO;
        this.setMsgtype(GroupMessageType.RADAR.getMessageType());
    }

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
        if (!GroupMessageType.VIDEO.getType().equals(detailEntity.getMediaType().toString()) || StringUtils.isBlank(detailEntity.getUrl())){
            return false;
        }
        try {
            String url = detailEntity.getUrl();
            File file;
            // 路径不包含"/profile"，直接使用URL获取文件
            if (!url.contains(Constants.RESOURCE_PREFIX)) {
                file = FileUtils.getFileByUrl(url);
            } else {
                // 本地上传，将url路径转换为绝对路径获取文件
                file = new File(url.replace(Constants.RESOURCE_PREFIX, RuoYiConfig.getProfile()));
            }
            return FileUtils.getDuration(file) <= 30;
        } catch (IOException | EncoderException e) {
            log.error("朋友圈获取视频临时文件失败 e:{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}
