package com.easywecom.web.controller.wecom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.CustomerTrajectoryEnums;
import com.easywecom.wecom.domain.WeCustomerPortrait;
import com.easywecom.wecom.domain.WeCustomerTrajectory;
import com.easywecom.wecom.domain.WeTagGroup;
import com.easywecom.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.*;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author admin
 * @description: 客户画像相关controller
 * @create: 2021-03-03 15:10
 **/
@RestController
@RequestMapping("/wecom/portrait")
@Api(tags = "客户画像相关controller")
public class WeCustomerPortraitController extends BaseController {


    @Autowired
    private WeCustomerService weCustomerService;


    @Autowired
    private WeTagGroupService weTagGroupService;


    @Autowired
    private WeUserService weUserService;


    @Autowired
    private WeGroupService weGroupService;


    @Autowired
    private WeCustomerTrajectoryService weCustomerTrajectoryService;
    @Autowired
    private WeOperationsCenterSopDetailService sopDetailService;


    /**
     * 根据客户id和当前企业员工id获取客户详细信息
     *
     * @param externalUserid
     * @param userId
     * @return
     */
    @ApiOperation("获取客户详细信息")
    @GetMapping(value = "/findWeCustomerInfo")
    public AjaxResult findWeCustomerInfo(@NotBlank(message = "外部联系人ID不能为空") String externalUserid, @NotBlank(message = "员工ID不能为空") String userId) {
        return AjaxResult.success(weCustomerService.findCustomerByOperUseridAndCustomerId(externalUserid, userId, LoginTokenService.getLoginUser().getCorpId()));
    }


    /**
     * 客户画像资料更新
     *
     * @param weCustomerPortrait
     * @return
     */
    @ApiOperation("客户画像资料更新")
    @PostMapping(value = "/updateWeCustomerInfo")
    @Deprecated
    public AjaxResult updateWeCustomerInfo(@RequestBody WeCustomerPortrait weCustomerPortrait) {
        return AjaxResult.success();
    }


    /**
     * 获取当前系统所有可用标签
     *
     * @return
     */
    @ApiOperation("获取当前系统所有可用标签")
    @GetMapping(value = "/findAllTags")
    public AjaxResult findAllTags() {
        return AjaxResult.success(weTagGroupService.selectWeTagGroupList(WeTagGroup.builder()
                .corpId(LoginTokenService.getLoginUser().getCorpId())
                .build()));
    }


    /**
     * 更新客户画像标签
     *
     * @param weMakeCustomerTag
     * @return
     */
    @ApiOperation("更新客户画像标签")
    @PostMapping(value = "/updateWeCustomerPorTraitTag")
    public AjaxResult updateWeCustomerPorTraitTag(@Validated @RequestBody WeMakeCustomerTagVO weMakeCustomerTag) {
        weMakeCustomerTag.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCustomerService.makeLabel(weMakeCustomerTag);
        return AjaxResult.success();
    }


    /**
     * 查看客户添加的员工
     *
     * @param externalUserid
     * @return
     */
    @ApiOperation("查看客户添加的员工")
    @GetMapping(value = "/findAddaddEmployes/{externalUserid}")
    public AjaxResult findaddEmployes(@PathVariable String externalUserid) {
        return AjaxResult.success(
                weUserService.findWeUserByCutomerId(LoginTokenService.getLoginUser().getCorpId(),externalUserid)
        );
    }


    /**
     * 获取用户添加的群
     *
     * @param externalUserid
     * @param userId
     * @return
     */
    @ApiOperation("获取用户添加的群")
    @GetMapping(value = "/findAddGroupNum")
    public AjaxResult findAddGroupNum(@NotBlank(message = "外部联系人ID不能为空") String externalUserid, @NotBlank(message = "员工ID不能为空") String userId) {
        return AjaxResult.success(weGroupService.findWeGroupByCustomer(userId, externalUserid, LoginTokenService.getLoginUser().getCorpId()));
    }


