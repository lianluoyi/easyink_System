package com.easyink.web.controller.system;

import com.easyink.common.annotation.Log;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.domain.entity.SysUser;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.enums.MediaType;
import com.easyink.common.exception.file.InvalidExtensionException;
import com.easyink.common.service.ISysUserService;
import com.easyink.common.token.TokenService;
import com.easyink.common.utils.SecurityUtils;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.file.MimeTypeUtils;
import com.easyink.wecom.client.WeMediaClient;
import com.easyink.wecom.client.WeUserClient;
import com.easyink.wecom.domain.dto.WeMediaDTO;
import com.easyink.wecom.domain.dto.WeUserDTO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.easyink.common.utils.wecom.LoginRsaUtil.decryptByPrivateKey;

/**
 * 个人信息 业务处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/system/user/profile")
@Api(tags = "个人信息 业务处理")
public class SysProfileController extends BaseController {
    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private WeMediaClient weMediaClient;
    @Autowired
    private WeUserService weUserService;
    @Autowired
    private WeUserClient weUserClient;
    @Autowired
    private RuoYiConfig ruoYiConfig;
    /**
     * 个人信息
     */
    @GetMapping
    @ApiOperation("个人信息")
    public AjaxResult profile() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改用户")
    public AjaxResult updateProfile(@RequestBody SysUser user) {
        if (userService.updateUserProfile(user) > 0) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            // 更新缓存用户信息
            loginUser.getUser().setNickName(user.getNickName());
            loginUser.getUser().setPhonenumber(user.getPhonenumber());
            loginUser.getUser().setEmail(user.getEmail());
            loginUser.getUser().setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    @ApiOperation("重置密码")
    public AjaxResult updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        String decryptOldPassword = decryptByPrivateKey(ruoYiConfig.getLoginRsaPrivateKey(), oldPassword);
        String decryptNewPassword = decryptByPrivateKey(ruoYiConfig.getLoginRsaPrivateKey(), newPassword);
        if (!SecurityUtils.matchesPassword(decryptOldPassword, password)) {
            return AjaxResult.error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(decryptNewPassword, password)) {
            return AjaxResult.error("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(decryptNewPassword)) > 0) {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(decryptNewPassword));
            tokenService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    @ApiOperation("头像上传")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file, String fileName) throws IOException, InvalidExtensionException {
        if (!file.isEmpty()) {
            FileUploadUtils.fileSuffixVerify(fileName, ruoYiConfig.getFile().getAllowUploadExtensionList());
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            //判断是系统用户（超管）还是企微用户
            if (loginUser.isSuperAdmin()) {
                // 获取资源映射前缀
                String avatar = FileUploadUtils.uploadV2(fileName, file, MimeTypeUtils.getDefaultAllowedExtension());
                userService.updateUserAvatar(loginUser.getUsername(), avatar);
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
            //企微用户
            if (!loginUser.isSuperAdmin()) {
                WeUser weUser = loginUser.getWeUser();
                WeMediaDTO weMediaDTO = weMediaClient.upload(file.getInputStream(), file.getName(), MediaType.IMAGE.getMediaType(), LoginTokenService.getLoginUser().getCorpId(), (int) file.getSize(), WeConstans.WE_UPLOAD_FORM_DATA_CONTENT_TYPE);
                weUser.setAvatarMediaid(weMediaDTO.getMedia_id());
                //用临时素材更新头像
                weUserClient.updateUser(new WeUserDTO(weUser), loginUser.getCorpId());
                //获取头像地址
                WeUserDTO user = weUserClient.getUserByUserId(weUser.getUserId(), loginUser.getCorpId());
                loginUser.getWeUser().setAvatarMediaid(user.getAvatar());
                weUser.setAvatarMediaid(user.getAvatar());
                //更新本地
                weUserService.updateWeUserNoToWeCom(weUser);
                tokenService.setLoginUser(loginUser);
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", user.getAvatar());
                return ajax;
            }

        }
        return AjaxResult.error("上传图片异常，请联系管理员");
    }
}
