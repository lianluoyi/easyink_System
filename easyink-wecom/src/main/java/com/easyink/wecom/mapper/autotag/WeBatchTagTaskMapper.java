package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.autotag.WeBatchTagTask;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 批量打标签（we_batch_tag_task）表数据库访问层
 *
 * @author lichaoyu
 * @date 2023/6/5 10:30
 */
@Repository
public interface WeBatchTagTaskMapper extends BaseMapper<WeBatchTagTask> {

    /**
     * 根据任务ID批量删除任务（逻辑删除）
     *
     * @param corpId 企业ID
     * @param taskIds 任务ID
     * @return 结果
     */
    Integer deleteBatchTaskByIds(@Param("corpId") String corpId, @Param("taskIds") Long[] taskIds);

    /**
     * 查询任务列表
     *
     * @param dto {@link BatchTagTaskDTO}
     * @return 结果
     */
    List<BatchTagTaskVO> selectBatchTaskList(@Param("dto") BatchTagTaskDTO dto, @Param("taskIds") List<Long> taskIds);

}
