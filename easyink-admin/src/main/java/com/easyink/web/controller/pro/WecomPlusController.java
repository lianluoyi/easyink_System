package com.easyink.web.controller.pro;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.dto.pro.EditCustomerFromPlusDTO;
import com.easyink.wecom.domain.dto.pro.QueryCustomerFromPlusDTO;
import com.easyink.wecom.domain.vo.QueryCustomerFromPlusVO;
import com.easyink.wecom.service.WeCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 类名: 对企微PRO提供的接口控制层
 *
 * @author : silver_chariot
 * @date : 2021/11/2 11:27
 */
@RestController
@RequestMapping("/wecom/plus")
@Api(tags = "对企微PRO提供的接口")
@Slf4j
public class WecomPlusController extends BaseController {

    private final WeCustomerService weCustomerService;

    @Autowired
    @Lazy
    public WecomPlusController(@NotNull WeCustomerService weCustomerService) {
        this.weCustomerService = weCustomerService;
    }

    @GetMapping("/queryCustomer")
    @ApiOperation("根据成员id和客户头像查询客户详情")
    public AjaxResult<QueryCustomerFromPlusVO> queryCustomer(@Validated QueryCustomerFromPlusDTO dto) {
        return AjaxResult.success(weCustomerService.getDetailByUserIdAndCustomerAvatar(
                dto.getCorpId(), dto.getUserId(), dto.getAvatar()
        ));
    }

    @PostMapping("/editCustomer")
    @ApiOperation("修改客户资料")
    public AjaxResult editCustomer(@RequestBody @Validated EditCustomerFromPlusDTO dto) {
        weCustomerService.editByUserIdAndCustomerAvatar(dto);
        return AjaxResult.success();
    }


}
