package com.easyink.wecom.mapper.autotag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 批量打标签-标签关联（we_batch_tag_task_rel）表数据库访问层
 *
 * @author lichaoyu
 * @date 2023/6/5 16:48
 */
@Repository
public interface WeBatchTagTaskRelMapper extends BaseMapper<WeBatchTagTaskRel> {

    /**
     * 保存批量打标签任务标签关联信息
     *
     * @param taskId 批量打标签任务ID
     * @param tagIds 标签ID列表
     * @return 结果
     */
    Integer saveBatch(@Param("taskId") Long taskId, @Param("tagIds") List<String> tagIds);
}
