package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.wecom.domain.dto.customersop.EditUserDTO;
import com.easywecom.wecom.domain.dto.groupsop.*;
import com.easywecom.wecom.domain.vo.sop.*;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeGroupSopV2Service;
import com.easywecom.wecom.service.WeOperationsCenterSopDetailService;
import com.easywecom.wecom.service.WeOperationsCenterSopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名：WeGroupSopController
 *
 * @author Society my sister Li
 * @date 2021-11-30 14:54
 */
@RestController
@RequestMapping("/wecom/sop")
@Api(tags = "SOP相关接口")
public class WeGroupSopController extends BaseController {

    private final WeGroupSopV2Service weGroupSopV2Service;
    private final WeOperationsCenterSopService sopService;
    private final WeOperationsCenterSopDetailService weOperationsCenterSopDetailService;

    @Autowired
    public WeGroupSopController(WeGroupSopV2Service weGroupSopV2Service, WeOperationsCenterSopService sopService, WeOperationsCenterSopDetailService weOperationsCenterSopDetailService) {
        this.weGroupSopV2Service = weGroupSopV2Service;
        this.sopService = sopService;
        this.weOperationsCenterSopDetailService = weOperationsCenterSopDetailService;
    }

    @Log(title = "新增SOP(客户sop/群sop)", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增SOP(客户sop/群sop)")
    @PreAuthorize("@ss.hasPermi('wecom:groupSop:add') || @ss.hasPermi('wecom:customerSop:add') || @ss.hasPermi('wecom:groupCalendar:add')")
    public AjaxResult add(@Validated @RequestBody AddWeGroupSopDTO addWeGroupSopDTO) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        addWeGroupSopDTO.setCreateBy(loginUser.getWeUser() == null ? loginUser.getUsername() : loginUser.getWeUser().getUserId());
        addWeGroupSopDTO.setCorpId(loginUser.getCorpId());
        weGroupSopV2Service.addSop(addWeGroupSopDTO);
        return AjaxResult.success();
    }


