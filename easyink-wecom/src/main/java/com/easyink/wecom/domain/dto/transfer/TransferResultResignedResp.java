package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import com.easyink.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 离职接替状态获取响应实体
 *
 * @author : silver_chariot
 * @date : 2021/12/8 9:23
 */
@Data
public class TransferResultResignedResp extends TransferResultResp {
    /**
     * 根据返回结果 生成变更记录表
     *
     * @param record {@link WeResignedCustomerTransferRecord} 分配记录
     * @return {@link  List<WeResignedCustomerTransferRecord> } 变更分配记录列表
     */
    public List<WeResignedCustomerTransferRecord> getTransferRecordList(WeResignedCustomerTransferRecord record) {
        if (CollectionUtils.isEmpty(getCustomer())) {
            return Collections.emptyList();
        }
        List<WeResignedCustomerTransferRecord> recordList = new ArrayList<>();
        for (ResultDetail detail : getCustomer()) {
            if (CustomerTransferStatusEnum.WAIT.getType().equals(detail.getStatus())) {
                // 不更新待接替状态
                continue;
            }
            WeResignedCustomerTransferRecord entity = WeResignedCustomerTransferRecord.builder()
                    .recordId(record.getRecordId())
                    .externalUserid(detail.getExternal_userid())
                    .status(detail.getStatus())
                    .remark(StringUtils.EMPTY)
                    .takeoverTime(detail.getTakeover_time() == null ? null : DateUtils.unix2Date(detail.getTakeover_time()))
                    .build();
            //失败记录多保存失败备注
            if (!CustomerTransferStatusEnum.SUCCEED.getType().equals(detail.getStatus())) {
                entity.setRemark(CustomerTransferStatusEnum.getDescByStatus(detail.getStatus()));
            }
            recordList.add(entity);
        }
        return recordList;
    }

    /**
     * 获取所有需要更新的客户关系
     *
     * @param weResignedCustomerTransferRecord {@link WeResignedCustomerTransferRecord}
     * @return {@link List<WeFlowerCustomerRel>}
     */
    public List<WeFlowerCustomerRel> getUpdateRelList(WeResignedCustomerTransferRecord weResignedCustomerTransferRecord) {
        if (weResignedCustomerTransferRecord == null) {
            return Collections.emptyList();
        }
        WeCustomerTransferRecord weCustomerTransferRecord = new WeCustomerTransferRecord();
        BeanUtils.copyPropertiesASM(weResignedCustomerTransferRecord, weCustomerTransferRecord);
        return super.getUpdateRelList(weCustomerTransferRecord);
    }
}
