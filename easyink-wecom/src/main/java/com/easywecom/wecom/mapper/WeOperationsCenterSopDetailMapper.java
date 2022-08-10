package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeOperationsCenterSopDetailEntity;
import com.easywecom.wecom.domain.dto.groupsop.FindWeSopDetailDTO;
import com.easywecom.wecom.domain.dto.groupsop.FindWeSopExecutedRulesDTO;
import com.easywecom.wecom.domain.dto.groupsop.FindWeSopExecutedUsersDTO;
import com.easywecom.wecom.domain.dto.groupsop.GetSopTaskDetailDTO;
import com.easywecom.wecom.domain.vo.sop.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author 佚名
 * @date 2021-11-30 14:05:23
 */
@Repository
public interface WeOperationsCenterSopDetailMapper extends BaseMapper<WeOperationsCenterSopDetailEntity> {
    /**
     * 查询客户SOP类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    List<WeOperationsCenterSopDetailCustomerVO> selectSopDetailBySopIdWithCustomerType(@Param("query") FindWeSopDetailDTO findWeSopDetailDTO);

    /**
     * 查询群SOP定时类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    List<WeOperationsCenterSopDetailByTimingTypeVO> selectSopDetailBySopIdWithTimingType(@Param("query") FindWeSopDetailDTO findWeSopDetailDTO);

    /**
     * 查询群SOP循环类型的执行详情
     *
     * @param findWeSopDetailDTO
     * @return
     */
    List<WeOperationsCenterSopDetailByCycleTypeVO> selectSopDetailBySopIdWithCycleType(@Param("query") FindWeSopDetailDTO findWeSopDetailDTO);

    /**
     * sop规则执行记录分页
     *
     * @param findWeSopExecutedRulesDTO
     * @return
     */
    List<WeSopExecutedRulesVO> selectSopExecutedRulesBySopId(FindWeSopExecutedRulesDTO findWeSopExecutedRulesDTO);

    /**
     * sop员工执行记录分页
     *
     * @param findWeSopExecutedUsersDTO
     * @return
     */
    List<WeSopExecutedUsersVO> selectSopExecutedUsersBySopId(FindWeSopExecutedUsersDTO findWeSopExecutedUsersDTO);

    /**
     * sop任务统计
     *
     * @param sopId
     * @param corpId
     * @return
     */
    WeSopTaskCountVO taskCount(@Param("sopId") String sopId, @Param("corpId") String corpId);

    /**
     * 查询员工下的sop任务执行详情
     *
     * @param getSopTaskDetailDTO 过滤条件
     */
    List<GetTaskDetailByUserIdVO> getTaskDetailByUserId(GetSopTaskDetailDTO getSopTaskDetailDTO);

    /**
     * 根据userId查询SOP任务信息
     *
     * @param corpId 企业ID
     * @param userId 员工userId
     * @return List<GetSopTaskByUserIdVO>
     */
    List<GetSopTaskByUserIdVO> getSopTaskByUserId(@Param("corpId") String corpId, @Param("userId") String userId, @Param("type") Integer type, @Param("detailId") Long detailId, @Param("isFinish") Boolean isFinish);

    /**
     * 完成执行计划
     *
     * @param corpId     企业ID
     * @param detailId   执行计划ID
     * @param finishTime 完成时间
     */
    void finishTask(@Param("corpId") String corpId, @Param("detailId") Long detailId, @Param("finishTime") Date finishTime);

}
