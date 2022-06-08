package com.easywecom.wecom.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类名: 自定义属性关系 数据交互实体
 *
 * @author : silver_chariot
 * @date : 2021/11/10 17:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("客户自定义属性")
@Builder
public class WeCustomerExtendProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableField("id")
    @TableId
    @ApiModelProperty(value = "主键ID")
    private Long id;
    /**
     * 企业id
     */
    @TableField("corp_id")
    @ApiModelProperty(value = "企业ID")
    private String corpId;
    /**
     * 扩展字段名称
     */
    @TableField("name")
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * 字段类型（1系统默认字段,2单行文本，3多行文本，4单选框，5多选框，6下拉框，7日期，8图片，9文件）
     */
    @TableField("type")
    @ApiModelProperty(value = "类型,1系统默认字段,2单行文本，3多行文本，4单选框，5多选框，6下拉框，7日期，8图片，9文件")
    private Integer type;
    /**
     * 是否必填（1必填0非必填）
     */
    @TableField("required")
    @ApiModelProperty(value = "1必填0非必填")
    private Boolean required;
    /**
     * 字段排序
     */
    @TableField("property_sort")
    @ApiModelProperty(value = "字段排序")
    private Integer propertySort;
    /**
     * 状态（0停用1启用）
     */
    @TableField("status")
    @ApiModelProperty(value = "状态（0停用1启用）")
    private Boolean status;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建人
     */
    @TableField("create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 更新时间
     */
    @TableField("update_time")
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "多选框选项")
    @TableField(exist = false)
    private List<ExtendPropertyMultipleOption> optionList = new ArrayList<>();
    /**
     * 构造函数
     *
     * @param corpId       企业ID
     * @param name         属性名
     * @param type         类型
     * @param propertySort 排序
     * @param createBy     创建人
     */
    public WeCustomerExtendProperty(String corpId, String name, Integer type, Integer propertySort, String createBy) {
        this.corpId = corpId;
        this.name = name;
        this.type = type;
        this.propertySort = propertySort;
        this.createBy = createBy;
    }


}
