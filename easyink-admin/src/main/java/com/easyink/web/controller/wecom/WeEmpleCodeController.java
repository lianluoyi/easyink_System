package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.EmployCodeSourceEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUtils;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeAnalyseDTO;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.domain.vo.WeEmplyCodeScopeUserVO;
import com.easyink.wecom.domain.vo.statistics.emplecode.EmpleCodeByNameVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeEmpleCodeAnalyseService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.service.WeEmpleCodeUseScopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类名： WeEmpleCodeController
 *
 * @author 佚名
 * @date 2021/9/30 15:52
 */
@RestController
@RequestMapping("/wecom/code")
@Slf4j
@Api(tags = "员工活码Controller")
public class WeEmpleCodeController extends BaseController {

    private final WeEmpleCodeService weEmpleCodeService;
    private final WeEmpleCodeUseScopService weEmpleCodeUseScopService;
    private final WeEmpleCodeAnalyseService weEmpleCodeAnalyseService;

    @Autowired
    public WeEmpleCodeController(WeEmpleCodeService weEmpleCodeService, WeEmpleCodeUseScopService weEmpleCodeUseScopService, WeEmpleCodeAnalyseService weEmpleCodeAnalyseService) {
        this.weEmpleCodeService = weEmpleCodeService;
        this.weEmpleCodeUseScopService = weEmpleCodeUseScopService;
        this.weEmpleCodeAnalyseService = weEmpleCodeAnalyseService;
    }


    @PreAuthorize("@ss.hasPermi('wecom:code:add')")
    @Log(title = "员工活码", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation("新增员工活码")
    public <T> AjaxResult<T> add(@RequestBody @Validated AddWeEmpleCodeDTO addWeEmpleCodeDTO) {
        addWeEmpleCodeDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        addWeEmpleCodeDTO.setSource(EmployCodeSourceEnum.CODE_CREATE.getSource());
        weEmpleCodeService.insertWeEmpleCode(addWeEmpleCodeDTO);
        return AjaxResult.success();
    }


    @GetMapping("/list")
    @ApiOperation("查询员工活码列表")
    public TableDataInfo<WeEmpleCodeVO> list(FindWeEmpleCodeDTO weEmpleCode) {
        weEmpleCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        startPage();
        weEmpleCode.setSource(EmployCodeSourceEnum.CODE_CREATE.getSource());
        List<WeEmpleCodeVO> list = weEmpleCodeService.selectWeEmpleCodeList(weEmpleCode);
        return getDataTable(list);
    }

    @GetMapping("/listByName")
    @ApiOperation("活码统计-根据名称模糊搜索活码信息")
    public TableDataInfo<EmpleCodeByNameVO> listByName(FindWeEmpleCodeDTO weEmpleCode) {
        weEmpleCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        startPage();
        List<EmpleCodeByNameVO> list = weEmpleCodeService.listByName(weEmpleCode);
        return getDataTable(list);
    }


    @PreAuthorize("@ss.hasPermi('wecom:code:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取员工活码详细信息")
    public <T> AjaxResult<T> getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(weEmpleCodeService.selectWeEmpleCodeById(id, LoginTokenService.getLoginUser().getCorpId()));
    }