    /**
     * 获取轨迹信息
     *
     * @param trajectoryType
     * @return
     */
    @ApiOperation("获取轨迹信息")
    @GetMapping(value = "/findTrajectory")
    public TableDataInfo<WeCustomerTrajectory> findTrajectory(String userId, String externalUserid, Integer trajectoryType) {
        startPage();
        LoginUser loginUser = LoginTokenService.getLoginUser();

        return getDataTable(
                weCustomerTrajectoryService.listOfTrajectory(loginUser.getCorpId(), externalUserid, trajectoryType, userId)
        );
    }

    @ApiOperation("修改任务完成状态")
    @PutMapping(value = "/finishTask")
    public AjaxResult finishTask(@RequestParam("detailId") Long detailId){
        sopDetailService.finishTask(LoginTokenService.getLoginUser().getCorpId(),detailId);
        return AjaxResult.success();
    }

    @ApiOperation("获取待办事项数量")
    @GetMapping(value = "/todoCount")
    public AjaxResult<Integer> getUnFinishedCount(@ApiParam("跟进人userId") @NotBlank(message = "参数缺失") String userId,
                                                  @ApiParam("外部联系人userId") @NotBlank(message = "参数缺失") String externalUserid) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        return AjaxResult.success(weCustomerTrajectoryService.count(new LambdaQueryWrapper<WeCustomerTrajectory>()
                .eq(WeCustomerTrajectory::getCorpId, loginUser.getCorpId())
                .ne(WeCustomerTrajectory::getStatus, CustomerTrajectoryEnums.TodoTaskStatusEnum.DEL.getCode())
                .eq(WeCustomerTrajectory::getExternalUserid, externalUserid)
                .eq(WeCustomerTrajectory::getTrajectoryType, CustomerTrajectoryEnums.Type.TO_DO.getType())
                .eq(WeCustomerTrajectory::getUserId, userId)
                .in(WeCustomerTrajectory::getStatus, new String[]{CustomerTrajectoryEnums.TodoTaskStatusEnum.NORMAL.getCode(), CustomerTrajectoryEnums.TodoTaskStatusEnum.INFORMED.getCode()})
        ));
    }

    @Override
    protected <T> TableDataInfo getDataTable(PageInfo<T> pageInfo) {
        return super.getDataTable(pageInfo);
    }

    /**
     * 添加或编辑轨迹
     *
     * @param trajectory
     * @return
     */
    @ApiOperation("添加或编辑轨迹")
    @PostMapping(value = "/addOrEditWaitHandle")
    public AjaxResult addOrEditWaitHandle(@RequestBody WeCustomerTrajectory trajectory) {
        trajectory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCustomerTrajectoryService.saveOrUpdate(trajectory);
        return AjaxResult.success();
    }


    /**
     * 删除轨迹
     *
     * @param trajectoryId
     * @return
     */
    @ApiOperation("删除轨迹")
    @DeleteMapping(value = "/removeTrajectory/{trajectoryId}")
    public AjaxResult removeTrajectory(@PathVariable String trajectoryId) {
        weCustomerTrajectoryService.updateById(WeCustomerTrajectory.builder()
                .id(trajectoryId)
                .status(CustomerTrajectoryEnums.TodoTaskStatusEnum.DEL.getCode())
                .build());
        return AjaxResult.success();
    }


    /**
     * 完成待办
     *
     * @param trajectoryId
     * @return
     */
    @ApiOperation("完成待办")
    @DeleteMapping(value = "/handleWait/{trajectoryId}")
    public AjaxResult handleWait(@PathVariable String trajectoryId) {
        weCustomerTrajectoryService.updateById(WeCustomerTrajectory.builder()
                .id(trajectoryId)
                .status(CustomerTrajectoryEnums.TodoTaskStatusEnum.FINISHED.getCode())
                .build());
        return AjaxResult.success();
    }


}
