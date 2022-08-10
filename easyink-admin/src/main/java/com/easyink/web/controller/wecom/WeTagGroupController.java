package com.easyink.web.controller.wecom;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.annotation.Log;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.WeTagGroup;
import com.easyink.wecom.domain.dto.tag.WeGroupTagDTO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeTagGroupService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签组Controller
 *
 * @author admin
 * @date 2020-09-07
 */
@RestController
@RequestMapping("/wecom/group")
@Api(tags = "标签组相关接口")
public class WeTagGroupController extends BaseController {
    @Autowired
    private WeTagGroupService weTagGroupService;

    /**
     * 查询标签组列表
     */
    @GetMapping("/list")
    @ApiOperation("查询标签列表")
    public TableDataInfo list(WeTagGroup weTagGroup) {
        startPage();
        weTagGroup.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return getDataTable(weTagGroupService.selectWeTagGroupList(weTagGroup));
    }

    /**
     * 查询标签组是否存在
     *
     * @param tagGroupName
     * @return
     */
    @GetMapping("/checkTagGroupName")
    @ApiOperation("查询标签组是否已存在")
    @ApiResponses({
            @ApiResponse(code = 500, message = "请求参数为空")
    })
    public AjaxResult<WeGroupTagDTO> checkTagGroupName(@ApiParam(name = "tagGroupName", value = "标签组名", required = true) String tagGroupName) {
        //状态码
        int repeat = 0;
        if (tagGroupName == null || StringUtils.isEmpty(tagGroupName)) {
            return AjaxResult.error("tagGroupName不能为空");
        }
        //查询是否存在标签名为tagGroupName且是正常状态的群组
        int count = weTagGroupService.count(new LambdaQueryWrapper<WeTagGroup>()
                .eq(WeTagGroup::getGroupName, tagGroupName)
                .eq(WeTagGroup::getCorpId, LoginTokenService.getLoginUser().getCorpId())
                .eq(WeTagGroup::getStatus, Constants.NORMAL_CODE));

        //如果大于1则是存在数据库
        if (count > 0) {
            repeat = 1;
        }

        //创建返回参数返回
        return AjaxResult.success(WeGroupTagDTO.builder().repeat(repeat).build());
    }

    /**
     * 新增标签组
     */
    @PreAuthorize("@ss.hasPermi('customerManage:tag:add')")
    @Log(title = "标签组", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增标签组")
    public AjaxResult add(@Validated @RequestBody WeTagGroup weTagGroup) {

        //校验标签组名称与标签名称是否相同
        if (StrUtil.isNotBlank(weTagGroup.getGroupName())) {
            List<WeTag> weTags = weTagGroup.getWeTags();
            if (CollUtil.isNotEmpty(weTags) && weTags.stream().anyMatch(m -> m.getName().equals(weTagGroup.getGroupName()))) {
                return AjaxResult.error("标签组名称与标签名不可重复");
            }
        }
        weTagGroup.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weTagGroupService.insertWeTagGroup(weTagGroup);

        return AjaxResult.success();
    }

    /**
     * 修改标签组
     */
    @ApiOperation("修改标签组")
    @PreAuthorize("@ss.hasPermi('customerManage:tag:edit')")
    @Log(title = "标签组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WeTagGroup weTagGroup) {
        weTagGroup.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weTagGroupService.updateWeTagGroup(weTagGroup);
        return AjaxResult.success();
    }

    /**
     * 删除标签组
     */
    @ApiOperation("删除标签组")
    @PreAuthorize("@ss.hasPermi('customerManage:tag:remove')")
    @Log(title = "标签组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(weTagGroupService.deleteWeTagGroupByIds(ids, LoginTokenService.getLoginUser().getCorpId()));
    }


    /**
     * 同步标签
     *
     * @return
     */
    @ApiOperation("同步标签")
    @PreAuthorize("@ss.hasPermi('customerManage:tag:sync')")
    @GetMapping("/synchWeTags")
    public AjaxResult synchWeTags() {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        //异步同步一下标签库,解决标签不同步问题
        weTagGroupService.synchWeTags(loginUser.getCorpId());

        return AjaxResult.success(WeConstans.SYNCH_TIP);
    }
}
