package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.enums.WeWordsCategoryTypeEnum;
import com.easywecom.wecom.domain.dto.wordscategory.*;
import com.easywecom.wecom.domain.vo.WeWordsCategoryVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeWordsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 类名: WeWordsCategoryController
 *
 * @author: Society my sister Li
 * @date: 2021-10-25 10:32
 */
@RestController
@RequestMapping("/wecom/wordscategory")
@Api(value = "WeWordsCategoryController", tags = "企业话术库文件夹接口")
public class WeWordsCategoryController extends BaseController {

    private final WeWordsCategoryService weWordsCategoryService;

    @Autowired
    public WeWordsCategoryController(WeWordsCategoryService weWordsCategoryService) {
        this.weWordsCategoryService = weWordsCategoryService;
    }

    @Log(title = "新增文件夹", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增文件夹")
    public AjaxResult add(@Validated @RequestBody AddWeWordsCategoryDTO weWordsCategory) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        weWordsCategory.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weWordsCategoryService.insert(loginUser, weWordsCategory);
        return AjaxResult.success();
    }


    @Log(title = "修改文件夹", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("修改文件夹")
    public AjaxResult update(@Validated @RequestBody UpdateWeWordsCategoryDTO updateWeWordsCategoryDTO) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        updateWeWordsCategoryDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weWordsCategoryService.update(loginUser, updateWeWordsCategoryDTO);
        return AjaxResult.success();
    }

    @Log(title = "删除文件夹", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete")
    @ApiOperation("删除文件夹")
    public AjaxResult delete(@Validated @RequestBody DeleteWeWordsCategoryDTO deleteWeWordsCategoryDTO) {
        deleteWeWordsCategoryDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weWordsCategoryService.delete(deleteWeWordsCategoryDTO);
        return AjaxResult.success();
    }

    @Log(title = "查询列表", businessType = BusinessType.OTHER)
    @GetMapping("/list")
    @ApiOperation("查询列表")
    public AjaxResult<WeWordsCategoryVO> list(@Validated FindWeWordsCategoryDTO findWeWordsCategoryDTO) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        Integer type = findWeWordsCategoryDTO.getType();
        //设置权限范围,若为admin则 不能查询部门和个人文件夹
        if (WeWordsCategoryTypeEnum.CORP.getType().equals(type)) {
            findWeWordsCategoryDTO.setUseRange(WeConstans.WE_ROOT_DEPARMENT_ID.toString());
        } else {
            if (loginUser.getWeUser() == null) {
                return AjaxResult.success(new ArrayList());
            }
            if (WeWordsCategoryTypeEnum.DEPARTMENT.getType().equals(type)) {
                findWeWordsCategoryDTO.setUseRange(loginUser.getWeUser().getMainDepartment().toString());
            } else {
                findWeWordsCategoryDTO.setUseRange(loginUser.getWeUser().getUserId());
            }
        }
        findWeWordsCategoryDTO.setCorpId(loginUser.getCorpId());
        return AjaxResult.success(weWordsCategoryService.list(findWeWordsCategoryDTO));
    }


    @Log(title = "文件夹上移/下移/置顶", businessType = BusinessType.OTHER)
    @PutMapping("/changeSort")
    @ApiOperation("文件夹上移/下移/置顶")
    public AjaxResult changeSort(@Validated @RequestBody WeWordsCategoryChangeSortDTO weWordsCategoryChangeSortDTO) {
        weWordsCategoryChangeSortDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weWordsCategoryService.changeSort(weWordsCategoryChangeSortDTO));
    }
}
