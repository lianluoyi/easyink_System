package com.easywecom.web.controller.wecom;


import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.WePresTagGroupTask;
import com.easywecom.wecom.domain.dto.WePresTagGroupTaskDTO;
import com.easywecom.wecom.domain.vo.WePresTagGroupTaskStatResultVO;
import com.easywecom.wecom.domain.vo.WePresTagGroupTaskStatVO;
import com.easywecom.wecom.domain.vo.WePresTagGroupTaskVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeGroupCodeService;
import com.easywecom.wecom.service.WePresTagGroupTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名： WeCommunityPresTagGroupController
 *
 * @author 佚名
 * @date 2021/9/30 15:27
 */
@RestController
@RequestMapping(value = "/wecom/communityPresTagGroup")
@Api(tags = "标签建群", hidden = true)
@Deprecated
public class WeCommunityPresTagGroupController extends BaseController {

    @Autowired
    private WePresTagGroupTaskService taskService;

    @Autowired
    private WeGroupCodeService groupCodeService;
    private static final String ERROR_MSG = "群活码不存在";

    /**
     * 获取老客标签建群列表数据
     */
    //  @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:list')")
    @ApiOperation("获取标签建群列表数据")
    @GetMapping(path = "/list")
    public TableDataInfo<WePresTagGroupTaskVO> getList(
            @RequestParam(value = "taskName") String taskName,
            @RequestParam(value = "sendType") Integer sendType,
            @RequestParam(value = "createBy") String createBy,
            @RequestParam(value = "beginTime") String beginTime,
            @RequestParam(value = "endTime") String endTime) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        startPage();
        List<WePresTagGroupTaskVO> wePresTagGroupTaskVoList = taskService.selectTaskList(corpId,taskName, sendType, createBy, beginTime, endTime);
        return getDataTable(wePresTagGroupTaskVoList);
    }

    /**
     * 新建老客标签建群任务
     */
    @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:add')")
    @PostMapping
    @ApiOperation("新建标签建群任务")
    public AjaxResult addTask(@RequestBody @Validated WePresTagGroupTaskDTO wePresTagGroupTaskDto) {
        if (null == groupCodeService.getById(wePresTagGroupTaskDto.getGroupCodeId())) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, ERROR_MSG);
        }
        WePresTagGroupTask task = new WePresTagGroupTask();
        BeanUtils.copyProperties(wePresTagGroupTaskDto, task);
        task.setCorpId(LoginTokenService.getLoginUser().getCorpId());

        // 检测任务名是否可用
        if (taskService.isNameOccupied(task)) {
            return AjaxResult.error("任务名已存在");
        }
        LoginUser loginUser = LoginTokenService.getLoginUser();
        task.setCreateBy(LoginTokenService.getUsername());
        return toAjax(taskService.addTask(wePresTagGroupTaskDto, task, loginUser));
    }

    /**
     * 根据获取任务详细信息
     */
    //  @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:query')")
    @GetMapping(path = "/{id}")
    @ApiOperation("根据获取任务详细信息")
    public AjaxResult getTask(@PathVariable("id") Long id) {
        WePresTagGroupTaskVO taskVo = taskService.getTaskById(id, LoginTokenService.getLoginUser().getCorpId());
        if (StringUtils.isNull(taskVo)) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, ERROR_MSG);
        }
        return AjaxResult.success(taskVo);
    }

    /**
     * 更新任务信息
     */
//    @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:edit')")
    @PutMapping(path = "/{id}")
    @ApiOperation("更新任务信息")
    public AjaxResult updateTask(@PathVariable("id") Long id, @RequestBody @Validated WePresTagGroupTaskDTO wePresTagGroupTaskDto) {
        if (null == groupCodeService.getById(wePresTagGroupTaskDto.getGroupCodeId())) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, ERROR_MSG);
        }
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(taskService.updateTask(corpId, id, wePresTagGroupTaskDto));
    }

    /**
     * 批量删除老客标签建群任务
     */
    @ApiOperation("批量删除标签建群任务")
    @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:remove')")
    @DeleteMapping(path = "/{ids}")
    public AjaxResult batchRemoveTask(@PathVariable("ids") Long[] ids) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(taskService.batchRemoveTaskByIds(corpId, ids));
    }

    /**
     * 根据老客标签建群id及过滤条件，获取其统计信息
     */
    //  @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:query')")
    @GetMapping(path = "/stat/{id}")
    @ApiOperation("获取统计信息")
    public TableDataInfo<WePresTagGroupTaskStatVO> getStatInfo(@PathVariable("id") Long id,
                                                               @RequestParam(value = "customerName", required = false) String customerName,
                                                               @RequestParam(value = "isInGroup", required = false) Integer isInGroup,
                                                               @RequestParam(value = "isSent", required = false) Integer isSent,
                                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        WePresTagGroupTaskStatResultVO resultVO = taskService.getStatByTaskId(corpId, id, customerName, isInGroup, isSent, pageNum, pageSize);
        //total=null时,以sql查询的total为准
        if (resultVO.getTotal() == null) {
            return getDataTable(resultVO.getData());
        }
        return getDataTable(resultVO.getData(), Long.parseLong(String.valueOf(resultVO.getTotal())));
    }


}
