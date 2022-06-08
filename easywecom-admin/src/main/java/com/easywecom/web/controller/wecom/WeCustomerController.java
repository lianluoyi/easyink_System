package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.wecom.domain.AllocateWeCustomerResp;
import com.easywecom.wecom.domain.WeCustomer;
import com.easywecom.wecom.domain.dto.customer.EditCustomerDTO;
import com.easywecom.wecom.domain.dto.tag.RemoveWeCustomerTagDTO;
import com.easywecom.wecom.domain.entity.WeCustomerExportDTO;
import com.easywecom.wecom.domain.vo.WeLeaveAllocateVO;
import com.easywecom.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easywecom.wecom.domain.vo.WeUserVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerSumVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerUserListVO;
import com.easywecom.wecom.domain.vo.customer.WeCustomerVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 企业微信客户Controller
 *
 * @author admin
 * @date 2020-09-13
 */
@RestController
@RequestMapping("/wecom/customer")
@Slf4j
@Api(tags = "企业微信客户")
public class WeCustomerController extends BaseController {

    @Autowired
    @Lazy
    private WeCustomerService weCustomerService;

    /**
     * 查询企业微信客户列表
     */
    @GetMapping("/list")
    @ApiOperation("查询企业微信客户列表")
    public TableDataInfo<WeCustomer> list(WeCustomer weCustomer) {
        return getDataTable(new ArrayList<>());
    }

    /**
     * @description: 由于之前离职客户和在职客户是分两个表存储, 本期将会把离职客户和在职客户存在同一张表（we_flower_customer_rel）中,用状态做区分
     * @since V1.7
     */
    @GetMapping("/listV2")
    @ApiOperation("查询企业微信客户列表第二版")
    public TableDataInfo<WeCustomerVO> listV2(WeCustomer weCustomer) {
        startPage();
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeCustomerVO> list = weCustomerService.selectWeCustomerListV2(weCustomer);
        return getDataTable(list);
    }

    /**
     * 会话存档客户检索客户列表
     *
     * @description: 因为会话存档模块的客户检索列表需要对客户去重，
     * 因此使用该接口代替原本{@link com.easywecom.web.controller.wecom.WeCustomerController#listV2(com.easywecom.wecom.domain.WeCustomer)} 接口
     */
    @GetMapping("/listDistinct")
    @ApiOperation("会话存档客户检索客户列表")
    public TableDataInfo<WeCustomerVO> listDistinct(WeCustomer weCustomer) {
        startPage();
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeCustomerVO> list = weCustomerService.selectWeCustomerListDistinct(weCustomer);
        return getDataTable(list);
    }

