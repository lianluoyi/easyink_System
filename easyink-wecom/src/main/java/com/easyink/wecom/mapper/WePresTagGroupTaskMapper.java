package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WePresTagGroupTask;
import com.easyink.wecom.domain.entity.BaseExternalUserEntity;
import com.easyink.wecom.domain.model.groupcode.GroupCodeTotalNumberModel;
import com.easyink.wecom.domain.vo.WePresTagGroupTaskVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 老客户标签建群相关Mapper接口
 */
@Repository
public interface WePresTagGroupTaskMapper extends BaseMapper<WePresTagGroupTask> {

    /**
     * 添加新任务
     *
     * @param task 老客标签建群任务
     * @return 结果
     */
    int insertTask(WePresTagGroupTask task);

    /**
     * 更新任务
     *
     * @param task 建群任务信息
     * @return 结果
     */
    int updateTask(WePresTagGroupTask task);

    /**
     * 获取老客户标签建群任务
     *
     * @param taskId 任务id
     * @return 结果
     */
    WePresTagGroupTaskVO selectTaskById(Long taskId);

    /**
     * 根据条件查询老客标签建群任务
     *
     * @param corpId    企业ID
     * @param taskName  任务名称
     * @param createBy  创建人
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param sendType  发送方式
     * @return 结果
     */
    List<WePresTagGroupTaskVO> selectTaskList(
            @Param("corpId")  String corpId,
            @Param("taskName") String taskName,
            @Param("sendType") Integer sendType,
            @Param("createBy") String createBy,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime
    );

    /**
     * 获取某员工的任务
     *
     * @param emplId 员工id
     * @param isDone 已完成的还是待处理
     * @return 结果
     */
    List<WePresTagGroupTaskVO> getTaskListByEmplId(@Param("emplId") String emplId, @Param("isDone") boolean isDone);


    List<String> selectExternalUserIds(
            @Param("taskId") Long taskId,
            @Param("hasScope") boolean hasScope,
            @Param("hasTag") boolean hasTag,
            @Param("gender") Integer gender,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime
    );

    List<BaseExternalUserEntity> getExternalUserIds(
            @Param("corpId") String corpId,
            @Param("scopeUserList") List<String> scopeUserList,
            @Param("tagList") List<String> tagList,
            @Param("gender") Integer gender,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime
    );

    /**
     * 获取群活码对应的使用统计数
     * @param groupCodeIdList 群活码codeId列表
     * @return 统计列表
     */
    List<GroupCodeTotalNumberModel> selectTotalNumberByGroupCodeIdList(@Param("groupCodeIdList") List<String> groupCodeIdList);
}
