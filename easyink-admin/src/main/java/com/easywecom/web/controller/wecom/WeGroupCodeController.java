package com.easywecom.web.controller.wecom;

import com.easywecom.common.annotation.Log;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.controller.BaseController;
import com.easywecom.common.core.domain.AjaxResult;
import com.easywecom.common.core.page.TableDataInfo;
import com.easywecom.common.enums.BusinessType;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.utils.DateUtils;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.common.utils.file.FileUtils;
import com.easywecom.framework.web.service.FileService;
import com.easywecom.wecom.domain.WeGroupCode;
import com.easywecom.wecom.domain.WeGroupCodeActual;
import com.easywecom.wecom.domain.dto.FindWeGroupCodeDTO;
import com.easywecom.wecom.domain.query.groupcode.GroupCodeDetailQuery;
import com.easywecom.wecom.domain.vo.groupcode.GroupCodeActivityFirstVO;
import com.easywecom.wecom.domain.vo.groupcode.GroupCodeDetailVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.service.WeCorpAccountService;
import com.easywecom.wecom.service.WeGroupCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 客户群活码Controller
 * 类名： WeGroupCodeController
 *
 * @author 佚名
 * @date 2021/9/30 16:07
 */
@Api(tags = "客户群活码Controller")
@RestController
@RequestMapping("/wecom/groupCode")
@Slf4j
@Validated
public class WeGroupCodeController extends BaseController {

    private final WeGroupCodeService groupCodeService;
    private final FileService fileService;
    private final WeCorpAccountService weCorpAccountService;

    @Autowired
    public WeGroupCodeController(WeGroupCodeService groupCodeService, FileService fileService, WeCorpAccountService weCorpAccountService) {
        this.groupCodeService = groupCodeService;
        this.fileService = fileService;
        this.weCorpAccountService = weCorpAccountService;
    }

    /**
     * 查询客户群活码列表
     */
    @ApiOperation(value = "查询客户群活码列表", httpMethod = "GET")
//     @PreAuthorize("@ss.hasPermi('wecom:groupCode:list')")
    @GetMapping("/list")
    public TableDataInfo<WeGroupCode> list(FindWeGroupCodeDTO weGroupCode) {
        startPage();
        //设置企业id
        weGroupCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        List<WeGroupCode> list = groupCodeService.selectWeGroupCodeList(weGroupCode);
        return getDataTable(list);
    }

