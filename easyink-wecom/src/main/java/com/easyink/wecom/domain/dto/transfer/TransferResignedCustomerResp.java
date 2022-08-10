package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 分配离职成员客户响应实体
 *
 * @author : silver_chariot
 * @date : 2021/12/6 16:26
 */
@Data
public class TransferResignedCustomerResp extends WeResultDTO {
    /**
     * 分配结果列表
     */
    private List<TransferResult> customer;

    @Data
    public class TransferResult {
        /**
         * 客户的external_userid
         */
        private String external_userid;
        /**
         * 对此客户进行分配的结果, 具体可参考全局错误码, 0表示开始分配流程,待24小时后自动接替,并不代表最终分配成功
         */
        private Integer errcode;
    }

    /**
     * 获取空的响应结果
     *
     * @return
     */
    public static TransferResignedCustomerResp emptyResult() {
        TransferResignedCustomerResp resp = new TransferResignedCustomerResp();
        resp.setCustomer(Collections.emptyList());
        return resp;
    }

    /**
     * 获取分配客户记录列表
     *
     * @param recordId 总记录id
     * @return {@link List<WeResignedCustomerTransferRecord> }
     */
    public List<WeResignedCustomerTransferRecord> getRecordList(Long recordId) {
        if (CollectionUtils.isEmpty(customer)) {
            return Collections.emptyList();
        }
        List<WeResignedCustomerTransferRecord> recordList = new ArrayList<>();
        for (TransferResult result : customer) {
            WeResignedCustomerTransferRecord record = WeResignedCustomerTransferRecord.builder()
                    .recordId(recordId)
                    .externalUserid(result.getExternal_userid())
                    .status(CustomerTransferStatusEnum.WAIT.getType())
                    .remark(StringUtils.EMPTY)
                    .build();
            // 如果存在错误码 则保存错误信息
            if (!WeConstans.WE_SUCCESS_CODE.equals(result.getErrcode())) {
                record.setStatus(CustomerTransferStatusEnum.FAIL.getType());
                record.setRemark(WeExceptionTip.getTipMsg(result.getErrcode(), result.getErrcode().toString()));
            }
            recordList.add(record);
        }
        return recordList;
    }



}
