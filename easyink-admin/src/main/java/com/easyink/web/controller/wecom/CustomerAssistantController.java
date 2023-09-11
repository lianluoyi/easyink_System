package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.EmployCodeSourceEnum;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.domain.dto.emplecode.*;
import com.easyink.wecom.domain.vo.*;
import com.easyink.wecom.domain.vo.emple.*;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.CustomerAssistantService;
import com.easyink.wecom.service.WeEmpleCodeChannelService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.service.WeEmpleCodeWarnConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 获客助手相关Controller
 *
 * @author lichaoyu
 * @date 2023/8/15 17:04
 */
@RestController
@RequestMapping("/wecom/customerAssistant")
@Slf4j
@Api(tags = "获客助手Controller")
public class CustomerAssistantController extends BaseController {

    private final WeEmpleCodeService weEmpleCodeService;
    private final CustomerAssistantService customerAssistantService;
    private final WeEmpleCodeWarnConfigService weEmpleCodeWarnConfigService;
    private final WeEmpleCodeChannelService weEmpleCodeChannelService;


    public CustomerAssistantController(WeEmpleCodeService weEmpleCodeService, CustomerAssistantService customerAssistantService, WeEmpleCodeWarnConfigService weEmpleCodeWarnConfigService, WeEmpleCodeChannelService weEmpleCodeChannelService) {
        this.weEmpleCodeService = weEmpleCodeService;
        this.customerAssistantService = customerAssistantService;
        this.weEmpleCodeWarnConfigService = weEmpleCodeWarnConfigService;
        this.weEmpleCodeChannelService = weEmpleCodeChannelService;
    }

    @PreAuthorize("@ss.hasPermi('customer:assistant:add')")
    @PostMapping("/add")
    @ApiOperation("新增获客链接")
    public AjaxResult<Integer> add(@RequestBody AddWeEmpleCodeDTO addWeEmpleCodeDTO) {
        addWeEmpleCodeDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        addWeEmpleCodeDTO.setSource(EmployCodeSourceEnum.CUSTOMER_ASSISTANT.getSource());
        return toAjax(customerAssistantService.insertCustomerAssistant(addWeEmpleCodeDTO));
    }

    @GetMapping("/list")
    @ApiOperation("查询获客链接列表")
    public TableDataInfo<WeEmpleCodeVO> list(FindAssistantDTO findAssistantDTO) {
        findAssistantDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        startPage();
        findAssistantDTO.setSource(EmployCodeSourceEnum.CUSTOMER_ASSISTANT.getSource());
        List<WeEmpleCodeVO> list = weEmpleCodeService.selectAssistantList(findAssistantDTO);
        return getDataTable(list);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation("获取获客链接详细信息")
    public AjaxResult<WeEmpleCodeVO> getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(customerAssistantService.selectCustomerAssistantById(id, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('customer:assistant:edit')")
    @PutMapping("/edit")
    @ApiOperation("修改获客链接")
    public AjaxResult<Integer> edit(@RequestBody AddWeEmpleCodeDTO customerAssistant) {
        customerAssistant.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(customerAssistantService.updateCustomerAssistant(customerAssistant));
    }

    @PreAuthorize("@ss.hasPermi('customer:assistant:delete')")
    @DeleteMapping("/delete/{ids}")
    @ApiOperation("删除/批量删除获客链接")
    public AjaxResult<Integer> delete(@PathVariable("ids") String ids) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return toAjax(customerAssistantService.batchRemoveCustomerAssistant(corpId, ids));
    }

    @PostMapping("/warnConfig")
    @ApiOperation("获客链接告警设置")
    public AjaxResult<Integer> config(@RequestBody EmpleWarnConfigDTO warnConfigDTO) {
        warnConfigDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weEmpleCodeWarnConfigService.saveOrUpdateConfig(warnConfigDTO));
    }

    @GetMapping("/warnConfig")
    @ApiOperation("获客链接告警设置查询")
    public AjaxResult<WeEmpleCodeWarnConfigVO> getConfig() {
        return AjaxResult.success(weEmpleCodeWarnConfigService.getConfig(LoginTokenService.getLoginUser().getCorpId()));
    }

    @PostMapping("/channel/add")
    @ApiOperation("新增自定义渠道")
    public AjaxResult<Integer> addChannel(@RequestBody AddCustomChannelDTO addCustomChannelDTO) {
        addCustomChannelDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weEmpleCodeChannelService.addChannel(addCustomChannelDTO));
    }

    @PutMapping("/channel/edit")
    @ApiOperation("编辑自定义渠道")
    public AjaxResult<Integer> editChannel(@RequestBody EditCustomerChannelDTO editCustomerChannelDTO) {
        editCustomerChannelDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return toAjax(weEmpleCodeChannelService.editChannel(editCustomerChannelDTO));
    }

