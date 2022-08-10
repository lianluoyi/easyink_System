package com.easyink.quartz.task;

import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.transfer.TransferResultResignedReq;
import com.easyink.wecom.domain.dto.transfer.TransferResultResignedResp;
import com.easyink.wecom.domain.entity.transfer.WeResignedCustomerTransferRecord;
import com.easyink.wecom.mapper.WeResignedCustomerTransferRecordMapper;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeResignedCustomerTransferRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类名: 离职继承接替状态更新任务
 *
 * @author : silver_chariot
 * @date : 2021/12/7 21:50
 */
@Component("transferResignedResultTask")
@Slf4j
public class TransferResignedResultTask {

    private final WeResignedCustomerTransferRecordService weResignedCustomerTransferRecordService;
    private final WeResignedCustomerTransferRecordMapper weResignedCustomerTransferRecordMapper;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;

    @Autowired
    public TransferResignedResultTask(@NotNull WeResignedCustomerTransferRecordService weResignedCustomerTransferRecordService, WeResignedCustomerTransferRecordMapper weResignedCustomerTransferRecordMapper, WeFlowerCustomerRelService weFlowerCustomerRelService) {
        this.weResignedCustomerTransferRecordService = weResignedCustomerTransferRecordService;
        this.weResignedCustomerTransferRecordMapper = weResignedCustomerTransferRecordMapper;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
    }

    /**
     * 执行任务
     */
    public void execute() {
        // 查询所有带接替的客户分配任务
        List<WeResignedCustomerTransferRecord> recordList = weResignedCustomerTransferRecordService.getToBeTransferList();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        // 根据corpId分组
        Map<String, List<WeResignedCustomerTransferRecord>> map = recordList.stream().collect(Collectors.groupingBy(WeResignedCustomerTransferRecord::getCorpId));
        // 按照corpId 分组处理
        for (Map.Entry<String, List<WeResignedCustomerTransferRecord>> entry : map.entrySet()) {
            String corpId = entry.getKey();
            List<WeResignedCustomerTransferRecord> subList = entry.getValue();
            this.getResignedTransferResult(corpId, subList);
        }
    }

    /**
     * 获取并更新离职继承分配记录的继承状态
     *
     * @param corpId 企业ID
     * @param list   离职继承客户分配记录集合
     */
    private void getResignedTransferResult(String corpId, List<WeResignedCustomerTransferRecord> list) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(list)) {
            log.info("[更新离职继承接替状态]参数缺失,corpId:{},list :{}", corpId, list);
            return;
        }
        List<WeResignedCustomerTransferRecord> totalList = new ArrayList<>();
        List<WeFlowerCustomerRel> relList = new ArrayList<>();
        for (WeResignedCustomerTransferRecord record : list) {
            // 1. 请求企微API 获取该跟进人和接替人的客户对应跟进记录状态
            TransferResultResignedReq req = new TransferResultResignedReq(record.getHandoverUserid(), record.getTakeoverUserid());
            TransferResultResignedResp resp = (TransferResultResignedResp) req.executeTillNoNextPage(corpId);
            // 2. 根据返回结果构建需要更新接替状态的实体集合
            List<WeResignedCustomerTransferRecord> updateList = resp.getTransferRecordList(record);
            totalList.addAll(updateList);
            // 3. 根据返回结果构建所有需要更新的客户关系集合
            List<WeFlowerCustomerRel> updateRelList = resp.getUpdateRelList(record);
            relList.addAll(updateRelList);
        }
        // 4. 批量更新客户接替记录的状态
        if(CollectionUtils.isNotEmpty( totalList)) {
            BatchInsertUtil.doInsert(totalList,weResignedCustomerTransferRecordMapper::batchUpdateRecordStatus);
        }
        // 5. 批量更新员工客户关系为 删除
        if(CollectionUtils.isNotEmpty(relList)) {
            BatchInsertUtil.doInsert(relList,weFlowerCustomerRelService::batchUpdateStatus);
        }

    }
}
