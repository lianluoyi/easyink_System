package com.easyink.wecom.domain.vo.emple;

import com.easyink.wecom.domain.WeEmpleCodeSituation;
import lombok.Data;

/**
 * 获客链接-主页获客情况VO
 *
 * @author lichaoyu
 * @date 2023/8/24 13:45
 */
@Data
public class WeEmpleCodeSituationVO extends WeEmpleCodeSituation {

    /**
     * 处理获客情况数据
     *
     * @param todayNewCustomerCnt 今日新增客户数
     */
    public void handleSituationValue(Integer todayNewCustomerCnt) {
        super.setNewCustomerCnt(super.getNewCustomerCnt() + todayNewCustomerCnt);
        super.setTotal(super.getTotal() + todayNewCustomerCnt);
        super.setBalance(super.getBalance() - todayNewCustomerCnt);
    }
}
