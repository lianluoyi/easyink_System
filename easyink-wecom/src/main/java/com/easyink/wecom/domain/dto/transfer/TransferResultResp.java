package com.easyink.wecom.domain.dto.transfer;

import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.resp.WePageBaseResp;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类名: 查询客户接替状态响应实体
 *
 * @author : silver_chariot
 * @date : 2021/11/30 18:21
 */
@Data
public class TransferResultResp extends WePageBaseResp<TransferResultResp.ResultDetail> {
    /**
     * 接替结果列表
     */
    private List<ResultDetail> customer;


    @Data
    public static class ResultDetail {
        /**
         * 转接客户的外部联系人userid
         */
        private String external_userid;
        /**
         * 接替状态， 1-接替完毕 2-等待接替 3-客户拒绝 4-接替成员客户达到上限 5-无接替记录
         */
        private Integer status;
        /**
         * 接替客户的时间，如果是等待接替状态，则为未来的自动接替时间
         */
        private Long takeover_time;
    }

    @Override
    public List<ResultDetail> getPageList() {
        if (CollectionUtils.isEmpty(customer)) {
            return Collections.emptyList();
        }
        return customer;
    }

    @Override
    public void handleData(String corpId) {

    }

    /**
     * 获取本次所有获取到的接替记录列表
     *
     * @param corpId         企业id
     * @param handoverUserId 原跟进成员id
     * @param takeoverUserId 接替成员id
     */
    public List<WeCustomerTransferRecord> getTransferResultList(String corpId, String handoverUserId, String takeoverUserId) {
        if (this.isFail() || CollectionUtils.isEmpty(customer)) {
            return Collections.emptyList();
        }
        List<WeCustomerTransferRecord> recordList = new ArrayList<>();
        for (ResultDetail result : customer) {
            if (CustomerTransferStatusEnum.WAIT.getType().equals(result.getStatus())) {
                // 还是待接替中状态的记录不需要更新
                continue;
            }
            WeCustomerTransferRecord record = WeCustomerTransferRecord.builder()
                    .corpId(corpId)
                    .takeoverUserid(takeoverUserId)
                    .handoverUserid(handoverUserId)
                    .externalUserid(result.getExternal_userid())
                    .status(result.getStatus())
                    .build();
            record.updateRemark(result.getStatus());
            if (result.getTakeover_time() != null) {
                record.setTakeoverTime(DateUtils.unix2Date(result.getTakeover_time()));
            }
            recordList.add(record);
        }
        return recordList;
    }

    /**
     * 根据返回的接替成功记录 构建需要修改状态为‘已删除’的客户列表集合
     *
     * @param weCustomerTransferRecord 记录实体 {@link WeCustomerTransferRecord}
     * @return 需要修改状态为[已删除]的客户列表集合
     */
    public List<WeFlowerCustomerRel> getUpdateRelList(WeCustomerTransferRecord weCustomerTransferRecord) {
        if (CollectionUtils.isEmpty(getCustomer())) {
            return Collections.emptyList();
        }
        List<WeFlowerCustomerRel> relList = new ArrayList<>();
        for (ResultDetail detail : getCustomer()) {
            if (CustomerTransferStatusEnum.SUCCEED.getType().equals(detail.getStatus())) {
                relList.add(
                        WeFlowerCustomerRel.builder()
                                .id(SnowFlakeUtil.nextId())
                                .corpId(weCustomerTransferRecord.getCorpId())
                                .userId(weCustomerTransferRecord.getHandoverUserid())
                                .externalUserid(detail.getExternal_userid())
                                .status(CustomerStatusEnum.DELETE.getCode().toString())
                                .build()
                );
            }
        }
        return relList;
    }
}
