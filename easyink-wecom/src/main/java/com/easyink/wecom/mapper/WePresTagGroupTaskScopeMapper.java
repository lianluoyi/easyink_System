package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WePresTagGroupTaskScope;
import com.easyink.wecom.domain.vo.WeCommunityTaskEmplVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WePresTagGroupTaskScopeMapper extends BaseMapper<WePresTagGroupTaskScope> {

    /**
     * 根据建群任务id获取所有使用人员信息
     *
     * @param taskId 建群任务id
     * @param corpId 企业id
     * @return {@link List<WeCommunityTaskEmplVO>}
     */
    List<WeCommunityTaskEmplVO> getScopeListByTaskId(@Param("taskId") Long taskId, @Param("corpId") String corpId);

    /**
     * 批量绑定任务与使用人员
     *
     * @param taskScopeList 待绑定对象
     * @return 结果
     */
    int batchBindsTaskScopes(List<WePresTagGroupTaskScope> taskScopeList);

    /**
     * 员工发送信息后，变更其任务状态为 "完成"
     *
     * @param taskId 任务id
     * @param emplId 员工id
     * @return 结果
     */
    int updateEmplTaskStatus(@Param("taskId") Long taskId, @Param("emplId") String emplId);
}
