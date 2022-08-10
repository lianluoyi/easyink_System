package com.easywecom.wecom.service;

import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.wecom.domain.WePresTagGroupTask;
import com.easywecom.wecom.domain.WeTag;
import com.easywecom.wecom.domain.dto.WePresTagGroupTaskDTO;
import com.easywecom.wecom.domain.vo.PresTagExpectedReceptionVO;
import com.easywecom.wecom.domain.vo.WeCommunityTaskEmplVO;
import com.easywecom.wecom.domain.vo.WePresTagGroupTaskStatResultVO;
import com.easywecom.wecom.domain.vo.WePresTagGroupTaskVO;

import java.util.List;

/**
 * 社区运营 老客户标签建群 相关逻辑
 */
public interface WePresTagGroupTaskService {

    /**
     * 添加新标签建群任务
     *
     * @param task       建群任务本体信息
     * @param tagIdList  标签列表
     * @param emplIdList 员工列表
     * @return 结果
     */
    int add(WePresTagGroupTask task, List<String> tagIdList, List<String> emplIdList);

    /**
     * 创建标签建群业务处理
     *
     * @param taskDTO   请求数据
     * @param task      标签记录数据
     * @param loginUser 用户信息
     * @return int
     */
    int addTask(WePresTagGroupTaskDTO taskDTO, WePresTagGroupTask task, LoginUser loginUser);

    /**
     * 创建一个标签入群群发任务
     *
     * @param wePresTagGroupTaskDTO 任务参数对象
     * @param loginUser             当前登录人
     */
    void addTaskV2(WePresTagGroupTaskDTO wePresTagGroupTaskDTO, LoginUser loginUser);

    /**
     * 根据条件查询任务列表
     *
     * @param corpId    企业ID
     * @param taskName  任务名称
     * @param sendType  发送方式
     * @param createBy  创建人
     * @param beginTime 起始时间 正确格式为yyyy-MM-dd
     * @param endTime   结束时间 正确格式为:yyyy-MM-dd
     * @return 结果
     */
    List<WePresTagGroupTaskVO> selectTaskList(String corpId, String taskName, Integer sendType, String createBy, String beginTime, String endTime);

    /**
     * 通过id获取老客标签建群任务
     *
     * @param corpId 企业ID
     * @param taskId 任务id
     * @return {@link WePresTagGroupTaskVO}
     */
    WePresTagGroupTaskVO getTaskById(Long taskId, String corpId);

    /**
     * 批量删除老客标签建群任务
     *
     * @param corpId 企业ID
     * @param idList 任务id列表
     * @return 删除的行数
     */
    int batchRemoveTaskByIds(String corpId, Long[] idList);

    /**
     * 更新老客户标签建群任务
     *
     * @param corpId                企业ID
     * @param taskId                待更新任务id
     * @param wePresTagGroupTaskDto 更新数据
     * @return 更新条数
     */
    int updateTask(String corpId, Long taskId, WePresTagGroupTaskDTO wePresTagGroupTaskDto);

    /**
     * 查询标签建群详情数据
     *
     * @param corpId       企业id
     * @param taskId       任务id
     * @param customerName 客户名称
     * @param isInGroup    是否已进群
     * @param isSent       送达状态
     * @param pageNum      页码
     * @param pageSize     每页数量
     * @return WePresTagGroupTaskStatResultVO
     */
    WePresTagGroupTaskStatResultVO getStatByTaskId(String corpId, Long taskId, String customerName, Integer isInGroup, Integer isSent, Integer pageNum, Integer pageSize);


    /**
     * 根据任务id获取对应员工信息列表
     *
     * @param taskId 任务id
     * @param corpId 企业id
     * @return {@link List<WeCommunityTaskEmplVO>}
     */
    List<WeCommunityTaskEmplVO> getScopeListByTaskId(Long taskId, String corpId);

    /**
     * 根据任务id获取对应标签信息列表
     *
     * @param taskId 任务id
     * @return 结果
     */
    List<WeTag> getTagListByTaskId(Long taskId);

    /**
     * 获取员工建群任务信息
     *
     * @param emplId 员工id
     * @param isDone 是否已处理
     * @param corpId 企业id
     * @return {@link List<WePresTagGroupTaskVO>}
     */
    List<WePresTagGroupTaskVO> getEmplTaskList(String emplId, boolean isDone, String corpId);

    /**
     * 员工发送信息后，变更其任务状态为 "完成"
     *
     * @param taskId 任务id
     * @param emplId 员工id
     * @return 结果
     */
    int updateEmplTaskStatus(Long taskId, String emplId);

    /**
     * 根据标签建群任务信息发送消息
     *
     * @param task 标签建群任务
     */
    void sendMessage(WePresTagGroupTask task, List<String> externalIds);

    /**
     * 任务名是否已占用
     *
     * @param task 任务信息
     * @return 名称是否占用
     */
    boolean isNameOccupied(WePresTagGroupTask task);

    /**
     *
     * @param taskId 老客户标签建群任务id
     * @param hasScope 发送范围过滤
     * @param hasTag 老客户标签建群过滤
     * @param gender  性别
     * @param beginTime 起始时间 正确格式：yyyy-MM-dd
     * @param endTime 结束时间 正确格式如：yyyy-MM-dd
     * @return
     */
    List<String> selectExternalUserIds(
            Long taskId,
            boolean hasScope,
            boolean hasTag,
            Integer gender,
            String beginTime,
            String endTime
    );

    /**
     * 保存标签建群任务下选中员工的客户信息
     *
     * @param taskId    标签建群任务ID
     * @param scopeList 选中的员工列表
     */
    void saveWePresTagGroupStat(Long taskId, List<String> scopeList);

    /**
     * 计算预期发送数量
     *
     * @param wePresTagGroupTaskDTO 任务条件
     * @param loginUser             当前登录
     * @return {@link PresTagExpectedReceptionVO}
     */
    PresTagExpectedReceptionVO getExpectedReceptionData(WePresTagGroupTaskDTO wePresTagGroupTaskDTO, LoginUser loginUser);
}
