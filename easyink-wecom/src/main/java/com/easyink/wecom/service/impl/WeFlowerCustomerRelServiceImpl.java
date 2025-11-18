package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.encrypt.SensitiveFieldProcessor;
import com.easyink.common.enums.AddWayEnum;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.BaseException;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.SnowFlakeUtil;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.customer.resp.GetByUserResp;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendPropertyRel;
import com.easyink.wecom.domain.entity.transfer.WeCustomerTransferConfig;
import com.easyink.wecom.mapper.WeCustomerExtendPropertyRelMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.mapper.WeUserBehaviorDataMapper;
import com.easyink.wecom.service.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
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
    private final WeUserService weUserService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeUserBehaviorDataMapper weUserBehaviorDataMapper;

    @Lazy
    public WeFlowerCustomerRelServiceImpl(@NotNull WeFlowerCustomerRelMapper weFlowerCustomerRelMapper, WeFlowerCustomerTagRelService weFlowerCustomerTagRelService, WeCustomerTransferConfigService weCustomerTransferConfigService, WeCustomerExtendPropertyRelMapper weCustomerExtendPropertyRelMapper, WeUserService weUserService, WeCorpAccountService weCorpAccountService, WeUserBehaviorDataMapper weUserBehaviorDataMapper) {
        this.weFlowerCustomerRelMapper = weFlowerCustomerRelMapper;
        this.weFlowerCustomerTagRelService = weFlowerCustomerTagRelService;
        this.weCustomerTransferConfigService = weCustomerTransferConfigService;
        this.weCustomerExtendPropertyRelMapper = weCustomerExtendPropertyRelMapper;
        this.weUserService = weUserService;
        this.weCorpAccountService = weCorpAccountService;
        this.weUserBehaviorDataMapper = weUserBehaviorDataMapper;
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
    public Boolean deleteCustomer(String userId, String externalUserid, String type, String corpId) {
        if (StringUtils.isAnyBlank(userId, externalUserid, type, corpId)) {
            log.error("[更新员工删除客户状态] 参数异常缺失：userId:{},externalUserid:{},type:{},corpId:{}", userId, externalUserid, type, corpId);
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return this.update(WeFlowerCustomerRel.builder()
                        .delByUserTime(DateUtils.getNowDate())
                        .status(type)
                        .build()
                , new LambdaQueryWrapper<WeFlowerCustomerRel>()
                        .eq(WeFlowerCustomerRel::getUserId, userId)
                        .eq(WeFlowerCustomerRel::getExternalUserid, externalUserid)
                        .eq(WeFlowerCustomerRel::getCorpId, corpId)
                        // 员工删除客户,可能删除的是正常状态的客户(单删),也可能是客户已经将员工删除的客户(双删),二者都需要更新状态
                        .in(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.NORMAL.getCode(), CustomerStatusEnum.DRAIN.getCode()));
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

    /**
     * 获取客户最近添加的员工关系
     *
     * @param externalUserid 客户ID
     * @param corpId 企业ID
     * @return 客户-员工关系信息
     */
    @Override
    public WeFlowerCustomerRel getLastUser(String externalUserid, String corpId) {
        if (StringUtils.isAnyBlank(externalUserid, corpId)) {
            return null;
        }
        // 查出该客户最近添加的员工
        LambdaQueryWrapper<WeFlowerCustomerRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeFlowerCustomerRel::getExternalUserid, externalUserid);
        wrapper.eq(WeFlowerCustomerRel::getCorpId, corpId);
        wrapper.orderByDesc(WeFlowerCustomerRel::getCreateTime);
        wrapper.last(GenConstants.LIMIT_1);
        return getOne(wrapper);
    }

    @Override
    public void alignData(GetByUserResp resp, String userId, String corpId, List<WeFlowerCustomerRel> localRelList) {
        // 删除远端不存在的本地客户
        delNotExistCustomer(resp, userId, corpId,localRelList);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(localRelList)) {
            // 更新客户信息
            updateCustomerRel(resp, localRelList, corpId);
        }
    }

    /**
     * 更新流失后添加的客户数据，客户-员工关系
     *
     * @param resp         企微响应
     * @param localRelList 本地数据
     */
    public void updateCustomerRel(GetByUserResp resp, List<WeFlowerCustomerRel> localRelList, String corpId) {
        List<WeFlowerCustomerRel> updateRelList = new ArrayList<>();
        Map<String, WeFlowerCustomerRel> localMap = localRelList.stream()
                                                                .collect(Collectors.toMap(WeFlowerCustomerRel::getExternalUserid, Function.identity()));
        // 本地数据与远端数据对比
        for (WeFlowerCustomerRel remoteFlowerCustomerRel : resp.getRelList()) {
            WeFlowerCustomerRel localFlowerCustomerRel = localMap.get(remoteFlowerCustomerRel.getExternalUserid());
            // 若该客户与本地记录的添加时间不一样，表示此客户是流失后重新添加员工的客户，将该客户存入列表等待更新状态
            if (localFlowerCustomerRel != null) {
                if (localFlowerCustomerRel.getCreateTime() == null || remoteFlowerCustomerRel.getCreateTime().compareTo(localFlowerCustomerRel.getCreateTime()) != 0) {
                    remoteFlowerCustomerRel.setStatus(Constants.NORMAL_CODE);
                    updateRelList.add(remoteFlowerCustomerRel);
                }
            }
        }
        // 存在创建时间不同的客户则更新客户状态
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(updateRelList)) {
            LambdaUpdateWrapper<WeFlowerCustomerRel> updateRelWrapper = new LambdaUpdateWrapper<>();
            updateRelWrapper.eq(WeFlowerCustomerRel::getCorpId, corpId);
            BatchInsertUtil.doInsert(updateRelList, this::batchUpdateStatus);
        }
    }

    /**
     * 删除 不存在于远端的客户
     *
     * @param resp         获取客户响应
     * @param userId       员工ID
     * @param corpId       企业ID
     * @param localRelList
     */
    public void delNotExistCustomer(GetByUserResp resp, String userId, String corpId, List<WeFlowerCustomerRel> localRelList) {
        List<WeCustomer> customerList = resp.getCustomerList();
        List<String> externalUserIdList = resp.getCustomerList()
                                              .stream()
                                              .map(WeCustomer::getExternalUserid)
                                              .collect(Collectors.toList());
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
            //存在客户，删除本地数据库里所有不存在的客户
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
        SensitiveFieldProcessor.processForSave(entity);
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
        SensitiveFieldProcessor.processForSave(list);
        for (WeFlowerCustomerRel weFlowerCustomerRel : list) {
            if(weFlowerCustomerRel.getRemarkMobilesEncrypt() == null){
                weFlowerCustomerRel.setRemarkMobilesEncrypt(StringUtils.EMPTY);
            }
        }
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
            log.info("[客户员工关系迁移]企业继承配置获取失败,corpId:{}, userId:{}, externalUserId:{}", corpId, handoverUserId, externalUserId);
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
            SensitiveFieldProcessor.processForSave(takeoverRel);
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

    /**
     * 根据开始，结束时间，获取截止时间下的有效客户数
     *
     * @param corpId    企业ID
     * @param beginTime 开始时间，格式为YYYY-MM-DD 00:00:00
     * @param endTime   结束时间，格式为YYYY-MM-DD 23:59:59
     * @param userIds
     * @return 有效客户数
     */
    @Override
    public Integer getCurrentNewCustomerCnt(String corpId, String beginTime, String endTime, List<String> userIds) {
        if (StringUtils.isAnyBlank(corpId, beginTime, endTime)) {
            return null;
        }
        LambdaQueryWrapper<WeFlowerCustomerRel> queryWrapper = new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .in(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.NORMAL.getCode(), CustomerStatusEnum.TO_BE_TRANSFERRED.getCode(), CustomerStatusEnum.TRANSFERRING.getCode())
                .gt(WeFlowerCustomerRel::getCreateTime, DateUtils.parseBeginDay(beginTime))
                .lt(WeFlowerCustomerRel::getCreateTime, DateUtils.parseEndDay(endTime));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIds)) {
            queryWrapper.in(WeFlowerCustomerRel::getUserId, userIds);
        }
        return (int)this.count(queryWrapper);
    }

    /**
     * 根据开始和结束时间,获取首页-数据总览-客户总数
     *
     * @param corpId 企业ID
     * @param beginTime 开始时间，格式为YYYY-MM-DD 00:00:00
     * @param endTime 结束时间，格式为YYYY-MM-DD 23:59:59
     * @return 首页-数据总览-客户总数
     */
    @Override
    public Integer getTotalAllContactCnt(String corpId, String beginTime, String endTime) {
        if(StringUtils.isAnyBlank(corpId, beginTime, endTime)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        int totalExternalCnt = 0;
        // 获取企业下所有的员工
        List<WeUser> userList = weUserService.list(new LambdaQueryWrapper<WeUser>().eq(WeUser::getCorpId, corpId));
        // 获取状态为已激活的员工
        List<String> nomalUserIdList = userList.stream().filter(item -> WeConstans.WE_USER_IS_ACTIVATE.equals(item.getIsActivate())).map(WeUser::getUserId).collect(Collectors.toList());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(nomalUserIdList)) {
            // 获取已激活的员工下的客户总数
            totalExternalCnt += weFlowerCustomerRelMapper.getNormalTotalAllContactCnt(corpId, beginTime, endTime, nomalUserIdList);
        }
        // 获取状态为已删除的员工
        List<String> delUserIdList = userList.stream().filter(item -> WeConstans.WE_USER_IS_LEAVE.equals(item.getIsActivate())).map(WeUser::getUserId).collect(Collectors.toList());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(delUserIdList)) {
            // 获取已删除的员工下的客户总数
            totalExternalCnt += weFlowerCustomerRelMapper.getDelTotalAllContactCnt(corpId, beginTime, endTime, delUserIdList);
        }
        return totalExternalCnt;
    }

    /**
     * 数据统计-联系客户-客户总数-旧数据兼容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void updateTotalAllCustomerCnt() {
        List<WeCorpAccount> weCorpAccountList = weCorpAccountService.listOfAuthCorpInternalWeCorpAccount();
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(weCorpAccountList)) {
            log.info("[数据统计-联系客户-客户总数-旧数据兼容] 当前无可更新信息的企业，请检查配置或联系管理员");
            return;
        }
        // 获取当前日期的前180天日期范围列表
        List<String> dateAgoList = DateUtils.get180DateAgoList();
        weCorpAccountList.forEach(corpAccount -> {
            String corpId = corpAccount.getCorpId();
            log.info("[数据统计-联系客户-客户总数-旧数据兼容] 开始旧数据兼容，当前企业corpId:{}", corpId);
            try {
                // 获取企业下所有在职员工
                List<WeUser> userList = weUserService.list(new LambdaQueryWrapper<WeUser>().eq(WeUser::getIsActivate, WeConstans.WE_USER_IS_ACTIVATE).eq(WeUser::getCorpId, corpId));
                // 获取员工id列表
                List<String> userIdList = userList.stream().map(WeUser::getUserId).collect(Collectors.toList());
                // 最后需要更新的信息列表
                List<WeUserBehaviorData> updateList = new ArrayList<>();
                for (String date : dateAgoList) {
                    // 转换当前时间
                    Date statTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, date);
                    // 获取每个员工对应日期下的客户总数
                    List<WeUserBehaviorData> countList = weFlowerCustomerRelMapper.getTotalAllContactCntByUserId(corpId, DateUtils.parseEndDay(date), userIdList);
                    // 设置日期时间
                    countList.forEach(item -> {
                        item.setStatTime(statTime);
                    });
                    updateList.addAll(countList);
                }
                // 批量更新
                BatchInsertUtil.doInsert(updateList, weUserBehaviorDataMapper::saveBatchUpdateOrInsert);
                log.info("[数据统计-联系客户-客户总数-旧数据兼容] 旧数据统计结束，本次更新数据条数：{}", updateList.size());
            } catch (Exception e) {
                log.info("[数据统计-联系客户-客户总数-旧数据兼容] 出现异常，当前企业corpId:{}，异常原因:{}", corpId, ExceptionUtils.getStackTrace(e));
            }
        });
    }

    /**
     * 更新已流失重新添加回来的客户状态
     *
     * @param corpId 企业ID
     * @param userId 员工ID
     * @param external_userid 外部联系人ID
     * @return 结果
     */
    @Override
    public Integer updateLossExternalUser(String corpId, String userId, String external_userid) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(userId) || StringUtils.isBlank(external_userid)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        return weFlowerCustomerRelMapper.updateLossExternalUser(corpId, userId, external_userid);
    }
}
