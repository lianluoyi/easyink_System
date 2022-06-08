package com.easywecom.wecom.domain.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类名： FileDTO
 *
 * @author 佚名
 * @date 2021/10/13 10:34
 */
@Data
@ApiModel("文件DTO")
public class FileDTO {
    /**
     * 文件素材id
     */
    @ApiModelProperty("文件素材id")
    private String media_id;
    /**
     * 文件url
     */
    @ApiModelProperty("文件url")
    private String fileUrl;

    @ApiModelProperty("文件标题")
    private String title;

}
