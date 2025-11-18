package com.easyink.wecom.domain.autotag;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.annotation.EncryptField;
import com.easyink.common.annotation.Excel;
import com.easyink.common.enums.batchtag.BatchTagDetailStatusEnum;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 批量打标签任务详情实体类
 *
 * @author lichaoyu
 * @date 2023/6/5 9:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_batch_tag_task_detail")
public class WeBatchTagTaskDetail implements Serializable {
  @ApiModelProperty(value = "主键id")
  @TableField("id")
  private Long id = SnowFlakeUtil.nextId();

  @ApiModelProperty(value = "批量群发任务id")
  @TableField("task_id")
  private Long taskId;

  @ApiModelProperty(value = "导入的表格中的外部联系人id")
  @TableField("import_external_userid")
  @Excel(name = "external_userid", sort = 1)
  private String importExternalUserid;

  @ApiModelProperty(value = "导入的表格中的unionId,外部联系人在微信开放平台的唯一身份标识,通过此字段企业可将外部联系人与公众号/小程序用户关联起来。")
  @TableField("import_union_id")
  @Excel(name = "unionid", sort = 2)
  private String importUnionId;

  @ApiModelProperty(value = "导入的手机号")
  @TableField("import_mobile")
  @Excel(name = "手机号", sort = 3)
  @EncryptField(EncryptField.FieldType.MOBILE)
  private String importMobile;
  @ApiModelProperty(value = "导入的手机号加密")
  private String importMobileEncrypt;

  @ApiModelProperty(value = "打标签状态（0 待执行， 1 成功 ， 2 失败）")
  @TableField("status")
  @Excel(name = "打标状态", sort = 4, readConverterExp = "0=待执行,1=成功,2=失败", defaultValue = "待执行")
  private Integer status;

  @ApiModelProperty(value = "实际打标签的user_id")
  @TableField("tag_user_id")
  private String tagUserId;

  @ApiModelProperty(value = "实际打标签的客户id")
  @TableField("tag_external_userid")
  private String tagExternalUserid;

  @ApiModelProperty(value = "备注")
  @TableField("remark")
  @Excel(name = "备注", sort = 5)
  private String remark;

  @ApiModelProperty(value = "需要打标签的userId列表")
  @TableField(exist = false)
  private Set<String> tagUserIds = new HashSet<>();

  @ApiModelProperty(value = "需要打标签的客户关系rel 列表")
  @TableField(exist = false)
  private Set<WeFlowerCustomerRel> relSet = new HashSet<>();

  /**
   * 判断是否有匹配到客户
   *
   * @return true or false
   */
  public boolean notMatchCustomer() {
    return StringUtils.isBlank(this.tagExternalUserid);
  }

  /**
   * 设置打标签成功
   */
  public void success() {
    this.status = BatchTagDetailStatusEnum.SUCCESS.getStatus();
    this.remark = StringUtils.EMPTY;
  }

  /**
   * 设置打标签失败
   *
   * @param remark 失败备注
   */
  public void fail(String remark) {
    if (Objects.equals(this.status, BatchTagDetailStatusEnum.SUCCESS.getStatus())) {
      // 如果同一组已经有成功的,就算其他员工打失败也算成功
      return;
    }
    this.status = BatchTagDetailStatusEnum.FAIL.getStatus();
    this.remark = remark;
  }
  public WeBatchTagTaskDetail(Long taskId, String importExternalUserid, String importUnionId, String importMobile) {
    this.taskId = taskId;
    if (StringUtils.isBlank(importExternalUserid)) {
      this.importExternalUserid = "";
    } else {
      this.importExternalUserid = importExternalUserid.trim();
    }
    if (StringUtils.isBlank(importUnionId)) {
      this.importUnionId = "";
    } else {
      this.importUnionId = importUnionId.trim();
    }
    if (StringUtils.isBlank(importMobile)) {
      this.importMobile = "";
    } else {
      this.importMobile = importMobile.trim();
    }

  }
}