package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.dto.transfer.GetResignedTransferDetailDTO;
import com.easyink.wecom.domain.dto.transfer.TransferResignedUserDTO;
import com.easyink.wecom.domain.vo.transfer.GetResignedTransferCustomerDetailVO;
import com.easyink.wecom.domain.vo.transfer.GetResignedTransferGroupDetailVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeResignedTransferRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: 离职继承记录接口 V3
 *
 * @author : silver_chariot
 * @date : 2021/12/6 14:36
 */
@RestController
@RequestMapping("/wecom/resigned/transfer")
@Api(tags = "离职继承记录接口V3")
public class WeResignedTransferRecordController extends BaseController {

    private final WeResignedTransferRecordService weResignedTransferRecordService;

    @Autowired
    public WeResignedTransferRecordController(@NotNull WeResignedTransferRecordService weResignedTransferRecordService) {
        this.weResignedTransferRecordService = weResignedTransferRecordService;
    }

    @PostMapping
    @ApiOperation("离职继承分配V3")
    @ApiResponses({
            @ApiResponse(code = 3005, message = "接替成员不存在或未激活"),
            @ApiResponse(code = 3006, message = "该企业不存在可分配员工"),
            @ApiResponse(code = 3997, message = "该成员不存在可分配客户"),
            @ApiResponse(code = 4011, message = "指定的客户不可被分配"),
            @ApiResponse(code = 4012, message = "指定的群聊不可被分配"),
            @ApiResponse(code = 4013, message = "原跟进人与接手人一样，不可继承")
    })
    public AjaxResult transfer(@Validated @RequestBody TransferResignedUserDTO dto) {
        weResignedTransferRecordService.transfer(
                LoginTokenService.getLoginUser().getCorpId(),
                dto.getHandoverUserList(),
                dto.getTakeoverUserid(),
                dto.getChatId(),
                dto.getExternalUserid()
        );
        return AjaxResult.success();
    }

    @ApiOperation("获取历史已分配客户详情列表V3")
    @GetMapping("/customerRecord")
    public TableDataInfo<GetResignedTransferCustomerDetailVO> customerRecord(@Validated GetResignedTransferDetailDTO dto) {
        startPage();
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<GetResignedTransferCustomerDetailVO> list = weResignedTransferRecordService.listOfCustomerRecord(dto);
        return getDataTable(list);
    }

    @GetMapping("/groupRecord")
    @ApiOperation("获取历史已分配的客户群V3")
    public TableDataInfo<GetResignedTransferGroupDetailVO> groupRecord(@Validated GetResignedTransferDetailDTO dto ){
        startPage();
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<GetResignedTransferGroupDetailVO> list = weResignedTransferRecordService.listOfGroupRecord(dto);
        return getDataTable(list);
    }




}
