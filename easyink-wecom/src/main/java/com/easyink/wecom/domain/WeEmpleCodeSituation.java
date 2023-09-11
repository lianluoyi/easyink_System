package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获客链接-主页获客情况表实体类
 *
 * @author lichaoyu
 * @date 2023/8/24 14:25
 */
@Data
@NoArgsConstructor
@TableName("we_emple_code_situation")
public class WeEmpleCodeSituation {

    @ApiModelProperty("企业ID")
    @TableId
    @TableField("corp_id")
    private String corpId;
    @ApiModelProperty("今日新增客户数")
    @TableField("new_customer_cnt")
    private Integer newCustomerCnt;
    @ApiModelProperty("累计新增客户数（历史累计使用量），企微官方获取")
    @TableField("total")
    private Integer total;
    @ApiModelProperty("剩余使用量，企微官方获取")
    @TableField("balance")
    private Integer balance;

    /**
     * 当没有获取到当前企业下的获客情况信息，构造默认值返回
     *
     * @param corpId  企业ID
     * @param total   累计新增客户数
     * @param balance 剩余使用量
     */
    public WeEmpleCodeSituation(String corpId, Integer total, Integer balance) {
        this.corpId = corpId;
        this.newCustomerCnt = 0;
        this.total = total;
        this.balance = balance;
    }

    /**
     * 处理同步获客情况数据
     *
     * @param newCustomerCnt 今日新增客户数
     * @param total          累计新增客户数
     * @param balance        剩余使用量
     */
    public void handleSyncData(Integer newCustomerCnt, Integer total, Integer balance) {
        this.newCustomerCnt += newCustomerCnt;
        this.total = total;
        this.balance = balance;
    }

    /**
     * 处理获客情况定时任务数据
     *
     * @param total   累计新增客户数
     * @param balance 剩余使用量
     */
    public void handleTaskData(Integer total, Integer balance) {
        this.newCustomerCnt = 0;
        this.total = total;
        this.balance = balance;
    }
}
