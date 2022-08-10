package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.dto.WeChatCollectionDTO;
import com.easyink.wecom.domain.vo.FindCollectionsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 类名: WeChatCollectionController
 *
 * @author: 1*+
 * @date: 2021-08-27 17:43
 */
@Api(value = "WeChatCollectionController", tags = "聊天侧边栏-素材收藏接口")
@RequestMapping(value = "/wecom/chat/collection")
@RestController
@Deprecated
public class WeChatCollectionController extends BaseController {
    /**
     * 添加收藏
     */
    //@PreAuthorize("@ss.hasPermi('chat:collection:add')")
    @Log(title = "添加收藏", businessType = BusinessType.INSERT)
    @PostMapping("addCollection")
    @ApiOperation("添加收藏")
    public AjaxResult addCollection(@RequestBody WeChatCollectionDTO chatCollectionDto) {
        return AjaxResult.success();
    }


    /**
     * 取消收藏
     */
    // @PreAuthorize("@ss.hasPermi('chat:collection:delete')")
    // @Log(title = "取消收藏", businessType = BusinessType.UPDATE)
    @PostMapping(value = "cancleCollection")
    @ApiOperation("取消收藏")
    public AjaxResult cancleCollection(@RequestBody WeChatCollectionDTO chatCollectionDto) {
        return AjaxResult.success();
    }

    /**
     * 收藏列表
     */
    //  @PreAuthorize("@ss.hasPermi('chat:collection:list')")
    @GetMapping("/list")
    @ApiOperation(value = "收藏列表")
    public TableDataInfo<FindCollectionsVO> list(@ApiParam("员工ID") @RequestParam(value = "userId") String userId, @ApiParam(value = "关键词", required = false) @RequestParam(value = "keyword") String keyword) {
        return getDataTable(new ArrayList<>());
    }

}
