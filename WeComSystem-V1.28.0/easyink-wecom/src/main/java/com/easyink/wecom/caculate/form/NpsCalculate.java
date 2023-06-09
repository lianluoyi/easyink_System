package com.easyink.wecom.caculate.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * NPS评分计算器
 *
 * @author tigger
 * 2023/1/13 18:09
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NpsCalculate extends AbstractCustomerFeedbackCalculate {

    public NpsCalculate(Integer scoreValue) {
        super(scoreValue);
    }

    @Override
    public Integer getFinalScore() {
        return super.scoreValue;
    }
}
