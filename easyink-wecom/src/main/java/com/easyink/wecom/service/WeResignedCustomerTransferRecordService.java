package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;

import java.util.List;

/**
 * 类名: 离职客户继承记录表业务层接口
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:32
 */
public interface WeResignedCustomerTransferRecordService extends IService<WeResignedCustomerTransferRecord> {
    /**
     * 获取所有待接替的记录列表
     *
     * @return {@link List<WeResignedCustomerTransferRecord> }
     */
    List<WeResignedCustomerTransferRecord> getToBeTransferList();

}
