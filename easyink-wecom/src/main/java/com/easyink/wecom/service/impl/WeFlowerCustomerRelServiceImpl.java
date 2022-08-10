package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.enums.AddWayEnum;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferConfig;
import com.easyink.wecom.mapper.WeCustomerExtendPropertyRelMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.service.WeCustomerTransferConfigService;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeFlowerCustomerTagRelService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 具有外部联系人功能企业员工也客户的关系Service业务层处理
 *
 * @author admin
 * @date 2020-09-19
 */
@Slf4j
@Service
public class WeFlowerCustomerRelServiceImpl extends ServiceImpl<WeFlowerCustomerRelMapper, WeFlowerCustomerRel> implements WeFlowerCustomerRelService {

    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;
    private final WeFlowerCustomerTagRelService weFlowerCustomerTagRelService;
    private final WeCustomerTransferConfigService weCustomerTransferConfigService;
    private final WeCustomerExtendPropertyRelMapper weCustomerExtendPropertyRelMapper;

    @Lazy
    public WeFlowerCustomerRelServiceImpl(@NotNull WeFlowerCustomerRelMapper weFlowerCustomerRelMapper, WeFlowerCustomerTagRelService weFlowerCustomerTagRelService, WeCustomerTransferConfigService weCustomerTransferConfigService, WeCustomerExtendPropertyRelMapper weCustomerExtendPropertyRelMapper) {
        this.weFlowerCustomerRelMapper = weFlowerCustomerRelMapper;
        this.weFlowerCustomerTagRelService = weFlowerCustomerTagRelService;
        this.weCustomerTransferConfigService = weCustomerTransferConfigService;
        this.weCustomerExtendPropertyRelMapper = weCustomerExtendPropertyRelMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFollowUser(String userId, String externalUserid, String type, String corpId) {
        if (StringUtils.isAnyBlank(userId, externalUserid, type, corpId)) {
            log.error("删除客户关系失败：userId:{},externalUserid:{},type:{},corpId:{}", userId, externalUserid, type, corpId);
            throw new BaseException("删除客户关系失败");
        }
        return this.update(WeFlowerCustomerRel.builder()
                        .deleteTime(DateUtils.getNowDate())
                        .status(type)
                        .build()
                , new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getUserId, userId)
                        .eq(WeFlowerCustomerRel::getExternalUserid, externalUserid)
                        .eq(WeFlowerCustomerRel::getCorpId, corpId)
                        .eq(WeFlowerCustomerRel::getStatus, Constants.NORMAL_CODE));
    }

