package com.easyink.wecom.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WePresTagGroupTaskStat;
import com.easyink.wecom.domain.vo.WePresTagGroupTaskStatVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WePresTagGroupTaskStatMapper extends BaseMapper<WePresTagGroupTaskStat> {

    /**
     * 根据 老客标签建群任务id及附属相关属性条件获取任务对应的客户统计
     *
     * @param taskId       任务id
     * @param customerName 客户名称
     * @param isInGroup    是否已进群
     * @param isSent       送达状态
     * @param corpId       企业ID
     * @return 客户统计列表
     */
    List<WePresTagGroupTaskStatVO> selectStatInfoByTaskId(@Param("taskId") Long taskId, @Param("customerName") String customerName, @Param("isInGroup") Integer isInGroup, @Param("isSent") Integer isSent, @Param("corpId") String corpId);

    /**
     * 通过taskId获取所有外部联系人id
     *
     * @param taskId 老客标签建群任务id
     * @return 结果
     */
    List<String> getAllExternalIdByTaskId(Long taskId);

    /**
     * 保存标签建群任务下选中员工的客户信息
     *
     * @param taskId     标签建群任务ID
     * @param userIdList 选中的员工列表
     */
    void saveByFlowerCustomer(@Param("taskId") Long taskId, @Param("userIdList") List<String> userIdList);

    /**
     * 批量插入
     *
     * @param list 对象集合
     */
    void insertBatch(@Param("list") List<WePresTagGroupTaskStat> list);
}