    @Log(title = "删除SOP(定时SOP/循环SOP)", businessType = BusinessType.DELETE)
    @DeleteMapping("/del")
    @ApiOperation("删除SOP(定时SOP/循环SOP)")
    @PreAuthorize("@ss.hasPermi('wecom:groupSop:del') || @ss.hasPermi('wecom:customerSop:del') || @ss.hasPermi('wecom:groupCalendar:del')")
    public AjaxResult delSop(@Validated @RequestBody DelWeGroupSopDTO delWeGroupSopDTO) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        delWeGroupSopDTO.setCorpId(loginUser.getCorpId());
        weGroupSopV2Service.delSop(delWeGroupSopDTO);
        return AjaxResult.success();
    }


    @Log(title = "查询SOP列表", businessType = BusinessType.OTHER)
    @GetMapping("/list")
    @ApiOperation("查询SOP列表")
    public TableDataInfo<BaseWeOperationsCenterSopVo> list(@Validated FindWeGroupSopDTO findWeGroupSopDTO) {
        startPage();
        List<BaseWeOperationsCenterSopVo> list = sopService.list(LoginTokenService.getLoginUser().getCorpId(), findWeGroupSopDTO.getSopType(), findWeGroupSopDTO.getName(), findWeGroupSopDTO.getUserName(), findWeGroupSopDTO.getIsOpen());
        return getDataTable(list);
    }


    @Log(title = "SOP批量开关", businessType = BusinessType.UPDATE)
    @PutMapping("/batchSwitch")
    @ApiOperation("SOP批量开关")
    @PreAuthorize("@ss.hasPermi('wecom:groupSop:switch') || @ss.hasPermi('wecom:customerSop:switch') || @ss.hasPermi('wecom:groupCalendar:switch')")
    public AjaxResult batchSwitch(@Validated @RequestBody SopBatchSwitchDTO switchDTO) {
        switchDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        sopService.batchSwitch(switchDTO);
        return AjaxResult.success();
    }

    @Log(title = "修改SOP", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("修改SOP")
    @PreAuthorize("@ss.hasPermi('wecom:groupSop:edit') || @ss.hasPermi('wecom:customerSop:edit') || @ss.hasPermi('wecom:groupCalendar:edit')")
    public AjaxResult update(@Validated @RequestBody UpdateWeSopDTO updateWeSopDTO) {
        updateWeSopDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weGroupSopV2Service.update(updateWeSopDTO);
        return AjaxResult.success();
    }

    @Log(title = "修改SOP使用员工", businessType = BusinessType.UPDATE)
    @PutMapping("/editUser")
    @ApiOperation("修改SOP使用员工")
    @PreAuthorize("@ss.hasPermi('wecom:customerSop:edit')")
    public AjaxResult editUser(@Validated @RequestBody EditUserDTO updateWeSopDTO) {
        updateWeSopDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        sopService.editUser(updateWeSopDTO);
        return AjaxResult.success();
    }

    @ApiOperation("查询客户SOP类型的执行详情")
    @GetMapping("/detail/list/customer")
    public TableDataInfo<WeOperationsCenterSopDetailCustomerVO> listByCustomer(@Validated FindWeSopDetailDTO findWeSopDetailDTO){
        startPage();
        findWeSopDetailDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeOperationsCenterSopDetailCustomerVO> list = weOperationsCenterSopDetailService.getSopDetailBySopIdWithCustomerType(findWeSopDetailDTO);
        return getDataTable(list);
    }

    @ApiOperation("查询群SOP定时类型的执行详情")
    @GetMapping("/detail/list/timing")
    public TableDataInfo<WeOperationsCenterSopDetailByTimingTypeVO> listByTiming(@Validated FindWeSopDetailDTO findWeSopDetailDTO){
        startPage();
        findWeSopDetailDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeOperationsCenterSopDetailByTimingTypeVO> list = weOperationsCenterSopDetailService.getSopDetailBySopIdWithTimingType(findWeSopDetailDTO);
        return getDataTable(list);
    }

    @ApiOperation("查询群SOP循环类型的执行详情")
    @GetMapping("/detail/list/cycle")
    public TableDataInfo<WeOperationsCenterSopDetailByCycleTypeVO> listByCycle(@Validated FindWeSopDetailDTO findWeSopDetailDTO){
        startPage();
        findWeSopDetailDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeOperationsCenterSopDetailByCycleTypeVO> list = weOperationsCenterSopDetailService.getSopDetailBySopIdWithCycleType(findWeSopDetailDTO);
        return getDataTable(list);
    }


    @GetMapping("/listOfDetail")
    @ApiOperation("sop详情")
    public AjaxResult<SopDetailVO> listOfDetail(@RequestParam("sopId") Long sopId) {
        return AjaxResult.success(sopService.listOfDetail(sopId, LoginTokenService.getLoginUser().getCorpId()));
    }

    @Log(title = "sop规则执行记录分页", businessType = BusinessType.OTHER)
    @ApiOperation("sop规则执行记录分页")
    @GetMapping("/rules")
    public TableDataInfo<WeSopExecutedRulesVO> listRecordByRule(@Validated FindWeSopExecutedRulesDTO findWeSopExecutedRulesDTO){
        startPage();
        findWeSopExecutedRulesDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeSopExecutedRulesVO> list = weOperationsCenterSopDetailService.getSopExecutedRulesBySopId(findWeSopExecutedRulesDTO);
        return getDataTable(list);
    }

    @Log(title = "sop员工执行记录分页", businessType = BusinessType.OTHER)
    @ApiOperation("sop员工执行记录分页")
    @GetMapping("/users")
    public TableDataInfo<WeSopExecutedUsersVO> listRecordByUser(@Validated FindWeSopExecutedUsersDTO findWeSopExecutedUsersDTO){
        startPage();
        findWeSopExecutedUsersDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeSopExecutedUsersVO> list = weOperationsCenterSopDetailService.getSopExecutedUsersBySopId(findWeSopExecutedUsersDTO);
        return getDataTable(list);
    }

    @Log(title = "sop任务统计", businessType = BusinessType.OTHER)
    @ApiOperation("sop任务统计")
    @GetMapping("/count")
    public AjaxResult<WeSopTaskCountVO> taskCount(@RequestParam("sopId") String sopId){
        return AjaxResult.success(weOperationsCenterSopDetailService.taskCount(sopId, LoginTokenService.getLoginUser().getCorpId()));
    }

}
