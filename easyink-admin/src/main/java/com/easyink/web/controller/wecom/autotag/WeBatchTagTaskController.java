package com.easyink.web.controller.wecom.autotag;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDTO;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDetailDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskDetailVO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.autotag.WeBatchTagTaskDetailService;
import com.easyink.wecom.service.autotag.WeBatchTagTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 批量打标签-Controller
 *
 * @author lichaoyu
 * @date 2023/6/5 9:47
 */
@Api(tags = "批量打标签任务")
@RestController
@RequestMapping("wecom/batchTagTask")
public class WeBatchTagTaskController extends BaseController {

    private final WeBatchTagTaskService weBatchTagTaskService;

    private final WeBatchTagTaskDetailService weBatchTagTaskDetailService;

    public WeBatchTagTaskController(WeBatchTagTaskService weBatchTagTaskService, WeBatchTagTaskDetailService weBatchTagTaskDetailService) {
        this.weBatchTagTaskService = weBatchTagTaskService;
        this.weBatchTagTaskDetailService = weBatchTagTaskDetailService;
    }

    @PreAuthorize("@ss.hasPermi('wecom:batchtag:add')")
    @ApiOperation(value = "导入批量打标签任务", httpMethod = "POST")
    @PostMapping("/import")
    public <T> AjaxResult<T> importTask (@RequestParam("file") MultipartFile file, @RequestParam("tagIds") List<String> tagIds, @RequestParam("taskName") String taskName) throws Exception {
        return AjaxResult.success(weBatchTagTaskService.importBatchTagTask(file, tagIds, taskName));
    }

    @PreAuthorize("@ss.hasPermi('wecom:batchtag:del')")
    @ApiOperation("批量删除打标签任务")
    @DeleteMapping("/delete")
    public AjaxResult remove(@ApiParam("记录ID数组") @RequestParam("taskIds") Long[] taskIds) {
        return toAjax(weBatchTagTaskService.deleteBatchTaskByIds(LoginTokenService.getLoginUser().getCorpId(), taskIds));
    }

    @PreAuthorize("@ss.hasPermi('wecom:batchtag:list')")
    @ApiOperation("查询批量打标签任务列表")
    @GetMapping("/list")
    public TableDataInfo<BatchTagTaskVO> list(BatchTagTaskDTO dto){
        startPage();
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<BatchTagTaskVO> resultList = weBatchTagTaskService.selectBatchTaskList(dto);
        return getDataTable(resultList);
    }

    @PreAuthorize("@ss.hasPermi('wecom:batchtag:list')")
    @ApiOperation("查询批量打标签任务详情")
    @GetMapping("/detail")
    public TableDataInfo<BatchTagTaskDetailVO> detail(BatchTagTaskDetailDTO dto){
        startPage();
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<BatchTagTaskDetailVO> resultList = weBatchTagTaskDetailService.selectBatchTaskDetailList(dto);
        return getDataTable(resultList);
    }

    @PreAuthorize("@ss.hasPermi('wecom:batchtag:export')")
    @ApiOperation("导出批量打标签任务详情")
    @PostMapping("/export")
    public AjaxResult<BatchTagTaskDetailVO> export(@RequestBody BatchTagTaskDetailDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weBatchTagTaskDetailService.exportBatchTaskDetailList(dto));
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

}
