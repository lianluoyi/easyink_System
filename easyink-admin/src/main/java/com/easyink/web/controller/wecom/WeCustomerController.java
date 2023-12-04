package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.dto.WeCustomerSearchDTO;
import com.easyink.wecom.domain.dto.customer.EditCustomerDTO;
import com.easyink.wecom.domain.dto.tag.RemoveWeCustomerTagDTO;
import com.easyink.wecom.domain.entity.WeCustomerExportDTO;
import com.easyink.wecom.domain.vo.WeMakeCustomerTagVO;
import com.easyink.wecom.domain.vo.customer.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.utils.OprIdGenerator;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.easyink.common.constant.Constants.EXPORT_MAX_WAIT_TIME;

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

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

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
    @PostMapping("/listV2")
    @ApiOperation("查询企业微信客户列表第二版")
    public TableDataInfo<WeCustomerVO> listV2(@RequestBody WeCustomerSearchDTO weCustomerSearchDTO) {
        Integer pageNum = weCustomerSearchDTO.getPageNum();
        Integer pageSize = weCustomerSearchDTO.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            //预设分页参数
            PageInfoUtil.setPage(pageNum, pageSize);
        }
        weCustomerSearchDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        WeCustomer weCustomer=weCustomerService.changeWecustomer(weCustomerSearchDTO);
        List<WeCustomerVO> list = weCustomerService.selectWeCustomerListV3(weCustomer);
        return getDataTable(list);
    }

    /**
     * 会话存档客户检索客户列表
     *
     * @description: 因为会话存档模块的客户检索列表需要对客户去重，
     * 因此使用该接口代替原本{@link com.easyink.web.controller.wecom.WeCustomerController# listV2(com.easyink.wecom.domain.WeCustomer)} 接口
     */
    @GetMapping("/listDistinct")
    @ApiOperation("会话存档客户检索客户列表")
    public TableDataInfo<SessionArchiveCustomerVO> listDistinct(WeCustomer weCustomer) {
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageInfoUtil.setPage();
        List<SessionArchiveCustomerVO> list = weCustomerService.selectWeCustomerListDistinctV3(weCustomer);
        return getDataTable(list);
    }

    @GetMapping("/listDistinct/count")
    @ApiOperation("会话存档客户检索客户列表-去重后的客户总数")
    public AjaxResult<WeCustomerSumVO> listDistinctCount(WeCustomer weCustomer) {
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weCustomerService.customerCount(weCustomer));
    }

    @PostMapping("/sum")
    @ApiOperation("查询企业客户统计数据")
    public AjaxResult<WeCustomerSumVO> sum(@RequestBody WeCustomerSearchDTO weCustomerSearchDTO) {
        weCustomerSearchDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        WeCustomer weCustomer=weCustomerService.changeWecustomer(weCustomerSearchDTO);
        weCustomer.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weCustomerService.weCustomerCount(weCustomer));
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
//    @PostMapping("/export")
    @ApiOperation("导出企业微信客户列表")
    @Deprecated
    public <T> AjaxResult<T> export(@RequestBody WeCustomerExportDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        log.info("[导出客户]开始导出,corpId:{}", dto.getCorpId());
        long startTime = System.currentTimeMillis();
        AjaxResult<T> export = weCustomerService.export(dto);
        long endTime = System.currentTimeMillis();
        log.info("[导出客户]导出完成,corpId:{} , time:{} ", dto.getCorpId(), (endTime - startTime) / 1000.00D);
        return export;
    }

    /**
     * 导出企业微信客户列表V2
     */
    @PreAuthorize("@ss.hasPermi('customerManage:customer:export') || @ss.hasPermi('customerManage:lossRemind:export')")
    @Log(title = "企业微信客户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出企业微信客户列表")
    public AjaxResult<ExportOprVO> exportV2(@RequestBody WeCustomerExportDTO dto) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        dto.setCorpId(loginUser.getCorpId());
        dto.setAdmin(loginUser.isSuperAdmin());
        String oprId = OprIdGenerator.EXPORT.get();
        String fileName = UUID.randomUUID() + "_" + "customer" + ".xlsx";
        ExportOprVO result = ExportOprVO.builder().oprId(oprId).fileName(fileName).hasFinished(false).build();
        if(dto.getSelectedProperties() == null || dto.getSelectedProperties().size() == 0) {
            dto.setSelectedProperties(            Lists.newArrayList("客户","添加时间","所属员工","标签"));
        }
        WeCustomerExportDTO customer = weCustomerService.transferData(dto);
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            // 执行异步处理任务
            weCustomerService.genExportData(customer, oprId, fileName);
        }, threadPoolTaskExecutor);
        try {
            // 在3秒内等待异步处理任务完成
            future.get(EXPORT_MAX_WAIT_TIME, TimeUnit.SECONDS);
            result.setHasFinished(true);
            return AjaxResult.success(result);
        } catch (TimeoutException e) {
            // 处理未完成，只返回OprId
            return AjaxResult.success(result);
        } catch (InterruptedException | ExecutionException e) {
            // 处理出现异常
            return AjaxResult.error();
        }
    }


    @GetMapping("/export/result")
    @ApiOperation("获取导出客户的结果")
    public AjaxResult getExportResult(String oprId) {
        return AjaxResult.success(new CustomerExportResultVO(
                weCustomerService.getExportResult(oprId)
                )
        );
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
        weCustomerService.batchMakeLabel(weMakeCustomerTag, loginUser.getUsername());
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

    @ApiOperation("查询客户所属的员工列表")
    @GetMapping("/listUserByCustomerId/{customerId}")
    public AjaxResult<WeCustomerUserListVO> getCustomerUserList(@PathVariable(value = "customerId") String customerId) {
        List<WeCustomerUserListVO> list = weCustomerService.listUserListByCustomerId(customerId, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(list);
    }

}
