package com.easyink.wecom.domain.autotag;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.utils.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 批量打标签任务实体类
 *
 * @author lichaoyu
 * @date 2023/6/5 9:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_batch_tag_task")
public class WeBatchTagTask implements Serializable {

  @ApiModelProperty(value = "主键id")
  @TableField("id")
  private Long id = SnowFlakeUtil.nextId();

  @ApiModelProperty(value = "企业id")
  @TableField("corp_id")
  private String corpId;

  @ApiModelProperty(value = "任务名称")
  @TableField("name")
  private String name;

  @ApiModelProperty(value = "执行状态（0 [false]执行中，1 [true]已执行)")
  @TableField("execute_flag")
  private Boolean executeFlag;

  @ApiModelProperty(value = "创建时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @TableField("create_time")
  private Date createTime;

  @ApiModelProperty(value = "创建人")
  @TableField("create_by")
  private String createBy;

  @ApiModelProperty(value = "更新时间")
  @TableField("update_time")
  private Date updateTime;

  @ApiModelProperty(value = "需要打上的标签（作冗余,同时因为后续可能存在标签被删除的情况，所以这里直接存打标签时的名称)")
  @TableField("tag_name")
  private String tagName;

  @ApiModelProperty(value = "是否被删除(0 否， 1 被删除）")
  @TableField("del_flag")
  private Boolean delFlag;
}
