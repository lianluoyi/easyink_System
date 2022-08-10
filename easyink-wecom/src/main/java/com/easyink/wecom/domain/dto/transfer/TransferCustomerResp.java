package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 分配在职员工API响应实体
 *
 * @author : silver_chariot
 * @date : 2021/11/30 9:20
 */
@Data
public class TransferCustomerResp extends WeResultDTO {
    /**
     * 分配结果
     */
    private TransferResult[] customer;


    @Data
    public static class TransferResult {
        /**
         * 客户的external_userid
         */
        private String external_userid;
        /**
         * 对此客户进行分配的结果, 具体可参考全局错误码, 0表示成功发起接替,待24小时后自动接替,并不代表最终接替成功
         */
        private Integer errcode;
    }

    /**
     * 根据API调用结果 处理分配失败的客户分配记录,并返回转接成功的客户userId列表
     *
     * @param list {@link List<WeCustomerTransferRecord>} 分配记录列表
     * @return 转接成功的客户userId列表
     */
    public List<String> handleFailRecord(List<WeCustomerTransferRecord> list) {
        if (CollectionUtils.isEmpty(list) || ArrayUtils.isEmpty(customer)) {
            return Collections.emptyList();
        }
        // 转接成功的客户列表
        List<String> transferSuccessExternalUserList = new ArrayList<>();
        for (TransferResult result : customer) {
            if (WeConstans.WE_SUCCESS_CODE.equals(result.getErrcode())) {
                // 保存分配成功的客户id
                transferSuccessExternalUserList.add(result.getExternal_userid());
                continue;
            }
            for (WeCustomerTransferRecord record : list) {
                if (result.getExternal_userid().equals(record.getExternalUserid())) {
                    // 分配失败的记录修改状态,并保存失败备注
                    record.setStatus(CustomerTransferStatusEnum.FAIL.getType());
                    record.setRemark(WeExceptionTip.getTipMsg(result.getErrcode(), result.getErrcode().toString()));
                    break;
                }
            }
        }
        return transferSuccessExternalUserList;
    }

}
