package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskDetail;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDetailDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskDetailVO;

import java.util.List;

/**
 * 批量打标签任务详情（we_batch_tag_task_detail）接口
 *
 * @author lichaoyu
 * @date 2023/6/5 10:21
 */
public interface WeBatchTagTaskDetailService extends IService<WeBatchTagTaskDetail> {

    /**
     * 查询批量打标签任务详情列表
     *
     * @param dto {@link BatchTagTaskDetailDTO}
     * @return 结果
     */
    List<BatchTagTaskDetailVO> selectBatchTaskDetailList(BatchTagTaskDetailDTO dto);

    /**
     * 导出批量打标签任务详情列表
     *
     * @param dto {@link BatchTagTaskDetailDTO}
     * @return
     */
    AjaxResult exportBatchTaskDetailList(BatchTagTaskDetailDTO dto);
}
