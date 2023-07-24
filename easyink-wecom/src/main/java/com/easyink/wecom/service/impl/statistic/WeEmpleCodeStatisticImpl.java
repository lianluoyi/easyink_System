package com.easyink.wecom.service.impl.statistic;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.ExceptionUtil;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeEmpleCodeAnalyse;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.statistics.EmpleCodeStatisticDTO;
import com.easyink.wecom.domain.redis.RedisEmpleStatisticBaseModel;
import com.easyink.wecom.domain.vo.statistics.emplecode.*;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeEmpleCodeAnalyseService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.statistic.WeEmpleCodeStatisticService;
import com.easyink.wecom.utils.redis.EmpleStatisticRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活码统计Service业务处理层
 *
 * @author lichaoyu
 * @date 2023/7/5 14:34
 */
@Slf4j
@Service
public class WeEmpleCodeStatisticImpl extends ServiceImpl<WeEmpleCodeStatisticMapper, WeEmpleCodeStatistic> implements WeEmpleCodeStatisticService{


    private final WeUserMapper weUserMapper;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    @Resource(name = "empleStatisticRedisCache")
    private EmpleStatisticRedisCache empleStatisticRedisCache;
    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;
    private final WeEmpleCodeService weEmpleCodeService;

    private final WeCorpAccountService weCorpAccountService;

    /**
     * 活码维度报表名称
     */
    private final String EMPLE_NAME = "活码统计报表（活码维度）";
    /**
     * 员工维度报表名称
     */
    private final String EMPLE_USER_NAME = "活码统计报表（员工维度）";
    /**
     * 日期维度报表名称
     */
    private final String EMPLE_DATE_NAME = "活码统计报表（日期维度）";

    @Lazy
    public WeEmpleCodeStatisticImpl(WeUserMapper weUserMapper, WeFlowerCustomerRelService weFlowerCustomerRelService, WeEmpleCodeAnalyseService weEmpleCodeAnalyseService, WeEmpleCodeService weEmpleCodeService, WeCorpAccountService weCorpAccountService) {
        this.weUserMapper = weUserMapper;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
        this.weEmpleCodeService = weEmpleCodeService;
        this.weCorpAccountService = weCorpAccountService;
    }

    /**
     * 活码统计-数据总览
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeBaseVO}
     */
    @Override
    public EmpleCodeBaseVO listEmpleTotal(EmpleCodeStatisticDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 设置部门下的员工id列表
        setUserListByDepartment(dto);
        // 获取基础数据
        EmpleCodeBaseVO baseVO = this.baseMapper.listEmpleTotal(dto);
        int currentCustomerCnt = 0;
        // 不存在数据，返回默认值，而不是null
        if (baseVO == null) {
            return new EmpleCodeBaseVO();
        }
        if (needRedisData(dto.getEndDate())) {
            // 处理缓存数据
            handleTotalRedisData(dto.getEndDate(), dto.getCorpId(), dto.getEmpleCodeIdList(), dto.getUserIds(), baseVO);
        }
        // 存在数据，则查询活码对应的截止当前时间下，新增客户数
        LambdaQueryWrapper<WeFlowerCustomerRel> queryWrapper = new LambdaQueryWrapper<>();
        // 统计we_flower_customer_rel表中state = 活码id，status != 1,2，时间为截止日期（YYYY-MM-DD HH:MM:DD） 的记录数
        queryWrapper.eq(WeFlowerCustomerRel::getCorpId, dto.getCorpId())
                .notIn(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.DRAIN.getCode(), CustomerStatusEnum.DELETE.getCode())
                .gt(WeFlowerCustomerRel::getCreateTime, DateUtils.parseBeginDay(dto.getBeginDate()))
                .lt(WeFlowerCustomerRel::getCreateTime, DateUtils.parseEndDay(dto.getEndDate()));
        if (CollectionUtils.isNotEmpty(dto.getEmpleCodeIdList())) {
            queryWrapper.in(WeFlowerCustomerRel::getState, dto.getEmpleCodeIdList());
        }
        if (CollectionUtils.isNotEmpty(dto.getUserIds())) {
            queryWrapper.in(WeFlowerCustomerRel::getUserId, dto.getUserIds());
        }
        currentCustomerCnt = weFlowerCustomerRelService.count(queryWrapper);
        // 设置截止当前时间下的新增客户数
        baseVO.setCurrentNewCustomerCnt(currentCustomerCnt);
        return baseVO;
    }

