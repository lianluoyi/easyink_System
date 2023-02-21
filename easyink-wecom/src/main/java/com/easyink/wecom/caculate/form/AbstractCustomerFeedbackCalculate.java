package com.easyink.wecom.caculate.form;

import com.easyink.wecom.domain.enums.form.FormComponentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户评分
 *
 * @author tigger
 * 2023/1/13 16:47
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractCustomerFeedbackCalculate {

    /**
     * 组件评分值
     */
    protected Integer scoreValue;

    /**
     * 计算评分
     * @param componentType 组件类型
     * @param scoreValue 组件评分值
     * @return 计算后的评分
     */
    public static Integer calculate(FormComponentType componentType, Integer scoreValue) {
        AbstractCustomerFeedbackCalculate calculate;
        if (componentType == FormComponentType.NPS_COMPONENT) {
            calculate = new NpsCalculate(scoreValue);
        } else {
            calculate = new ScoreCalculate(scoreValue);
        }
        return calculate.getFinalScore();
    }


    /**
     * 获取最终评分
     *
     * @return 评分值
     */
    protected abstract Integer getFinalScore();

}