    @PreAuthorize("@ss.hasPermi('wecom:code:edit')")
    @Log(title = "员工活码", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @ApiOperation("修改员工活码")
    public <T> AjaxResult<T> edit(@Validated @RequestBody AddWeEmpleCodeDTO weEmpleCode) {
        weEmpleCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weEmpleCodeService.updateWeEmpleCode(weEmpleCode);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('wecom:code:remove')")
    @Log(title = "员工活码", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ids}")
    @ApiOperation("删除员工活码")
    public <T> AjaxResult<T> remove(@PathVariable String ids) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        List<Long> idList = Arrays.stream(StringUtils.split(ids, ",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        return toAjax(weEmpleCodeService.batchRemoveWeEmpleCodeIds(corpId, idList));
    }


    //   @PreAuthorize("@ss.hasPermi('wecom:code:qrcode')")
    @Log(title = "获取员工二维码", businessType = BusinessType.DELETE)
    @GetMapping("/getQrcode")
    @ApiOperation("获取员工二维码")
    public <T> AjaxResult<T> getQrcode(String userIds, String departmentIds) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weEmpleCodeService.getQrcode(userIds, departmentIds, corpId));
    }

    /**
     * 员工活码批量下载
     *
     * @param ids      员工活码ids
     * @param response 输出
     */
    @Log(title = "员工活码批量下载", businessType = BusinessType.OTHER)
    @GetMapping("/downloadBatch")
    @ApiOperation("员工活码批量下载")
    public void downloadBatch(String ids, HttpServletResponse response) {
        List<Map<String, String>> fileList = Arrays
                .stream(Optional.ofNullable(ids).orElse(StringUtils.EMPTY).split(WeConstans.COMMA))
                .filter(StringUtils::isNotEmpty)
                .map(id -> {
                    WeEmpleCode code = weEmpleCodeService.getById(id);
                    List<WeEmpleCodeUseScop> weEmpleCodeUseScops = weEmpleCodeUseScopService.selectWeEmpleCodeUseScopListById(Long.parseLong(id), LoginTokenService.getLoginUser().getCorpId());
                    Map<String, String> fileMap = new HashMap<>();
                    //code=null说明活码已被删除，不下载
                    if (code != null) {
                        //查出使用者
                        StringBuilder useUserName = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(weEmpleCodeUseScops)) {
                            weEmpleCodeUseScops.forEach(weEmpleCodeUseScop -> useUserName.append(weEmpleCodeUseScop.getBusinessName()).append(WeConstans.COMMA));
                            useUserName.deleteCharAt(useUserName.lastIndexOf(WeConstans.COMMA));
                        }
                        fileMap.put("fileName", useUserName + "-" + code.getScenario() + ".jpg");
                        fileMap.put("url", code.getQrCode());
                    }
                    return fileMap;
                })
                .collect(Collectors.toList());
        try {
            FileUtils.batchDownloadFile(fileList, response.getOutputStream());
        } catch (Exception e) {
            log.error("员工活码批量下载异常 ex：{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @Log(title = "员工活码下载", businessType = BusinessType.OTHER)
    @GetMapping("/download")
    @ApiOperation("员工活码下载")
    public void download(String id, HttpServletResponse response) {
        WeEmpleCode weEmpleCode = weEmpleCodeService.selectWeEmpleCodeById(Long.valueOf(id), LoginTokenService.getLoginUser().getCorpId());
        if (StringUtils.isEmpty(weEmpleCode.getQrCode())) {
            throw new CustomException("活码不存在");
        }
        try {
            FileUtils.downloadFile(weEmpleCode.getQrCode(), response.getOutputStream());
        } catch (IOException e) {
            log.error("员工活码下载异常 ex：{}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_DOWNLOAD_ERROR);
        }
    }

    @Log(title = "时间段内新增和流失客户数据统计", businessType = BusinessType.OTHER)
    @GetMapping("/getTimeRangeAnalyseCount")
    @ApiOperation("时间段内新增和流失客户数据统计")
    public <T> AjaxResult<T> getTimeRangeAnalyseCount(@Validated FindWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO) {
        findWeEmpleCodeAnalyseDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weEmpleCodeAnalyseService.getTimeRangeAnalyseCount(findWeEmpleCodeAnalyseDTO));
    }


    @Log(title = "导出时间段内新增和流失客户数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('wecom:codeAnalyse:export')")
    @GetMapping("/exportTimeRangeAnalyseCount")
    @ApiOperation("导出时间段内新增和流失客户数据")
    public <T> AjaxResult<T> exportTimeRangeAnalyseCount(@Validated FindWeEmpleCodeAnalyseDTO findWeEmpleCodeAnalyseDTO) {
        findWeEmpleCodeAnalyseDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        return AjaxResult.success(weEmpleCodeAnalyseService.exportTimeRangeAnalyseCount(findWeEmpleCodeAnalyseDTO));
    }


    @Log(title = "获取员工活码的使用员工数据", businessType = BusinessType.OTHER)
    @GetMapping("/getUserByEmplyCode/{id}")
    @ApiOperation("获取员工活码的使用员工数据")
    public AjaxResult<List<WeEmplyCodeScopeUserVO>> getUserByEmplyCode(@PathVariable Long id) {
        return AjaxResult.success(weEmpleCodeService.getUserByEmplyCode(LoginTokenService.getLoginUser().getCorpId(), id));
    }


    @GetMapping("/appLink")
    @ApiOperation("获取活码小程序短链")
    public AjaxResult getCodeAppLink(@ApiParam("活码id")Long id ) {
        return AjaxResult.success("success",weEmpleCodeService.getCodeAppLink(id));
    }
    @PostMapping("/refresh/code")
    @ApiOperation("刷新活码")
    public AjaxResult refreshCode(@RequestBody @ApiParam("活码id")List<Long> ids ) {
        weEmpleCodeService.refreshCode(ids);
        return AjaxResult.success("success");
    }
}
