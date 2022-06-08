package com.easywecom.web.controller.wecom;

import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.domain.model.LoginResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.dto.FindWeMaterialDTO;
import com.easywecom.wecom.domain.vo.WeCategoryBaseInfoVO;
import com.easywecom.wecom.domain.vo.WeCorpInfoVO;
import com.easywecom.wecom.domain.vo.WeMaterialVO;
import com.easywecom.wecom.domain.vo.WeUserInfoVO;
import com.easywecom.wecom.login.service.SysLoginService;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeCategoryService;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeMaterialService;
import com.easywecom.wecom.service.WeUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 聊天工具侧边栏
 *
 * @author admin
 */
@RequestMapping(value = "/wecom/chat/side")
@RestController
@Api(tags = "聊天工具侧边栏")
public class WeChatSideController extends BaseController {

    private final WeCorpAccountService weCorpAccountService;
    private final SysLoginService sysLoginService;
    private final WeUserService weUserService;
    private final WeCategoryService weCategoryService;
    private final WeMaterialService weMaterialService;


    @Autowired
    public WeChatSideController(WeCorpAccountService weCorpAccountService, SysLoginService sysLoginService, WeUserService weUserService, WeCategoryService weCategoryService, WeMaterialService weMaterialService) {
        this.weCorpAccountService = weCorpAccountService;
        this.sysLoginService = sysLoginService;
        this.weUserService = weUserService;
        this.weCategoryService = weCategoryService;
        this.weMaterialService = weMaterialService;
    }

    /**
     * 获取当前启用配置
     * update by Society my sister Li 增加code、corpId传参，用于确认成员信息，增加返回token,便于后续侧边栏接口直接获取corpId
     *
     * @return AjaxResult
     */
    @GetMapping("/getCorpInfo")
    @ApiOperation("获取当前启用配置")
    public AjaxResult getCorpInfo(@ApiParam("code") @RequestParam(value = "code") String code, @ApiParam("corpId") @RequestParam(value = "corpId") String corpId) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(code)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeCorpAccount validWeCorpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (validWeCorpAccount == null) {
            throw new CustomException(ResultTip.TIP_NOT_CORP_CONFIG);
        }
        WeCorpInfoVO weCorpInfoVO = new WeCorpInfoVO();
        BeanUtils.copyProperties(validWeCorpAccount, weCorpInfoVO);
        if (StringUtils.isBlank(weCorpInfoVO.getCorpId())) {
            throw new CustomException(ResultTip.TIP_NOT_CORP_CONFIG);
        }
        WeUserInfoVO userInfo = weUserService.getUserInfo(code, validWeCorpAccount.getAgentId(), validWeCorpAccount.getCorpId());
        LoginResult loginResult = sysLoginService.loginByUserId(userInfo.getUserId(), validWeCorpAccount.getCorpId(), validWeCorpAccount, true, false);
        weCorpInfoVO.setToken(loginResult.getToken());
        return AjaxResult.success(weCorpInfoVO);
    }


    /**
     * 群发侧边栏列表
     * updateBy Society my sister Li 前端传参token后，可自行获取corpId
     */
    @GetMapping("/h5List")
    @ApiOperation("侧边栏素材类型列表")
    public TableDataInfo h5List() {
        startPage();
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        List<WeCategoryBaseInfoVO> showWeCategory = weCategoryService.findShowWeCategory(corpId);
        return getDataTable(showWeCategory);
    }

    @GetMapping("/h5materialList")
    @ApiOperation("侧边栏素材类型列表")
    public TableDataInfo materialList(@Validated FindWeMaterialDTO findWeMaterialDTO) {
        startPage();
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        findWeMaterialDTO.setCorpId(corpId);
        findWeMaterialDTO.setShowMaterial(WeConstans.DEFAULT_WE_MATERIAL_USING);
        findWeMaterialDTO.setIsExpire(WeConstans.MATERIAL_UN_EXPIRE);
        List<WeMaterialVO> weMaterials = weMaterialService.findWeMaterials(findWeMaterialDTO);
        return getDataTable(weMaterials);
    }
}
