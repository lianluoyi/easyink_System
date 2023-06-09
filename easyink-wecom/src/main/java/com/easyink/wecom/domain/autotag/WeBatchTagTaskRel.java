package com.easyink.wecom.domain.autotag;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量打标签任务-标签关联实体类
 *
 * @author lichaoyu
 * @date 2023/6/5 9:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_batch_tag_task_rel")
public class WeBatchTagTaskRel {

  @ApiModelProperty(value = "任务id")
  @TableField("task_id")
  private Long taskId;

  @ApiModelProperty(value = "此次任务需要打上的标签id")
  @TableField("tag_id")
  private String tagId;

}
