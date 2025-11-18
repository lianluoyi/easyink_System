package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.dto.WeCustomerMessageDTO;
import com.easyink.wecom.domain.dto.WeCustomerMessagePushResultDTO;
import com.easyink.wecom.domain.dto.WeCustomerMessageToUserDTO;
import com.easyink.wecom.domain.dto.message.AsyncResultDTO;
import com.easyink.wecom.domain.dto.message.CustomerMessagePushDTO;
import com.easyink.wecom.domain.vo.CustomerMessagePushVO;
import com.easyink.wecom.domain.vo.WeCustomerMessageResultVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeCustomerMessageOriginalService;
import com.easyink.wecom.service.WeCustomerMessagePushService;
import com.easyink.wecom.service.WeCustomerMessgaeResultService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * 类名： 企业微信 群发消息
 *
 * @author 佚名
 * @date 2021/9/13 18:21
 */
@Slf4j
@RestController
@RequestMapping("/wecom/customerMessagePush")
@Api(value = "WeCustomerMessagePushController", tags = "企业微信群发消息接口")
public class WeCustomerMessagePushController extends BaseController {
    private final WeCustomerMessagePushService weCustomerMessagePushService;
    private final WeCustomerMessageOriginalService weCustomerMessageOriginalService;
    private final WeCustomerMessgaeResultService weCustomerMessgaeResultService;

    @Autowired
    public WeCustomerMessagePushController(WeCustomerMessagePushService weCustomerMessagePushService, WeCustomerMessageOriginalService weCustomerMessageOriginalService, WeCustomerMessgaeResultService weCustomerMessgaeResultService) {
        this.weCustomerMessagePushService = weCustomerMessagePushService;
        this.weCustomerMessageOriginalService = weCustomerMessageOriginalService;
        this.weCustomerMessgaeResultService = weCustomerMessgaeResultService;
    }

