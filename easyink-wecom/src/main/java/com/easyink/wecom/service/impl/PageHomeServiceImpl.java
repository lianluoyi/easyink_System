package com.easyink.wecom.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GroupConstants;
import com.easyink.common.constant.RedisKeyConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeGroup;
import com.easyink.wecom.domain.WeGroupMember;
import com.easyink.wecom.domain.WeUserBehaviorData;
import com.easyink.wecom.domain.dto.WePageCountDTO;
import com.easyink.wecom.domain.dto.WePageStaticDataDTO;
import com.easyink.wecom.domain.query.WePageStateQuery;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.service.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名： PageHomeServiceImpl
 *
 * @author 佚名
 * @date 2021/8/26 20:56
 */
@Service
public class PageHomeServiceImpl implements PageHomeService {

    private final WeGroupService weGroupService;
    private final WeGroupMemberService weGroupMemberService;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final WeUserBehaviorDataService weUserBehaviorDataService;
    private final WeGroupStatisticService weGroupStatisticService;
    private final RedisCache redisCache;
    private final WeUserService weUserService;
    private final WeCustomerService weCustomerService;
    private final WeCustomerMapper weCustomerMapper;

    @Autowired
    @Lazy
    public PageHomeServiceImpl(
            WeGroupService weGroupService,
            WeGroupMemberService weGroupMemberService,
            WeFlowerCustomerRelService weFlowerCustomerRelService,
            WeUserBehaviorDataService weUserBehaviorDataService,
            WeGroupStatisticService weGroupStatisticService,
            RedisCache redisCache,
            WeUserService weUserService, WeCustomerService weCustomerService, WeCustomerMapper weCustomerMapper) {
        this.weGroupService = weGroupService;
        this.weGroupMemberService = weGroupMemberService;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
        this.weUserBehaviorDataService = weUserBehaviorDataService;
        this.weGroupStatisticService = weGroupStatisticService;
        this.redisCache = redisCache;
        this.weUserService = weUserService;
        this.weCustomerService = weCustomerService;
        this.weCustomerMapper = weCustomerMapper;
    }

    @Override
    public void reloadPageHome(String corpId) {
        getCorpRealTimeData(corpId);
        getCorpBasicData(corpId);

    }

    /**
     *
     */
    @Override
    public void getCorpBasicData(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        Map<String, Object> totalMap = new HashMap<>(16);
        //企业成员总数
        int userCount = weUserService.count(new LambdaQueryWrapper<WeUser>()
                .eq(WeUser::getCorpId, corpId)
                .eq(WeUser::getIsActivate, WeConstans.WE_USER_IS_ACTIVATE));
        //客户总人数
        int customerCount = weCustomerService.customerCount(corpId);
        //客户群总数( 1.21.0 改成读取官方统计接口里的数据 ，)
        int groupCount = weGroupService.count(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getCorpId, corpId)
                .in(WeGroup :: getStatus , Lists.newArrayList(GroupConstants.NARMAL,GroupConstants.OWNER_LEAVE_EXTEND_SUCCESS)));
        //群成员总数
        int groupMemberCount = weGroupStatisticService.getGroupMemberCnt(corpId, DateUtil.yesterday());
        totalMap.put("userCount", userCount);
        totalMap.put("customerCount", customerCount);
        totalMap.put("groupCount", groupCount);
        totalMap.put("groupMemberCount", groupMemberCount);
        redisCache.setCacheMap(RedisKeyConstants.CORP_BASIC_DATA + corpId, totalMap);
    }

    @Override
    public void getUserData(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        //企业成员总数
        int userCount = weUserService.count(new LambdaQueryWrapper<WeUser>()
                .eq(WeUser::getCorpId, corpId)
                .eq(WeUser::getIsActivate, WeConstans.WE_USER_IS_ACTIVATE));
        Map<String, Object> map = new HashMap<>(16);
        map.put("userCount", userCount);
        redisCache.setCacheMap(RedisKeyConstants.CORP_BASIC_DATA + corpId, map);
    }

    @Override
    public void getCustomerData(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        Map<String, Object> map = new HashMap<>(16);
        //客户总人数
        map.put("customerCount", weCustomerService.customerCount(corpId));
        redisCache.setCacheMap(RedisKeyConstants.CORP_BASIC_DATA + corpId, map);
    }

    @Override
    public void getGroupData(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
        Map<String, Object> map = new HashMap<>(16);
        //客户群总数
        int groupCount = weGroupService.count(new LambdaQueryWrapper<WeGroup>().eq(WeGroup::getCorpId, corpId));
        //群成员总数
        int groupMemberCount = weGroupMemberService.count(new LambdaQueryWrapper<WeGroupMember>().eq(WeGroupMember::getCorpId, corpId));
        map.put("groupCount", groupCount);
        map.put("groupMemberCount", groupMemberCount);
        redisCache.setCacheMap(RedisKeyConstants.CORP_BASIC_DATA + corpId, map);
    }


    private void getCorpRealTimeData(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return;
        }