    @Override
    public Map<String, Object> getUserAddCustomerStat(WeFlowerCustomerRel weFlowerCustomerRel) {
        if (StringUtils.isAnyBlank(weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getState())) {
            log.error("成员添加客户统计查询失败：corpId：{}，state：{}", weFlowerCustomerRel.getCorpId(), weFlowerCustomerRel.getState());
            throw new BaseException("成员添加客户统计失败");
        }

        //员工添加客户的时间
        String createTime = "createTime";
        String total = "total";

        Map<String, Object> resultMap = new HashMap<>(16);
        List<String> dateList = new ArrayList<>();
        List<Long> statList = new ArrayList<>();
        Long resultTotal = 0L;
        List<Map<String, Object>> userAddCustomerStatList = this.baseMapper.getUserAddCustomerStat(weFlowerCustomerRel);
        String beginTime = weFlowerCustomerRel.getBeginTime();
        String endTime = weFlowerCustomerRel.getEndTime();
        if (beginTime != null && endTime != null) {
            Date beginDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, beginTime);
            Date endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, endTime);
            List<Date> dates = DateUtils.findDates(beginDate, endDate);
            dateList = dates.stream().map(DateUtils::dateTime).collect(Collectors.toList());
            Optional.ofNullable(dateList).orElseGet(ArrayList::new).forEach(date -> {
                AtomicLong totals = new AtomicLong(0);
                Optional.ofNullable(userAddCustomerStatList).orElseGet(ArrayList::new).forEach(statInfo -> {
                    if (date.equals(statInfo.get(createTime))) {
                        totals.set((Long) statInfo.get(total));
                        //在lambda表达式中使用return时，这个方法是不会返回的，而只是执行下一次遍历
                        return;
                    }
                });
                statList.add(totals.get());
            });
            resultTotal += statList.stream().mapToLong(Long::longValue).sum();
        } else {
            dateList.addAll(Optional.ofNullable(userAddCustomerStatList).orElseGet(ArrayList::new)
                    .stream()
                    .filter(statInfo -> null != statInfo.get(createTime))
                    .map(statInfo -> (String) statInfo.get(createTime)).collect(Collectors.toList()));

            statList.addAll(Optional.ofNullable(userAddCustomerStatList).orElseGet(ArrayList::new)
                    .stream()
                    .filter(statInfo -> null != statInfo.get(total))
                    .map(statInfo -> (Long) statInfo.get(total)).collect(Collectors.toList()));

            resultTotal += Optional.ofNullable(userAddCustomerStatList).orElseGet(ArrayList::new)
                    .stream()
                    .filter(statInfo -> null != statInfo.get(total))
                    .mapToLong(statInfo -> (Long) statInfo.get(total)).sum();
        }
        resultMap.put("dateList", dateList);
        resultMap.put("statList", statList);
        resultMap.put(total, resultTotal);
        return resultMap;
    }

    @Override
    public int batchUpdateOrInsert(List<WeFlowerCustomerRel> weFlowerCustomerRels) {
        return this.baseMapper.myBatchUpdateOrInsert(weFlowerCustomerRels);
    }

    /**
     * 获取客户关系
     *
     * @param userId
     * @param externalUserid
     * @return
     */
    @Override
    public WeFlowerCustomerRel getOne(String userId, String externalUserid, String corpId) {
        if (StringUtils.isAnyBlank(userId, externalUserid, corpId)) {
            log.error("员工id，客户id，公司id都不能为空，userId：{}，externalUserId：{}，corpId：{}", userId, externalUserid, corpId);
            throw new CustomException("获取客户关系失败");
        }
        LambdaQueryWrapper<WeFlowerCustomerRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeFlowerCustomerRel::getExternalUserid, externalUserid);
        wrapper.eq(WeFlowerCustomerRel::getUserId, userId);
        wrapper.eq(WeFlowerCustomerRel::getCorpId, corpId);
        wrapper.last("limit 1");
        return getOne(wrapper);
    }


    @Override
    public void alignData(GetByUserResp resp, String userId, String corpId) {
        List<WeCustomer> customerList = resp.getCustomerList();
        List<String> externalUserIdList = resp.getCustomerList().stream().map(WeCustomer::getExternalUserid).collect(Collectors.toList());
        // 1. 把不存在的客户至为删除状态
        if (CollectionUtils.isEmpty(customerList)) {
            //员工不存在客户，修改员工的正常状态为员工删除客户状态
            this.update(WeFlowerCustomerRel.builder()
                            .userId(userId)
                            .deleteTime(DateUtils.getNowDate())
                            .status(Constants.DELETE_CODE)
                            .build()
                    , new LambdaQueryWrapper<WeFlowerCustomerRel>()
                            .eq(WeFlowerCustomerRel::getUserId, userId)
                            .eq(WeFlowerCustomerRel::getCorpId, corpId));
        } else {
            //存在客户，删除本地数据库里所有不存在的客户————————————————问题同上
            this.update(WeFlowerCustomerRel.builder()
                            .userId(userId)
                            .deleteTime(DateUtils.getNowDate())
                            .status(Constants.DELETE_CODE)
                            .build()
                    , new LambdaQueryWrapper<WeFlowerCustomerRel>()
                            .eq(WeFlowerCustomerRel::getUserId, userId)
                            .notIn(WeFlowerCustomerRel::getExternalUserid, externalUserIdList)
                            .eq(WeFlowerCustomerRel::getCorpId, corpId));
        }

    }

    @Override
    public boolean saveOrUpdate(WeFlowerCustomerRel entity) {
        return SqlHelper.retBool(weFlowerCustomerRelMapper.saveOrUpdate(entity));
    }

    @Override
    public void insert(WeFlowerCustomerRel remoteRel) {
        List<WeFlowerCustomerRel> list = new ArrayList<>();
        list.add(remoteRel);
        this.batchInsert(list);
    }

    @Override
    public void batchInsert(List<WeFlowerCustomerRel> list) {
        weFlowerCustomerRelMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferCustomerRel(String corpId, String handoverUserId, String takeoverUserId, String externalUserId) {
        if (StringUtils.isAnyBlank(corpId, takeoverUserId, externalUserId, handoverUserId)) {
            log.error("[客户员工关系迁移],参数缺失,corpId:{},handoverUserId:{},takeoverUserId:{},externalUserId:{}", corpId, handoverUserId, takeoverUserId, externalUserId);
            return;
        }
        // 1. 获取继承配置
        WeCustomerTransferConfig config = weCustomerTransferConfigService.getById(corpId);
        if (config == null || config.getEnableTransferInfo() == null) {
            log.info("[客户员工关系迁移]企业继承配置获取失败,corpId:{},userId:{},externalUserId:{}", corpId);
            return;
        }
        // 2. 获取原跟进人信息
        WeFlowerCustomerRel handoverRel = this.getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                .eq(WeFlowerCustomerRel::getUserId, handoverUserId)
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .last(GenConstants.LIMIT_1)
        );
        if (handoverRel == null) {
            log.info("[客户员工关系迁移]获取原跟进人信息失败,corpId:{},userId:{},externalUserId:{}", corpId, handoverUserId, externalUserId);
            return;
        }
        // 3. 判断新接替人关系是否已存在 ,如不存在则插入接替人新的客户关系
        WeFlowerCustomerRel takeoverRel = this.getOne(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getExternalUserid, externalUserId)
                .eq(WeFlowerCustomerRel::getUserId, takeoverUserId)
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .last(GenConstants.LIMIT_1));
        if (takeoverRel == null || takeoverRel.getId() == null) {
            Long relId = SnowFlakeUtil.nextId();
            takeoverRel = WeFlowerCustomerRel.builder()
                    .id(relId)
                    .corpId(corpId)
                    .userId(takeoverUserId)
                    .externalUserid(externalUserId)
                    .status(String.valueOf(CustomerStatusEnum.NORMAL.getCode()))
                    .addWay(String.valueOf(AddWayEnum.TRANSFER.getCode()))
                    .operUserid(handoverRel.getOperUserid())
                    .build();
            this.save(takeoverRel);
        }
        // 4. 根据继承设置 选择是否接替客户资料(跟进人信息,扩展字段,客户标签关系)
        boolean enableTransferInfo = config.getEnableTransferInfo();
        if (enableTransferInfo) {
            // 接替客户资料
            this.transferRel(corpId, handoverRel.getId(), takeoverRel.getId(), takeoverUserId);
            weCustomerExtendPropertyRelMapper.transferProp(corpId, externalUserId, handoverUserId, takeoverUserId);
            weFlowerCustomerTagRelService.transferTag(handoverRel.getId(), takeoverRel.getId());
        } else {
            // 删除原跟进人的客户相关信息
            this.removeById(handoverRel.getId());
            weFlowerCustomerTagRelService.removeByRelId(handoverRel.getId());
            weCustomerExtendPropertyRelMapper.delete(new LambdaUpdateWrapper<WeCustomerExtendPropertyRel>()
                    .eq(WeCustomerExtendPropertyRel::getCorpId, corpId)
                    .eq(WeCustomerExtendPropertyRel::getUserId, handoverUserId)
                    .eq(WeCustomerExtendPropertyRel::getExternalUserid, externalUserId)
            );
        }

    }

    @Override
    public void transferRel(String corpId, Long handoverRelId, Long takeoverRelId, String takeoverUserId) {
        // 删除现关系
        this.removeById(takeoverRelId);
        // 把原客户关系id替换成现关系id
        weFlowerCustomerRelMapper.transferRel(corpId, handoverRelId, takeoverRelId, takeoverUserId);
    }

    @Override
    public void batchUpdateStatus(List<WeFlowerCustomerRel> relList) {
        weFlowerCustomerRelMapper.batchUpdateStatus(relList);
    }

    /**
     * 查询客户所属员工id列表
     *
     * @param customerId 客户id
     * @param corpId     企业id
     * @return
     */
    @Override
    public List<String> listUpUserIdListByCustomerId(String customerId, String corpId) {
        if (StringUtils.isBlank(customerId) || StringUtils.isBlank(corpId)) {
            return Lists.newArrayList();
        }
        List<WeFlowerCustomerRel> weFlowerCustomerRelList = this.list(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getStatus, 0)
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .eq(WeFlowerCustomerRel::getExternalUserid, customerId));
        return weFlowerCustomerRelList.stream().map(WeFlowerCustomerRel::getUserId).collect(Collectors.toList());
    }
}