    @PreAuthorize("@ss.hasPermi('customerMessagePush:push:add')")
    @Log(title = "新增群发消息发送", businessType = BusinessType.INSERT)
    @PostMapping(value = "add")
    @ApiOperation("新增群发消息发送")
    public <T> AjaxResult<T> add(@Validated @RequestBody CustomerMessagePushDTO customerMessagePushDTO) {
        try {
            customerMessagePushDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
            weCustomerMessagePushService.addWeCustomerMessagePush(customerMessagePushDTO, LoginTokenService.getLoginUser());
        } catch (JsonProcessingException | ParseException | CloneNotSupportedException e) {
            log.error("新增群发消息发送异常 ex：{}", ExceptionUtils.getStackTrace(e));
            return AjaxResult.error("群发失败");
        }
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('customerMessagePush:push:list')")
    @GetMapping(value = "/list")
    @ApiOperation("获取群发消息列表")
    public TableDataInfo<CustomerMessagePushVO> list(@RequestParam(value = "sender", required = false) String sender
            , @RequestParam(value = "content", required = false) String content
            , @RequestParam(value = "pushType", required = false) String pushType
            , @RequestParam(value = "beginTime", required = false) String beginTime
            , @RequestParam(value = "endTime", required = false) String endTime) {
        startPage();
        LoginUser loginUser = LoginTokenService.getLoginUser();
        WeCustomerMessageDTO weCustomerMessageDTO = new WeCustomerMessageDTO(loginUser.isSuperAdmin(), sender, content, pushType, DateUtils.parseBeginDay(beginTime), DateUtils.parseEndDay(endTime));
        weCustomerMessageDTO.setCorpId(loginUser.getCorpId());
        List<CustomerMessagePushVO> list = weCustomerMessagePushService.customerMessagePushs(weCustomerMessageDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('customerMessagePush:push:view')")
    @GetMapping(value = "/getInfo")
    @ApiOperation("群发消息详情")
    public <T> AjaxResult<T> getInfo(@RequestParam(value = "messageId") Long messageId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        CustomerMessagePushVO customerMessagePushVo = weCustomerMessageOriginalService.customerMessagePushDetail(messageId, corpId);
        return AjaxResult.success(customerMessagePushVo);
    }

    @GetMapping(value = "/getMessageTask")
    @ApiOperation("群发任务数据回显（编辑时使用）")
    public AjaxResult<CustomerMessagePushDTO> getMessageTask(@RequestParam(value = "messageId") Long messageId){
        return AjaxResult.success(weCustomerMessagePushService.getCopyInfo(messageId));
    }

    @GetMapping(value = "/getCopyInfo")
    @ApiOperation("群发任务复制")
    public AjaxResult<CustomerMessagePushDTO> getCopyInfo(@RequestParam(value = "messageId") Long messageId) {
        return AjaxResult.success(weCustomerMessagePushService.getCopyInfo(messageId));
    }

    @PostMapping(value = "/getSendSize")
    @ApiOperation("获取消息发送人数")
    public AjaxResult getSendSize(@Validated @RequestBody CustomerMessagePushDTO customerMessagePushDTO) {
        return AjaxResult.success(weCustomerMessagePushService.getSendSize(customerMessagePushDTO, LoginTokenService.getLoginUser()));
    }


    @Log(title = "修改定时群发消息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/update")
    @ApiOperation("修改定时群发消息")
    public AjaxResult update(@Validated @RequestBody CustomerMessagePushDTO customerMessagePushDTO){
        try {
            weCustomerMessagePushService.updateTimeTask(customerMessagePushDTO, LoginTokenService.getLoginUser());
        } catch (ParseException e) {
            log.error("修改定时群发异常：ex:{}", ExceptionUtils.getStackTrace(e));
        }
        return AjaxResult.success();
    }

    @Log(title = "删除定时群发消息", businessType = BusinessType.INSERT)
    @DeleteMapping(value = "/delete")
    @ApiOperation("删除定时群发消息")
    public AjaxResult delete(@RequestParam(value = "messageId") Long messageId) {
        weCustomerMessagePushService.delete(messageId, LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @PostMapping(value = "/pushResults")
    @ApiOperation("群发消息结果列表")
    public TableDataInfo<WeCustomerMessageResultVO> customerMessagePushResult(@Validated @RequestBody WeCustomerMessagePushResultDTO weCustomerMessagePushResultDTO) {
        weCustomerMessagePushResultDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (StringUtils.isNotNull(weCustomerMessagePushResultDTO.getPageNum()) && StringUtils.isNotNull(weCustomerMessagePushResultDTO.getPageSize())) {
            PageHelper.startPage(weCustomerMessagePushResultDTO.getPageNum(), weCustomerMessagePushResultDTO.getPageSize());
        }
        List<WeCustomerMessageResultVO> weCustomerMessageResuls = weCustomerMessgaeResultService.customerMessagePushs(weCustomerMessagePushResultDTO);
        return getDataTable(weCustomerMessageResuls);
    }

    @PreAuthorize("@ss.hasPermi('customerMessagePush:push:asyncResult')")
    @PostMapping(value = "/asyncResult")
    @ApiOperation("同步消息发送结果")
    @ApiResponses(
            {@ApiResponse(code = 2041, message = "正在派送群发任务，请稍后再试")}
    )
    public <T> AjaxResult<T> asyncResult(@RequestBody AsyncResultDTO asyncResultDTO) throws JsonProcessingException {
        weCustomerMessageOriginalService.asyncResult(asyncResultDTO.getMessageIdDTOList(), LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @PostMapping(value = "/sendToUser")
    @ApiOperation("发送消息提醒员工接口")
    public AjaxResult sendToUser(@Validated @RequestBody WeCustomerMessageToUserDTO weCustomerMessageToUserDTO) {
        weCustomerMessageToUserDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCustomerMessagePushService.sendToUser(weCustomerMessageToUserDTO);
        return AjaxResult.success();
    }


}
