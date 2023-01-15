package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.dto.WeGroupSopDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 社区运营 - 群sop controller
 */
@Api(tags = "新客自动拉群 Controller")
@RestController
@RequestMapping(value = "/wecom/communityGroupSop")
@Deprecated
public class WeCommunityGroupSopController extends BaseController {

    /**
     * 通过规则id获取sop规则
     *
     * @param ruleId 规则id
     * @return 结果
     */
    @ApiOperation(value = "通过规则id获取sop规则详情", httpMethod = "GET")
//    @PreAuthorize("@ss.hasPermi('wecom:communityGroupSop:query')")
    @GetMapping(path = "/{ruleId}")
    public AjaxResult getGroupSop(@PathVariable("ruleId") Long ruleId) {
        return AjaxResult.success();
    }

    /**
     * 更改SOP规则
     *
     * @param ruleId      SOP规则 id
     * @param groupSopDto 更新数据
     * @return 结果
     */
    @ApiOperation(value = "更改SOP规则", httpMethod = "PUT")
//    @PreAuthorize("@ss.hasPermi('wecom:communityGroupSop:edit')")
    @PutMapping(path = "/{ruleId}")
    public AjaxResult updateGroupSop(@PathVariable Long ruleId, @Validated @RequestBody WeGroupSopDTO groupSopDto) {
        return AjaxResult.success();
    }

    /**
     * 根据id列表批量删除群sop规则
     *
     * @param ids 群sop规则列表
     * @return 结果
     */
    @ApiOperation(value = "根据id列表批量删除群sop规则", httpMethod = "DELETE")
//    @PreAuthorize("@ss.hasPermi('wecom:communityGroupSop:remove')")
    @DeleteMapping(path = "/{ids}")
    public AjaxResult batchDeleteSopRule(@PathVariable("ids") Long[] ids) {
        return AjaxResult.success();
    }

}