    /**
     * 活码统计-员工维度
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    @Override
    public List<EmpleCodeUserVO> listEmpleUser(EmpleCodeStatisticDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 活码id为空，无查询数据
        if (CollectionUtils.isEmpty(dto.getEmpleCodeIdList())) {
            return new ArrayList<>();
        }
        // 设置部门下的员工id列表
        setUserListByDepartment(dto);
        // 获取统计数据
        List<EmpleCodeUserVO> resultList = this.baseMapper.listEmpleUser(dto);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        // 判断是否需要查询redis的数据
        if (needRedisData(dto.getEndDate())) {
            // 处理缓存的数据
            handleUserRedisData(dto.getEndDate(), dto.getCorpId(), dto.getEmpleCodeIdList(), resultList);
        }
        // 获取截止当前时间，员工对应的新增客户数
        List<EmpleCodeUserVO> userCustomerRels = this.baseMapper.listUserCustomerRel(dto.getCorpId(), dto.getEmpleCodeIdList(), dto.getUserIds(), DateUtils.parseEndDay(dto.getEndDate()), DateUtils.parseBeginDay(dto.getBeginDate()));
        if (CollectionUtils.isNotEmpty(userCustomerRels)) {
            // 为对应的员工设置截止当前时间，员工对应的新增客户数
            for (EmpleCodeUserVO empleCodeUserVO : resultList) {
                for (EmpleCodeUserVO userCustomerRel : userCustomerRels) {
                    if (empleCodeUserVO.getUserId().equals(userCustomerRel.getUserId())) {
                        empleCodeUserVO.setCurrentNewCustomerCnt(userCustomerRel.getCurrentNewCustomerCnt());
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 活码统计-员工维度-导出报表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 报表
     */
    @Override
    public AjaxResult exportEmpleUser(EmpleCodeStatisticDTO dto) {
        List<EmpleCodeUserVO> list = this.listEmpleUser(dto);
        // 导出
        list.forEach(EmpleCodeUserVO::bindExportData);
        ExcelUtil<EmpleCodeUserVO> util = new ExcelUtil<>(EmpleCodeUserVO.class);
        return util.exportExcel(list, EMPLE_USER_NAME);
    }

    /**
     * 活码统计-活码维度
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    @Override
    public List<EmpleCodeVO> listEmple(EmpleCodeStatisticDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (CollectionUtils.isEmpty(dto.getEmpleCodeIdList())) {
            return new ArrayList<>();
        }
        // 设置部门下的员工id列表
        setUserListByDepartment(dto);
        // 获取统计数据
        List<EmpleCodeVO> resultList = this.baseMapper.listEmple(dto);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        // 是否需要Redis处理
        if (needRedisData(dto.getEndDate())) {
            // 处理缓存的数据
            handleEmpleRedisData(dto.getEndDate(), dto.getCorpId(), dto.getEmpleCodeIdList(), resultList, dto.getUserIds());
        }
        // 获取截止查询时间，活码对应的新增客户数
        List<EmpleCodeVO> empleUserCustomerRels = this.baseMapper.listStateUserCustomerRel(dto.getCorpId(), dto.getEmpleCodeIdList(), dto.getUserIds(), DateUtils.parseEndDay(dto.getEndDate()), DateUtils.parseBeginDay(dto.getBeginDate()));
        if (CollectionUtils.isNotEmpty(empleUserCustomerRels)) {
            // 根据活码id为对应的活码设置截止当前时间，员工对应的新增客户数
            for (EmpleCodeVO empleCodeVO : resultList) {
                for (EmpleCodeVO empleRels : empleUserCustomerRels) {
                    if (empleCodeVO.getEmpleCodeId().equals(empleRels.getEmpleCodeId())) {
                        empleCodeVO.setCurrentNewCustomerCnt(empleRels.getCurrentNewCustomerCnt());
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 活码统计-活码维度-导出报表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 报表
     */
    @Override
    public AjaxResult exportEmple(EmpleCodeStatisticDTO dto) {
        List<EmpleCodeVO> list = this.listEmple(dto);
        // 导出
        list.forEach(EmpleCodeVO::bindExportData);
        ExcelUtil<EmpleCodeVO> util = new ExcelUtil<>(EmpleCodeVO.class);
        return util.exportExcel(list, EMPLE_NAME);
    }

