package com.easyink.common.utils.file;

/**
 * 媒体类型工具类
 *
 * @author admin
 */
public class MimeTypeUtils {
    public static final String IMAGE_PNG = "image/png";

    public static final String IMAGE_JPG = "image/jpg";

    public static final String IMAGE_JPEG = "image/jpeg";

    public static final String IMAGE_BMP = "image/bmp";

    public static final String IMAGE_GIF = "image/gif";

    public static final String XLSX = "xlsx";

    public static final String XLS = "xls";

    protected static final String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

    private MimeTypeUtils() {
    }

    /**
     * 获取IMAGE_EXTENSION
     *
     * @return IMAGE_EXTENSION
     */
    public static String[] getImageExtension(){
        return IMAGE_EXTENSION;
    }

    protected static final String[] FLASH_EXTENSION = {"swf", "flv"};

    /**
     * 获取FLASH_EXTENSION
     *
     * @return FLASH_EXTENSION
     */
    public static String[] getFlashExtension(){
        return FLASH_EXTENSION;
    }

    protected static final String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
            "asf", "rm", "rmvb"};

    /**
     * 获取MEDIA_EXTENSION
     *
     * @return MEDIA_EXTENSION
     */
    public static String[] getMediaExtension(){
        return MEDIA_EXTENSION;
    }

    protected static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // pdf
            "pdf", "wav", "amr", "mp4", "mp3"};

    /**
     * 获取DEFAULT_ALLOWED_EXTENSION
     *
     * @return DEFAULT_ALLOWED_EXTENSION
     */
    public static String[] getDefaultAllowedExtension(){
        return DEFAULT_ALLOWED_EXTENSION;
    }

    public static String getExtension(String prefix) {
        switch (prefix) {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPG:
                return "jpg";
            case IMAGE_JPEG:
                return "jpeg";
            case IMAGE_BMP:
                return "bmp";
            case IMAGE_GIF:
                return "gif";
            case XLSX:
                return "xlsx";
            case XLS:
                return "xls";
            default:
                return "";
        }
    }
}
