package com.easywecom.web.controller.wecom;

import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.entity.SysUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.CommunityTaskType;
import com.easywecom.common.service.ISysUserService;
import com.easywecom.wecom.domain.dto.groupsop.GetSopTaskDetailDTO;
import com.easywecom.wecom.domain.vo.sop.GetSopTaskByUserIdVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeOperationsCenterSopDetailService;
import com.easywecom.wecom.service.WePresTagGroupTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 社区运营H5接口
 *
 * @author admin
 * @Date 2021/3/24 10:54
 */
@RestController
@RequestMapping(value = "/wecom/community/h5")
@Api(tags = "社区运营H5接口")
public class WeCommunityH5Controller extends BaseController {

    private final WePresTagGroupTaskService tagGroupTaskService;
    private final ISysUserService userService;
    private final WeOperationsCenterSopDetailService sopDetailService;

    @Autowired
    public WeCommunityH5Controller(WePresTagGroupTaskService tagGroupTaskService, ISysUserService userService, WeOperationsCenterSopDetailService sopDetailService) {
        this.tagGroupTaskService = tagGroupTaskService;
        this.userService = userService;
        this.sopDetailService = sopDetailService;
    }

    /**
     * 获取任务对应的执行人列表
     *
     * @param taskId 任务id
     * @param type   任务类型 1：标签建群任务 2：sop任务
     * @return
     */
    @GetMapping("/scope/{taskId}")
    @ApiOperation("获取任务对应的执行人列表")
    public AjaxResult getTaskScopeList(@PathVariable("taskId") Long taskId, @RequestParam(value = "type") Integer type) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        if (CommunityTaskType.TAG.getType().equals(type)) {
            return AjaxResult.success(tagGroupTaskService.getScopeListByTaskId(taskId, corpId));
        }
        return AjaxResult.success();
    }

    /**
     * h5页面根据员工id获取老客标签建群和群sop任务信息
     *
     * @param emplId 员工id
     * @param type   数据类型，0:全部数据 1:老客标签建群数据 2:群SOP数据
     * @return
     */
    @GetMapping("/{emplId}")
    @ApiOperation("员工id获取标签建群和群sop任务信息")
    public AjaxResult getEmplTask(@PathVariable("emplId") String emplId, @RequestParam(value = "type") Integer type) {
        AjaxResult res = AjaxResult.success();
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        if (CommunityTaskType.TAG.getType().equals(type)) {
            // 老客标签建群数据
            res.put("todo", tagGroupTaskService.getEmplTaskList(emplId, Boolean.FALSE, corpId));
            res.put("done", tagGroupTaskService.getEmplTaskList(emplId, Boolean.TRUE, corpId));
        } else {
            // 全部数据
            List todoList = new ArrayList();
            List doneList = new ArrayList();
            todoList.addAll(tagGroupTaskService.getEmplTaskList(emplId, Boolean.FALSE, corpId));
            res.put("todo", todoList);
            doneList.addAll(tagGroupTaskService.getEmplTaskList(emplId, Boolean.TRUE, corpId));
            res.put("done", doneList);
        }
        SysUser user = userService.selectUserByUserName(emplId);
        boolean isAdmin = user != null && user.isAdmin();
        res.put("isAdmin", isAdmin);
        return res;
    }

    /**
     * 员工发送老客标签建群任务信息或者发送sop到其客户群之后，变更其任务状态
     *
     * @param taskId 老客标签建群时代表任务id，sop时，代表规则id
     * @param emplId 老客标签建群时代表员工id，sop时，代表群主
     * @param type   类型 0：老客标签建群 1：sop
     * @return 结果
     */
    @GetMapping("/changeStatus")
    @ApiOperation("变更任务状态")
    public AjaxResult changeStatus(@RequestParam("taskId") Long taskId, @RequestParam("emplId") String emplId, @RequestParam("type") Integer type) {
        if (type.equals(0)) {
            return toAjax(tagGroupTaskService.updateEmplTaskStatus(taskId, emplId));
        }
        return AjaxResult.success();
    }

    /**
     * 用于支持H5页面的名称和关键字检索
     *
     * @param word 过滤字符
     * @return 结果
     */
    @GetMapping(path = "/filter")
    @ApiOperation("H5页面的名称和关键字检索")
    @Deprecated
    public TableDataInfo filter(@RequestParam("word") String word) {
        return getDataTable(new ArrayList<>());
    }


    @GetMapping("/sopTaskDetail/{emplId}")
    @ApiOperation("获取员工的SOP任务详情")
    public TableDataInfo<GetSopTaskByUserIdVO> sopTaskDetail(@PathVariable("emplId") String emplId, GetSopTaskDetailDTO getSopTaskDetailDTO) {
        startPage();
        getSopTaskDetailDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        getSopTaskDetailDTO.setUserId(emplId);
        List<GetSopTaskByUserIdVO> list = sopDetailService.getTaskDetailByUserId(getSopTaskDetailDTO);
        return getDataTable(list);
    }


}
