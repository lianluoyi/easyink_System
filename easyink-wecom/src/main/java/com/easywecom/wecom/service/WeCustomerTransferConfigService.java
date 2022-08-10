package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.entity.transfer.WeCustomerTransferConfig;

/**
 * 类名: 继承设置表业务层接口
 *
 * @author : silver_chariot
 * @date : 2021-12-01 10:50:32
 */
public interface WeCustomerTransferConfigService extends IService<WeCustomerTransferConfig> {
    /**
     * 修改继承配置
     *
     * @param weCustomerTransferConfig {@link WeCustomerTransferConfig}
     */
    void editConfig(WeCustomerTransferConfig weCustomerTransferConfig);

    /**
     * 初始化继承设置
     *
     * @param corpId 企业ID
     * @return 是否成功
     */
    Boolean initTransferConfig(String corpId);
}
