package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.EmployCodeSourceEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.file.FileUtils;
import com.easyink.wecom.domain.WeEmpleCodeUseScop;
import com.easyink.wecom.domain.dto.emplecode.AddWeEmpleCodeDTO;
import com.easyink.wecom.domain.dto.emplecode.FindWeEmpleCodeDTO;
import com.easyink.wecom.domain.vo.WeCommunityNewGroupVO;
import com.easyink.wecom.domain.vo.WeEmpleCodeVO;
import com.easyink.wecom.domain.vo.WeEmplyCodeDownloadVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeEmpleCodeService;
import com.easyink.wecom.service.WeEmpleCodeUseScopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * 社群运营 新客自动拉群 Controller
 *
 * @author admin
 * @date 2021-02-19
 */
@Api(tags = "新客自动拉群 Controller")
@RestController
@RequestMapping(value = "/wecom/communityNewGroup")
@Slf4j
public class WeCommunityNewGroupController extends BaseController {

    @Autowired
    private WeEmpleCodeService weEmpleCodeService;

    @Autowired
    private WeEmpleCodeUseScopService weEmpleCodeUseScopService;

    @ApiOperation(value = "新增新客自动拉群", httpMethod = "POST")
    @PreAuthorize("@ss.hasPermi('wecom:communityNewGroup:add')")
    @Log(title = "新客自动拉群", businessType = BusinessType.INSERT)
    @PostMapping("/")
    public <T> AjaxResult<T> add(@RequestBody AddWeEmpleCodeDTO addWeEmpleCodeDTO) {
        addWeEmpleCodeDTO.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        addWeEmpleCodeDTO.setSource(EmployCodeSourceEnum.NEW_GROUP.getSource());
        weEmpleCodeService.insertWeEmpleCode(addWeEmpleCodeDTO);
        return AjaxResult.success();
    }


    /**
     * 修改新客自动拉群
     */
    @ApiOperation(value = "修改新客自动拉群", httpMethod = "PUT")
    @PreAuthorize("@ss.hasPermi('wecom:communityNewGroup:edit')")
    @Log(title = "新客自动拉群", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public <T> AjaxResult<T> edit(@PathVariable("id") String id, @RequestBody @Validated AddWeEmpleCodeDTO weEmpleCode) {
        weEmpleCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weEmpleCodeService.updateWeEmpleCode(weEmpleCode);
        return AjaxResult.success();
    }

    /**
     * 单个下载
     *
     * @param id       待下载员工活码
     * @param response 响应
     */
    @ApiOperation(value = "新客建群下载", httpMethod = "GET")
    @Log(title = "新客建群下载", businessType = BusinessType.OTHER)
    @GetMapping("/download")
    public void download(Long id, HttpServletResponse response) {
        if (id == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<Long> idList = new ArrayList<>();
        idList.add(id);
        List<WeEmplyCodeDownloadVO> empleCodeList = weEmpleCodeService.downloadWeEmplyCodeData(LoginTokenService.getLoginUser().getCorpId(), idList);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(empleCodeList)) {
            throw new CustomException(ResultTip.TIP_EMPLY_CODE_NOT_FOUND);
        }
        WeEmplyCodeDownloadVO emplyCode = empleCodeList.get(0);
        try {
            FileUtils.downloadFile(emplyCode.getQrCode(), response.getOutputStream());
        } catch (IOException exc) {
            log.error("员工活码下载异常 ex：{}", ExceptionUtils.getStackTrace(exc));
            throw new CustomException(ResultTip.TIP_DOWNLOAD_ERROR);
        }
    }

    /**
     * 员工活码批量下载
     *
     * @param ids      新客自动拉群ids
     * @param response 输出
     */
    @ApiOperation(value = "员工活码批量下载", httpMethod = "GET")
    @Log(title = "员工活码批量下载", businessType = BusinessType.OTHER)
    @GetMapping("/downloadBatch")
    public void downloadBatch(Long[] ids, HttpServletResponse response) {
        if (ids == null || ids.length == 0) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<Long> idList = Arrays.asList(ids);
        List<WeEmplyCodeDownloadVO> list = weEmpleCodeService.downloadWeEmplyCodeData(LoginTokenService.getLoginUser().getCorpId(), idList);

        List<Map<String, String>> fileList = list.stream().map(e -> {
            Map<String, String> fileMap = new HashMap<>();
            List<WeEmpleCodeUseScop> weEmployCodeUseScopes = weEmpleCodeUseScopService.selectWeEmpleCodeUseScopListById(e.getId(), LoginTokenService.getLoginUser().getCorpId());
            List<String> userList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(weEmployCodeUseScopes)) {
                weEmployCodeUseScopes.forEach(useScope -> userList.add(useScope.getBusinessName()));
            }
            fileMap.put("fileName", StringUtils.join(userList, ",") + "-" + e.getScenario() + ".jpg");
            fileMap.put("url", e.getQrCode());
            return fileMap;
        }).collect(Collectors.toList());

        try {
            FileUtils.batchDownloadFile(fileList, response.getOutputStream());
        } catch (IOException e) {
            log.error("员工活码批量下载异常 ex：{}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_DOWNLOAD_ERROR);
        }
    }

    /**
     * 查询新客自动拉群列表
     */
    @ApiOperation(value = "查询新客自动拉群列表", httpMethod = "GET")
    //    @PreAuthorize("@ss.hasPermi('wecom:communityNewGroup:list')")
    @GetMapping("/list")
    public TableDataInfo<WeEmpleCodeVO> list(FindWeEmpleCodeDTO weEmpleCode) {
        startPage();
        weEmpleCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weEmpleCode.setSource(EmployCodeSourceEnum.NEW_GROUP.getSource());
        List<WeEmpleCodeVO> list = weEmpleCodeService.selectWeEmpleCodeList(weEmpleCode);
        return getDataTable(list);
    }

    /**
     * 获取新客自动拉群详细信息
     */
    @ApiOperation(value = "获取新客自动拉群详细信息", httpMethod = "GET")
//    @PreAuthorize("@ss.hasPermi('wecom:communityNewGroup:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult<WeCommunityNewGroupVO> getInfo(@PathVariable("id") @ApiParam("主键ID") Long id) {
        return AjaxResult.success(weEmpleCodeService.selectWeEmpleCodeById(id, LoginTokenService.getLoginUser().getCorpId()));
    }


    /**
     * 删除新客自动拉群
     */
    @ApiOperation(value = "删除新客进群", httpMethod = "DELETE")
    @PreAuthorize("@ss.hasPermi('wecom:communityNewGroup:remove')")
    @Log(title = "删除新客进群", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public <T> AjaxResult<T> remove(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        List<Long> idList = Arrays.asList(ids);
        return toAjax(weEmpleCodeService.batchRemoveWeEmpleCodeIds(corpId, idList));
    }

}

