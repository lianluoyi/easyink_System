package com.easyink.wecom.service.impl.statistic;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.wecom.domain.WeEmpleCodeStatistic;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.dto.statistics.EmpleCodeStatisticDTO;
import com.easyink.wecom.domain.vo.statistics.emplecode.*;
import com.easyink.wecom.mapper.WeUserMapper;
import com.easyink.wecom.mapper.statistic.WeEmpleCodeStatisticMapper;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.statistic.WeEmpleCodeStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活码统计Service业务处理层
 *
 * @author lichaoyu
 * @date 2023/7/5 14:34
 */
@Slf4j
@Service
public class WeEmpleCodeStatisticImpl extends ServiceImpl<WeEmpleCodeStatisticMapper, WeEmpleCodeStatistic> implements WeEmpleCodeStatisticService {


    private final WeUserMapper weUserMapper;

    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
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

    public WeEmpleCodeStatisticImpl(WeUserMapper weUserMapper, WeFlowerCustomerRelService weFlowerCustomerRelService) {
        this.weUserMapper = weUserMapper;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
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
        EmpleCodeBaseVO empleCodeBaseVOS = this.baseMapper.listEmpleTotal(dto);
        int currentCustomerCnt = 0;
        // 不存在数据，返回默认值，而不是null
        if (empleCodeBaseVOS == null) {
            return new EmpleCodeBaseVO();
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
        empleCodeBaseVOS.setCurrentNewCustomerCnt(currentCustomerCnt);
        return empleCodeBaseVOS;
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
        if (CollectionUtils.isEmpty(statisticList) || CollectionUtils.isEmpty(accumulateRetainCntList)) {
            return getEmptyListByDate(dto);
        }
        // 组装累计客户数和留存客户总数
        for (EmpleCodeDateVO empleCodeDateVO : statisticList) {
            for (EmpleCodeDateVO codeDateVO : accumulateRetainCntList) {
                if (empleCodeDateVO.getTime().equals(codeDateVO.getTime())) {
                    empleCodeDateVO.setAccumulateCustomerCnt(codeDateVO.getAccumulateCustomerCnt());
                    empleCodeDateVO.setRetainCustomerCnt(codeDateVO.getRetainCustomerCnt());
                }
            }
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