//        doSystemCustomStat(corpId, true );
        WePageStaticDataDTO wePageStaticDataDto = new WePageStaticDataDTO();
        //获取数据概览的实时数据
        WePageStaticDataDTO wePageStaticDataDtoByRedis = initCorpRealTimeData(corpId);
        //今天
        wePageStaticDataDto.setToday(getTodayData(corpId, wePageStaticDataDtoByRedis));
        wePageStaticDataDto.setWeek(getWeekData(corpId, wePageStaticDataDtoByRedis));
        wePageStaticDataDto.setMonth(getMonthData(corpId, wePageStaticDataDtoByRedis));
        wePageStaticDataDto.setUpdateTime(DateUtil.now());
        redisCache.setCacheObject(RedisKeyConstants.CORP_REAL_TIME + corpId, wePageStaticDataDto);
    }

    /**
     * 获取今日数据
     *
     * @param wePageStaticDataDto wePageStaticDataDto
     * @return 今日数据
     */
    private WePageStaticDataDTO.PageStaticData getTodayData(String corpId, WePageStaticDataDTO wePageStaticDataDto) {
        //获取今日数据
        WePageStaticDataDTO.PageStaticData today = wePageStaticDataDto.getToday();
        //获取昨天数据
        WePageCountDTO yesterday = wePageStaticDataDto.getToday()
                                                      .getDataList()
                                                      .get(wePageStaticDataDto.getToday()
                                                                              .getDataList()
                                                                              .size() - 2);
        DateTime beginTime = DateUtil.beginOfDay(new Date());
        DateTime endTime = DateUtil.endOfDay(new Date());
        // 获取今日添加客户数量，所有状态的客户数
        int newCustomerCount = weFlowerCustomerRelService.count(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .between(WeFlowerCustomerRel::getCreateTime, beginTime, endTime));
        //今日群新增人数
        int groupMemberCount = weGroupMemberService.count(new LambdaQueryWrapper<WeGroupMember>()
                .eq(WeGroupMember::getCorpId, corpId)
                .between(WeGroupMember::getJoinTime, beginTime, endTime));
        today.setNewMemberCnt(groupMemberCount);
        //流失客户数
        Integer lossCount = weFlowerCustomerRelService.count(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .between(WeFlowerCustomerRel::getDeleteTime, beginTime, endTime)
                .eq(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.DRAIN.getCode()
                                                                            .toString()));
        // 今日流失数
        Integer todayLossCnt = weFlowerCustomerRelService.count(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .between(WeFlowerCustomerRel::getDeleteTime, beginTime, endTime)
                .between(WeFlowerCustomerRel::getCreateTime, beginTime, endTime)
                .eq(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.DRAIN.getCode()
                                                                            .toString()));
        // 群聊总数
        Integer chatTotal = weGroupService.count(new LambdaQueryWrapper<WeGroup>()
                .eq(WeGroup::getCorpId, corpId)
                .in(WeGroup::getStatus, Lists.newArrayList(GroupConstants.NARMAL, GroupConstants.OWNER_LEAVE_EXTEND, GroupConstants.OWNER_LEAVE_EXTEND_SUCCESS)));
        // 今日新增群聊数
        Integer todayNewChatCnt = weGroupService.count(new LambdaQueryWrapper<WeGroup>()
                .eq(WeGroup::getCorpId, corpId)
                .eq(WeGroup::getStatus, GroupConstants.NARMAL)
                .between(WeGroup::getCreateTime, beginTime, endTime)

        );
        // 截止今日有效客户数
        int currentNewCustomerCnt = weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, DateUtils.parseBeginDay(DateUtils.dateTime(new Date())), DateUtils.parseEndDay(DateUtils.dateTime(new Date())), null);
        // 群聊客户总数
        // 由于 官方统计接口统计的群聊成员数是包含离职员工,但是获取客户群详情的成员里面不包含离职员工
        // 所以我们无法统计正确的统计今日 群聊成员数, 此处和产品商量后 今日群成员数直接返回0给前端
        Integer memberTotal = 0;
        Integer totalContactCnt = weCustomerService.customerCount(corpId);
        //今天的数量就是新增客户数
        today.setNewContactCnt(newCustomerCount);
        //新增客户数差(今日新增减去昨天新增)
        today.setNewContactCntDiff(newCustomerCount - yesterday.getNewContactCnt());
        //今天群成员新增的数量-昨天的数量
        today.setNewMemberCntDiff(groupMemberCount - yesterday.getNewMemberCnt());
        //流失数量和数量差
        today.setNegativeFeedbackCnt(lossCount);
        today.setNegativeFeedbackCntDiff(lossCount - yesterday.getNegativeFeedbackCnt());
        //客户留存率和留存率差
        String retentionRate = genRetentionRate(newCustomerCount, currentNewCustomerCnt);
        today.setNewContactRetentionRate(retentionRate);
        today.setNewContactRetentionRateDiff(genRetentionRateCliff(retentionRate, yesterday.getNewContactRetentionRate()));
        // 设置群聊总数、客户群人数总数 以及增幅
        today.setChatTotal(chatTotal);
        today.setChatTotalDiff(chatTotal - yesterday.getChatTotal());
        today.setMemberTotal(memberTotal);
        today.setMemberTotalDiff(memberTotal - yesterday.getMemberTotal());
        today.setNewContactLossCnt(todayLossCnt);
        today.setNewChatCnt(todayNewChatCnt);
        today.setNewChatCntDiff(todayNewChatCnt- yesterday.getNewChatCnt());
        today.setCurrentNewCustomerCnt(currentNewCustomerCnt);
        //折线统计图，当天数据
        WePageCountDTO wePageCountDTO = today.getDataList()
                                             .get(today.getDataList()
                                                       .size() - 1);
        wePageCountDTO.setNewContactCnt(newCustomerCount);
        wePageCountDTO.setNewMemberCnt(groupMemberCount);
        wePageCountDTO.setNegativeFeedbackCnt(lossCount);
        wePageCountDTO.setMemberTotal(memberTotal);
        wePageCountDTO.setChatTotal(chatTotal);
        wePageCountDTO.setNewContactRetentionRate(retentionRate);
        wePageCountDTO.setTotalContactCnt(totalContactCnt);
        wePageCountDTO.setNewContactLossCnt(todayLossCnt);
        wePageCountDTO.setNewChatCnt(todayNewChatCnt);
        wePageCountDTO.setCurrentNewCustomerCnt(currentNewCustomerCnt);
        List<WePageCountDTO> dataList = wePageStaticDataDto.getToday()
                                                           .getDataList();
        //删除今日数据，再加入修改后的
        dataList.remove(dataList.size() - 1);
        dataList.add(wePageCountDTO);
        return today;
    }

    /**
     * 生成客户留存率的比较值
     *
     * @param newRate 新的比例
     * @param oldRate 旧的比例
     * @return 留存率的比较值
     */
    private String genRetentionRateCliff(String newRate, String oldRate) {
        if (newRate.equals(Constants.EMPTY_RETAIN_RATE_VALUE) || oldRate.equals(Constants.EMPTY_RETAIN_RATE_VALUE)) {
            return Constants.EMPTY_RETAIN_RATE_VALUE;
        }
        return new BigDecimal(newRate).subtract(new BigDecimal(oldRate))
                                      .stripTrailingZeros()
                                      .toPlainString();
    }

    /**
     * 计算 留存率 截止当前的有效客户数 / 新增客户数
     *
     * @param newCnt                新增客户数
     * @param currentNewCustomerCnt 截止当前，有效客户数
     * @return 留存率
     */
    public String genRetentionRate(Integer newCnt, Integer currentNewCustomerCnt) {
        if (newCnt == 0 || newCnt == null || currentNewCustomerCnt == null) {
            return Constants.EMPTY_RETAIN_RATE_VALUE;
        }
        BigDecimal percent = new BigDecimal(100);
        // 百分比
        BigDecimal newCntDecimal = new BigDecimal(newCnt);
        BigDecimal currCustomerCntDecimal = new BigDecimal(currentNewCustomerCnt);
        int scale = 2;
        return currCustomerCntDecimal
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();

    }


    /**
     * 获取本周用户行为数据统计
     *
     * @param corpId 公司id
     * @return WePageCountDTO
     */
    private WePageCountDTO getThisWeekWeUserBehaviorData(String corpId) {
        return weUserBehaviorDataService.getCountDataByDayNew(corpId, DateUtil.beginOfWeek(new Date(), true)
                                                                              .toString(), DateUtil.endOfWeek(new Date(), true)
                                                                                                   .toString());
    }

    /**
     * 获取本周群相关数据统计
     *
     * @param corpId 公司id
     * @return WePageCountDTO
     */
    private WePageCountDTO getThisWeekWeGroupStatisticData(String corpId) {
        return weGroupStatisticService.getCountDataByDayNew(corpId, DateUtil.beginOfWeek(new Date(), true)
                                                                            .toString(), DateUtil.endOfWeek(new Date(), true)
                                                                                                 .toString());
    }

    /**
     * 获取本月用户行为数据统计
     *
     * @param corpId 公司id
     * @return WePageCountDTO
     */
    private WePageCountDTO getThisMonthWeUserBehaviorData(String corpId) {
        return weUserBehaviorDataService.getCountDataByDayNew(corpId, DateUtil.beginOfMonth(new Date())
                                                                              .toString(), DateUtil.endOfMonth(new Date())
                                                                                                   .toString());
    }

    /**
     * 获取本月群相关数据统计
     *
     * @param corpId 公司id
     * @return WePageCountDTO
     */
    private WePageCountDTO getThisMonthWeGroupStatisticData(String corpId) {
        return weGroupStatisticService.getCountDataByDayNew(corpId, DateUtil.beginOfMonth(new Date())
                                                                            .toString(), DateUtil.endOfMonth(new Date())
                                                                                                 .toString());
    }

    private WePageStaticDataDTO.PageStaticData getWeekData(String corpId, WePageStaticDataDTO wePageStaticDataDto) {
        //由于先执行setToday,那么可以有today的数据拼接week的数据
        WePageStaticDataDTO.PageStaticData today = wePageStaticDataDto.getToday();
        WePageStaticDataDTO.PageStaticData week = wePageStaticDataDto.getWeek();

        //获取本周数据
        WePageCountDTO newTime = getThisWeekWeUserBehaviorData(corpId);
        WePageCountDTO nowTimeGroupChatData = getThisWeekWeGroupStatisticData(corpId);
        String beginWeek = DateUtil.beginOfWeek(new Date(), true).toString();
        String endWeek = DateUtil.endOfWeek(new Date(), true).toString();
        if (nowTimeGroupChatData != null) {
            newTime.setGroupChatData(nowTimeGroupChatData);
        }
        // 设置本周，截止当前的有效客户数
        newTime.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, beginWeek, endWeek, null));
        //redis中获取上周
        //客户统计
        WePageCountDTO lastTime = wePageStaticDataDto.getWeek()
                                                     .getDataList()
                                                     .get(wePageStaticDataDto.getWeek()
                                                                             .getDataList()
                                                                             .size() - 2);
        String lastBeginWeek = DateUtil.beginOfWeek(DateUtil.lastWeek(), true).toString();
        String lastEndWeek = DateUtil.endOfWeek(DateUtil.lastWeek(), true).toString();
        // 设置上周，截止当前的有效客户数
        lastTime.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, lastBeginWeek, lastEndWeek, null));
        return setPageStaticData(newTime, lastTime, today, week);

    }

    private WePageStaticDataDTO.PageStaticData getMonthData(String corpId, WePageStaticDataDTO wePageStaticDataDto) {
        //由于先执行setToday,那么可以有today的数据拼接week的数据
        WePageStaticDataDTO.PageStaticData today = wePageStaticDataDto.getToday();
        WePageStaticDataDTO.PageStaticData month = wePageStaticDataDto.getMonth();

        //获取本月数据
        WePageCountDTO newTime = getThisMonthWeUserBehaviorData(corpId);
        WePageCountDTO nowTimeGroupChatData = getThisMonthWeGroupStatisticData(corpId);
        // 设置截止本月，有效客户数
        String beginMouth = DateUtil.beginOfMonth(new Date()).toString();
        String endMouth = DateUtil.endOfMonth(new Date()).toString();
        newTime.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, beginMouth, endMouth, null));
        if (nowTimeGroupChatData != null) {
            newTime.setGroupChatData(nowTimeGroupChatData);
        }
        WePageCountDTO lastTime = wePageStaticDataDto.getMonth()
                                                     .getDataList()
                                                     .get(wePageStaticDataDto.getWeek()
                                                                             .getDataList()
                                                                             .size() - 2);
        // 设置截止上月，有效客户数
        String lastBeginMouth = DateUtil.beginOfMonth(DateUtil.lastMonth()).toString();
        String lastEndMouth = DateUtil.endOfMonth(DateUtil.lastMonth()).toString();
        lastTime.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, lastBeginMouth, lastEndMouth, null));
        return setPageStaticData(newTime, lastTime, today, month);
    }

    private WePageStaticDataDTO.PageStaticData setPageStaticData(WePageCountDTO nowTime, WePageCountDTO lastTime, WePageStaticDataDTO.PageStaticData today, WePageStaticDataDTO.PageStaticData week) {

        //申请数，不操作
        //流失数(原流失数加上今日流失数)
        Integer lossCnt = nowTime.getNegativeFeedbackCnt() + today.getNegativeFeedbackCnt();
        week.setNegativeFeedbackCnt(lossCnt);
        week.setNegativeFeedbackCntDiff(nowTime.getNegativeFeedbackCnt() - lastTime.getNegativeFeedbackCnt() + today.getNegativeFeedbackCnt());
        // 时间段内加入的新客流失数
        Integer periodLossCnt = nowTime.getNewContactLossCnt() + today.getNewContactLossCnt();
        //新增客户数（原数量+今日加入）
        Integer newCnt = nowTime.getNewContactCnt() + today.getNewContactCnt();
        week.setNewContactCnt(newCnt);
        week.setNewContactCntDiff(newCnt - lastTime.getNewContactCnt());
        //群聊数量（原数据+今日加入）
        week.setNewMemberCnt(nowTime.getNewMemberCnt() + today.getNewMemberCnt());
        week.setNewMemberCntDiff(nowTime.getNewMemberCnt() - lastTime.getNewMemberCnt() + today.getNewMemberCnt());
        // 新客留存率 截止当前的有效客户数 / (原数据 + 今日加入新增客户数)
        String retentionRate = genRetentionRate(newCnt, nowTime.getCurrentNewCustomerCnt());
        // 设置本周的截止当前有效客户数
        week.setCurrentNewCustomerCnt(nowTime.getCurrentNewCustomerCnt());
        week.setNewContactRetentionRate(retentionRate);
        week.setNewContactRetentionRateDiff(genRetentionRateCliff(retentionRate, lastTime.getNewContactRetentionRate()));
        // 群聊总数和 群聊人数总数
        week.setChatTotal(today.getChatTotal());
        week.setChatTotalDiff(today.getChatTotal() - lastTime.getChatTotal());
        week.setMemberTotal(today.getMemberTotal());
        week.setMemberTotalDiff(today.getMemberTotal() - lastTime.getMemberTotal());
        // 获取当月/当周的群聊总数
        Integer newChatCnt = nowTime.getNewChatCnt() + today.getNewChatCnt() ;
        week.setNewChatCnt(newChatCnt);
        if(lastTime.getNewChatCnt() != null) {
            week.setNewChatCntDiff(newChatCnt - lastTime.getNewChatCnt());
        }

        //获取折线统计图数据
        WePageCountDTO wePageCountDTO = week.getDataList()
                                                 .get(week.getDataList()
                                                               .size() - 1);
        //设置客户数量（原数量加上现在客户的数量）
        wePageCountDTO.setNewContactCnt(nowTime.getNewContactCnt() + today.getNewContactCnt());
        //设置群新增人数（原人数+今天的人数）
        wePageCountDTO.setNewMemberCnt(nowTime.getNewMemberCnt() + today.getNewMemberCnt());
        //设置流失数据（原流失数据+今天流失数据）
        wePageCountDTO.setNegativeFeedbackCnt(nowTime.getNegativeFeedbackCnt() + today.getNegativeFeedbackCnt());
        wePageCountDTO.setNewContactRetentionRate(retentionRate);
        wePageCountDTO.setNewChatCnt(newChatCnt);
        wePageCountDTO.setChatTotal(today.getChatTotal());
        wePageCountDTO.setCurrentNewCustomerCnt(nowTime.getCurrentNewCustomerCnt());
        List<WePageCountDTO> dataList = week.getDataList();
        //删除本周，月数据，再加入修改后的
        dataList.remove(dataList.size() - 1);
        dataList.add(wePageCountDTO);
        return week;
    }

    @Override
    public WePageStaticDataDTO initCorpRealTimeData(String corpId) {
        WePageStaticDataDTO wePageStaticDataDto = new WePageStaticDataDTO();
        //今天
        wePageStaticDataDto.setToday(getTodayData(corpId));
        wePageStaticDataDto.setWeek(getWeekData(corpId));
        wePageStaticDataDto.setMonth(getMonthData(corpId));
        wePageStaticDataDto.setUpdateTime(DateUtil.now());
        return wePageStaticDataDto;
    }

    @Override
    public void doSystemCustomStat(String corpId, boolean isToday, String time) {
        if(StringUtils.isBlank(corpId) ) {
            return;
        }
        // 转换为YY:MM:DD HH:MM:SS格式
        Date beginTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, time + DateUtils.BEGIN_TIME_SUFFIX);
        Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, time + DateUtils.END_TIME_SUFFIX);
        //  统计客户总数
        Integer totalContactCnt = weCustomerMapper.countCustomerNumByTime(corpId, DateUtils.parseEndDay(time));
        // 今日流失数
        Integer todayLossCnt = weFlowerCustomerRelService.count(new LambdaQueryWrapper<WeFlowerCustomerRel>()
                .eq(WeFlowerCustomerRel::getCorpId, corpId)
                .between(WeFlowerCustomerRel::getDeleteTime, beginTime, endTime)
                .between(WeFlowerCustomerRel::getCreateTime, beginTime, endTime)
                .eq(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.DRAIN.getCode()
                                                                            .toString()));
        WeUserBehaviorData userBehaviorData = new WeUserBehaviorData();
        userBehaviorData.setTotalContactCnt(totalContactCnt);
        userBehaviorData.setNewContactLossCnt(todayLossCnt);
        userBehaviorData.setCorpId(corpId);
        userBehaviorData.setStatTime(beginTime);
        // 由于客户总数是统计整个公司的,所以没有user_id, 取值为与corpId一样
        userBehaviorData.setUserId(corpId);
        // 先删除之前的数据
        weUserBehaviorDataService.remove(new LambdaQueryWrapper<WeUserBehaviorData>()
                .eq(WeUserBehaviorData::getCorpId, corpId)
                .eq(WeUserBehaviorData::getStatTime, beginTime)
                .eq(WeUserBehaviorData::getUserId, corpId)
        );
        // 插入新数据
        weUserBehaviorDataService.save(userBehaviorData);
    }

    private WePageStaticDataDTO.PageStaticData getTodayData(String corpId) {
        //1、统计今日
        String today = DateUtil.today();

        //客户统计
        WePageCountDTO nowData = weUserBehaviorDataService.getCountDataByDayNew(corpId, DateUtils.parseBeginDay(today), DateUtils.parseEndDay(today));
        //客户群统计
        WePageCountDTO nowTimeGroupChatData = weGroupStatisticService.getCountDataByDayNew(corpId, DateUtils.parseBeginDay(today), DateUtils.parseEndDay(today));
        if (nowTimeGroupChatData != null) {
            nowData.setGroupChatData(nowTimeGroupChatData);
        }
        // 计算截止今日有效的客户数
        nowData.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, DateUtils.parseBeginDay(today), DateUtils.parseEndDay(today), null));
        //2、统计昨日
        String yesterday = DateUtil.yesterday()
                                   .toDateStr();
        //客户统计
        WePageCountDTO lastTime = weUserBehaviorDataService.getCountDataByDayNew(corpId, DateUtils.parseBeginDay(yesterday), DateUtils.parseEndDay(yesterday));
        // 计算截止昨日有效的客户数
        lastTime.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, DateUtils.parseBeginDay(yesterday), DateUtils.parseEndDay(yesterday), null));
        //客户群统计
        WePageCountDTO lastGroupChatTime = weGroupStatisticService.getCountDataByDayNew(corpId, DateUtils.parseBeginDay(yesterday), DateUtils.parseEndDay(yesterday));
        if (lastGroupChatTime != null) {
            lastTime.setGroupChatData(lastGroupChatTime);
        }

        WePageStaticDataDTO.PageStaticData pageStaticData = setPageStaticData(nowData, lastTime);

        WePageStateQuery wePageStateQuery = new WePageStateQuery();
        //获取15天前的时间,设置成 2021-10-11 00:00:00
        wePageStateQuery.setStartTime(DateUtils.parseBeginDay(DateUtil.offsetDay(new Date(), -15)
                                                                      .toDateStr()));
        wePageStateQuery.setEndTime(DateUtils.parseEndDay(today));
        int dayFew = 14;
        wePageStateQuery.setFew(dayFew);
        wePageStateQuery.setCorpId(corpId);
        List<WePageCountDTO> dayCountData = weUserBehaviorDataService.getDayCountData(wePageStateQuery);
        List<WePageCountDTO> dayGroupChatCountData = weGroupStatisticService.getDayCountData(wePageStateQuery);
        // 查询每日的总客户数
        pageStaticData.setDataList(processWePageCountDTOList(dayCountData, dayGroupChatCountData));
        // 为前14天设置截止当前时间，有效客户数
        List<Date> dates = DateUtils.findDates(DateUtil.offsetDay(new Date(), -15), new Date());
        for (Date date : dates) {
            // 当前日期，格式为YYYY-MM-DD
            String time = DateUtils.dateTime(date);
            // 当前日期开始时间，格式为YYYY-MM-DD 00:00:00
            String beginTime = DateUtils.parseBeginDay(time);
            // 当前日期结束时间，格式为YYYY-MM-DD 23:59:59
            String endTime = DateUtils.parseEndDay(time);
            for (WePageCountDTO dayCountDatum : dayCountData) {
                // 给对应时间设置截止当前时间的有效客户数
                if (dayCountDatum.getXTime().equals(time)) {
                    dayCountDatum.setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, beginTime, endTime, null));
                }
            }
        }
        return pageStaticData;
    }

    /**
     * 将群聊统计信息合并整理进入实际统计的列表中
     *
     * @param countDataList      实际统计列表
     * @param groupChatCountData 群聊统计列表
     * @return 最终统计列表
     */
    private List<WePageCountDTO> processWePageCountDTOList(List<WePageCountDTO> countDataList, List<WePageCountDTO> groupChatCountData) {
        for (WePageCountDTO countDataDTO : countDataList) {
            for (WePageCountDTO groupChatDataDTO : groupChatCountData) {
                if (countDataDTO.getXTime()
                                .equals(groupChatDataDTO.getXTime())) {
                    countDataDTO.setGroupChatData(groupChatDataDTO);
                    break;
                }
            }
        }

        return countDataList;
    }

    private WePageStaticDataDTO.PageStaticData getWeekData(String corpId) {
        //1.本周
        //客户统计
        WePageCountDTO newTime = getThisWeekWeUserBehaviorData(corpId);
        WePageCountDTO nowTimeGroupChatData = getThisWeekWeGroupStatisticData(corpId);
        if (nowTimeGroupChatData != null) {
            newTime.setGroupChatData(nowTimeGroupChatData);
        }

        //2.上周
        //客户统计
        WePageCountDTO lastTime = weUserBehaviorDataService.getCountDataByDayNew(corpId, DateUtil.beginOfWeek(DateUtil.lastWeek(), true)
                                                                                                 .toString(), DateUtil.endOfWeek(DateUtil.lastWeek(), true)
                                                                                                                      .toString());
        WePageCountDTO lastTimeGroupChatData = weGroupStatisticService.getCountDataByDayNew(corpId, DateUtil.beginOfWeek(DateUtil.lastWeek(), true)
                                                                                                            .toString(), DateUtil.endOfWeek(DateUtil.lastWeek(), true)
                                                                                                                                 .toString());
        if (lastTimeGroupChatData != null) {
            lastTime.setGroupChatData(lastTimeGroupChatData);
        }
        WePageStaticDataDTO.PageStaticData pageStaticData = setPageStaticData(newTime, lastTime);

        WePageStateQuery wePageStateQuery = new WePageStateQuery();
        int weekFew = 5;
        wePageStateQuery.setFew(weekFew);
        wePageStateQuery.setCorpId(corpId);

        //此处mysql中的yearweek统计是以周日作为一周第一天，此处调整为周一作为一周第一天，计算出开始时间和结束时间
        String beginTime = DateUtil.beginOfWeek(DateUtil.offsetWeek(new Date(), -weekFew), true)
                                   .toString();
        String endTime = DateUtil.endOfWeek(new Date(), true)
                                 .toString();
        wePageStateQuery.setStartTime(beginTime);
        wePageStateQuery.setEndTime(endTime);

        List<WePageCountDTO> weekCountData = weUserBehaviorDataService.getWeekCountData(wePageStateQuery);
        List<WePageCountDTO> weekGroupChatCountData = weGroupStatisticService.getWeekCountData(wePageStateQuery);

        pageStaticData.setDataList(processWePageCountDTOList(weekCountData, weekGroupChatCountData));
        // 为前五周设置截止本周的有效客户数
        for (int i = 1; i <= weekFew; i++) {
            // 获取前 i 周的开始时间
            String beginWeekTime = DateUtil.beginOfWeek(DateUtil.offsetWeek(new Date(), -i), true).toString();
            // 获取前 i 周的结束时间
            String endWeekTime = DateUtil.endOfWeek(DateUtil.offsetWeek(new Date(), -i), true).toString();
            // 因为周数是按时间顺序排序的，weekFew间隔-1，就是当前周的前 i 周 的数据
            weekCountData.get(weekFew - i).setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, beginWeekTime, endWeekTime, null));
        }
        return pageStaticData;
    }


    private WePageStaticDataDTO.PageStaticData getMonthData(String corpId) {

        //1.本月
        //客户统计
        WePageCountDTO newTime = getThisMonthWeUserBehaviorData(corpId);
        WePageCountDTO nowTimeGroupChatData = getThisMonthWeGroupStatisticData(corpId);
        if (nowTimeGroupChatData != null) {
            newTime.setGroupChatData(nowTimeGroupChatData);
        }
        //2.上月
        //客户统计
        WePageCountDTO lastTime = weUserBehaviorDataService.getCountDataByDayNew(corpId, DateUtil.beginOfMonth(DateUtil.lastMonth())
                                                                                                 .toString(), DateUtil.endOfMonth(DateUtil.lastMonth())
                                                                                                                      .toString());
        WePageCountDTO lastTimeGroupChatData = weGroupStatisticService.getCountDataByDayNew(corpId, DateUtil.beginOfMonth(DateUtil.lastMonth())
                                                                                                            .toString(), DateUtil.endOfMonth(DateUtil.lastMonth())
                                                                                                                                 .toString());
        if (lastTimeGroupChatData != null) {
            lastTime.setGroupChatData(lastTimeGroupChatData);
        }
        WePageStaticDataDTO.PageStaticData pageStaticData = setPageStaticData(newTime, lastTime);

        WePageStateQuery wePageStateQuery = new WePageStateQuery();
        int monthFew = 5;
        wePageStateQuery.setFew(monthFew);
        wePageStateQuery.setCorpId(corpId);

        //此处sql未涉及时间区间
        List<WePageCountDTO> monthCountData = weUserBehaviorDataService.getMonthCountData(wePageStateQuery);
        List<WePageCountDTO> monthGroupChatCountData = weGroupStatisticService.getMonthCountData(wePageStateQuery);

        pageStaticData.setDataList(processWePageCountDTOList(monthCountData, monthGroupChatCountData));
        // 为前五月设置截止本月的有效客户数
        for (int i = 1; i <= monthFew; i++) {
            // 获取前 i 月的开始时间
            String beginMouthTime = DateUtil.beginOfMonth(DateUtil.offsetMonth(new Date(), -i)).toString();
            // 获取前 i 月的结束时间
            String endMouthTime = DateUtil.endOfMonth(DateUtil.offsetMonth(new Date(), -i)).toString();
            // 因为月数是按时间顺序排序的，monthFew间隔-1，就是当前月的前 i 月 的数据
            pageStaticData.getDataList().get(monthFew - i).setCurrentNewCustomerCnt(weFlowerCustomerRelService.getCurrentNewCustomerCnt(corpId, beginMouthTime, endMouthTime, null));
        }
        return pageStaticData;
    }

    private WePageStaticDataDTO.PageStaticData setPageStaticData(WePageCountDTO nowTime, WePageCountDTO lastTime) {
        WePageStaticDataDTO.PageStaticData pageStaticData = new WePageStaticDataDTO.PageStaticData();

        pageStaticData.setNewApplyCnt(nowTime.getNewApplyCnt());
        pageStaticData.setNewApplyCntDiff(nowTime.getNewApplyCnt() - lastTime.getNewApplyCnt());
        pageStaticData.setNegativeFeedbackCnt(nowTime.getNegativeFeedbackCnt());
        pageStaticData.setNegativeFeedbackCntDiff(nowTime.getNegativeFeedbackCnt() - lastTime.getNegativeFeedbackCnt());
        pageStaticData.setNewContactCnt(nowTime.getNewContactCnt());
        pageStaticData.setNewContactCntDiff(nowTime.getNewContactCnt() - lastTime.getNewContactCnt());
        pageStaticData.setNewMemberCnt(nowTime.getNewMemberCnt());
        pageStaticData.setNewMemberCntDiff(nowTime.getNewMemberCnt() - lastTime.getNewMemberCnt());
        pageStaticData.setNewContactLossCnt(nowTime.getNewContactLossCnt());
        String newContactRetentionRate = genRetentionRate(nowTime.getNewContactCnt(), nowTime.getCurrentNewCustomerCnt());
        pageStaticData.setNewContactRetentionRate(newContactRetentionRate);
        pageStaticData.setNewContactRetentionRateDiff(genRetentionRateCliff(newContactRetentionRate, lastTime.getNewContactRetentionRate()));
        pageStaticData.setNewChatCnt(nowTime.getNewChatCnt());
        pageStaticData.setNewChatCntDiff(nowTime.getNewChatCnt() - lastTime.getNewChatCnt());
        pageStaticData.setCurrentNewCustomerCnt(nowTime.getCurrentNewCustomerCnt());
        return pageStaticData;
    }
}
