package com.easyink.wecom.domain.vo.form;

import com.easyink.wecom.utils.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数据总览VO
 *
 * @author tigger
 * 2023/1/13 13:37
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormTotalView {


    /**
     * 点击总人数
     */
    private Integer clickTotalCount;
    /**
     * 提交总人数
     */
    private Integer submitTotalCount;
    /**
     * 填写率, 运算好未+% 如: 55.55
     */
    private String submitPercent;
    /**
     * 今日点击人数
     */
    private Integer todayClickCount;
    /**
     * 今日提交人数
     */
    private Integer todaySubmitCount;
    /**
     * 今日提交率, 运算好未+% 如: 55.55
     */
    private String todaySubmitPercent;

    /**
     * 计算百分比
     *
     * @return FormTotalView
     */
    public FormTotalView fillPercent() {
        this.submitPercent = MathUtil.calculatePercent(new BigDecimal(submitTotalCount), new BigDecimal(this.clickTotalCount))
                .stripTrailingZeros().toPlainString();
        this.todaySubmitPercent = MathUtil.calculatePercent(new BigDecimal(this.todaySubmitCount), new BigDecimal(this.todayClickCount))
                .stripTrailingZeros().toPlainString();
        return this;
    }


}