    @ApiOperation(value = "批量下载群活码", httpMethod = "GET")
    @Log(title = "群活码批量下载", businessType = BusinessType.OTHER)
    @GetMapping("/downloadBatch")
    public void downloadBatch(String ids, HttpServletRequest request, HttpServletResponse response) {
        // 构建文件信息列表
        List<Map<String, String>> fileList = Arrays
                .stream(Optional.ofNullable(ids).orElse("").split(","))
                .filter(StringUtils::isNotEmpty)
                .map(id -> {
                    WeGroupCode code = groupCodeService.getById(id);
                    Map<String, String> fileMap = new HashMap<>();
                    fileMap.put("fileName", code.getActivityName() + ".png");
                    fileMap.put("url", code.getCodeUrl());
                    return fileMap;
                })
                .collect(Collectors.toList());
        try {
            FileUtils.batchDownloadFile(fileList, response.getOutputStream());
        } catch (IOException e) {
            log.error("下载群活码异常 ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @ApiOperation(value = "群活码下载", httpMethod = "GET")
    @Log(title = "群活码下载", businessType = BusinessType.OTHER)
    @GetMapping("/download")
    public void download(String id, HttpServletResponse response) {
        WeGroupCode weGroupCode = groupCodeService.getById(Long.valueOf(id));
        try {
            FileUtils.downloadFile(weGroupCode.getCodeUrl(), response.getOutputStream());
        } catch (IOException e) {
            log.error("下载群活码异常 ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @ApiOperation(value = "获取客户群活码详细信息", httpMethod = "GET")
    @GetMapping
    public AjaxResult getInfo(@Validated GroupCodeDetailQuery groupCodeDetailQuery) {
        WeGroupCode weGroupCode = groupCodeService.getById(groupCodeDetailQuery.getId());
        if (StringUtils.isNull(weGroupCode)) {
            return AjaxResult.error(ResultTip.TIP_GENERAL_NOT_FOUND, "数据不存在");
        }
        startPage();
        List<GroupCodeDetailVO> groupCodeDetailVOList = groupCodeService.getGroupCodeDetail(groupCodeDetailQuery, weGroupCode.getCreateType());
        groupCodeDetailVOList.forEach(groupCode -> {
            //如果为2099年 显示空值给前端
            if (DateUtils.isSameDay(groupCode.getEffectTime(), DateUtils.dateTime(com.easywecom.common.utils.DateUtils.YYYY_MM_DD_HH_MM, WeConstans.DEFAULT_MATERIAL_NOT_EXPIRE))) {
                groupCode.setEffectTime(null);
            }
        });
        weGroupCode.setGroupCodeDetailVOList(groupCodeDetailVOList);
        return AjaxResult.success(weGroupCode);
    }

    /**
     * 新增客户群活码
     */
    @PreAuthorize("@ss.hasPermi('wecom:groupCode:add')")
    @Log(title = "客户群活码", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增客户群活码", httpMethod = "POST")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WeGroupCode weGroupCode) {
        weGroupCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            weGroupCode.setCreateBy(LoginTokenService.getUsername());
        } else {
            //员工则保存userId
            weGroupCode.setCreateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        groupCodeService.add(weGroupCode);
        return AjaxResult.success();
    }

    /**
     * 修改客户群活码
     */
    @ApiOperation(value = "修改客户群活码", httpMethod = "PUT")
    @PreAuthorize("@ss.hasPermi('wecom:groupCode:edit')")
    @Log(title = "客户群活码", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{id}")
    public AjaxResult edit(@PathVariable("id") Long id, @RequestBody WeGroupCode weGroupCode) {
        weGroupCode.setId(id);
        weGroupCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (LoginTokenService.getLoginUser().isSuperAdmin()) {
            weGroupCode.setUpdateBy(LoginTokenService.getUsername());
        } else {
            //员工则保存userId
            weGroupCode.setUpdateBy(LoginTokenService.getLoginUser().getWeUser().getUserId());
        }
        ;
        return toAjax(groupCodeService.edit(weGroupCode));
    }

    /**
     * 删除客户群活码
     */
    @ApiOperation(value = "删除客户群活码", httpMethod = "DELETE")
    @PreAuthorize("@ss.hasPermi('wecom:groupCode:remove')")
    @Log(title = "客户群活码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult batchRemove(@PathVariable Long[] ids) {
        return toAjax(groupCodeService.remove(ids));
    }

    /**
     * 从群活码获取第一个可用的实际码
     */
    @ApiOperation(value = "从群活码获取第一个可用的实际码", httpMethod = "GET")
    @GetMapping("/getActualCode/{groupCodeId}")
    public AjaxResult<WeGroupCodeActual> getActual(@PathVariable("groupCodeId") Long id) {
        WeGroupCode groupCode = groupCodeService.getById(id);
        if (groupCode == null) {
            return AjaxResult.success(ResultTip.TIP_NO_AVAILABLE_GROUP_CODE.getTipMsg());
        }
        GroupCodeActivityFirstVO activityFirstVO = groupCodeService.doGetActual(id, groupCode);

        if (activityFirstVO != null) {
            return AjaxResult.success(activityFirstVO);
        }
        // 找不到可用的实际群活码也不要抛出错误，否则前端H5页面不好处理。
        return AjaxResult.success(ResultTip.TIP_NO_AVAILABLE_GROUP_CODE.getTipMsg());
    }


}
