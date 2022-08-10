package com.easywecom.wecom.domain.vo;

import com.easywecom.wecom.domain.vo.radar.WeRadarVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 素材Vo
 *
 * @author admin
 * @Date 2021/3/26 17:51
 */
@Data
@ApiModel("查询素材实体Vo")
public class WeMaterialVO {
    /**
     * 素材类型。参考 {@link com.easywecom.common.enums.MediaType}
     */
    private Integer mediaType;

    private Long id;

    /**
     * 本地资源文件地址
     */
    private String materialUrl;

    /**
     * 文本内容、图片文案
     */
    private String content;

    /**
     * 图片名称
     */
    private String materialName;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 封面本地资源文件
     */
    private String coverUrl;

    /**
     * 音频时长
     */
    private String audioTime;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String expireTime;

    /**
     * 是否发布到侧边栏（0否，1是）
     */
    private Boolean showMaterial;

    @ApiModelProperty("链接是否使用自定义信息")
    private Boolean isDefined;

    /**
     * 已关联的标签
     */
    private List<WeMaterialAndTagRel> tagList;

    private String tagIds;

    @ApiModelProperty("保存雷达时使用")
    private Long radarId;

    @ApiModelProperty("链接时使用(0,不转化为雷达，1：转化为雷达)")
    private Boolean enableConvertRadar;
}