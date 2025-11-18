package com.easyink.wecom.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeAnalyse;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.handle.job.EmpleStatisticHandle;
import com.easyink.wecom.mapper.WeEmpleCodeAnalyseMapper;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.service.CustomerCompensationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 客户补偿服务实现类
 * 用于处理5月1日至5月5日期间的新增和流失客户补偿逻辑
 *
 * @author 系统
 * @date 2024-05-01
 */
@Slf4j
@Service
public class CustomerCompensationServiceImpl implements CustomerCompensationService {

    @Autowired
    private WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;
    @Autowired
    private WeEmpleCodeMapper weEmpleCodeMapper;
    @Autowired
    private WeEmpleCodeAnalyseMapper weEmpleCodeAnalyseMapper;
    @Autowired
    private WeEmpleCodeStatisticMapper weEmpleCodeStatisticMapper;

    /**
     * 补偿5月1日至5月5日期间的新增和流失客户
     *
     * @param corpId 企业ID
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> compensateCustomers(String corpId, Date beginTime, Date endTime) {
        log.info("开始补偿企业[{}]在{}至{}期间的客户数据", corpId, beginTime, beginTime);

        Map<String, Object> result = new HashMap<>();
        result.put("corpId", corpId);
        result.put("startDate", beginTime);
        result.put("endDate", endTime);

        try {
            // 1. 查询指定时间段内的客户关系数据, 并且state, 来源state不为空不为null
            List<WeFlowerCustomerRel> customerRels = queryCustomerRelByDateRange(corpId, beginTime, endTime);
            log.info("查询到{}条客户关系记录", customerRels.size());
            result.put("totalCustomerRels", customerRels.size());

            if (CollectionUtils.isEmpty(customerRels)) {
                result.put("status", "success");
                result.put("message", "未发现需要补偿的客户关系数据");
                return result;
            }

            // 2. 处理客户关系数据，判断state字段是否为有效活码ID
            List<WeEmpleCodeAnalyse> newAnalyseRecords = new ArrayList<>();
            List<WeEmpleCodeAnalyse> lossAnalyseRecords = new ArrayList<>();

            int processedCount = processCustomerRelations(customerRels, newAnalyseRecords, lossAnalyseRecords);

            result.put("processedCount", processedCount);
            result.put("newCustomerCount", newAnalyseRecords.size());
            result.put("lossCustomerCount", lossAnalyseRecords.size());

            // 3. 批量插入分析记录
            if (CollectionUtils.isNotEmpty(newAnalyseRecords)) {
                insertAnalyseRecords(newAnalyseRecords);
                log.info("插入{}条新增客户分析记录", newAnalyseRecords.size());
            }

            if (CollectionUtils.isNotEmpty(lossAnalyseRecords)) {
                insertAnalyseRecords(lossAnalyseRecords);
                log.info("插入{}条流失客户分析记录", lossAnalyseRecords.size());
            }

            // 4. 删除统计表中指定时间段的数据
//            int deletedStatisticCount = deleteStatisticData(corpId, beginTime, endTime);
//            result.put("deletedStatisticCount", deletedStatisticCount);
//            log.info("删除{}条统计记录", deletedStatisticCount);

            result.put("status", "success");
            result.put("message", "客户补偿处理完成");

            log.info("企业[{}]客户补偿处理完成，新增{}条，流失{}条", corpId, newAnalyseRecords.size(), lossAnalyseRecords.size());

        } catch (Exception e) {
            log.error("客户补偿处理失败：", e);
            result.put("status", "error");
            result.put("message", "处理失败：" + e.getMessage());
            throw e;
        }

        return result;
    }

    /**
     * 查询指定时间段内的客户关系数据
     */
    private List<WeFlowerCustomerRel> queryCustomerRelByDateRange(String corpId,  Date beginTime, Date endTime) {
        return  weFlowerCustomerRelMapper.selectCompensationData(corpId, beginTime, endTime);
    }

