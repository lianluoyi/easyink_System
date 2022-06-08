package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.enums.WelcomeMsgTplTypeEnum;
import com.easywecom.wecom.domain.WeMsgTlp;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgAddDTO;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgDeleteDTO;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgUpdateEmployDTO;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgUpdateGroupDTO;
import com.easywecom.wecom.domain.vo.welcomemsg.WeMsgTlpListVO;
import com.easywecom.wecom.domain.vo.welcomemsg.WelcomeMsgGroupMaterialCountVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeMsgTlpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 欢迎语模板Controller
 *
 * @author admin
 * @date 2020-10-04
 */
@RestController
@RequestMapping("/wecom/tlp")
@Api(tags = "欢迎语模板Controller")
public class WeMsgTlpController extends BaseController {

    private final WeMsgTlpService weMsgTlpService;

    @Autowired
    public WeMsgTlpController(WeMsgTlpService weMsgTlpService) {
        this.weMsgTlpService = weMsgTlpService;
    }

    @PreAuthorize("@ss.hasPermi('wecom:tlp:add')")
    @Log(title = "新增好友欢迎语模板", businessType = BusinessType.INSERT)
    @PostMapping("employ")
    @ApiOperation("新增好友欢迎语模板")
    public AjaxResult addEmployMsg(@Validated @RequestBody WelComeMsgAddDTO welComeMsgAddDTO) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        welComeMsgAddDTO.getWeMsgTlp().setCorpId(loginUser.getCorpId());
        if (loginUser.isSuperAdmin()) {
            welComeMsgAddDTO.getWeMsgTlp().setCreateBy(loginUser.getUser().getUserId().toString());
        } else {
            welComeMsgAddDTO.getWeMsgTlp().setCreateBy(loginUser.getWeUser().getUserId());
        }
        welComeMsgAddDTO.getWeMsgTlp().setWelcomeMsgTplType(WelcomeMsgTplTypeEnum.EMP_WELCOME.getType());
        weMsgTlpService.insertWeMsgTlpWithEmploy(welComeMsgAddDTO);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:groupWelcome:add')")
    @Log(title = "新增群欢迎语模板", businessType = BusinessType.INSERT)
    @PostMapping("group")
    @ApiOperation("新增群欢迎语模板")
    public AjaxResult addGroupMsg(@Validated @RequestBody WelComeMsgAddDTO welComeMsgAddDTO) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        welComeMsgAddDTO.getWeMsgTlp().setCorpId(loginUser.getCorpId());
        if (loginUser.isSuperAdmin()) {
            welComeMsgAddDTO.getWeMsgTlp().setCreateBy(loginUser.getUser().getUserId().toString());
        } else {
            welComeMsgAddDTO.getWeMsgTlp().setCreateBy(loginUser.getWeUser().getUserId());
        }
        welComeMsgAddDTO.getWeMsgTlp().setWelcomeMsgTplType(WelcomeMsgTplTypeEnum.GROUP_WELCOME.getType());
        weMsgTlpService.insertWeMsgTlpWithGroup(welComeMsgAddDTO);
        return AjaxResult.success();
    }


    @GetMapping("/list/employ")
    @ApiOperation("查询欢迎语模板列表")
    public TableDataInfo<WeMsgTlpListVO> list(WeMsgTlp weMsgTlp) {
        weMsgTlp.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        startPage();
        List<WeMsgTlpListVO> list = weMsgTlpService.selectWeMsgTlpList(weMsgTlp);
        return getDataTable(list);
    }


    //   @PreAuthorize("@ss.hasPermi('wecom:tlp:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取欢迎语模板详细信息")
    public AjaxResult<WeMsgTlpListVO> getInfo(@ApiParam("默认欢迎语id") @PathVariable("id") Long id) {

        return AjaxResult.success(
                weMsgTlpService.detail(WeMsgTlp.builder().corpId(LoginTokenService.getLoginUser().getCorpId()).id(id).build())
        );
    }


    @PreAuthorize("@ss.hasPermi('wecom:tlp:edit')")
    @Log(title = "修改好友迎语模板", businessType = BusinessType.UPDATE)
    @PutMapping("/edit/employ")
    @ApiOperation("修改好友迎语模板")
    public AjaxResult editWithEmploy(@Validated @RequestBody WelComeMsgUpdateEmployDTO welComeMsgUpdateDTO) {
        welComeMsgUpdateDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weMsgTlpService.updateWeMsgTlpWithEmploy(welComeMsgUpdateDTO);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:groupWelcome:edit')")
    @Log(title = "修改群欢迎语模板", businessType = BusinessType.UPDATE)
    @PutMapping("/edit/group")
    @ApiOperation("修改群欢迎语模板")
    public AjaxResult editWithGroup(@Validated @RequestBody WelComeMsgUpdateGroupDTO welComeMsgUpdateDTO) {
        welComeMsgUpdateDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weMsgTlpService.updateWeMsgTlpWithGroup(welComeMsgUpdateDTO);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:tlp:remove')")
    @Log(title = "删除好友欢迎语模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/employ")
    @ApiOperation("删除好友欢迎语模板")
    public AjaxResult removeWithEmploy(@Validated @RequestBody WelComeMsgDeleteDTO welComeMsgDeleteDTO) {
        weMsgTlpService.deleteEmployWeMsgTlpById(LoginTokenService.getLoginUser().getCorpId(), welComeMsgDeleteDTO.getIds());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:groupWelcome:del')")
    @Log(title = "删除群欢迎语模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/group")
    @ApiOperation("删除群欢迎语模板")
    public AjaxResult removeWithGroup(@Validated @RequestBody WelComeMsgDeleteDTO welComeMsgDeleteDTO) {
        weMsgTlpService.deleteGroupWeMsgTlpById(LoginTokenService.getLoginUser().getCorpId(), welComeMsgDeleteDTO.getIds());
        return AjaxResult.success();
    }

    @GetMapping("/group/count")
    @ApiOperation("群欢迎语模板统计")
    public AjaxResult groupCount() {
        WelcomeMsgGroupMaterialCountVO vo = weMsgTlpService.groupCount(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(vo);
    }

}