    /**
     * 活码统计-日期维度
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return {@link EmpleCodeUserVO}
     */
    @Override
    public List<EmpleCodeDateVO> listEmpleDate(EmpleCodeStatisticDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (CollectionUtils.isEmpty(dto.getEmpleCodeIdList())) {
            return new ArrayList<>();
        }
        // 设置部门下的员工id列表
        setUserListByDepartment(dto);
        // 获取统计数据
        List<EmpleCodeDateVO> statisticList = this.baseMapper.listEmpleDate(dto);
        List<EmpleCodeDateVO> accumulateRetainCntList = this.baseMapper.listDateAccumulateRetainCnt(dto);
        // 组装累计客户数和留存客户总数
        if (CollectionUtils.isEmpty(statisticList) || CollectionUtils.isEmpty(accumulateRetainCntList)) {
            return getEmptyListByDate(dto);
        }
        for (EmpleCodeDateVO empleCodeDateVO : statisticList) {
            for (EmpleCodeDateVO codeDateVO : accumulateRetainCntList) {
                if (empleCodeDateVO.getTime().equals(codeDateVO.getTime())) {
                    empleCodeDateVO.setAccumulateCustomerCnt(codeDateVO.getAccumulateCustomerCnt());
                    empleCodeDateVO.setRetainCustomerCnt(codeDateVO.getRetainCustomerCnt());
                }
            }
        }
        if (needRedisData(dto.getEndDate())) {
            // 处理缓存数据
            handleDateRedisData(dto.getEndDate(), dto.getCorpId(), dto.getEmpleCodeIdList(), dto.getUserIds(), statisticList);
        }
        // 获取截止当前时间，日期下，所有的活码对应的新增客户数
        List<EmpleCodeDateVO> empleUserCustomerRels = this.baseMapper.listEmpleDateUserCustomerRel(dto.getCorpId(), dto.getEmpleCodeIdList(), dto.getUserIds(), DateUtils.parseEndDay(dto.getEndDate()));
        // 为对应的日期设置截止当前时间，员工对应的新增客户数
        if (CollectionUtils.isNotEmpty(empleUserCustomerRels)) {
            for (EmpleCodeDateVO empleCodeDateVO : statisticList) {
                for (EmpleCodeDateVO empleDateRels : empleUserCustomerRels) {
                    if (empleCodeDateVO.getTime().equals(empleDateRels.getTime())) {
                        empleCodeDateVO.setCurrentNewCustomerCnt(empleDateRels.getCurrentNewCustomerCnt());
                    }
                }
            }
        }
        // 组装结果数据
        List<EmpleCodeDateVO> resultList = handleResultList(statisticList, dto);
        return resultList;
    }

