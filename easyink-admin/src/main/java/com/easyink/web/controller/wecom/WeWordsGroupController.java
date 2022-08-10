package com.easyink.web.controller.wecom;

import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.wecom.domain.WeWordsGroupEntity;
import com.easyink.wecom.domain.WeWordsLastUseEntity;
import com.easyink.wecom.domain.dto.WeWordsDTO;
import com.easyink.wecom.domain.dto.WeWordsDelDTO;
import com.easyink.wecom.domain.dto.WeWordsQueryDTO;
import com.easyink.wecom.domain.dto.WeWordsSortDTO;
import com.easyink.wecom.domain.dto.tag.WeWordsModifyCategoryDTO;
import com.easyink.wecom.domain.vo.WeWordsUrlVO;
import com.easyink.wecom.domain.vo.WeWordsVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeWordsGroupService;
import com.easyink.wecom.service.WeWordsLastUseService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类名： 企业话术库接口
 *
 * @author 佚名
 * @date 2021/10/28 14:51
 */
@RestController
@RequestMapping("/wecom/wordsGroup")
@Slf4j
@Api(value = "WeWordsGroupController", tags = "企业话术库接口")
public class WeWordsGroupController extends BaseController {
    private final WeWordsGroupService weWordsGroupService;
    private final WeWordsLastUseService weWordsLastUseService;

    public WeWordsGroupController(WeWordsGroupService weWordsGroupService, WeWordsLastUseService weWordsLastUseService) {
        this.weWordsGroupService = weWordsGroupService;
        this.weWordsLastUseService = weWordsLastUseService;
    }

    @PostMapping("/add")
    @ApiOperation("添加")
    public AjaxResult add(@Validated @RequestBody WeWordsDTO weWordsDTO) {
        weWordsDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        //非admin 设置员工主部门
        if (!LoginTokenService.getLoginUser().isSuperAdmin()) {
            weWordsDTO.setMainDepartment(LoginTokenService.getLoginUser().getWeUser().getMainDepartment());
        }
        weWordsGroupService.add(weWordsDTO);
        return AjaxResult.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除")
    public AjaxResult delete(@Validated @RequestBody WeWordsDelDTO weWordsDelDTO) {
        weWordsGroupService.delete(weWordsDelDTO.getIds(), LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改")
    public AjaxResult update(@Validated @RequestBody WeWordsDTO weWordsDTO) {
        weWordsDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (!LoginTokenService.getLoginUser().isSuperAdmin()) {
            weWordsDTO.setMainDepartment(LoginTokenService.getLoginUser().getWeUser().getMainDepartment());
        }
        weWordsGroupService.update(weWordsDTO);
        return AjaxResult.success();
    }

    @PostMapping("/listOfWords")
    @ApiOperation("查询")
    public TableDataInfo<WeWordsVO> listOfWords(@Validated @RequestBody WeWordsQueryDTO weWordsQueryDTO) {
        weWordsQueryDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (weWordsQueryDTO.getPageSize() != null) {
            if (weWordsQueryDTO.getPageNum() == null) {
                weWordsQueryDTO.setPageNum(WeConstans.FIRST_PAGE);
            }
            if (weWordsQueryDTO.getId() != null) {
                WeWordsGroupEntity weWordsGroupEntity = weWordsGroupService.get(weWordsQueryDTO.getId());
                weWordsQueryDTO.setSort(weWordsGroupEntity.getSort());
            }
            PageHelper.startPage(weWordsQueryDTO.getPageNum(), weWordsQueryDTO.getPageSize());
        }
        return getDataTable(weWordsGroupService.listOfWords(weWordsQueryDTO));
    }

    @PostMapping("/updateCategory")
    @ApiOperation("批量修改所属文件夹")
    public AjaxResult updateCategory(@Validated @RequestBody WeWordsModifyCategoryDTO weWordsModifyCategoryDTO) {
        weWordsGroupService.updateCategory(weWordsModifyCategoryDTO.getCategoryId(), weWordsModifyCategoryDTO.getIds(), LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success();
    }

    @PostMapping("/importWords")
    @ApiOperation("导入话术")
    public AjaxResult importWords(MultipartFile file, @RequestParam("type") Integer type) {
        return AjaxResult.success(weWordsGroupService.importWords(file, LoginTokenService.getLoginUser(), type));
    }

    @PostMapping("/addOrUpdateLastUse")
    @ApiOperation("保存或更新最近使用")
    public AjaxResult addOrUpdateLastUse(@RequestBody WeWordsLastUseEntity weWordsLastUseEntity) {
        weWordsLastUseEntity.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        String userId;
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            userId = LoginTokenService.getLoginUser().getUsername();
        } else {
            userId = LoginTokenService.getLoginUser().getWeUser().getUserId();
        }
        weWordsLastUseEntity.setUserId(userId);
        weWordsLastUseService.addOrUpdateLastUse(weWordsLastUseEntity);
        return AjaxResult.success();
    }

    @GetMapping("/getLastUse")
    @ApiOperation("查询最近使用")
    public AjaxResult<WeWordsGroupEntity> getLastUse(@RequestParam("type") Integer type) {
        String userId;
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            userId = LoginTokenService.getLoginUser().getUsername();
        } else {
            userId = LoginTokenService.getLoginUser().getWeUser().getUserId();
        }
        return AjaxResult.success(weWordsLastUseService.listOfWordsVO(userId, LoginTokenService.getLoginUser().getCorpId(), type));
    }

    @GetMapping("/getUrlContent")
    @ApiOperation("获取链接信息")
    @ApiResponses({
            @ApiResponse(code = 2035, message = "话术库获取链接内容失败")
    })
    public AjaxResult<WeWordsUrlVO> getUrlContent(@RequestParam("url") String url) {
        return AjaxResult.success(weWordsGroupService.matchUrl(url));
    }

    @PostMapping("/changeSort")
    @ApiOperation("修改排序")
    @ApiResponses({
            @ApiResponse(code = 2036, message = "话术库缺少排序号、话术id,修改失败")
    })
    public AjaxResult changeSort(@RequestBody WeWordsSortDTO weWordsSortDTO) {
        weWordsSortDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weWordsGroupService.changeSort(weWordsSortDTO);
        return AjaxResult.success();
    }
}
