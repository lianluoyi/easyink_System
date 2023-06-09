package com.easyink.wecom.service.autotag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.autotag.WeBatchTagTask;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskVO;
import com.easyink.wecom.domain.vo.autotag.ImportBatchTagTaskVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 批量打标签（we_batch_tag_task）接口
 *
 * @author lichaoyu
 * @date 2023/6/5 10:18
 */
public interface WeBatchTagTaskService extends IService<WeBatchTagTask> {

    /**
     * 导入批量打标签任务
     *
     * @param file     excel文件
     * @param tagIds   企业ID
     * @param taskName
     * @return 结果 {@link ImportBatchTagTaskVO}
     */
    ImportBatchTagTaskVO importBatchTagTask(MultipartFile file, List<String> tagIds, String taskName) throws IOException;

    /**
     * 根据任务ID批量删除任务详情（逻辑删除）
     *
     * @param corpId 企业ID
     * @param taskIds 任务ID
     * @return 结果
     */
    Integer deleteBatchTaskByIds(String corpId, Long[] taskIds);


    /**
     * 查询任务列表
     *
     * @param dto {@link BatchTagTaskDTO}
     * @return 结果
     */
    List<BatchTagTaskVO> selectBatchTaskList(BatchTagTaskDTO dto);

}
