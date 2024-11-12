package com.easyink.web.controller.openapi;


import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.handler.third.SessionArchiveHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/import/sessionArchive")
    @ApiOperation("导入会话存档")
    public AjaxResult<String> importSessionArchive(MultipartFile file, @RequestParam String corpId) {
        try (InputStream is = file.getInputStream()) {
            sessionArchiveHandler.importSessionArchive(is, corpId);
        } catch (Exception e) {
            log.info("导入会话存档失败: {}", e.getMessage());
            return AjaxResult.error("导入会话存档失败");
        }
        return AjaxResult.success();
    }

}
