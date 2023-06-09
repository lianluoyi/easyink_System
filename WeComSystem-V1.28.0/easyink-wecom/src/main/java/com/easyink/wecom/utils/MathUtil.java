package com.easyink.wecom.utils;

import java.math.BigDecimal;

/**
 * 数学计算工具类
 *
 * @author tigger
 * 2023/2/7 9:20
 **/
public class MathUtil {

    /**
     * 默认保留小数点位数
     */
    private static final int DEFAULT_SCALE  = 4;
    /**
     * 转为百分比相乘系数
     */
    private static final String CONVERT_TO_PERCENT_MULTIPLY_NUM = "100";

    /**
     * 计算保留两位小数的百分比 BigDecimal
     * 如 0 55 66.66 100
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return
     */
    public static BigDecimal calculatePercent(BigDecimal numerator, BigDecimal denominator) {
        BigDecimal result;
        if (denominator.equals(BigDecimal.ZERO)) {
            result = BigDecimal.ZERO;
        } else {
            result = numerator
                    .divide(denominator, DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(CONVERT_TO_PERCENT_MULTIPLY_NUM))
                    .stripTrailingZeros();
        }
        return result;
    }
}
