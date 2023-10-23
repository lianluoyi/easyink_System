package com.easyink.wecom.domain.vo.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 类名: 客户导出结果VO
 *
 * @author : silver_chariot
 * @date : 2023/9/21 15:16
 **/
@Data
@AllArgsConstructor
public class CustomerExportResultVO {
    /**
     * 是否已完成
     */
    private Boolean hasFinished ;
}
