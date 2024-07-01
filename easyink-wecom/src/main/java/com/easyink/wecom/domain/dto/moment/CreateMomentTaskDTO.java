package com.easyink.wecom.domain.dto.moment;

import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 类名： 朋友圈创建发表任务DTO（本地）
 *
 * @author 佚名
 * @date 2022/1/10 14:11
 */
@Data
@ApiModel("朋友圈创建发表任务DTO（本地）")
public class CreateMomentTaskDTO {

    @ApiModelProperty(value = "朋友圈任务id 更新时必传")
    private Long momentTaskId;

    @ApiModelProperty(value = "发布类型（0：企业 1：个人）")
    private Integer type;

    @ApiModelProperty(value = "企业id", hidden = true)
    @JsonIgnore
    private String corpId;

    @ApiModelProperty(value = "任务类型（0：立即发送 1：定时发送）")
    private Integer taskType;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date sendTime;

    @ApiModelProperty("文本消息")
    private TextMessageDTO text;

    @ApiModelProperty("附件，不能与text.content同时为空，最多支持9个图片类型，或者1个视频，或者1个链接。类型只能三选一，若传了不同类型，报错’不支持的附件类型’")
    private List<WeWordsDetailEntity> attachments;

    @ApiModelProperty(value = "客户所属员工")
    private List<String> users;

    @ApiModelProperty(value = "客户所属员工所在的部门")
    private List<String> departments;

    @ApiModelProperty("客户标签")
    private List<String> tags;

    @ApiModelProperty("创建人 更新时必传")
    private String createBy;

    @ApiModelProperty(value = "可见范围（0：全部客户 1：部分客户）",required = true)
    @NotNull(message = "pushRange可见范围不能为空")
    private Integer pushRange;

    @ApiModelProperty(value = "接口tokenId", required = true)
    @NotBlank(message = "幂等token缺失")
    private String tokenId;
}
