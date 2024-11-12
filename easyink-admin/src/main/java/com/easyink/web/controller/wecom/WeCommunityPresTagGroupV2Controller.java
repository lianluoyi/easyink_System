package com.easyink.web.controller.wecom;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.QueryPresTagGroupDTO;
import com.easyink.wecom.domain.dto.QueryPresTagGroupStatDTO;
import com.easyink.wecom.domain.dto.WePresTagGroupTaskDTO;
import com.easyink.wecom.domain.vo.PresTagExpectedReceptionVO;
import com.easyink.wecom.domain.vo.WePresTagGroupTaskStatResultVO;
import com.easyink.wecom.domain.vo.WePresTagGroupTaskStatVO;
import com.easyink.wecom.domain.vo.WePresTagGroupTaskVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeGroupCodeService;
import com.easyink.wecom.service.WePresTagGroupTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: 老客进群接口
 *
 * @author: 1*+
 * @date: 2021-11-01 17:46
 */
@RestController
@RequestMapping(value = "/wecom/communityPresTagGroupV2")
@Api(tags = "老客进群")
@Slf4j
public class WeCommunityPresTagGroupV2Controller extends BaseController {

    private final WePresTagGroupTaskService taskService;
    private final WeGroupCodeService groupCodeService;

    @Autowired
    public WeCommunityPresTagGroupV2Controller(WePresTagGroupTaskService taskService, WeGroupCodeService groupCodeService) {
        this.taskService = taskService;
        this.groupCodeService = groupCodeService;
    }

    @ApiOperation("获取标签入群列表数据")
    @GetMapping(path = "/list")
    public TableDataInfo<WePresTagGroupTaskVO> getList(QueryPresTagGroupDTO queryPresTagGroupDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        startPage();
        TimeInterval timer = DateUtil.timer();
        List<WePresTagGroupTaskVO> wePresTagGroupTaskVoList =
                taskService.selectTaskList(corpId, queryPresTagGroupDTO.getTaskName(), queryPresTagGroupDTO.getSendType()
                        , queryPresTagGroupDTO.getCreateBy(), queryPresTagGroupDTO.getBeginTime(), queryPresTagGroupDTO.getEndTime());
        log.info("查询老客进群列表耗时:{}ms", timer.interval());
        return getDataTable(wePresTagGroupTaskVoList);
    }

    @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:add')")
    @PostMapping
    @ApiOperation("新建标签建群任务")
    @ApiResponses({
            @ApiResponse(code = 2024, message = "所选群活码失效，请重新选择"),
            @ApiResponse(code = 2025, message = "任务已存在，请重新填写任务名"),
    })
    public AjaxResult<Object> addTask(@RequestBody @Validated WePresTagGroupTaskDTO wePresTagGroupTaskDto) {
        if(org.apache.commons.lang3.StringUtils.isBlank(wePresTagGroupTaskDto.getWelcomeMsg())||wePresTagGroupTaskDto.getGroupCodeId()==null){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        taskService.addTaskV2(wePresTagGroupTaskDto, LoginTokenService.getLoginUser());
        return AjaxResult.success();
    }

    //  @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:query')")
    @GetMapping(path = "/{id}")
    @ApiOperation("根据获取任务详细信息")
    public AjaxResult<WePresTagGroupTaskVO> getTask(@PathVariable("id") Long id) {
        WePresTagGroupTaskVO taskVo = taskService.getTaskById(id, LoginTokenService.getLoginUser().getCorpId());
        if (StringUtils.isNull(taskVo)) {
            return AjaxResult.error(ResultTip.TIP_ACTIVE_CODE_NOT_EXSIT);
        }
        return AjaxResult.success(taskVo);
    }

//    @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:edit')")
    @PutMapping(path = "/{id}")
    @ApiOperation("更新任务信息")
    public AjaxResult updateTask(@PathVariable("id") Long id, @RequestBody @Validated WePresTagGroupTaskDTO wePresTagGroupTaskDto) {
        if(org.apache.commons.lang3.StringUtils.isBlank(wePresTagGroupTaskDto.getWelcomeMsg())||wePresTagGroupTaskDto.getGroupCodeId()==null){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (null == groupCodeService.getById(wePresTagGroupTaskDto.getGroupCodeId())) {
            return AjaxResult.error(ResultTip.TIP_ACTIVE_CODE_NOT_EXSIT);
        }
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(taskService.updateTask(corpId, id, wePresTagGroupTaskDto));
    }

    @ApiOperation("批量删除标签建群任务")
    @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:remove')")
    @DeleteMapping(path = "/{ids}")
    public AjaxResult batchRemoveTask(@PathVariable("ids") Long[] ids) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(taskService.batchRemoveTaskByIds(corpId, ids));
    }

    //  @PreAuthorize("@ss.hasPermi('wecom:communitytagGroup:query')")
    @GetMapping(path = "/stat/{id}")
    @ApiOperation("获取进群详情")
    public TableDataInfo<WePresTagGroupTaskStatVO> getStatInfo(@PathVariable("id") Long id,
                                                               QueryPresTagGroupStatDTO queryPresTagGroupStatDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        WePresTagGroupTaskStatResultVO resultVO = taskService.getStatByTaskId(corpId, id, queryPresTagGroupStatDTO.getCustomerName()
                , queryPresTagGroupStatDTO.getIsInGroup(), queryPresTagGroupStatDTO.getIsSent()
                , queryPresTagGroupStatDTO.getPageNum(), queryPresTagGroupStatDTO.getPageSize());
        //total=null时,以sql查询的total为准
        if (resultVO.getTotal() == null) {
            return getDataTable(resultVO.getData());
        }
        return getDataTable(resultVO.getData(), Long.parseLong(String.valueOf(resultVO.getTotal())));
    }

    @PostMapping(path = "/getExpectedReceptionData")
    @ApiOperation("获取预计发送数据")
    public AjaxResult<PresTagExpectedReceptionVO> getExpectedReceptionData(@RequestBody @Validated WePresTagGroupTaskDTO wePresTagGroupTaskDto) {
        return AjaxResult.success(taskService.getExpectedReceptionData(wePresTagGroupTaskDto, LoginTokenService.getLoginUser()));
    }


}
