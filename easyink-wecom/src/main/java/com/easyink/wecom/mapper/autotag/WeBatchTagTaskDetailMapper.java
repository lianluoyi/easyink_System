package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskDetail;
import org.apache.ibatis.annotations.Param;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDetailDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskDetailVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 批量打标签任务详情（we_batch_tag_task_detail）表数据库访问层
 *
 * @author lichaoyu
 * @date 2023/6/5 10:24
 */
@Repository
public interface WeBatchTagTaskDetailMapper extends BaseMapper<WeBatchTagTaskDetail> {


    /**
     * 查询批量打标签任务详情列表
     *
     * @param dto {@link BatchTagTaskDetailDTO}
     * @return 结果
     */
    List<BatchTagTaskDetailVO> selectBatchTaskDetailList(BatchTagTaskDetailDTO dto);

    /**
     * 批量更新或者插入 批量打标签任务详情
     *
     * @param list {@link WeBatchTagTaskDetail }
     * @return affected rows
     */
    Integer batchInsertOrUpdate(@Param("list") List<WeBatchTagTaskDetail> list);

    /**
     * 批量插入 批量打标签任务详情
     *
     * @param list {@link WeBatchTagTaskDetail}
     * @return 结果
     */
    Integer batchInsert(@Param("list") List<WeBatchTagTaskDetail> list);
}
