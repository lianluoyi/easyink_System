package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;
import com.easyink.wecom.mapper.WeResignedCustomerTransferRecordMapper;
import com.easyink.wecom.service.WeResignedCustomerTransferRecordService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: 离职客户继承记录表业务层接口实现类
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:33
 */
@Service
public class WeResignedCustomerTransferRecordServiceImpl extends ServiceImpl<WeResignedCustomerTransferRecordMapper, WeResignedCustomerTransferRecord> implements WeResignedCustomerTransferRecordService {

    private final WeResignedCustomerTransferRecordMapper weResignedCustomerTransferRecordMapper;

    public WeResignedCustomerTransferRecordServiceImpl(@NotNull WeResignedCustomerTransferRecordMapper weResignedCustomerTransferRecordMapper) {
        this.weResignedCustomerTransferRecordMapper = weResignedCustomerTransferRecordMapper;
    }

    @Override
    public List<WeResignedCustomerTransferRecord> getToBeTransferList() {
        return weResignedCustomerTransferRecordMapper.getToBeTransferList();
    }


}
