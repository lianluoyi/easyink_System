package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeOperationsCenterSopDetailEntity;
import com.easywecom.wecom.domain.dto.groupsop.FindWeSopDetailDTO;
import com.easywecom.wecom.domain.dto.groupsop.FindWeSopExecutedRulesDTO;
import com.easywecom.wecom.domain.dto.groupsop.FindWeSopExecutedUsersDTO;
import com.easywecom.wecom.domain.dto.groupsop.GetSopTaskDetailDTO;
import com.easywecom.wecom.domain.vo.sop.*;

import java.util.List;

/**
 * 类名： SOP详情接口
 *
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
public interface WeOperationsCenterSopDetailService extends IService<WeOperationsCenterSopDetailEntity> {
    /**
     * 查询客户SOP类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    List<WeOperationsCenterSopDetailCustomerVO> getSopDetailBySopIdWithCustomerType(FindWeSopDetailDTO findWeSopDetailDTO);

    /**
     * 查询群SOP定时类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    List<WeOperationsCenterSopDetailByTimingTypeVO> getSopDetailBySopIdWithTimingType(FindWeSopDetailDTO findWeSopDetailDTO);

    /**
     * 查询群SOP循环类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    List<WeOperationsCenterSopDetailByCycleTypeVO> getSopDetailBySopIdWithCycleType(FindWeSopDetailDTO findWeSopDetailDTO);


    /**
     * sop规则执行记录分页
     *
     * @param findWeSopExecutedRulesDTO
     * @return
     */
    List<WeSopExecutedRulesVO> getSopExecutedRulesBySopId(FindWeSopExecutedRulesDTO findWeSopExecutedRulesDTO);

    /**
     * sop员工执行记录分页
     *
     * @param findWeSopExecutedUsersDTO
     * @return
     */
    List<WeSopExecutedUsersVO> getSopExecutedUsersBySopId(FindWeSopExecutedUsersDTO findWeSopExecutedUsersDTO);

    /**
     * sop任务统计
     *
     * @param sopId  sopId
     * @param corpId 企业Id
     * @return
     */
    WeSopTaskCountVO taskCount(String sopId, String corpId);


    /**
     * 查询员工下的执行任务数据
     *
     * @param getSopTaskDetailDTO 过滤条件
     * @return List<GetSopTaskByUserIdVO>
     */
    List<GetSopTaskByUserIdVO> getTaskDetailByUserId(GetSopTaskDetailDTO getSopTaskDetailDTO);

    /**
     * 完成执行计划
     *
     * @param corpId   企业ID
     * @param detailId 执行计划ID
     */
    void finishTask(String corpId, Long detailId);

}

