package com.easyink.quartz.controller;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.exception.job.TaskException;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.quartz.domain.SysJob;
import com.easyink.quartz.service.ISysJobService;
import com.easyink.quartz.util.CronUtils;
import com.easyink.quartz.util.JobInvokeUtil;
import com.easyink.wecom.login.util.LoginTokenService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度任务信息操作处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SysJobController.class);
    
    @Autowired
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysJob sysJob) {
        startPage();
        List<SysJob> list = jobService.selectJobList(sysJob);
        return getDataTable(list);
    }

    /**
     * 导出定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<>(SysJob.class);
        return util.exportExcel(list, "定时任务");
    }

    /**
     * 获取定时任务详细信息
     */
//    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId) {
        return AjaxResult.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysJob sysJob) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(sysJob.getCronExpression())) {
            return AjaxResult.error("cron表达式不正确");
        }
        
        // 安全检查：验证任务调用目标是否安全
        try {
            JobInvokeUtil.validateTaskSecurity(sysJob.getInvokeTarget());
        } catch (SecurityException e) {
            return AjaxResult.error("任务安全检查失败: " + e.getMessage());
        }
        
        // 安全保护：新增任务时，invokeTarget必须由开发人员预先配置
        if (sysJob.getInvokeTarget() == null || sysJob.getInvokeTarget().trim().isEmpty()) {
            return AjaxResult.error("调用目标不能为空，请联系开发人员配置");
        }
        
        sysJob.setCreateBy(LoginTokenService.getUsername());
        return toAjax(jobService.insertJob(sysJob));
    }

    /**
     * 修改定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysJob sysJob) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(sysJob.getCronExpression())) {
            return AjaxResult.error("cron表达式不正确");
        }
        
        // 安全保护：修改任务时，不允许修改invokeTarget字段
        // 获取原始任务信息，保持invokeTarget不变
        SysJob originalJob = jobService.selectJobById(sysJob.getJobId());
        if (originalJob != null) {
            sysJob.setInvokeTarget(originalJob.getInvokeTarget());
            log.info("修改任务时保持原始invokeTarget不变: {}", originalJob.getInvokeTarget());
        }
        
        sysJob.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(jobService.updateJob(sysJob));
    }

    /**
     * 定时任务状态修改
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return AjaxResult.success();
    }

    /**
     * 删除定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException {
        jobService.deleteJobByIds(jobIds);
        return AjaxResult.success();
    }



    

}
