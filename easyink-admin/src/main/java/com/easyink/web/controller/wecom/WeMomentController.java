package com.easyink.web.controller.wecom;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.easyink.common.annotation.RepeatSubmit;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.dto.moment.CreateMomentTaskDTO;
import com.easyink.wecom.domain.dto.moment.MomentUserCustomerDTO;
import com.easyink.wecom.domain.dto.moment.SearchMomentContentDTO;
import com.easyink.wecom.domain.dto.moment.SendToUserDTO;
import com.easyink.wecom.domain.vo.moment.MomentTotalVO;
import com.easyink.wecom.domain.vo.moment.MomentUserCustomerVO;
import com.easyink.wecom.domain.vo.moment.SearchMomentVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.moment.WeMomentTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名： 朋友圈接口
 *
 * @author 佚名
 * @date 2022/1/11 11:29
 */
@Slf4j
@RestController
@RequestMapping("/wecom/moment")
@Api(tags = "朋友圈")
public class WeMomentController extends BaseController {
    private final WeMomentTaskService weMomentTaskService;

    @Autowired
    public WeMomentController(WeMomentTaskService weMomentTaskService) {
        this.weMomentTaskService = weMomentTaskService;
    }

    @PreAuthorize("@ss.hasPermi('wecom:moments:publish')")
    @PostMapping("/create")
    @ApiOperation("创建朋友圈任务")
    @RepeatSubmit
    public AjaxResult createMoment(@Validated @RequestBody CreateMomentTaskDTO createMomentTaskDTO) {
        createMomentTaskDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weMomentTaskService.createMomentTask(createMomentTaskDTO, LoginTokenService.getLoginUser());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:moments:del')")
    @DeleteMapping("/deleteMoment")
    @ApiOperation("删除朋友圈")
    public AjaxResult deleteMoment(@RequestParam("momentTaskId") Long momentTaskId) {
        weMomentTaskService.deleteMoment(momentTaskId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:moments:edit')")
    @PostMapping("/updateMoment")
    @ApiOperation("编辑朋友圈")
    @RepeatSubmit
    public AjaxResult updateMoment(@RequestBody CreateMomentTaskDTO createMomentTaskDTO) {
        weMomentTaskService.updateMoment(createMomentTaskDTO);
        return AjaxResult.success();
    }

    @GetMapping("/listOfMomentTask")
    @ApiOperation("查询朋友圈发布记录")
    public TableDataInfo<SearchMomentVO> listOfMomentTask(@Validated SearchMomentContentDTO searchMomentContentDTO) {
        searchMomentContentDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weMomentTaskService.listOfMomentTask(searchMomentContentDTO.initTime(), LoginTokenService.getLoginUser()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:moments:detail')")
    @GetMapping("/listOfMomentPublishDetail")
    @ApiOperation("查询朋友圈发布记录详情")
    public TableDataInfo<MomentUserCustomerVO> listOfMomentPublishDetail(@Validated MomentUserCustomerDTO momentUserCustomerDTO) {
        PageDomain pageDomain = startPageManual();
        TimeInterval timer = DateUtil.timer();
        List<MomentUserCustomerVO> list = weMomentTaskService.listOfMomentPublishDetail(momentUserCustomerDTO, pageDomain);
        log.info("查询朋友圈发布记录详情: {}毫秒", timer.interval());
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('wecom:moments:detail')")
    @GetMapping("/getTotal")
    @ApiOperation("获取详情统计接口")
    public AjaxResult<MomentTotalVO> getTotal(@RequestParam("momentTaskId") Long momentTaskId) {
        return AjaxResult.success(weMomentTaskService.getTotal(momentTaskId));
    }

    @PreAuthorize("@ss.hasPermi('wecom:moments:detail')")
    @GetMapping("/getMomentTaskBasicInfo")
    @ApiOperation("获取朋友圈任务基础信息")
    public AjaxResult<SearchMomentVO> getMomentTaskBasicInfo(@RequestParam("momentTaskId") Long momentTaskId) {
        return AjaxResult.success(weMomentTaskService.getMomentTaskBasicInfo(momentTaskId));
    }
    @PutMapping("/refreshMomentTask")
    @ApiOperation("刷新朋友圈执行")
    public AjaxResult refreshMomentTask(@RequestParam("momentTaskId") Long momentTaskId) {
        weMomentTaskService.refreshMoment(momentTaskId);
        return AjaxResult.success();
    }

    @PostMapping("/sendToUser")
    @ApiOperation("发送提醒信息")
    public AjaxResult sendToUser(@Validated @RequestBody SendToUserDTO sendToUserDTO) {
        weMomentTaskService.sendToUser(sendToUserDTO.getUserIds(), sendToUserDTO.getType(), sendToUserDTO.getSendTime(), sendToUserDTO.getMomentTaskId());
        return AjaxResult.success();
    }

    @PutMapping("/updateUserMoment")
    @ApiOperation("修改员工朋友圈执行详情（h5个人朋友圈）")
    public AjaxResult updateUserMoment(@RequestParam("momentTaskId") Long momentTaskId, @RequestParam("userId") String userId) {
        weMomentTaskService.updateUserMoment(momentTaskId, userId);
        return AjaxResult.success();
    }

    @GetMapping("/getMomentTask")
    @ApiOperation("h5查询接口")
    public AjaxResult<SearchMomentVO> getMomentTask(@RequestParam("momentTaskId") Long momentTaskId, @RequestParam("userId") String userId) {
        return AjaxResult.success(weMomentTaskService.getMomentTask(momentTaskId, userId));
    }

}