    /**
     * 处理客户关系数据，生成分析记录
     */
    private int processCustomerRelations(List<WeFlowerCustomerRel> customerRels,
                                         List<WeEmpleCodeAnalyse> newAnalyseRecords,
                                         List<WeEmpleCodeAnalyse> lossAnalyseRecords) {
        int processedCount = 0;

        for (WeFlowerCustomerRel customerRel : customerRels) {
            try {
                // 判断是否有对应的活码
                Long empleCodeId = validateAndGetEmpleCodeId(customerRel.getState(), customerRel.getCorpId());
                if (empleCodeId == null) {
                    continue;
                }

                // 过滤不处理的状态
                if (isUnValidState(customerRel.getStatus())) {
                    continue;
                }
                // 根据关系状态生成对应的分析记录
                if (isAddedRef(customerRel)) {
                    // 生成新增客户记录
                    WeEmpleCodeAnalyse newRecord = createAnalyseRecord(
                            customerRel, empleCodeId);
                    newAnalyseRecords.add(newRecord);
                }
                if (isLostedRef(customerRel)) {
                    // 生成流失客户记录
                    WeEmpleCodeAnalyse lossRecord = createAnalyseLostRecord(
                            customerRel, empleCodeId);
                    lossAnalyseRecords.add(lossRecord);
                }
                processedCount++;
            } catch (Exception e) {
                log.warn("处理客户关系记录失败，客户ID：{}，员工ID：{}，错误：{}",
                        customerRel.getExternalUserid(), customerRel.getUserId(), e.getMessage());
            }
        }

        return processedCount;
    }

    /**
     * 不需要处理的status
     * @param status
     * @return
     */
    private boolean isUnValidState(String status) {
        return status == null || status.isEmpty()
                || status.equals(CustomerStatusEnum.DELETE.getCode().toString())
                || status.equals(CustomerStatusEnum.TO_BE_TRANSFERRED.getCode().toString())
                || status.equals(CustomerStatusEnum.TRANSFERRING.getCode().toString())
                ;
    }

    /**
     * 验证state是否为有效的活码ID
     */
    private Long validateAndGetEmpleCodeId(String state, String corpId) {
        try {
            Long empleCodeId = Long.parseLong(state);

            // 查询活码是否存在
            WeEmpleCode empleCode = weEmpleCodeMapper.selectIgnoreDelFlag(empleCodeId, corpId);

            return empleCode != null ? empleCodeId : null;

        } catch (Exception e) {
            log.error("");
            return null;
        }
    }

    /**
     * 判断客户关系是否有效（用于区分新增还是流失）
     */
    private boolean isAddedRef(WeFlowerCustomerRel customerRel) {
        // 根据业务规则判断，这里假设status=0表示正常关系，其他表示已删除/流失
        return CustomerStatusEnum.NORMAL.getCode().toString().equals(customerRel.getStatus());
    }
    private boolean isLostedRef(WeFlowerCustomerRel customerRel) {
        // 根据业务规则判断，这里假设status=0表示正常关系，其他表示已删除/流失
        return CustomerStatusEnum.DRAIN.getCode().toString().equals(customerRel.getStatus());
    }

    /**
     * 创建分析记录
     */
    private WeEmpleCodeAnalyse createAnalyseRecord(WeFlowerCustomerRel customerRel, Long empleCodeId) {
        WeEmpleCodeAnalyse analyse = new WeEmpleCodeAnalyse();
        analyse.setCorpId(customerRel.getCorpId());
        analyse.setEmpleCodeId(empleCodeId);
        analyse.setUserId(customerRel.getUserId());
        analyse.setExternalUserId(customerRel.getExternalUserid());
        analyse.setTime(customerRel.getCreateTime());
        analyse.setType(true); // 转换Integer到Boolean，1为true（新增），0为false（流失）
        analyse.setAddTime(customerRel.getCreateTime());
        return analyse;
    }
    private WeEmpleCodeAnalyse createAnalyseLostRecord(WeFlowerCustomerRel customerRel, Long empleCodeId) {
        WeEmpleCodeAnalyse analyse = new WeEmpleCodeAnalyse();
        analyse.setCorpId(customerRel.getCorpId());
        analyse.setEmpleCodeId(empleCodeId);
        analyse.setUserId(customerRel.getUserId());
        analyse.setExternalUserId(customerRel.getExternalUserid());
        analyse.setTime(customerRel.getDeleteTime());
        analyse.setType(false); // 转换Integer到Boolean，1为true（新增），0为false（流失）
        analyse.setAddTime(customerRel.getCreateTime());
        return analyse;
    }

