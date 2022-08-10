package com.easywecom.common.core.domain.conversation.msgtype;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * 文件消息VO
 *
 * @author tigger
 * 2022/1/25 18:01
 **/
@Data
@Alias("filev")
public class FileVO extends AttachmentBaseVO {

    private String filename;

    private String md5sum;

    private String sdkfileid;

    private Integer filesize;

    private String fileext;


}
