package com.easyink.common.enums.material;

import com.easyink.common.utils.StringUtils;
import lombok.Getter;

/**
 * 从URL中获取的MIME文件类型枚举类
 *
 * @author lichaoyu
 * @date 2023/6/29 15:54
 */
public enum UrlFileTypeEnum {

    // TODO 此处只列举了部分常用的文件类型，待后续全部补充 Tower 任务: 重新上传时补充文件名没有后缀的文件 ( https://tower.im/teams/636204/todos/71064 )
    JPEG("image/jpeg", ".jpeg"),
    MPEG("video/mpeg", ".mpeg"),
    PNG("image/png", ".png"),
    GIF("image/gif", ".gif"),
    PDF("application/pdf", ".pdf"),
    CDF("application/x-cdf", ".cdf"),
    TXT("text/plain", ".txt"),
    DOC("application/msword", ".doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    DCX("image/x-dcx", ".dcx"),
    XLS( "application/vnd.ms-excel", ".xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    XSL("text/xml", ".xsl"),
    PPT("application/vnd.ms-powerpoint", ".ppt"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx"),
    WAV("audio/x-wav", ".wav"),
    MP3("audio/mpeg", ".mp3"),
    MP4("video/mp4", ".mp4"),
    AVI("video/x-msvideo", ".avi"),
    FLV("application/octet-stream", ".flv"),
    ZIP("application/zip", ".zip"),
    GZ("application/x-gzip", ".gz"),
    TAR("application/x-tar", ".tar"),
    EXE("application/octet-stream", ".exe"),
    ;

    @Getter
    private final String type;

    @Getter
    private final String suffix;

    UrlFileTypeEnum(String type, String suffix) {
        this.type = type;
        this.suffix = suffix;
    }

    /**
     * 根据MIME类型获取文件后缀
     *
     * @param type MIME类型
     * @return 文件后缀
     */
    public static String getSuffixByType(String type) {
        for (UrlFileTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value.suffix;
            }
        }
        return StringUtils.EMPTY;
    }
}