    /**
     * 批量插入分析记录
     */
    private void insertAnalyseRecords(List<WeEmpleCodeAnalyse> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        // 分批插入，每批1000条
        BatchInsertUtil.doInsert(records, list -> weEmpleCodeAnalyseMapper.insertBatch(list), 500);
    }

    /**
     * 删除统计表中指定时间段的数据
     */
    private int deleteStatisticData(String corpId, Date startDate, Date endDate) {
        LambdaUpdateWrapper<WeEmpleCodeStatistic> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeEmpleCodeStatistic::getCorpId, corpId)
                .ge(WeEmpleCodeStatistic::getTime, startDate)
                .le(WeEmpleCodeStatistic::getTime, endDate);

        return weEmpleCodeStatisticMapper.delete(wrapper);
    }

    /**
     * 将列表分批
     */
    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            batches.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return batches;
    }

    /**
     * 重新生成统计数据（需要调用定时任务）
     *
     * @param corpId 企业ID
     * @return 处理结果
     */
    @Override
    public Map<String, Object> regenerateStatistics(String corpId, Date beginTime, Date endTime) {
        log.info("准备重新生成企业[{}]的统计数据", corpId);

        List<DateTime> dateList = DateUtil.rangeToList(beginTime, endTime, DateField.DAY_OF_YEAR);
        for (DateTime dateTime : dateList) {
            log.info("开始重新生成企业[{}]在{}的统计数据", corpId, dateTime);
            // 调用定时任务得处理
            SpringUtil.getBean(EmpleStatisticHandle.class).handle(DateUtil.formatDate(dateTime), corpId);
            log.info("重新生成企业[{}]在{}的统计数据结束", corpId, dateTime);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("corpId", corpId);
        result.put("message", "统计数据重新生成任务已提交，请通过定时任务执行器查看执行结果");
        result.put("suggestion", "建议手动执行活码统计定时任务来重新生成" + beginTime + "至" + endTime + "期间的统计数据");

        return result;
    }

    /**
     * 测试方法：仅查询指定时间段的客户关系数据，用于验证查询逻辑
     *
     * @param corpId 企业ID
     * @return 查询结果统计
     */
    public Map<String, Object> testQueryCustomerRels(String corpId,  Date beginTime, Date endTime) {
        log.info("测试查询企业[{}]在{}至{}期间的客户关系数据", corpId, beginTime, endTime);

        Map<String, Object> result = new HashMap<>();
        result.put("corpId", corpId);
        result.put("startDate", beginTime);
        result.put("endDate", endTime);

        try {
            // 查询指定时间段内的客户关系数据
            List<WeFlowerCustomerRel> customerRels = queryCustomerRelByDateRange(corpId, beginTime, endTime);

            result.put("totalRecords", customerRels.size());
            result.put("status", "success");


            // 统计正常关系和流失关系数量
            long normalRelCount = customerRels.stream()
                    .filter(this::isAddedRef)
                    .count();
            long lossRelCount = customerRels.stream()
                    .filter(this::isLostedRef)
                    .count();

            result.put("正常关系normalRelCount", normalRelCount);
            result.put("流失关系lossRelCount", lossRelCount);
            result.put("其他关系abnormalRelCount", customerRels.size() - normalRelCount - lossRelCount);

            log.info("测试查询完成，企业[{}]共查询到{}条记录，正常关系{}条, 遗失关系{}条",
                    corpId, customerRels.size(), normalRelCount, lossRelCount);

        } catch (Exception e) {
            log.error("测试查询失败：", e);
            result.put("status", "error");
            result.put("message", "查询失败：" + e.getMessage());
        }

        return result;
    }
} 