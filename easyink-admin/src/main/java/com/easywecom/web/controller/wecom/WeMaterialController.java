package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.enums.MediaType;
import com.easywecom.wecom.domain.WeMaterial;
import com.easywecom.wecom.domain.WeMaterialTagEntity;
import com.easywecom.wecom.domain.dto.*;
import com.easywecom.wecom.domain.dto.tag.WeMaterialTagAddDTO;
import com.easywecom.wecom.domain.vo.*;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeMaterialService;
import com.easywecom.wecom.service.WeMaterialTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 企业微信素材Controller
 *
 * @author admin
 * @date 2020-10-08
 */
@Api(tags = "企业微信素材管理接口")
@RestController
@RequestMapping("/wecom/material")
public class WeMaterialController extends BaseController {
    private final WeMaterialService materialService;
    private final WeMaterialTagService weMaterialTagService;

    @Autowired
    public WeMaterialController(@NotNull WeMaterialService materialService, @NotNull WeMaterialTagService weMaterialTagService) {
        this.materialService = materialService;
        this.weMaterialTagService = weMaterialTagService;
    }


    //   @PreAuthorize("@ss.hasPermi('wecom:material:list')")
    @GetMapping("/list")
    @ApiOperation("查询素材列表")
    public TableDataInfo<WeMaterialVO> list(@Validated FindWeMaterialDTO findWeMaterialDTO) {
        startPage();
        findWeMaterialDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeMaterialVO> list = materialService.findWeMaterials(findWeMaterialDTO);
        return getDataTable(list);
    }


