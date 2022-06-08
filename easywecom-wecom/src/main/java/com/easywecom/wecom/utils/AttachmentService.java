package com.easywecom.wecom.utils;

import cn.hutool.core.io.FileUtil;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.enums.AttachmentTypeEnum;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.domain.dto.WeMediaDTO;
import com.easywecom.wecom.domain.dto.common.*;
import com.easywecom.wecom.service.WeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 附件构建工具类
 *
 * @author tigger
 * 2022/1/20 15:32
 **/
@Component
@Slf4j
public class AttachmentService {

    private final WeMaterialService weMaterialService;

    @Autowired
    public AttachmentService(WeMaterialService weMaterialService) {
        this.weMaterialService = weMaterialService;
    }


    public Attachments buildAttachment(AttachmentParam param, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (param == null || param.getTypeEnum() == null) {
            return null;
        }
        switch (param.getTypeEnum()) {
            case IMAGE:
                return buildImage(param.getPicUrl(), param.getTypeEnum(), corpId);
            case LINK:
                return buildLink(param.getContent(), param.getPicUrl(), param.getDescription(), param.getUrl(), param.getTypeEnum());
            case MINIPROGRAM:
                return buildMiniprogram(param.getContent(), param.getPicUrl(), param.getDescription(), param.getUrl(), param.getTypeEnum(), corpId);
            case FILE:
                return buildFile(param.getPicUrl(), param.getTypeEnum(), corpId);
            case VIDEO:
                return buildVideo(param.getContent(), param.getPicUrl(), param.getDescription(), param.getUrl(), param.getTypeEnum(), corpId);
            default:
                log.error("type error !!!");
                return null;
        }
    }


    /**
     * 构建视频
     */
    private Attachments buildVideo(String content, String picUrl, String fileSize, String url, AttachmentTypeEnum type, String corpId) {
        if (StringUtils.isEmpty(picUrl)) {
            return null;
        }
        Attachments attachments;
        // 大于10M，需要发送为link形式
        if (StringUtils.isNotEmpty(fileSize) && Integer.parseInt(fileSize) > WeConstans.DEFAULT_MAX_VIDEO_SIZE) {
            // 标题
            content = StringUtils.isNotEmpty(content) ? content : FileUtil.getName(picUrl);
            // 链接地址
            url = picUrl;
            // 封面图片
            picUrl = WeConstans.DEFAULT_VIDEO_COVER_URL;
            // 描述信息
            String desc = WeConstans.CLICK_SEE_VIDEO;
            attachments = buildLink(content, picUrl, desc, url, AttachmentTypeEnum.LINK);
            return attachments;
        }
        attachments = new Attachments();
        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, type.getTypeStr(), FileUtil.getName(picUrl), corpId);
        if (weMediaDto == null) {
            toE();
        }
        attachments.setMsgtype(type.getTypeStr());
        attachments.setVideo(Video.builder().media_id(weMediaDto.getMedia_id()).build());
        return attachments;
    }

    /**
     * 构建文件
     */
    private Attachments buildFile(String picUrl, AttachmentTypeEnum type, String corpId) {
        if (StringUtils.isEmpty(picUrl)) {
            return null;
        }
        Attachments attachments = new Attachments();
        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, type.getTypeStr(), FileUtil.getName(picUrl), corpId);
        if (weMediaDto == null) {
            toE();
        }
        attachments.setMsgtype(type.getTypeStr());
        attachments.setFile(File.builder().media_id(weMediaDto.getMedia_id()).build());
        return attachments;
    }

    /**
     * 构建小程序
     */
    private Attachments buildMiniprogram(String content, String picUrl, String description, String url, AttachmentTypeEnum type, String corpId) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(picUrl) || StringUtils.isEmpty(description) || StringUtils.isEmpty(url)) {
            return null;
        }
        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, AttachmentTypeEnum.IMAGE.getTypeStr(), FileUtil.getName(picUrl), corpId);
        if (weMediaDto == null) {
            toE();
        }
        Attachments attachments = new Attachments();
        attachments.setMsgtype(type.getTypeStr());
        attachments.setMiniprogram(MiniProgram.builder()
                .title(content)
                .pic_media_id(weMediaDto.getMedia_id())
                .appid(description)
                .page(url)
                .build());
        return attachments;
    }

    /**
     * 构建链接
     */
    private Attachments buildLink(String content, String picUrl, String description, String url, AttachmentTypeEnum type) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(url)) {
            return null;
        }
        Attachments attachments = new Attachments();
        attachments.setMsgtype(type.getTypeStr());
        attachments.setLink(Link.builder()
                .title(content)
                .picurl(picUrl)
                .desc(description)
                .url(url)
                .build());
        return attachments;
    }

    /**
     * 构建图片
     */
    private Attachments buildImage(String picUrl, AttachmentTypeEnum type, String corpId) {
        if (StringUtils.isEmpty(picUrl)) {
            return null;
        }
        Attachments attachments = new Attachments();
        WeMediaDTO weMediaDto = weMaterialService.uploadTemporaryMaterial(picUrl, type.getTypeStr(), FileUtil.getName(picUrl), corpId);
        if (weMediaDto == null) {
            toE();
        }
        attachments.setMsgtype(type.getTypeStr());
        attachments.setImage(Image.builder().media_id(weMediaDto.getMedia_id()).build());
        return attachments;
    }

    private void toE() {
        log.error("AttachmentService文件上传失败");
        throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
    }


}
