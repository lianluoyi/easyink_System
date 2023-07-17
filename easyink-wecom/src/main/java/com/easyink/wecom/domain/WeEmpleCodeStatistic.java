package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 活码统计表实体类
 *
 * @author lichaoyu
 * @date 2023/7/5 13:44
 */
@Data
@TableName("we_emple_code_statistic")
@NoArgsConstructor
@ApiModel("活码统计")
public class WeEmpleCodeStatistic {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId
    @TableField("id")
    private Long id;

    @ApiModelProperty("企业id")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("活码id")
    @TableField("emple_code_id")
    private Long empleCodeId;

    @ApiModelProperty("员工id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("累计客户数")
    @TableField("accumulate_customer_cnt")
    private Integer accumulateCustomerCnt;

    @ApiModelProperty("留存客户数")
    @TableField("retain_customer_cnt")
    private Integer retainCustomerCnt;

    @ApiModelProperty("新增客户数")
    @TableField("new_customer_cnt")
    private Integer newCustomerCnt;

    @ApiModelProperty("流失客户数")
    @TableField("loss_customer_cnt")
    private Integer lossCustomerCnt;

    @ApiModelProperty("日期")
    @TableField("time")
    private String time;

    /**
     * 用于初始化默认数据构造
     *
     * @param corpId 企业ID
     * @param empleCodeId 活码ID
     * @param userId 员工ID
     * @param time 日期，格式为YYYY-MM-DD
     */
    public WeEmpleCodeStatistic(String corpId, Long empleCodeId, String userId, String time) {
        this.corpId = corpId;
        this.empleCodeId = empleCodeId;
        this.userId = userId;
        this.time = time;
        accumulateCustomerCnt = 0;
        retainCustomerCnt = 0;
        newCustomerCnt = 0;
        lossCustomerCnt = 0;
    }

    /**
     * 添加回调数据处理方法
     */
    public void addHandle() {
        this.accumulateCustomerCnt++;
        this.retainCustomerCnt++;
        this.newCustomerCnt++;
    }


}
