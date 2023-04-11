package com.easyink.web.controller.wecom;

import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.dto.customerloss.CustomerAddLossTagDTO;
import com.easyink.wecom.domain.vo.customerloss.CustomerLossTagVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeLossTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 流失标签Controller
 *
 * @author lichaoyu
 * @date 2023/3/24 11:44
 */
@RestController
@RequestMapping("/wecom/lossTag")
@Api(tags = "流失提醒相关接口")
public class WeLossTagController {


    private final WeLossTagService weLossTagService;

    @Autowired
    public WeLossTagController(WeLossTagService weLossTagService) {
        this.weLossTagService = weLossTagService;
    }


    @ApiOperation("添加流失标签")
    @PreAuthorize("@ss.hasPermi('wechat:corp:loss:setting')")
    @PostMapping("/addLossTag")
    public AjaxResult addLossTag(@RequestBody CustomerAddLossTagDTO customerAddLossTagDTO) {
        weLossTagService.insertWeLossTag(customerAddLossTagDTO);
        return AjaxResult.success();
    }

    @ApiOperation("查找流失标签")
    @GetMapping("/selectLossTag")
    public AjaxResult<CustomerLossTagVO> selectLossTag() {
        return AjaxResult.success("操作成功！", weLossTagService.selectLossWeTag(LoginTokenService.getLoginUser().getCorpId()));
    }
}
