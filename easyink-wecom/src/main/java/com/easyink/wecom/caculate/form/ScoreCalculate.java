package com.easyink.wecom.caculate.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 普通评分计算器
 *
 * @author tigger
 * 2023/1/13 18:07
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ScoreCalculate extends AbstractCustomerFeedbackCalculate {
    public ScoreCalculate(Integer scoreValue) {
        super(scoreValue);
    }

    @Override
    public Integer getFinalScore() {
        return super.scoreValue * 2;
    }
}
