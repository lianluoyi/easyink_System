package com.easyink.wecom.domain.vo;

import com.easyink.wecom.domain.WeWordsDetailEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类名： 企业话术库VO
 *
 * @author 佚名
 * @date 2021/10/28 10:29
 */
@Data
@ApiModel("企业话术库VO")
@NoArgsConstructor
public class WeWordsVO {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID")
    private Long categoryId;
    /**
     * 话术标题
     */
    @ApiModelProperty(value = "话术标题")
    private String title;
    /**
     * 附件ID用逗号隔开，从左往右表示先后顺序
     */
    @ApiModelProperty(value = "附件ID用逗号隔开，从左往右表示先后顺序")
    private String[] seq;
    /**
     * 是否推送到应用（0：不推送，1推送）
     */
    @ApiModelProperty(value = "是否推送到应用（false：不推送，true推送）")
    private Boolean isPush;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty("话术库附件")
    private List<WeWordsDetailEntity> weWordsDetailList;
}
