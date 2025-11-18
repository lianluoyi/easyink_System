package com.easyink.wecom.domain.vo.statistics;

import cn.hutool.core.util.StrUtil;
import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 客户概况-部门维度VO
 *
 * @author wx
 * 2023/12/XX XX:XX
 **/
@Data
@NoArgsConstructor
public class CustomerOverviewDepartmentVO {
    
    @ApiModelProperty("部门ID")
    private String departmentId;
    
    @ApiModelProperty("部门名称")
    @Excel(name = "部门名", sort = 1)
    private String departmentName;

    @ApiModelProperty("父部门ID")
    private String parentId;

    @Excel(name = "所属上级部门", sort = 2)
    @ApiModelProperty("所属上级部门")
    private String parentDepartmentName;

    @ApiModelProperty("部门员工总数")
    @Excel(name = "员工数", sort = 3)
    private Integer userCount;



    @ApiModelProperty("客户总数(由每日定时任务统计，不去重)")
    @Excel(name = "客户总数", sort = 4)
    private Integer totalAllContactCnt;

    @ApiModelProperty("留存客户总数")
    @Excel(name = "留存客户总数", sort = 5)
    private Integer totalContactCnt;

    @ApiModelProperty("流失客户数")
    @Excel(name = "流失客户数", sort = 6)
    private Integer contactLossCnt;

    @ApiModelProperty("新增客户数")
    @Excel(name = "新增客户数", sort = 7)
    private Integer newContactCnt;

    @ApiModelProperty("新客留存数")
    @Excel(name = "新客留存数", sort = 8)
    private Integer newCustomerRetentionCnt = 0;

    @ApiModelProperty("新客流失客户数")
    private Integer newContactLossCnt;

    @ApiModelProperty("新客留存率")
    @Excel(name = "新客留存率", sort = 9)
    private String newContactRetentionRate;

    @ApiModelProperty("新客开口率")
    @Excel(name = "新客开口率", sort = 10)
    private String newContactStartTalkRate;

    @ApiModelProperty("服务响应率")
    @Excel(name = "服务响应率", sort = 11)
    private String serviceResponseRate;

    @ApiModelProperty("当天新增客户中与员工对话过的人数")
    private Integer newContactSpeakCnt;

    @ApiModelProperty("当天员工首次给客户发消息，客户在30分钟内回复的客户数")
    private Integer repliedWithinThirtyMinCustomerCnt;

    @ApiModelProperty("当天员工会话数-不区分是否为员工主动发起")
    private Integer allChatCnt;

    @ApiModelProperty("当天员工主动发送消息数")
    private Integer userActiveChatCnt;

    @ApiModelProperty("截止到现在的有效客户数，客户状态为正常、待继承、转接中（0、3、4）")
    private Integer currentNewCustomerCnt = 0;





    /**
     * 获取新客留存率   截止当前的有效客户数 / 新增客户数
     *
     * @return 新客留存率
     */
    public String getNewContactRetentionRate() {
        if (newContactCnt == null || newContactCnt == 0) {
            return Constants.EMPTY_RETAIN_RATE_VALUE;
        }
        BigDecimal percent = new BigDecimal(100);
        BigDecimal newCntDecimal = new BigDecimal(newContactCnt);
        BigDecimal currCustomerCntDecimal = new BigDecimal(currentNewCustomerCnt);
        int scale = 2;
        return currCustomerCntDecimal
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    /**
     * 获取新客开口率   当天新增客户中与员工对话过的人数 / 新增客户数
     *
     * @return 新客开口率
     */
    public String getNewContactStartTalkRate() {
        if (newContactCnt == null || newContactCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        BigDecimal newCntDecimal = new BigDecimal(newContactCnt);
        BigDecimal newContactSpeakCntDecimal = new BigDecimal(newContactSpeakCnt);
        int scale = 2;
        return newContactSpeakCntDecimal
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 获取服务响应率   当天员工首次给客户发消息，客户在30分钟内回复的客户数 / 员工主动发起的会话数
     *
     * @return 服务响应率
     */
    public String getServiceResponseRate() {
        if (userActiveChatCnt == null || repliedWithinThirtyMinCustomerCnt == null) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal percent = new BigDecimal(100);
        if(userActiveChatCnt == 0) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal userActiveChatCntDecimal = new BigDecimal(userActiveChatCnt);
        BigDecimal repliedWithinThirtyMinCustomerCntDecimal = new BigDecimal(repliedWithinThirtyMinCustomerCnt);
        int scale = 2;
        return repliedWithinThirtyMinCustomerCntDecimal
                .multiply(percent)
                .divide(userActiveChatCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 绑定导出数据
     */
    public void bindExportData() {
        if (newContactCnt == null || newContactCnt == 0) {
            newContactRetentionRate = getNewContactRetentionRate() + Constants.EMPTY_RETAIN_RATE_VALUE;
        } else {
            newContactRetentionRate = getNewContactRetentionRate() + GenConstants.PERCENT;
        }
        newContactStartTalkRate = getNewContactStartTalkRate() + GenConstants.PERCENT;
        serviceResponseRate = getServiceResponseRate() + GenConstants.PERCENT;
        if(StringUtils.isBlank(this.getParentDepartmentName())){
            this.setParentDepartmentName(StrUtil.DASHED);
        }
    }
} 