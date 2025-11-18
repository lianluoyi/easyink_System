package com.easyink.web.controller.openapi;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.dto.QueryCustomerExtendPropertyDTO;
import com.easyink.wecom.domain.entity.customer.WeCustomerExtendProperty;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.service.H5Service;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: easyink获取unionId 接口
 *
 * @author : silver_chariot
 * @date : 2023/1/4 14:19
 **/
@RestController
@RequestMapping("/h5")
@AllArgsConstructor
public class H5Controller extends BaseController {

    private final H5Service h5Service;

    @ApiOperation("获取原活码标签列表, 无需token")
    @GetMapping("/getOriginEmpleInfo/{originEmpleId}/{corpId}")
    public AjaxResult<WeEmpleCodeVO> getOriginEmpleTag(@PathVariable(value = "originEmpleId") String originEmpleId,
                                                       @PathVariable(value = "corpId") String corpId) {
        WeEmpleCodeVO vo = h5Service.getOriginEmpleTag(originEmpleId, corpId);
        return AjaxResult.success(vo);
    }

    /**
     * 查询标签组列表
     */
    @GetMapping("/tag/list/{corpId}")
    @ApiOperation("查询标签列表")
    public TableDataInfo<WeTagGroup> list(@PathVariable(name = "corpId") String corpId, WeTagGroup weTagGroup) {
        startPage();
        List<WeTagGroup> list = h5Service.selectWeTagGroupList(weTagGroup, corpId);
        return getDataTable(list);
    }
    /**
     * 查询专属活码配置的标签组列表(如果存在)
     */
    @GetMapping("/customer/tag/list/{corpId}/{originEmpleId}")
    @ApiOperation("查询标签列表")
    public TableDataInfo<WeTagGroup> customerLinkTagList(
            @PathVariable(name = "corpId") String corpId,
            @PathVariable(name = "originEmpleId") String originEmpleId,
            WeTagGroup weTagGroup) {
        startPage();
        weTagGroup.setCorpId(corpId);
        List<WeTagGroup> list = h5Service.customerLinkTagList(originEmpleId, weTagGroup);
        return getDataTable(list);
    }

    @GetMapping("/extendProperty/list/{corpId}")
    @ApiOperation(("获取客户扩展属性列表"))
    public AjaxResult<List<WeCustomerExtendProperty>> list(@Validated @PathVariable(name = "corpId") String corpId, QueryCustomerExtendPropertyDTO dto) {
        WeCustomerExtendProperty property = new WeCustomerExtendProperty();
        BeanUtils.copyPropertiesASM(dto, property);
        List<WeCustomerExtendProperty> list = h5Service.getCustomerExtendPropertyList(property, corpId);
        return AjaxResult.success(list);
    }

    @GetMapping("/tagGroupValid/{empleCodeId}/{corpId}")
    @ApiOperation("获取员工活码的专属活码页面配置")
    public AjaxResult<Integer> getTagGroupValid(@PathVariable(name = "empleCodeId") Long empleCodeId,
                                                @PathVariable(name = "corpId") String corpId) {
        Integer tagGroupValid = h5Service.getTagGroupValid(empleCodeId, corpId);
        return AjaxResult.success(tagGroupValid);
    }

}
