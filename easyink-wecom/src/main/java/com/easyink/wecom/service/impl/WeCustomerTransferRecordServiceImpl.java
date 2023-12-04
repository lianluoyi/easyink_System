package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.CustomerTransferStatusEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.StaffActivateEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.client.WeExternalContactClient;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.transfer.TransferCustomerDTO;
import com.easyink.wecom.domain.dto.transfer.TransferCustomerReq;
import com.easyink.wecom.domain.dto.transfer.TransferCustomerResp;
import com.easyink.wecom.domain.dto.transfer.TransferRecordPageDTO;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferRecord;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.transfer.WeCustomerTransferRecordVO;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeCustomerTransferRecordMapper;
import com.easyink.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 类名: 在职继承分配记录表业务层接口实现类
 *
 * @author : silver_chariot
 * @date : 2021/11/29 17:54
 */
@Service
@Slf4j
public class WeCustomerTransferRecordServiceImpl extends ServiceImpl<WeCustomerTransferRecordMapper, WeCustomerTransferRecord> implements WeCustomerTransferRecordService {

    private final WeCustomerTransferRecordMapper weCustomerTransferRecordMapper;
    private final WeUserService weUserService;
    private final WeExternalContactClient externalContactClient;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WeCustomerMapper weCustomerMapper;
    private final WeDepartmentService weDepartmentService;
    private final WeCustomerService weCustomerService;
    private final WeTagService weTagService;
    private final WeFlowerCustomerTagRelService weFlowerCustomerTagRelService;
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public WeCustomerTransferRecordServiceImpl(@NotNull WeCustomerTransferRecordMapper weCustomerTransferRecordMapper, @NotNull WeUserService weUserService,
                                               @NotNull WeExternalContactClient externalContactClient, @NotNull WeFlowerCustomerRelService weFlowerCustomerRelService,
                                               @NotNull WeCustomerMapper weCustomerMapper, WeDepartmentService weDepartmentService, WeCustomerService weCustomerService, WeTagService weTagService, WeFlowerCustomerTagRelService weFlowerCustomerTagRelService) {
        this.weCustomerTransferRecordMapper = weCustomerTransferRecordMapper;
        this.weUserService = weUserService;
        this.externalContactClient = externalContactClient;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
        this.weCustomerMapper = weCustomerMapper;
        this.weDepartmentService = weDepartmentService;
        this.weCustomerService = weCustomerService;
        this.weTagService = weTagService;
        this.weFlowerCustomerTagRelService = weFlowerCustomerTagRelService;
    }

