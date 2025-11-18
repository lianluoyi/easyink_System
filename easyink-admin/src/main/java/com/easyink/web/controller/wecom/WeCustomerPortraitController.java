package com.easyink.web.controller.wecom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.annotation.Encrypt;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.enums.MethodParamType;
import com.easyink.wecom.annotation.Convert2Cipher;
import com.easyink.wecom.domain.WeCustomerTrajectory;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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


    private final WeCustomerService weCustomerService;
    private final WeTagGroupService weTagGroupService;
    private final WeUserService weUserService;
    private final WeGroupService weGroupService;
    private final WeCustomerTrajectoryService weCustomerTrajectoryService;
    private final WeOperationsCenterSopDetailService sopDetailService;

    @Autowired
    public WeCustomerPortraitController(WeCustomerService weCustomerService, WeTagGroupService weTagGroupService, WeUserService weUserService, WeGroupService weGroupService, WeCustomerTrajectoryService weCustomerTrajectoryService, WeOperationsCenterSopDetailService sopDetailService) {
        this.weCustomerService = weCustomerService;
        this.weTagGroupService = weTagGroupService;
        this.weUserService = weUserService;
        this.weGroupService = weGroupService;
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
        this.sopDetailService = sopDetailService;
    }

    /**
     * 根据客户id和当前企业员工id获取客户详细信息
     *
     * @param externalUserid
     * @param userId
     * @return
     */
    @ApiOperation("获取客户详细信息")
    @GetMapping(value = "/findWeCustomerInfo")
    @Encrypt
    public AjaxResult findWeCustomerInfo(@NotBlank(message = "外部联系人ID不能为空") String externalUserid, @NotBlank(message = "员工ID不能为空") String userId) {
        return AjaxResult.success(weCustomerService.findCustomerByOperUseridAndCustomerId(externalUserid, userId, LoginTokenService.getLoginUser().getCorpId()));
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
    @Convert2Cipher
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
    @Convert2Cipher
    public AjaxResult<Integer> getUnFinishedCount(@ApiParam("跟进人userId") @NotBlank(message = "参数缺失") String userId,
                                                  @ApiParam("外部联系人userId") @NotBlank(message = "参数缺失") String externalUserid) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        return AjaxResult.success(weCustomerTrajectoryService.count(new LambdaQueryWrapper<WeCustomerTrajectory>()
                .eq(WeCustomerTrajectory::getCorpId, loginUser.getCorpId())
                .ne(WeCustomerTrajectory::getStatus, CustomerTrajectoryEnums.TodoTaskStatusEnum.DEL.getCode())
                .eq(WeCustomerTrajectory::getExternalUserid, externalUserid)
                .eq(WeCustomerTrajectory::getTrajectoryType, CustomerTrajectoryEnums.Type.TO_DO.getDesc())
                .eq(WeCustomerTrajectory::getUserId, userId)
                .in(WeCustomerTrajectory::getStatus, new String[]{CustomerTrajectoryEnums.TodoTaskStatusEnum.NORMAL.getCode(), CustomerTrajectoryEnums.TodoTaskStatusEnum.INFORMED.getCode()})
        ));
    }

    /**
     * 添加或编辑轨迹
     *
     * @param trajectory
     * @return
     */
    @ApiOperation("添加或编辑轨迹")
    @PostMapping(value = "/addOrEditWaitHandle")
    @Convert2Cipher(paramType = MethodParamType.STRUCT)
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
