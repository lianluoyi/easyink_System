package com.easywecom.web.controller.wecom;

import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.utils.bean.BeanUtils;
import com.easywecom.wecom.domain.dto.BatchSaveCustomerExtendPropertyDTO;
import com.easywecom.wecom.domain.dto.QueryCustomerExtendPropertyDTO;
import com.easywecom.wecom.domain.dto.SaveCustomerExtendPropertyDTO;
import com.easywecom.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeCustomerExtendPropertyService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: 员工扩展属性接口
 *
 * @author : silver_chariot
 * @date : 2021/11/10 18:01
 */
@RequestMapping("/wecom/extendProperty")
@RestController
@Api(tags = "客户扩展属性接口")
public class WeCustomerExtendPropertyController extends BaseController {
    private final WeCustomerExtendPropertyService weCustomerExtendPropertyService;

    @Autowired
    public WeCustomerExtendPropertyController(@NotNull WeCustomerExtendPropertyService weCustomerExtendPropertyService) {
        this.weCustomerExtendPropertyService = weCustomerExtendPropertyService;
    }


    @GetMapping("/list")
    @ApiOperation(("获取客户扩展属性列表"))
    public AjaxResult<List<WeCustomerExtendProperty>> list(@Validated QueryCustomerExtendPropertyDTO dto) {
        WeCustomerExtendProperty property = new WeCustomerExtendProperty();
        BeanUtils.copyPropertiesASM(dto, property);
        property.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weCustomerExtendPropertyService.getList(property));
    }

    @PostMapping("/add")
    @ApiOperation("保存客户扩展属性")
    @ApiResponses({
            @ApiResponse(code = 4006, message = "该客户扩展标签已存在")
    })
    @PreAuthorize("@ss.hasPermi('customer:extendProp:add')")
    public AjaxResult<Integer> add(@Validated @RequestBody SaveCustomerExtendPropertyDTO dto) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        dto.setCreateBy(loginUser);
        dto.setCorpId(loginUser.getCorpId());
        int result = weCustomerExtendPropertyService.add(dto);
        return result > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @PutMapping("/edit")
    @ApiOperation("编辑客户扩展属性")
    @ApiResponses({
            @ApiResponse(code = 4006, message = "该客户扩展标签已存在"),
            @ApiResponse(code = 4007, message = "更新客户自定义属性失败"),
            @ApiResponse(code = 4008, message = "缺少多选选项值")
    })
    @PreAuthorize("@ss.hasPermi('customer:extendProp:edit')")
    public AjaxResult<Integer> edit(@Validated @RequestBody SaveCustomerExtendPropertyDTO dto) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        dto.setCorpId(loginUser.getCorpId());
        weCustomerExtendPropertyService.edit(dto);
        return AjaxResult.success();
    }

    @PutMapping("/editBatch")
    @ApiOperation("批量编辑客户扩展属性")
    @ApiResponses({
            @ApiResponse(code = 4006, message = "该客户扩展标签已存在"),
            @ApiResponse(code = 4007, message = "更新客户自定义属性失败"),
            @ApiResponse(code = 4008, message = "缺少多选选项值"),
            @ApiResponse(code = 4009, message = "标签类型无法编辑")
    })
    @PreAuthorize("@ss.hasPermi('customer:extendProp:edit')")
    public AjaxResult<Integer> editBatch(@RequestBody BatchSaveCustomerExtendPropertyDTO dto) {
        dto.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weCustomerExtendPropertyService.editBatch(dto);
        return AjaxResult.success();

    }

    @DeleteMapping("/del/{ids}")
    @ApiOperation("删除客户扩展属性")
    @PreAuthorize("@ss.hasPermi('customer:extendProp:remove')")
    public AjaxResult<Integer> del(@Validated @NotEmpty(message = "id不能为空") @ApiParam("删除id数组") @PathVariable String[] ids) {
        weCustomerExtendPropertyService.del(LoginTokenService.getLoginUser().getCorpId(), ids);
        return AjaxResult.success();
    }


}
