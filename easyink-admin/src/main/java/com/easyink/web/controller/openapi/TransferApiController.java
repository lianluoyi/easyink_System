package com.easyink.web.controller.openapi;


import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.handler.third.SessionArchiveHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 类名: 对外开放的api接口
 *
 * @author : silver_chariot
 * @date : 2022/3/14 15:44
 */
@RestController
@RequestMapping("/transfer")
@Api(tags = {"迁移接口"})
@AllArgsConstructor
@Slf4j
public class TransferApiController {

    private final SessionArchiveHandler sessionArchiveHandler;


    @PostMapping("/sync/mapping")
    @ApiOperation("保存saas企业服务商员工和外部联系人映射关系")
    public AjaxResult<String> syncMapping(@RequestParam String corpId) {
        try {
            sessionArchiveHandler.syncMapping(corpId);
        } catch (Exception e) {
            log.error("[保存saas企业服务商员工和外部联系人映射关系] 异常: {}", ExceptionUtils.getStackTrace(e));
            return AjaxResult.error("保存saas企业服务商员工和外部联系人映射关系失败");
        }
        return AjaxResult.success();
    }


    /**
     * @param file excel文件
     * @param corpId saas服务的对应的企业id密文
     * @param serviceAgentId 第三方服务商的agentId
     * @return
     */
    @PostMapping("/import/sessionArchive")
    @ApiOperation("导入会话存档")
    public AjaxResult<String> importSessionArchive(MultipartFile file, @RequestParam String corpId, @RequestParam String serviceAgentId) {
        try (InputStream is = file.getInputStream()) {
            sessionArchiveHandler.importSessionArchive(is, corpId, serviceAgentId);
        } catch (Exception e) {
            log.error("[导入会话存档] 失败: {}", ExceptionUtils.getStackTrace(e));
            return AjaxResult.error("导入会话存档失败");
        }
        return AjaxResult.success();
    }

}
