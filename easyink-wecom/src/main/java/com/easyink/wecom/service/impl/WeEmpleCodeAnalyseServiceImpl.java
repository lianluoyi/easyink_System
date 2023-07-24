package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeEmpleCodeAnalyseTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeAnalyse;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeAnalyseDTO;
import com.easyink.wecom.domain.vo.WeEmplyCodeAnalyseCountVO;
import com.easyink.wecom.domain.vo.WeEmplyCodeAnalyseVO;
import com.easyink.wecom.mapper.*;
import com.easyink.wecom.service.WeEmpleCodeAnalyseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名：WeEmpleCodeAnalyseServiceImpl
 *
 * @author Society my sister Li
 * @date 2021-11-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeEmpleCodeAnalyseServiceImpl extends ServiceImpl<WeEmpleCodeAnalyseMapper, WeEmpleCodeAnalyse> implements WeEmpleCodeAnalyseService {

    private final WeEmpleCodeMapper weEmpleCodeMapper;

    private final WeUserMapper weUserMapper;

    private final WeDepartmentMapper weDepartmentMapper;

    private final WeEmpleCodeUseScopMapper weEmpleCodeUseScopMapper;

    private final WeEmpleCodeAnalyseMapper weEmpleCodeAnalyseMapper;

    @Override
    public WeEmplyCodeAnalyseVO getTimeRangeAnalyseCount(FindWeEmpleCodeAnalyseDTO analyseDTO) {
        if (analyseDTO == null || StringUtils.isBlank(analyseDTO.getCorpId()) || StringUtils.isBlank(analyseDTO.getState()) || StringUtils.isBlank(analyseDTO.getBeginTime()) || StringUtils.isBlank(analyseDTO.getEndTime())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //校验开始时间和结束时间格式
        if (Boolean.TRUE.equals(!DateUtils.isMatchFormat(analyseDTO.getBeginTime(), DateUtils.YYYY_MM_DD)) || Boolean.TRUE.equals(!DateUtils.isMatchFormat(analyseDTO.getEndTime(), DateUtils.YYYY_MM_DD))) {
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        //查询两个时间内的所有日期
        Date startTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, analyseDTO.getBeginTime());
        Date endTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD, analyseDTO.getEndTime());
        List<Date> dates = DateUtils.findDates(startTime, endTime);

        // 员工ID列表
        List<String> userIdList = new ArrayList<>();
        // 如果是部门查询
        if(StringUtils.isNotBlank(analyseDTO.getDepartmentId())){

            WeDepartment weDepartment = new WeDepartment();
            weDepartment.setId(Long.valueOf(analyseDTO.getDepartmentId()));
            weDepartment.setCorpId(analyseDTO.getCorpId());
            // 获取当前部门和所有子部门的部门ID列表
            String departmentList = weDepartmentMapper.selectDepartmentAndChild(weDepartment);
            List<String> departmentIdList = Arrays.stream(departmentList.split(",")).collect(Collectors.toList());
            // 根据部门ID获取当前部门下所有正常状态的员工userId
            userIdList = weUserMapper.listOfUserId(analyseDTO.getCorpId(), departmentIdList.toArray(new String[0]));
            analyseDTO.setUserIdList(userIdList);
        }
        //查询时间段内新增和流失客户数据
        List<WeEmplyCodeAnalyseCountVO> weEmpleCodeAnalyses = baseMapper.selectCountList(analyseDTO);

        //dataMap <time,WeEmplyCodeAnalyseCountVO>
        Map<Date, WeEmplyCodeAnalyseCountVO> dataMap = new HashMap<>(weEmpleCodeAnalyses.size());
        if (CollectionUtils.isNotEmpty(weEmpleCodeAnalyses)) {
            for (WeEmplyCodeAnalyseCountVO weEmplyCodeAnalyseCountVO : weEmpleCodeAnalyses) {
                dataMap.put(weEmplyCodeAnalyseCountVO.getTime(), weEmplyCodeAnalyseCountVO);
            }
        }
        List<WeEmplyCodeAnalyseCountVO> resultList = new ArrayList<>();
        WeEmplyCodeAnalyseCountVO analyseCountVO;
        for (Date date : dates) {
            analyseCountVO = dataMap.get(date);
            //初始化addCount=0、loseCount=0的数据
            if (analyseCountVO == null) {
                analyseCountVO = new WeEmplyCodeAnalyseCountVO();
                analyseCountVO.setTime(date);
            }
            resultList.add(analyseCountVO);
        }
        int total = getAddCountByState(analyseDTO.getState());
        // 部门查询下，若查询的部门内未拥有员工，返回空数据，但统计总数依旧返回
        if(CollectionUtils.isEmpty(userIdList) && StringUtils.isNotBlank(analyseDTO.getDepartmentId())){
            List<WeEmplyCodeAnalyseCountVO> result = new ArrayList<>();
            for (Date date : dates) {
                analyseCountVO = new WeEmplyCodeAnalyseCountVO();
                analyseCountVO.setTime(date);
                result.add(analyseCountVO);
            }
            return new WeEmplyCodeAnalyseVO(result, total);
        }
        return new WeEmplyCodeAnalyseVO(resultList,total);
    }

    @Override
    public AjaxResult exportTimeRangeAnalyseCount(FindWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO) {
        WeEmpleCode weEmpleCode = weEmpleCodeMapper.selectById(findWeEmpleCodeAnalyseDTO.getState());
        if (weEmpleCode == null || StringUtils.isBlank(weEmpleCode.getScenario())) {
            throw new CustomException(ResultTip.TIP_EMPLY_CODE_NOT_FOUND);
        }
        WeEmplyCodeAnalyseVO timeRangeAnalyseCount = getTimeRangeAnalyseCount(findWeEmpleCodeAnalyseDTO);
        if (timeRangeAnalyseCount == null || CollectionUtils.isEmpty(timeRangeAnalyseCount.getList())) {
            throw new CustomException(ResultTip.TIP_NO_DATA_TO_EXPORT);
        }
        String sheetName = weEmpleCode.getScenario() + "的数据详情";
        List<WeEmplyCodeAnalyseCountVO> list = timeRangeAnalyseCount.getList();
        ExcelUtil<WeEmplyCodeAnalyseCountVO> util = new ExcelUtil<>(WeEmplyCodeAnalyseCountVO.class);
        return util.exportExcel(list, sheetName);
    }

    @Override
    public int getAddCountByState(String state){
        if(StringUtils.isBlank(state)){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeEmpleCodeAnalyse> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(WeEmpleCodeAnalyse::getEmpleCodeId, state)
                .eq(WeEmpleCodeAnalyse::getType, WeEmpleCodeAnalyseTypeEnum.ADD.getType());
        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * 获取企业下所有活码-员工对应的统计数据
     *
     * @param corpId          企业ID
     * @param date            日期 格式为YYYY-MM-DD
     * @param empleCodeIdList 活码ID列表
     * @return 统计数据
     */
    @Override
    public List<WeEmpleCodeStatistic> getEmpleStatisticData(String corpId, String date, List<Long> empleCodeIdList) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(date)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        // 初始化统计表数据
        List<WeEmpleCodeStatistic> resultList = initData(corpId, empleCodeIdList, date);
        // 获取企业下所有活码-员工对应的新增客户数和流失客户数
        List<WeEmpleCodeStatistic> newAndLossCntList = baseMapper.getEmpleStatisticDateData(corpId, date, empleCodeIdList);
        // 获取企业下所有活码-员工对应的累计客户数
        List<WeEmpleCodeStatistic> accumulateList = baseMapper.getAccumulateData(corpId, date, empleCodeIdList);
        // 获取企业下所有活码-员工对应的留存客户数总数
        List<WeEmpleCodeStatistic> retainCntList = baseMapper.getRetainData(corpId, date, empleCodeIdList);
        // 处理数据
        resultList.forEach(result -> {
            // 将新增客户数和流失客户数设置到对应的活码中,为活码id相同且userid相同的员工设置新增客户数和流失客户数
            newAndLossCntList.stream().filter(newAndLossItem -> result.getEmpleCodeId().equals(newAndLossItem.getEmpleCodeId()) && result.getUserId().equals(newAndLossItem.getUserId()))
                    .findFirst()
                    .ifPresent(empleCodeStatistic -> {
                        result.setNewCustomerCnt(empleCodeStatistic.getNewCustomerCnt());
                        result.setLossCustomerCnt(empleCodeStatistic.getLossCustomerCnt());
                    });
            // 将累计客户数设置到对应的活码中,为活码id相同且userid相同的员工设置累计客户数
            accumulateList.stream().filter(accumulateItem -> result.getEmpleCodeId().equals(accumulateItem.getEmpleCodeId()) && result.getUserId().equals(accumulateItem.getUserId()))
                    .findFirst()
                    .ifPresent(empleCodeStatistic -> result.setAccumulateCustomerCnt(empleCodeStatistic.getAccumulateCustomerCnt()));
            // 将留存客户总数设置到对应的活码中,为活码id相同且userid相同的员工设置留存客户总数
            retainCntList.stream().filter(retainItem -> result.getEmpleCodeId().equals(retainItem.getEmpleCodeId()) && result.getUserId().equals(retainItem.getUserId()))
                    .findFirst()
                    .ifPresent(empleCodeStatistic -> result.setRetainCustomerCnt(empleCodeStatistic.getRetainCustomerCnt()));
        });
        return resultList;
    }

    /**
     * 初始化数据
     *
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @param date 日期，格式为 YYYY-MM-DD
     * @return 初始化数据
     */
    @Override
    public List<WeEmpleCodeStatistic> initData(String corpId, List<Long> empleCodeIdList, String date) {
        List<WeEmpleCodeStatistic> resultList = new ArrayList<>();
        // 查询使用人
        List<WeEmpleCodeUseScop> useScopeList = weEmpleCodeUseScopMapper.selectWeEmpleCodeUseScopListByIds(empleCodeIdList, corpId);
        // 查询使用部门(查询使用人时需要用businessId关联we_user表，活码使用部门时不传入businessId)
        List<WeEmpleCodeUseScop> departmentScopeList = weEmpleCodeUseScopMapper.selectDepartmentWeEmpleCodeUseScopListByIds(empleCodeIdList);
        for (Long empleCodeId : empleCodeIdList) {
            // 使用人，部门不存在则跳过当前活码
            if (CollectionUtils.isEmpty(useScopeList) && CollectionUtils.isEmpty(departmentScopeList)) {
                continue;
            }
            // 获取当前活码对应的userId
            List<String> userIds = getUserIds(useScopeList.stream().filter(item -> item.getEmpleCodeId().equals(empleCodeId)).collect(Collectors.toList()),
                    departmentScopeList.stream().filter(item -> item.getEmpleCodeId().equals(empleCodeId)).collect(Collectors.toList()), corpId, empleCodeId);
            // 获取不到userId则跳过当前活码
            if (CollectionUtils.isEmpty(userIds)) {
                continue;
            }
            // 根据userId，初始化统计数据
            for (String userId : userIds) {
                WeEmpleCodeStatistic data = new WeEmpleCodeStatistic(corpId, empleCodeId, userId, date);
                resultList.add(data);
            }
        }
        return resultList;
    }

    /**
     * 根据EmpleCodeId获取UserId
     *
     * @param corpId 企业ID
     * @param empleCodeIdList 活码ID列表
     * @return userid列表
     */
    @Override
    public List<String> selectUserIds(String corpId, List<Long> empleCodeIdList) {
        HashSet<String> userIds = new HashSet<>();
        // 查询使用人
        List<WeEmpleCodeUseScop> useScopeList = weEmpleCodeUseScopMapper.selectWeEmpleCodeUseScopListByIds(empleCodeIdList, corpId);
        // 查询使用部门(查询使用人时需要用businessId关联we_user表，活码使用部门时不传入businessId)
        List<WeEmpleCodeUseScop> departmentScopeList = weEmpleCodeUseScopMapper.selectDepartmentWeEmpleCodeUseScopListByIds(empleCodeIdList);
        for (Long empleCodeId : empleCodeIdList) {
            // 使用人，部门不存在则跳过当前活码
            if (CollectionUtils.isEmpty(useScopeList) && CollectionUtils.isEmpty(departmentScopeList)) {
                continue;
            }
            // 获取当前活码对应的userId
            userIds.addAll(
                    getUserIds(useScopeList.stream()
                                           .filter(item -> item.getEmpleCodeId().equals(empleCodeId))
                                           .collect(Collectors.toList()),
                               departmentScopeList.stream()
                                                  .filter(item -> item.getEmpleCodeId().equals(empleCodeId))
                                                  .collect(Collectors.toList()),
                    corpId,
                    empleCodeId)
            );
        }
        return new ArrayList<>(userIds);
    }

    /**
     * 根据使用人和部门范围获取UserId
     *
     * @param useScopeList 使用人范围列表
     * @param departmentScopeList 部门范围列表
     * @param corpId 企业ID
     * @param empleCodeId 活码ID
     * @return userId 列表
     */
    private List<String> getUserIds(List<WeEmpleCodeUseScop> useScopeList, List<WeEmpleCodeUseScop> departmentScopeList, String corpId, Long empleCodeId) {
        // 去重处理
        HashSet<String> userIds = new HashSet<>();
        // 从活码分析表获取活码员工ID
        List<String> analysesUserIds = weEmpleCodeAnalyseMapper.selectUserIdsById(corpId, empleCodeId);
        // 添加userId
        if (CollectionUtils.isNotEmpty(analysesUserIds)) {
            userIds.addAll(analysesUserIds);
        }
        // 从使用人获取userId
        if (CollectionUtils.isNotEmpty(useScopeList)) {
            // 添加userId
            userIds.addAll(useScopeList.stream().map(WeEmpleCodeUseScop::getBusinessId).collect(Collectors.toList()));
        }
        // 从部门获取userId
        if (CollectionUtils.isNotEmpty(departmentScopeList)) {
            // 获取部门id
            List<String> departmentIds = departmentScopeList.stream().map(WeEmpleCodeUseScop::getBusinessId).collect(Collectors.toList());
            // 根据部门获取员工信息
            List<WeUser> weUserList = weUserMapper.getUserByDepartmentList(corpId, departmentIds.toArray(new String[0]));
            // 部门下没有userId，则直接返回上一步获取的userId列表
            if (CollectionUtils.isEmpty(weUserList)) {
                return new ArrayList<>(userIds);
            }
            // 添加userId
            userIds.addAll(weUserList.stream().map(WeUser::getUserId).collect(Collectors.toList()));
        }
        return new ArrayList<>(userIds);
    }

    /**
     * 保存员工活码数据统计
     */
    @Override
    public boolean saveWeEmpleCodeAnalyse(String corpId, String userId, String externalUserId, String state, Boolean addFlag) {
        try {
            if (StringUtils.isBlank(corpId) || StringUtils.isBlank(userId) || StringUtils.isBlank(externalUserId) || StringUtils.isBlank(state) || addFlag == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }

            WeEmpleCodeAnalyse weEmpleCodeAnalyse = new WeEmpleCodeAnalyse();
            weEmpleCodeAnalyse.setCorpId(corpId);
            weEmpleCodeAnalyse.setEmpleCodeId(Long.parseLong(state));
            weEmpleCodeAnalyse.setUserId(userId);
            weEmpleCodeAnalyse.setExternalUserId(externalUserId);
            weEmpleCodeAnalyse.setTime(new Date());
            weEmpleCodeAnalyse.setType(addFlag);
            baseMapper.insert(weEmpleCodeAnalyse);
            return true;
        } catch (Exception e) {
            log.error("saveWeEmpleCodeAnalyse error!! e={}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}
