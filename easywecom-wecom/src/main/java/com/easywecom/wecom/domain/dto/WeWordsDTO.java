package com.easywecom.wecom.domain.dto;

import com.easywecom.wecom.domain.WeWordsDetailEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名： 话术保存DTO
 *
 * @author 佚名
 * @date 2021/10/27 14:17
 */
@Data
@ApiModel("话术保存DTO")
public class WeWordsDTO {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID 更新时传参")
    private Long id;
    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID")
    @JsonIgnore
    private String corpId;
    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID", required = true)
    @NotNull(message = "文件夹ID不能为空")
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
    @JsonIgnore
    private String[] seq;
    /**
     * 是否推送到应用（0：不推送，1推送）
     */
    @ApiModelProperty(value = "是否推送到应用", required = true)
    @NotNull(message = "是否推送不能为空")
    private Boolean isPush;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @NotEmpty(message = "话术库附件实体列表不能为空")
    @ApiModelProperty(value = "话术库附件实体列表 (列表的顺序就是显示的顺序)", required = true)
    private List<WeWordsDetailEntity> weWordsDetailList;

    @ApiModelProperty("待删除的附件id 删除附件时传参")
    private List<Long> wordsDetailIds;

    @ApiModelProperty("员工主部门id")
    @JsonIgnore
    private Long mainDepartment;
}