    @Override
    public void transfer(String corpId, TransferCustomerDTO.HandoverCustomer[] transferList, String takeoverUserId, String transferSuccessMsg) {
        if (ArrayUtils.isEmpty(transferList) || StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 1. 校验接替成员是否存在且激活
        WeUser takeoverUser = weUserService.getUserDetail(corpId, takeoverUserId);
        if (takeoverUser == null || !StaffActivateEnum.ACTIVE.getCode().equals(takeoverUser.getIsActivate())) {
            throw new CustomException(ResultTip.TIP_USER_NOT_ACTIVE);
        }
        // 2. 校验分配的跟进人和接替人是否一样
        this.validateTakeoverUser(transferList,takeoverUserId);
        // 3. 按照原跟进人分组(由于企微只支持一次分配单个跟进人和单个接替人的多个客户,所以后端这里分组后再依次调用API并处理)
        Map<String, List<TransferCustomerDTO.HandoverCustomer>> map = Arrays.stream(transferList)
                .collect(Collectors.groupingBy(TransferCustomerDTO.HandoverCustomer::getHandoverUserid));
        // 4. 按照原跟进人 逐批处理需要分配的在职员工客户
        for (Map.Entry<String, List<TransferCustomerDTO.HandoverCustomer>> entry : map.entrySet()) {
            String handoverUserId = entry.getKey();
            WeUser handoverUser = weUserService.getUserDetail(corpId, handoverUserId);
            List<String> externalUserIds = entry.getValue().stream().map(TransferCustomerDTO.HandoverCustomer::getExternalUserid).collect(Collectors.toList());
            this.doTransfer(corpId, handoverUser, takeoverUser, externalUserIds, transferSuccessMsg);
        }
    }

    /**
     * 校验接替人
     *
     * @param transferList 跟进人列表
     * @param takeoverUserId 接替人id
     */
    private void validateTakeoverUser(TransferCustomerDTO.HandoverCustomer[] transferList, String takeoverUserId) {
        if(ArrayUtils.isEmpty(transferList) || StringUtils.isBlank(takeoverUserId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<String> list = Arrays.asList(transferList).stream().map(TransferCustomerDTO.HandoverCustomer::getHandoverUserid).collect(Collectors.toList());
        if(list.contains(takeoverUserId)) {
            throw new CustomException(ResultTip.TIP_CAN_TRANSFER_SELF_CUSTOMER);
        }
    }

    @Override
    public void doTransfer(String corpId, WeUser handoverUser, WeUser takeoverUser, List<String> externalUserIds, String transferSuccessMsg) {
        if (StringUtils.isBlank(corpId) || handoverUser == null || takeoverUser == null || CollectionUtils.isEmpty(externalUserIds)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        // 1. 构建请求,调用企微API
        TransferCustomerReq req = TransferCustomerReq.builder()
                .handover_userid(handoverUser.getUserId())
                .takeover_userid(takeoverUser.getUserId())
                .external_userid(externalUserIds.toArray(new String[]{}))
                .transfer_success_msg(transferSuccessMsg)
                .build();
        TransferCustomerResp resp = externalContactClient.transferCustomer(req, corpId);
        // 2. 记录转接记录
        List<WeCustomerTransferRecord> recordList = this.buildRecord(corpId, handoverUser, takeoverUser, externalUserIds);
        // 3. 根据API调用返回结果 修改接替失败的记录并获取请求接替成功的客户id列表
        List<String> transferSuccessExternalUserList = resp.handleFailRecord(recordList);
        // 4. 插入分配在职员工客户记录, 更新客户的转接状态为：转接中
        if (CollectionUtils.isNotEmpty(recordList)) {
            this.saveBatch(recordList);
        }
        if (CollectionUtils.isNotEmpty(transferSuccessExternalUserList)) {
            // 更新客户状态
            WeFlowerCustomerRel entity = new WeFlowerCustomerRel();
            entity.setStatus(CustomerStatusEnum.TRANSFERRING.getCode().toString());
            weFlowerCustomerRelService.update(entity, new LambdaUpdateWrapper<WeFlowerCustomerRel>()
                    .eq(WeFlowerCustomerRel::getCorpId, corpId)
                    .eq(WeFlowerCustomerRel::getUserId, handoverUser.getUserId())
                    .in(WeFlowerCustomerRel::getExternalUserid, externalUserIds)
            );
        }
    }

    @Override
    public void batchUpdate(List<WeCustomerTransferRecord> totalList) {
        weCustomerTransferRecordMapper.batchUpdate(totalList);
    }

    @Override
    @DataScope
    public List<WeCustomerVO> transferCustomerList(WeCustomer weCustomer) {
        if (StringUtils.isBlank(weCustomer.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        String corpId = weCustomer.getCorpId();
        //  标签筛选满足条件 external_userid和 user_id
        if (!hasFilterCustomer(weCustomer)) {
            return Collections.emptyList();
        }
        // 根据查询条件，构建过滤客户id列表
        if (!weCustomerService.buildFilterExternalUseridList(corpId, weCustomer)) {
            return Collections.emptyList();
        }
        // 获取查询条件下的userid列表
        List<String> searchUserIdList = weDepartmentService.getDataScopeUserIdList(weCustomer.convertDepartmentList(), weCustomer.convertUserIdList(), weCustomer.getCorpId());
        // 开启分页
        PageInfoUtil.startPage();
        // 获取客户列表
        List<WeCustomerVO> list = weCustomerMapper.selectWeCustomerV4(weCustomer, searchUserIdList);
        // 根据返回的结果获取需要的标签详情
        CompletableFuture<Void> setTagRelFuture = CompletableFuture.runAsync(() -> {
            try {
                weFlowerCustomerTagRelService.setTagForCustomers(weCustomer.getCorpId(), list);
            } catch (Exception e) {
                log.info("[在职继承客户列表] 获取标签详情异步任务出现异常，异常原因：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
            }
        }, threadPoolTaskExecutor);
        // 设置客户分配记录状态
        CompletableFuture<Void> setRecordFuture = CompletableFuture.runAsync(() -> {
            try {
                setTransferRecordStatus(weCustomer.getCorpId(), list);
            } catch (Exception e) {
                log.info("[在职继承客户列表] 设置客户分配记录异步任务出现异常，异常原因：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
            }
        }, threadPoolTaskExecutor);
        // 补充客户信息
        CompletableFuture<Void> setCustomerInfoFuture = CompletableFuture.runAsync(() -> {
            try {
                weUserService.setUserInfoByList(list, corpId);
            } catch (Exception e) {
                log.info("[在职继承客户列表] 补充客户信息异步任务出现异常，异常原因：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
            }
        }, threadPoolTaskExecutor);
        // 补充员工信息
        CompletableFuture<Void> setUserInfoFuture = CompletableFuture.runAsync(() -> {
            try {
                weCustomerService.setCustomerInfoByList(list, corpId);
            } catch (Exception e) {
                log.info("[在职继承客户列表] 补充员工信息异步任务出现异常，异常原因：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
            }
        }, threadPoolTaskExecutor);
        CompletableFuture.allOf(setTagRelFuture, setRecordFuture, setCustomerInfoFuture, setUserInfoFuture).join();
        return list;
    }

    /**
     * 设置客户分配记录状态
     *
     * @param corpId           企业ID
     * @param weCustomerVOList 客户信息列表
     */
    private void setTransferRecordStatus(String corpId, List<WeCustomerVO> weCustomerVOList) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(weCustomerVOList)) {
            return;
        }
        // 获取分配记录
        List<WeCustomerTransferRecord> recordList = this.list(new LambdaQueryWrapper<WeCustomerTransferRecord>()
                .eq(WeCustomerTransferRecord::getCorpId, corpId)
                .in(WeCustomerTransferRecord::getStatus, CustomerTransferStatusEnum.getTransferAvailTypes())
        );
        // 根据分配记录,构建 员工客户-> 分配记录状态的映射
        Map<WeFlowerCustomerRel, Integer> map = recordList.stream()
                .collect(Collectors.toMap(WeFlowerCustomerRel::new, WeCustomerTransferRecord::getStatus, (oldValue, newValue) -> newValue));
        for (WeCustomerVO customer : weCustomerVOList) {
            WeFlowerCustomerRel rel = new WeFlowerCustomerRel(customer);
            // 判断该客户是否有分配记录,如果有则设置状态
            if (map.containsKey(rel)) {
                customer.setTransferStatus(map.get(rel));
            }
        }
    }

    /**
     * 是否存在过滤客户条件
     *
     * @param weCustomer {@link WeCustomer}
     * @return true 存在， false 不存在
     */
    private boolean hasFilterCustomer(WeCustomer weCustomer) {
        if(StringUtils.isNotBlank(weCustomer.getTagIds())) {
            List<Long> tagFilterCustomers = weTagService.getCustomerByTags(weCustomer.getCorpId(), weCustomer.getTagIds());
            if(CollectionUtils.isEmpty(tagFilterCustomers)) {
                return false;
            }
            weCustomer.setRelIds(tagFilterCustomers);
        }
        return true;
    }

    @Override
    @DataScope
    public List<WeCustomerTransferRecordVO> getList(TransferRecordPageDTO dto) {
        if (StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        return weCustomerTransferRecordMapper.getList(dto);
    }

    @Override
    public void handleTransferFail(String corpId, String userId, String externalUserId, String failReason) {
        if (StringUtils.isAnyBlank(corpId, userId, externalUserId, failReason)) {
            log.error("[在职继承失败处理]参数缺失,corpId:{},userId:{},externalUserId:{},failReason:{}", corpId, userId, externalUserId, failReason);
            return;
        }
        // 修改继承记录为失败,并记录失败记录
        WeCustomerTransferRecord weCustomerTransferRecord = new WeCustomerTransferRecord();
        CustomerTransferStatusEnum transferStatus = CustomerTransferStatusEnum.getByFailReason(failReason);
        if (transferStatus == null) {
            log.info("未知失败类型,corpId:{},userId:{},externalUserId:{},failReason:{}", corpId, userId, externalUserId, failReason);
            return;
        }
        weCustomerTransferRecord.setStatus(transferStatus.getType());
        weCustomerTransferRecord.setRemark(transferStatus.getDescribeType());
        this.update(weCustomerTransferRecord, new LambdaUpdateWrapper<WeCustomerTransferRecord>()
                .eq(WeCustomerTransferRecord::getCorpId, corpId)
                .eq(WeCustomerTransferRecord::getHandoverUserid, userId)
                .eq(WeCustomerTransferRecord::getExternalUserid, externalUserId)
        );
        // 把客户状态从转接中改为正常
        WeFlowerCustomerRel rel = new WeFlowerCustomerRel();
        rel.setStatus(String.valueOf(CustomerStatusEnum.NORMAL.getCode()));
        weFlowerCustomerRelService.update(rel, new LambdaUpdateWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                .eq(WeFlowerCustomerRel::getUserId, userId)
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
        );
    }

    @Override
    public void handleTransferSuccess(String corpId, String userId, String externalUserId) {
        if (StringUtils.isAnyBlank(corpId, userId, externalUserId)) {
            log.error("[在职继承成功处理]参数缺失,corpId:{},userId:{},externalUserId:{}", corpId, userId, externalUserId);
            return;
        }
        // 1. 获取继承记录
        WeCustomerTransferRecord record = this.getOne(new LambdaQueryWrapper<WeCustomerTransferRecord>()
                .eq(WeCustomerTransferRecord::getCorpId, corpId)
                .eq(WeCustomerTransferRecord::getExternalUserid, externalUserId)
                .eq(WeCustomerTransferRecord::getHandoverUserid, userId)
                .last(GenConstants.LIMIT_1)
        );
        if (record == null || StringUtils.isBlank(record.getTakeoverUserid())) {
            log.info("[在职继承成功处理]系统不存在该继承记录或接替人为空,corpId:{},userId:{},externalUserId:{},record:{}", corpId, userId, externalUserId, record);
            return;
        }
        // 2. 转接客户( 客户资料,扩展字段,标签关系)
        weFlowerCustomerRelService.transferCustomerRel(corpId, record.getHandoverUserid(), record.getTakeoverUserid(), externalUserId);
        // 3. 修改接替记录状态为成功,接替备注修改为空
        record.setStatus(CustomerTransferStatusEnum.SUCCEED.getType());
        record.setRemark(StringUtils.EMPTY);
        this.updateById(record);
        log.info("[在职继承]客户接替完成,record:{}", record);
    }


    private List<WeCustomerTransferRecord> buildRecord(String corpId, WeUser handoverUser, WeUser takeoverUser, List<String> externalUserIds) {
        if (StringUtils.isBlank(corpId) || handoverUser == null || takeoverUser == null || CollectionUtils.isEmpty(externalUserIds)) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<WeCustomerTransferRecord> recordList = new ArrayList<>();
        Date transferTime = new Date();
        for (String externalUserId : externalUserIds) {
            recordList.add(
                    WeCustomerTransferRecord.builder()
                            .corpId(corpId)
                            .handoverUserid(handoverUser.getUserId())
                            .externalUserid(externalUserId)
                            .takeoverUserid(takeoverUser.getUserId())
                            .hanoverUsername(handoverUser.getName())
                            .takeoverUsername(takeoverUser.getName())
                            .handoverDepartmentName(handoverUser.getDepartmentName())
                            .takeoverDepartmentName(takeoverUser.getDepartmentName())
                            .transferTime(transferTime)
                            .status(CustomerTransferStatusEnum.WAIT.getType())
                            .remark(WeConstans.DEFAULT_TRANSFER_NOTICE)
                            .build()
            );
        }
        return recordList;
    }

}
