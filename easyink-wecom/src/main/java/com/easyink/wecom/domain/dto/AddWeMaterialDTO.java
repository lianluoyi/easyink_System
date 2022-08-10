package com.easyink.wecom.domain.dto;

import com.easyink.wecom.domain.WeMaterial;
import com.easyink.wecom.domain.vo.radar.WeRadarVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名：AddWeMaterialDTO
 *
 * @author Society my sister Li
 * @date 2021-10-12 14:43
 */
@Data
@ApiModel("添加素材实体")
public class AddWeMaterialDTO extends WeMaterial {

    /**
     * WeCategoryMediaTypeEnum
     */
    @ApiModelProperty(value = "素材类型:0海报、1语音、2视频、3普通文件、4文本、5链接、6小程序、7雷达", required = true)
    @NotNull(message = "mediaType不能为空")
    private Integer mediaType;

    @ApiModelProperty(value = "标签")
    private List<Long> tagIdList;

    @ApiModelProperty(value = "雷达VO")
    private WeRadarVO radar;
}
