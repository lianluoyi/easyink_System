package com.easywecom.wecom.domain.vo.sop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计算执行率抽象VO
 *
 * @author tigger
 * 2021/12/7 9:19
 **/
@Data
public abstract class AbstractExecuteVO {

    @ApiModelProperty("总任务数")
    private Integer taskCount;
    @ApiModelProperty("执行总数")
    private Integer taskExecuteCount;
    @ApiModelProperty("未执行总数")
    private Integer taskUnExecuteCount;
    @ApiModelProperty("执行率")
    private String executeRate;

    public void handleExecuteRate() {
        String rate;
        if (this.taskCount == 0) {
            rate = new BigDecimal("0").toPlainString().concat("%");
        } else {
            rate = BigDecimal.valueOf(this.taskExecuteCount)
                    .divide(BigDecimal.valueOf(this.taskCount), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString().concat("%");
        }
        this.executeRate = rate;
    }

}