    /**
     * 组装结果数据
     *
     * @param statisticList {@link EmpleCodeDateVO}
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 结果
     */
    private List<EmpleCodeDateVO> handleResultList(List<EmpleCodeDateVO> statisticList, EmpleCodeStatisticDTO dto) {
        // 结果数据
        List<EmpleCodeDateVO> resultList = getEmptyListByDate(dto);
        // 组装数据
        for (EmpleCodeDateVO empleCodeDateVO : resultList) {
            for (EmpleCodeDateVO codeDateVO : statisticList) {
                if (empleCodeDateVO.getTime().equals(codeDateVO.getTime())) {
                    empleCodeDateVO.handleData(
                            codeDateVO.getAccumulateCustomerCnt(),
                            codeDateVO.getRetainCustomerCnt(),
                            codeDateVO.getNewCustomerCnt(),
                            codeDateVO.getLossCustomerCnt(),
                            codeDateVO.getCurrentNewCustomerCnt());
                }
            }
        }
        return resultList;
    }

    /**
     * 根据日期获取默认空数据列表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 日期范围下的空数据列表
     */
    private List<EmpleCodeDateVO> getEmptyListByDate(EmpleCodeStatisticDTO dto) {
        List<EmpleCodeDateVO> empleCodeDateVOList = new ArrayList<>();
        // 获取时间范围内的所有日期
        Date beginDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getBeginDate());
        Date endDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD, dto.getEndDate());
        List<Date> dates = DateUtils.findDates(beginDate, endDate);
        for (Date date : dates) {
            EmpleCodeDateVO empleCodeDateVO = new EmpleCodeDateVO(DateUtils.dateTime(date));
            empleCodeDateVOList.add(empleCodeDateVO);
        }
        // 默认按照时间倒序排序
        empleCodeDateVOList.sort(Comparator.comparing(EmpleCodeDateVO::getTime).reversed());
        return empleCodeDateVOList;
    }

    /**
     * 从活码分析表更新活码统计表历史旧数据
     *
     * @return 结果
     */
    @Override
    public AjaxResult updateHistoryData() {
        List<WeCorpAccount> weCorpAccounts = weCorpAccountService.list(new LambdaQueryWrapper<WeCorpAccount>().select(WeCorpAccount::getCorpId));
        List<String> corpIdList = weCorpAccounts.stream().map(WeCorpAccount::getCorpId).collect(Collectors.toList());
        for (String corpId : corpIdList) {
            // 获取分析表中的存在有数据的时间
            WeEmpleCodeAnalyse analyse = weEmpleCodeAnalyseService.getOne(new LambdaQueryWrapper<WeEmpleCodeAnalyse>()
                    .select(WeEmpleCodeAnalyse::getTime)
                    .eq(WeEmpleCodeAnalyse::getCorpId, corpId)
                    .orderByAsc(WeEmpleCodeAnalyse::getTime)
                    .last(GenConstants.LIMIT_1)
            );
            // 为空则跳过
            if (analyse == null) {
                continue;
            }
            // 获取分析表中最早的日期，到昨天的日期之间的时间范围
            List<Date> dates = DateUtils.findDates(analyse.getTime(), DateUtils.getYesterday(new Date()));
            // 循环更新旧数据
            for (Date date : dates) {
                // 转换格式为YYYY-MM-DD
                String dateTime = DateUtils.dateTime(date);
                try {
                    // 获取企业对应日期前创建的有效的活码ID列表
                    List<Long> effectEmpleCodeIdList = weEmpleCodeService.getEffectEmpleCodeId(corpId, dateTime);
                    // 根据日期，有效的活码id，获取企业的历史的活码统计数据
                    List<WeEmpleCodeStatistic> historyList = weEmpleCodeAnalyseService.getEmpleStatisticData(corpId, dateTime, effectEmpleCodeIdList);
                    // 分批批量插入或更新今天的数据
                    BatchInsertUtil.doInsert(historyList, list -> this.baseMapper.batchInsertOrUpdate(historyList));
                    log.info("[活码统计] 历史迁移数据完成，date:{}, corpId:{}", dateTime, corpId);
                } catch (Exception e) {
                    log.info("[活码统计] 历史迁移数据异常，date:{}, corpId:{}, ex:{}", dateTime, corpId, ExceptionUtil.getExceptionMessage(e));
                }
            }
        }
        return AjaxResult.success();
    }

    /**
     * 活码统计-日期维度-导出报表
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     * @return 报表
     */
    @Override
    public AjaxResult exportEmpleDate(EmpleCodeStatisticDTO dto) {
        List<EmpleCodeDateVO> list = this.listEmpleDate(dto);
        // 导出
        list.forEach(EmpleCodeDateVO::bindExportData);
        ExcelUtil<EmpleCodeDateVO> util = new ExcelUtil<>(EmpleCodeDateVO.class);
        return util.exportExcel(list, EMPLE_DATE_NAME);
    }

    /**
     * 处理日期维度-缓存的数据
     *
     * @param date 日期，格式为YYYY-MM-DD
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @param userIdList 员工ID列表
     * @param statisticList 结果数据
     */
    private void handleDateRedisData(String date, String corpId, List<Long> empleCodeIdList, List<String> userIdList, List<EmpleCodeDateVO> statisticList) {
        if (StringUtils.isAnyBlank(date, corpId) || CollectionUtils.isEmpty(empleCodeIdList) || CollectionUtils.isEmpty(statisticList)) {
            return;
        }
        // 实际的UserID
        List<String> realUserIdList = filterUserId(corpId, empleCodeIdList, userIdList);
        // 获取缓存数据
        RedisEmpleStatisticBaseModel redisData = empleStatisticRedisCache.getBatchDateValue(corpId, date, empleCodeIdList, realUserIdList);
        if (redisData == null) {
            return;
        }
        // 处理缓存数据
        for (EmpleCodeDateVO todayVO : statisticList) {
            if (todayVO.getTime().equals(date)) {
                // 处理Redis数据
                todayVO.handleRedisData(redisData.getNewCustomerCnt(), redisData.getLossCustomerCnt());
            }
        }
    }

    /**
     * 处理数据总览-缓存的数据
     *
     * @param date 日期，格式为YYYY-MM-DD
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @param userIdList 员工ID列表
     * @param baseVO 结果数据
     */
    private void handleTotalRedisData(String date, String corpId, List<Long> empleCodeIdList, List<String> userIdList, EmpleCodeBaseVO baseVO) {
        // 判断是否需要查询redis的数据
        if (StringUtils.isAnyBlank(date, corpId) || CollectionUtils.isEmpty(empleCodeIdList) || baseVO == null) {
            return;
        }
        // 实际的UserID
        List<String> realUserIdList = filterUserId(corpId, empleCodeIdList, userIdList);
        // 获取redis存放的数据
        RedisEmpleStatisticBaseModel redisData = empleStatisticRedisCache.getBatchDateValue(corpId, date, empleCodeIdList, realUserIdList);
        if (redisData == null) {
            return;
        }
        // 组装今日的数据
        baseVO.handleRedisData(redisData.getNewCustomerCnt(), redisData.getLossCustomerCnt());
    }

    /**
     * 处理活码维度-缓存的数据
     *
     * @param date 日期，格式为YYYY-MM-DD
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @param resultList 结果列表
     */
    private void handleEmpleRedisData(String date, String corpId, List<Long> empleCodeIdList, List<EmpleCodeVO> resultList, List<String> userIdList) {
        if (StringUtils.isAnyBlank(date, corpId) || CollectionUtils.isEmpty(empleCodeIdList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        // 获取活码对应的userId
        List<Long> realEmpleCodeIdList = resultList.stream().map(empleCodeVO -> Long.valueOf(empleCodeVO.getEmpleCodeId())).collect(Collectors.toList());
        // 实际的员工ID
        List<String> realUserIdList = filterUserId(corpId, realEmpleCodeIdList, userIdList);
        // redis存放的数据
        List<WeEmpleCodeStatistic> redisDataList = empleStatisticRedisCache.getBatchEmpleValue(corpId, date, empleCodeIdList, realUserIdList);
        // 处理缓存的数据
        if (CollectionUtils.isEmpty(redisDataList)) {
            return;
        }
        // 组装数据
        for (EmpleCodeVO resultData : resultList) {
            for (WeEmpleCodeStatistic redisData : redisDataList) {
                // 组装今日的数据
                if (resultData.getEmpleCodeId().equals(redisData.getEmpleCodeId().toString())) {
                    // 处理Redis数据
                    resultData.handleRedisData(redisData.getNewCustomerCnt(), redisData.getLossCustomerCnt());
                }
            }
        }
    }

    /**
     * 处理员工维度-缓存的数据
     *
     * @param date 日期，格式为YYYY-MM-DD
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @param resultList 结果列表
     */
    private void handleUserRedisData(String date, String corpId, List<Long> empleCodeIdList, List<EmpleCodeUserVO> resultList) {
        if (StringUtils.isAnyBlank(date, corpId) || CollectionUtils.isEmpty(empleCodeIdList) || CollectionUtils.isEmpty(resultList)) {
            return;
        }
        // 获取数据库查询出的userId
        List<String> realUserList = resultList.stream().map(EmpleCodeUserVO::getUserId).collect(Collectors.toList());
        // 根据数据库查出的UserId去Redis查询数据
        List<WeEmpleCodeStatistic> redisDataList = empleStatisticRedisCache.getBatchUserValue(corpId, date, realUserList, empleCodeIdList);
        if (CollectionUtils.isEmpty(redisDataList)) {
            return;
        }
        // 组装数据
        for (EmpleCodeUserVO resultData : resultList) {
            for (WeEmpleCodeStatistic redisData : redisDataList) {
                // 组装今日的数据
                if (redisData.getUserId().equals(resultData.getUserId())) {
                    // 处理Redis中的数据
                    resultData.handleUserRedisData(redisData.getNewCustomerCnt(), redisData.getLossCustomerCnt());
                }
            }
        }
    }

    /**
     * 过滤无效的userId
     *
     * @param corpId
     * @param empleCodeIdList
     * @param userIdList
     * @return userId列表
     */
    private List<String> filterUserId(String corpId, List<Long> empleCodeIdList, List<String> userIdList) {
        List<String> filterList = weEmpleCodeAnalyseService.selectUserIds(corpId, empleCodeIdList);
        if (CollectionUtils.isEmpty(filterList)) {
            return new ArrayList<>();
        }
        // 不存在查询条件，则返回
        if (CollectionUtils.isEmpty(userIdList)) {
            return filterList;
        }
        // 取交集，返回
        return userIdList.stream().filter(filterList::contains).collect(Collectors.toList());
    }

    /**
     * 是否需要拼接查询Redis中的数据
     *
     * @param date 日期 格式：YYYY-MM-DD
     * @return true 需要， false 不需要
     */
    private boolean needRedisData(String date) {
        if (StringUtils.isBlank(date)) {
            return false;
        }
        String today = DateUtils.dateTime(new Date());
        // 结束日期是否等于当天时间
        return date.equals(today);
    }

    /**
     * 当部门条件存在时，添加设置部门下的userId
     *
     * @param dto {@link EmpleCodeStatisticDTO}
     */
    private void setUserListByDepartment(EmpleCodeStatisticDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getDepartmentIds())) {
            return;
        }
        // 根据部门获取userid
        List<WeUser> weUserList = weUserMapper.getUserByDepartmentList(dto.getCorpId(), dto.getDepartmentIds().toArray(new String[0]));
        if (CollectionUtils.isEmpty(weUserList)) {
            return;
        }
        // 添加到userid列表中
        if (CollectionUtils.isNotEmpty(dto.getUserIds())) {
            dto.getUserIds().addAll(weUserList.stream().map(WeUser::getUserId).collect(Collectors.toList()));
        } else {
            dto.setUserIds(weUserList.stream().map(WeUser::getUserId).collect(Collectors.toList()));
        }
    }
}
