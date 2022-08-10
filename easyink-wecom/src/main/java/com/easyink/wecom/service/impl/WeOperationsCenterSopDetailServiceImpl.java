package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.WeOperationsCenterSopDetailEntity;
import com.easyink.wecom.domain.dto.groupsop.FindWeSopDetailDTO;
import com.easyink.wecom.domain.dto.groupsop.FindWeSopExecutedRulesDTO;
import com.easyink.wecom.domain.dto.groupsop.FindWeSopExecutedUsersDTO;
import com.easyink.wecom.domain.dto.groupsop.GetSopTaskDetailDTO;
import com.easyink.wecom.domain.vo.sop.*;
import com.easyink.wecom.mapper.WeOperationsCenterSopDetailMapper;
import com.easyink.wecom.service.WeOperationsCenterSopDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class WeOperationsCenterSopDetailServiceImpl extends ServiceImpl<WeOperationsCenterSopDetailMapper, WeOperationsCenterSopDetailEntity> implements WeOperationsCenterSopDetailService {


    /**
     * sop规则执行记录分页
     *
     * @param findWeSopExecutedRulesDTO
     * @return
     */
    @Override
    public List<WeSopExecutedRulesVO> getSopExecutedRulesBySopId(FindWeSopExecutedRulesDTO findWeSopExecutedRulesDTO) {
        if (StringUtils.isBlank(findWeSopExecutedRulesDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeSopExecutedRulesVO> weSopExecutedRulesVOList = this.baseMapper.selectSopExecutedRulesBySopId(findWeSopExecutedRulesDTO);
        // 处理一下执行率
        for (WeSopExecutedRulesVO rulesVO : weSopExecutedRulesVOList) {
            rulesVO.handleExecuteRate();
        }
        return weSopExecutedRulesVOList;
    }

    /**
     * sop员工执行记录分页
     *
     * @param findWeSopExecutedUsersDTO
     * @return
     */
    @Override
    public List<WeSopExecutedUsersVO> getSopExecutedUsersBySopId(FindWeSopExecutedUsersDTO findWeSopExecutedUsersDTO) {
        if (StringUtils.isBlank(findWeSopExecutedUsersDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeSopExecutedUsersVO> weSopExecutedUsersVOList = this.baseMapper.selectSopExecutedUsersBySopId(findWeSopExecutedUsersDTO);
        // 处理一下执行率
        for (WeSopExecutedUsersVO usersVO : weSopExecutedUsersVOList) {
            usersVO.handleExecuteRate();
        }
        return weSopExecutedUsersVOList;
    }

    /**
     * sop任务统计
     *
     * @param sopId  sopId
     * @param corpId 企业id
     * @return
     */
    @Override
    public WeSopTaskCountVO taskCount(String sopId, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeSopTaskCountVO taskCountVO = baseMapper.taskCount(sopId, corpId);
        taskCountVO.handleExecuteRate();
        return taskCountVO;
    }


    @Override
    public List<GetSopTaskByUserIdVO> getTaskDetailByUserId(GetSopTaskDetailDTO getSopTaskDetailDTO) {
        if (getSopTaskDetailDTO == null || StringUtils.isBlank(getSopTaskDetailDTO.getCorpId()) || StringUtils.isBlank(getSopTaskDetailDTO.getUserId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<GetSopTaskByUserIdVO> list = baseMapper.getSopTaskByUserId(getSopTaskDetailDTO.getCorpId(), getSopTaskDetailDTO.getUserId(), getSopTaskDetailDTO.getType(), getSopTaskDetailDTO.getDetailId(), getSopTaskDetailDTO.getIsFinish());
        for (GetSopTaskByUserIdVO task : list) {
            getSopTaskDetailDTO.setRuleId(task.getRuleId());
            getSopTaskDetailDTO.setStartTime(DateUtils.getSopTaskDateStart(task.getAlertDate(), task.getHour(), task.getMinute()));
            getSopTaskDetailDTO.setEndTime(DateUtils.getSopTaskDateEnd(task.getAlertDate(), task.getHour(), task.getMinute()));
            task.setDetailList(baseMapper.getTaskDetailByUserId(getSopTaskDetailDTO));
        }
        return list;
    }

    @Override
    public void finishTask(String corpId, Long detailId) {
        if (StringUtils.isBlank(corpId) || detailId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        baseMapper.finishTask(corpId, detailId, new Date());
    }

    /**
     * 查询客户SOP类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    @Override
    public List<WeOperationsCenterSopDetailCustomerVO> getSopDetailBySopIdWithCustomerType(FindWeSopDetailDTO findWeSopDetailDTO) {
        if (StringUtils.isBlank(findWeSopDetailDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //开始时间
        if (StringUtils.isNotBlank(findWeSopDetailDTO.getBeginTime())) {
            if (!DateUtils.isMatchFormat(findWeSopDetailDTO.getBeginTime(), DateUtils.YYYY_MM_DD)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            String beginDay = DateUtils.parseBeginDay(findWeSopDetailDTO.getBeginTime());
            findWeSopDetailDTO.setBeginTime(beginDay);
        }
        //结束时间
        if (StringUtils.isNotBlank(findWeSopDetailDTO.getEndTime())) {
            if (!DateUtils.isMatchFormat(findWeSopDetailDTO.getEndTime(), DateUtils.YYYY_MM_DD)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            String endDay = DateUtils.parseEndDay(findWeSopDetailDTO.getEndTime());
            findWeSopDetailDTO.setEndTime(endDay);
        }
        return baseMapper.selectSopDetailBySopIdWithCustomerType(findWeSopDetailDTO);
    }

    /**
     * 查询群SOP定时类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    @Override
    public List<WeOperationsCenterSopDetailByTimingTypeVO> getSopDetailBySopIdWithTimingType(FindWeSopDetailDTO findWeSopDetailDTO) {
        if (StringUtils.isBlank(findWeSopDetailDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //开始时间
        if (StringUtils.isNotBlank(findWeSopDetailDTO.getBeginTime())) {
            if (!DateUtils.isMatchFormat(findWeSopDetailDTO.getBeginTime(), DateUtils.YYYY_MM_DD)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            String beginDay = DateUtils.parseBeginDay(findWeSopDetailDTO.getBeginTime());
            findWeSopDetailDTO.setBeginTime(beginDay);
        }
        //结束时间
        if (StringUtils.isNotBlank(findWeSopDetailDTO.getEndTime())) {
            if (!DateUtils.isMatchFormat(findWeSopDetailDTO.getEndTime(), DateUtils.YYYY_MM_DD)) {
                throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
            }
            String endDay = DateUtils.parseEndDay(findWeSopDetailDTO.getEndTime());
            findWeSopDetailDTO.setEndTime(endDay);
        }
        return baseMapper.selectSopDetailBySopIdWithTimingType(findWeSopDetailDTO);
    }

    /**
     * 查询群SOP循环类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    @Override
    public List<WeOperationsCenterSopDetailByCycleTypeVO> getSopDetailBySopIdWithCycleType(FindWeSopDetailDTO findWeSopDetailDTO) {
        if (StringUtils.isBlank(findWeSopDetailDTO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return baseMapper.selectSopDetailBySopIdWithCycleType(findWeSopDetailDTO);
    }
}