    @GetMapping("/sum")
    @ApiOperation("查询企业客户统计数据")
    public AjaxResult<WeCustomerSumVO> sum(WeCustomer weCustomer) {
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weCustomerService.weCustomerCount(weCustomer));
    }


    /**
     * 根据客户ID获取客户
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('customerManage:lossRemind:view') || @ss.hasPermi('customerManage:customer:view')")
    @GetMapping("/getCustomersByUserId/{externalUserid}/{userId}")
    @ApiOperation("根据客户ID和员工ID获取客户详情")
    @Deprecated
    public AjaxResult<List<WeCustomer>> getCustomersByUserId(@PathVariable String externalUserid, @PathVariable String userId, @ApiParam("对应离职员工的离职时间,不传则获取在职员工客户") Date dimissionTime) {
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('customerManage:lossRemind:view') || @ss.hasPermi('customerManage:customer:view')")
    @GetMapping("/getCustomersByUserIdV2/{externalUserid}/{userId}")
    @ApiOperation("根据客户ID和员工ID获取客户详情V2")
    public AjaxResult<List<WeCustomerVO>> getCustomersByUserIdV2(@PathVariable String externalUserid, @PathVariable String userId) {
        return AjaxResult.success(weCustomerService.getCustomersByUserIdV2(externalUserid, userId, LoginTokenService.getLoginUser().getCorpId()));
    }

    /**
     * 导出企业微信客户列表
     */
    @PreAuthorize("@ss.hasPermi('customerManage:customer:export') || @ss.hasPermi('customerManage:lossRemind:export')")
    @Log(title = "企业微信客户", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation("导出企业微信客户列表")
    public <T> AjaxResult<T> export(WeCustomerExportDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return weCustomerService.export(dto);
    }

    /**
     * 获取企业微信客户详细信息-> 未被使用
     */
    @Deprecated
    @GetMapping(value = "/{externalUserId}")
    @ApiOperation("获取企业微信客户详细信息-> 未被使用")
    public <T> AjaxResult<T> getInfo(@PathVariable("externalUserId") String externalUserId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weCustomerService.selectWeCustomerById(externalUserId, corpId));
    }


    /**
     * 修改企业微信客户
     */
    @PreAuthorize("@ss.hasPermi('customerManage:customer:edit')")
    @Log(title = "企业微信客户", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改企业微信客户")
    public <T> AjaxResult<T> edit(@Validated @RequestBody EditCustomerDTO dto) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        dto.setCorpId(loginUser.getCorpId());
        dto.setUpdateBy(loginUser.getUsername());
        weCustomerService.editCustomer(dto);
        return AjaxResult.success();
    }

    /**
     * 离职分配单个客户
     *
     * @param weLeaveAllocateVO
     * @return
     */
    @PostMapping({"/allocateSingleCustomer"})
    @ApiOperation("离职继承分配")
    @Deprecated
    public AjaxResult<AllocateWeCustomerResp> allocateSingleCustomer(@RequestBody WeLeaveAllocateVO weLeaveAllocateVO) {
        return AjaxResult.success();
    }

    /**
     * 客户同步接口
     *
     * @return
     */
    @Log(title = "企业微信客户同步接口", businessType = BusinessType.DELETE)
    @GetMapping("/synchWeCustomer")
    @PreAuthorize("@ss.hasPermi('customerManage:customer:sync')")
    @ApiOperation("客户同步接口")
    public <T> AjaxResult<T> synchWeCustomer() {

        weCustomerService.syncWeCustomerV2(LoginTokenService.getLoginUser().getCorpId());

        return AjaxResult.success(WeConstans.SYNCH_TIP);
    }

    /**
     * 客户打标签
     *
     * @param weMakeCustomerTag
     * @return
     */
    @Log(title = "客户打标签", businessType = BusinessType.UPDATE)
    @PostMapping("/makeLabel")
    @PreAuthorize("@ss.hasPermi('customerManage:customer:makeTag')")
    @ApiOperation("客户打标签")
    public <T> AjaxResult<T> makeLabel(@Validated @RequestBody WeMakeCustomerTagVO weMakeCustomerTag) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        weMakeCustomerTag.setCorpId(loginUser.getCorpId());
        weMakeCustomerTag.setUpdateBy(loginUser.getUsername());
        weCustomerService.makeLabel(weMakeCustomerTag);

        return AjaxResult.success();
    }

    /**
     * 客户批量打标签
     *
     * @param weMakeCustomerTag
     * @return
     */
    @Log(title = "客户打标签", businessType = BusinessType.UPDATE)
    @PostMapping("/makeLabelbatch")
    @ApiOperation("客户批量打标签")
    public <T> AjaxResult<T> makeLabelbatch(@Validated @RequestBody List<WeMakeCustomerTagVO> weMakeCustomerTag) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        weMakeCustomerTag.forEach(weMakeCustomerTagVO -> weMakeCustomerTagVO.setCorpId(loginUser.getCorpId()));
        weCustomerService.makeLabelbatch(weMakeCustomerTag, loginUser.getUsername());
        return AjaxResult.success();
    }

    /**
     * 移除客户标签
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('customerManage:customer:removeTag')")
    @Log(title = "移除客户标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/removeLabel")
    @ApiOperation("移除客户标签")
    public <T> AjaxResult<T> removeLabel(@RequestBody RemoveWeCustomerTagDTO removeWeCustomerTagDTO) {
        removeWeCustomerTagDTO.getCustomerList().forEach(weUserCustomer -> weUserCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId()));
        weCustomerService.removeLabel(removeWeCustomerTagDTO);

        return AjaxResult.success();

    }

    /**
     * 查询企业微信客户列表(六感)
     */
    @Deprecated
    @GetMapping("/listConcise")
    @ApiOperation("查询企业微信客户列表(六感)")
    public TableDataInfo<WeCustomer> listConcise(WeCustomer weCustomer) {
        return getDataTable(new ArrayList<>());
    }


    @ApiOperation("查询客户所属的员工列表")
    @GetMapping("/listUserByCustomerId/{customerId}")
    public AjaxResult<WeCustomerUserListVO> getCustomerUserList(@PathVariable(value = "customerId") String customerId) {
        List<WeCustomerUserListVO> list = weCustomerService.listUserListByCustomerId(customerId, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(list);
    }

}