    @GetMapping("/channel/list")
    @ApiOperation("查询自定义渠道列表")
    public TableDataInfo<WeEmpleCodeChannelVO> listChannel(AddCustomChannelDTO addCustomChannelDTO) {
        addCustomChannelDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weEmpleCodeChannelService.listChannel(addCustomChannelDTO));
    }

    @DeleteMapping("/channel/delete/{channelId}")
    @ApiOperation("删除自定义渠道")
    public AjaxResult<Integer> deleteChannel(@PathVariable("channelId") String channelId) {
        return toAjax(weEmpleCodeChannelService.delChannel(channelId));
    }

    @PreAuthorize("@ss.hasPermi('customer:assistant:situation')")
    @GetMapping("/situation")
    @ApiOperation("主页获客情况查询")
    public AjaxResult<WeEmpleCodeSituationVO> situation() {
        return AjaxResult.success(customerAssistantService.listSituation(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/situation/sync")
    @ApiOperation("同步主页获客情况")
    public AjaxResult<Integer> syncSituation() {
        return AjaxResult.success(customerAssistantService.syncSituation(LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/detail/total/{empleCodeId}")
    @ApiOperation("获客链接详情-数据总览")
    public AjaxResult<CustomerAssistantDetailTotalVO> detailTotal(@PathVariable("empleCodeId") String empleCodeId) {
        return AjaxResult.success(customerAssistantService.detailTotal(empleCodeId, LoginTokenService.getLoginUser().getCorpId()));
    }

    @GetMapping("/detail/getNewAndLossCustomerCnt")
    @ApiOperation("获客链接详情-趋势图查询")
    public AjaxResult<ChannelDetailChartVO> detailChart(FindChannelRangeChartDTO findChannelRangeChartDTO) {
        findChannelRangeChartDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(customerAssistantService.detailChart(findChannelRangeChartDTO));
    }

    @GetMapping("/detail/channel/range")
    @ApiOperation("获客链接详情-渠道新增客户数排行查询")
    public AjaxResult<ChannelDetailRangeVO> detailRange(FindChannelRangeChartDTO findChannelRangeChartDTO) {
        findChannelRangeChartDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(customerAssistantService.detailRange(findChannelRangeChartDTO));
    }

    @GetMapping("/detail/statistic/customer")
    @ApiOperation("获客链接详情-数据统计-客户维度")
    public TableDataInfo<AssistantDetailStatisticCustomerVO> detailStatisticByCustomer(FindAssistantDetailStatisticCustomerDTO findAssistantDetailStatisticCustomerDTO) {
        findAssistantDetailStatisticCustomerDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageInfoUtil.setPage();
        return getDataTable(customerAssistantService.detailStatisticByCustomer(findAssistantDetailStatisticCustomerDTO));
    }

    @GetMapping("/detail/statistic/channel")
    @ApiOperation("获客链接详情-数据统计-渠道维度")
    public TableDataInfo<AssistantDetailStatisticChannelVO> detailStatisticByChannel(FindAssistantDetailStatisticCustomerDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageInfoUtil.setPage();
        return getDataTable(customerAssistantService.detailStatisticByChannel(dto));
    }

    @GetMapping("/detail/statistic/date")
    @ApiOperation("获客链接详情-数据统计-日期维度")
    public TableDataInfo<AssistantDetailStatisticDateVO> detailStatisticByDate(FindAssistantDetailStatisticCustomerDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        List<AssistantDetailStatisticDateVO> list = customerAssistantService.detailStatisticByDate(dto);
        return PageInfoUtil.getDataTable(list, pageDomain.getPageNum(), pageDomain.getPageSize());
    }
    @PreAuthorize("@ss.hasPermi('customer:assistant:export')")
    @GetMapping("/export/detail/statistic/customer")
    @ApiOperation("导出获客链接详情-数据统计-客户维度")
    public AjaxResult exportDetailStatisticByCustomer(FindAssistantDetailStatisticCustomerDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(customerAssistantService.exportDetailStatisticByCustomer(dto));
    }
    @PreAuthorize("@ss.hasPermi('customer:assistant:export')")
    @GetMapping("/export/detail/statistic/channel")
    @ApiOperation("导出获客链接详情-数据统计-渠道维度")
    public AjaxResult exportDetailStatisticByChannel(FindAssistantDetailStatisticCustomerDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(customerAssistantService.exportDetailStatisticByChannel(dto));
    }
    @PreAuthorize("@ss.hasPermi('customer:assistant:export')")
    @GetMapping("/export/detail/statistic/date")
    @ApiOperation("导出获客链接详情-数据统计-日期维度")
    public AjaxResult exportDetailStatisticByDate(FindAssistantDetailStatisticCustomerDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(customerAssistantService.exportDetailStatisticByDate(dto));
    }

}
