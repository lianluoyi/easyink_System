package com.easyink.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.transfer.TransferResultReq;
import com.easyink.wecom.domain.dto.transfer.TransferResultResp;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeCustomerTransferRecordService;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * 类名: 定期查询客户分配情况任务
 *
 * @author : silver_chariot
 * @date : 2021/11/30 17:37
 */
@Component("transferResultTask")
@Slf4j
public class TransferResultTask {

    private final WeCustomerTransferRecordService weCustomerTransferRecordService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;

    @Autowired
    public TransferResultTask(@NotNull WeCustomerTransferRecordService weCustomerTransferRecordService, @NotNull WeCorpAccountService weCorpAccountService, @NotNull WeFlowerCustomerRelService weFlowerCustomerRelService) {
        this.weCustomerTransferRecordService = weCustomerTransferRecordService;
        this.weCorpAccountService = weCorpAccountService;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
    }

    /**
     * 执行任务
     */
    public void execute() {
        // 获取所有接替中的分配记录
        List<WeCustomerTransferRecord> list = weCustomerTransferRecordService.list(new LambdaQueryWrapper<WeCustomerTransferRecord>()
                .eq(WeCustomerTransferRecord::getStatus, CustomerTransferStatusEnum.WAIT.getType())
        );
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 根据handover_userId和takeover_userId去重(由于企微接口是根据跟进人和接替人去获取接替记录,但是记录表会有多条相同跟进人和接替人但是客户不同的数据,因此这里去重防止相同参数多次调用API)
        List<WeCustomerTransferRecord> ignoreDuplicateList = list.stream().collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(
                                Comparator.comparing(o -> o.getHandoverUserid() + ";" + o.getTakeoverUserid()))), ArrayList::new)
        );
        // 按照corpId 分组后 按公司处理
        Map<String, List<WeCustomerTransferRecord>> map = ignoreDuplicateList.stream().collect(Collectors.groupingBy(WeCustomerTransferRecord::getCorpId));
        for (Map.Entry<String, List<WeCustomerTransferRecord>> entry : map.entrySet()) {
            String corpId = entry.getKey();
            // 去重后的记录列表
            List<WeCustomerTransferRecord> subList = entry.getValue();
            // 该公司所有待接替记录列表
            List<WeCustomerTransferRecord> allList = list.stream().filter(a -> a.getCorpId().equals(corpId)).collect(Collectors.toList());
            this.getTransferResult(corpId, subList, allList);
        }

    }

    /**
     * 查询客户接替状态 并更新
     *
     * @param corpId  企业id
     * @param subList 根据接替人和跟进人 去重后的待接替记录列表
     * @param allList 该企业所有的待接替记录列表
     */
    private void getTransferResult(String corpId, List<WeCustomerTransferRecord> subList, List<WeCustomerTransferRecord> allList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(subList) || CollectionUtils.isEmpty(allList)) {
            log.info("[TransferResultTask]参数缺失,corpId:{},list:{}", corpId, subList);
            return;
        }
        // 获取企业详情
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (null == corpAccount || StringUtils.isAnyBlank(corpAccount.getAgentId(), corpAccount.getCustomSecret())) {
            log.error("[TransferResultTask]查询客户接替状态异常:获取企业配置异常,corpId:{}", corpId);
            return;
        }
        List<WeCustomerTransferRecord> totalList = new ArrayList<>();
        List<WeFlowerCustomerRel> relList = new ArrayList<>();
        for (WeCustomerTransferRecord record : subList) {
            // 构建请求实体,请求API
            TransferResultReq req = TransferResultReq.builder()
                    .handover_userid(record.getHandoverUserid())
                    .takeover_userid(record.getTakeoverUserid())
                    .build();
            TransferResultResp resp = (TransferResultResp) req.executeTillNoNextPage(corpId);
            // 根据返回结果构建待修改记录集合
            List<WeCustomerTransferRecord> recordList = resp.getTransferResultList(corpId, record.getHandoverUserid(), record.getTakeoverUserid());
            totalList.addAll(recordList);
            // 根据返回结果构建需要修改状态的客户集合
            List<WeFlowerCustomerRel> updateList = resp.getUpdateRelList(record);
            relList.addAll(updateList);
        }
        // 根据本地记录,匹配远端记录,并设置ID
        matchRecord(totalList, allList);
        // 根据记录id,批量更新接替记录状态
        if (CollectionUtils.isNotEmpty(totalList)) {
            weCustomerTransferRecordService.updateBatchById(totalList);
            log.info("[TransferResultTask]更新【{}】个记录的接替状态,corpId:{}", totalList.size(), corpId);
        }
        // 更新接替成功的 客户状态为[已删除]
        if (CollectionUtils.isNotEmpty(relList)) {
            BatchInsertUtil.doInsert(relList, weFlowerCustomerRelService::batchUpdateStatus);
        }
    }

    /**
     * 根据跟进人userId,接替人userId,客户userId,为API获取的记录列表 匹配本地数据的记录id
     *
     * @param remoteList 从企微API 获取到所有接替记录列表
     * @param localList  该企业所有的待接替记录列表
     */
    private void matchRecord(List<WeCustomerTransferRecord> remoteList, List<WeCustomerTransferRecord> localList) {
        for (WeCustomerTransferRecord remoteRecord : remoteList) {
            for (WeCustomerTransferRecord localRecord : localList) {
                if (StringUtils.equals(remoteRecord.getExternalUserid(), localRecord.getExternalUserid())
                        && StringUtils.equals(remoteRecord.getHandoverUserid(), localRecord.getHandoverUserid())
                        && StringUtils.equals(remoteRecord.getTakeoverUserid(), localRecord.getTakeoverUserid())) {
                    remoteRecord.setId(localRecord.getId());
                    break;
                }
            }
        }
    }
}