    //  @PreAuthorize("@ss.hasPermi('wechat:material:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("查询素材详细信息")
    public AjaxResult<WeMaterial> getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(materialService.findWeMaterialById(id));
    }


    @PreAuthorize("@ss.hasPermi('wechat:material:add')")
    @Log(title = "添加素材信息", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加素材信息")
    public AjaxResult<InsertWeMaterialVO> add(@Validated @RequestBody AddWeMaterialDTO material) {
        return AjaxResult.success(materialService.insertWeMaterial(material));
    }


    @PreAuthorize("@ss.hasPermi('wechat:material:edit')")
    @Log(title = "更新素材信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("更新素材信息")
    public AjaxResult edit(@Validated @RequestBody UpdateWeMaterialDTO material) {
        material.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        material.setUpdateBy(LoginTokenService.getUsername());
        return toAjax(materialService.updateWeMaterial(material));
    }


    @PreAuthorize("@ss.hasPermi('wechat:material:remove')")
    @Log(title = "删除素材信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @ApiOperation("删除素材信息")
    public AjaxResult remove(@Validated @RequestBody RemoveMaterialDTO removeMaterialDTO) {
        removeMaterialDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        materialService.deleteWeMaterialByIds(removeMaterialDTO);
        return AjaxResult.success();
    }


    //    @PreAuthorize("@ss.hasPermi('wechat:material:upload')")
    @Log(title = "上传素材信息", businessType = BusinessType.OTHER)
    @PostMapping("/upload")
    @ApiOperation("上传素材信息")
    public AjaxResult upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "mediaType") String mediaType) {
        WeMaterialFileVO weMaterialFileVO = materialService.uploadWeMaterialFile(file, mediaType);
        return AjaxResult.success(weMaterialFileVO);
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:material:resetCategory')")
    @Log(title = "更换分组", businessType = BusinessType.OTHER)
    @PutMapping("/resetCategory")
    @ApiOperation("更换分组")
    public AjaxResult resetCategory(@RequestBody ResetCategoryDTO resetCategoryDTO) {
        materialService.resetCategory(resetCategoryDTO.getCategoryId(), resetCategoryDTO.getMaterials());
        return AjaxResult.success();
    }


    //@PreAuthorize("@ss.hasPermi('wechat:material:temporaryMaterialMediaId')")
    @Log(title = "获取素材media_id", businessType = BusinessType.OTHER)
    @GetMapping("/temporaryMaterialMediaId")
    @ApiOperation("H5端发送获取素材media_id")
    public AjaxResult temporaryMaterialMediaId(String url, String type, String name) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        WeMediaDTO weMediaDto = materialService.uploadTemporaryMaterial(url, type, name, corpId);
        return AjaxResult.success(weMediaDto);
    }


    //@PreAuthorize("@ss.hasPermi('wechat:material:temporaryMaterialMediaId')")
    @Log(title = "获取素材media_id", businessType = BusinessType.OTHER)
    @PostMapping("/temporaryMaterialMediaIdForWeb")
    @ApiOperation("web端发送获取素材media_id")
    public AjaxResult temporaryMaterialMediaIdForWeb(@RequestBody TemporaryMaterialDTO temporaryMaterialDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        WeMediaDTO weMediaDto = materialService.uploadTemporaryMaterial(temporaryMaterialDTO.getUrl(),
                MediaType.of(temporaryMaterialDTO.getType()).get().getMediaType()
                , temporaryMaterialDTO.getName(), corpId);
        return AjaxResult.success(weMediaDto);
    }


    @Log(title = "上传素材图片", businessType = BusinessType.OTHER)
    @PostMapping("/uploadimg")
    @ApiOperation("上传素材图片")
    public AjaxResult<WeMediaDTO> uploadImg(MultipartFile file, HttpServletRequest request) {
        WeMediaDTO weMediaDto = new WeMediaDTO();
        WeMaterialFileVO weMaterialFileVO = materialService.uploadWeMaterialFile(file, MediaType.IMAGE.getType());
        weMediaDto.setFileName(weMaterialFileVO.getMaterialName());
        weMediaDto.setUrl(weMaterialFileVO.getMaterialUrl() + weMaterialFileVO.getMaterialName());
        return AjaxResult.success(weMediaDto);
    }

    @Log(title = "保存素材标签", businessType = BusinessType.OTHER)
    @ApiOperation("保存素材标签")
    @PostMapping("/saveTag")
    public AjaxResult saveTag(@Validated @RequestBody WeMaterialTagAddDTO weMaterialTagAddDTO) {
        WeMaterialTagEntity weMaterialTagEntity = new WeMaterialTagEntity();
        weMaterialTagEntity.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weMaterialTagEntity.setTagName(weMaterialTagAddDTO.getTagName().trim());
        weMaterialTagService.insertTag(weMaterialTagEntity);
        return AjaxResult.success();
    }

    @Log(title = "删除素材标签", businessType = BusinessType.OTHER)
    @ApiOperation("删除素材标签")
    @DeleteMapping("/delTag/{tagId}")
    public AjaxResult delTag(@PathVariable Long tagId) {
        weMaterialTagService.delTag(tagId);
        return AjaxResult.success();
    }

    @Log(title = "批量打标签", businessType = BusinessType.OTHER)
    @PostMapping("/markTags")
    @ApiOperation("批量打标签")
    public AjaxResult markTags(@RequestBody WeMaterialTagDTO weMaterialTagDTO) {
        weMaterialTagService.markTags(weMaterialTagDTO.getMaterialIds(), weMaterialTagDTO.getTagIds());
        return AjaxResult.success();
    }

    @Log(title = "批量移除标签", businessType = BusinessType.OTHER)
    @PostMapping("/removeTags")
    @ApiOperation("批量移除标签")
    public AjaxResult removeTags(@Validated @RequestBody WeMaterialTagRelRemoveDTO weMaterialTagRelRemoveDTO) {
        weMaterialTagService.removeTags(weMaterialTagRelRemoveDTO.getTagIds(), weMaterialTagRelRemoveDTO.getMaterialIds());
        return AjaxResult.success();
    }

    @Log(title = "查询标签列表", businessType = BusinessType.OTHER)
    @GetMapping("/listTagByName")
    @ApiOperation("查询标签列表")
    public AjaxResult<WeMaterialTagVO> listTagByName(@Param("tagName") String tagName) {
        return AjaxResult.success(weMaterialTagService.listByName(tagName, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wechat:material:publish')")
    @Log(title = "素材发布/取消发布", businessType = BusinessType.OTHER)
    @PutMapping("/showMaterialSwitch")
    @ApiOperation("素材发布/取消发布")
    public AjaxResult showMaterialSwitch(@Validated @RequestBody ShowMaterialSwitchDTO showMaterialSwitchDTO) {
        showMaterialSwitchDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        materialService.showMaterialSwitch(showMaterialSwitchDTO);
        return AjaxResult.success();
    }

    @Log(title = "过期素材恢复/批量恢复", businessType = BusinessType.OTHER)
    @PutMapping("/restore")
    @ApiOperation("过期素材恢复/批量恢复")
    public AjaxResult<WeMediaDTO> restore(@Validated @RequestBody RestoreMaterialDTO restoreMaterialDTO) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        restoreMaterialDTO.setCorpId(corpId);
        materialService.restore(restoreMaterialDTO);
        return AjaxResult.success();
    }

    @Log(title = "查询素材/侧边栏数量", businessType = BusinessType.OTHER)
    @ApiOperation("查询素材/侧边栏数量接口")
    @PostMapping("/getMaterialCount")
    public AjaxResult<WeMaterialCountVO> getMaterialCount(@RequestBody FindWeMaterialDTO findWeMaterialDTO) {
        findWeMaterialDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(materialService.getMaterialCount(findWeMaterialDTO));
    }

}
