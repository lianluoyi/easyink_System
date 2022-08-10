package com.easywecom.wecom.client;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.annotation.*;
import com.easywecom.wecom.domain.dto.autoconfig.*;
import com.easywecom.wecom.interceptor.WeAutoConfigInterceptor;
import org.springframework.stereotype.Component;

/**
 * 类名: 企业微信管理后台接口
 *
 * @author: 1*+
 * @date: 2021-08-23 9:26
 */
@Component
@BaseRequest(baseURL = "https://work.weixin.qq.com/wework_admin", interceptor = WeAutoConfigInterceptor.class)
public interface WeAdminClient {

    /**
     * 获取登录微信后台二维码
     *
     * @param loginType   登录类型：login_admin
     * @param callback    回调："wwqrloginCallback_" + System.currentTimeMillis();
     * @param redirectUri 重定向url："https://work.weixin.qq.com/wework_admin/loginpage_wx?_r=989&url_hash=";
     * @param crossorign  跨域：1
     * @return 返回二维码的key
     */
    @Get(url = "/wwqrlogin/mng/get_key")
    BaseAdminResult<WeGetKeyResp> getKey(@Query("login_type") String loginType, @Query("callback") String callback,
                                         @Query("redirect_uri") String redirectUri, @Query("crossorign") Integer crossorign);

    /**
     * 检测二维码状态
     *
     * @param qrcodeKey 二维码key
     * @param status    二维码状态：QRCODE_SCAN_NEVER
     * @return
     */
    @Get(url = "/wwqrlogin/mng/check", retryCount = 0, timeout = 5000)
    JSONObject check(@Query("qrcode_key") String qrcodeKey, @Query("status") String status);

    /**
     * 登录接口
     *
     * @param urlHash    空字符串
     * @param code       检测二维码接口返回的auth_code
     * @param wwQrLogin  默认为1
     * @param qrcodeKey  二维码key
     * @param authSource 授权源：检测二维码接口返回的auth_source
     * @return
     */
    @Get(url = "/loginpage_wx")
    String login(@Query("url_hash") String urlHash, @Query("code") String code,
                 @Query("wwqrlogin") Integer wwQrLogin, @Query("qrcode_key") String qrcodeKey, @Query("auth_source") String authSource);


    /**
     * 获取公司所有应用列表
     *
     * @param appType 应用类型
     * @return
     */
    @Post(url = "/getCorpApplication")
    BaseAdminResult<WeCorpApplicationResp> getCorpApplication(@Body("app_type") Integer appType, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 创建应用接口
     *
     * @param name               应用名
     * @param description        应用描述
     * @param englishName        应用英文名
     * @param englishDescription 应用描述英文
     * @param appOpen            应用是否启用
     * @param logoImage          应用的logo
     * @param visiblePid         应用可见范围:部门
     * @return
     */
    @Post(url = "/apps/addOpenApiApp")
    BaseAdminResult<WeCorpApplicationResp.OpenapiApp> addOpenApiApp(@Body("name") String name, @Body("description") String description,
                                                                    @Body("english_name") String englishName, @Body("english_description") String englishDescription,
                                                                    @Body("app_open") boolean appOpen, @Body("logoimage") String logoImage,
                                                                    @Body("visible_pid[]") String visiblePid, @Header("qrcodeKey") String qrcodeKey
    );

    /**
     * 删除用用
     *
     * @param appId     应用ID
     * @param appOpenId 应用外部ID
     * @param appType   应用类型
     * @return
     */
    @Post(url = "/apps/delOpenApiApp")
    BaseAdminResult delOpenApiApp(@Body("app_id") String appId, @Body("app_open_id") Integer appOpenId, @Body("app_type") String appType, @Header("qrcodeKey") String qrcodeKey);


    /**
     * 获取应用可调用应用列表
     *
     * @param businessId 应用ID
     * @return
     */
    @Get(url = "/apps/getApiAccessibleApps")
    BaseAdminResult<WeApiAccessibleAppsResp> getApiAccessibleApps(@Query("businessId") Integer businessId, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 设置应用可调用应用列表
     *
     * @param businessId 应用ID
     * @param appid      列表
     * @return
     */
    @Post(url = "/apps/setApiAccessibleApps")
    BaseAdminResult setApiAccessibleApps(@Body("businessId") Integer businessId, @Body("auth_list[appid_list][]") String appid, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 保存应用配置-设置事件回调
     *
     * @param callbackUrl         回调地址
     * @param urlToken            token
     * @param aesKey              aeskey
     * @param reportApprovalEvent 审批状态通知事件：true
     * @param customerEvent       外部联系人变更回调：true
     * @param appId               应用内部ID
     * @param callbackHost        回调主域名
     * @param reportLocFlag       上报地理位置：0关闭，1开启
     * @param isReportEnter       上报进入应用事件：false
     * @return
     */
    @Post(url = "/apps/saveOpenApiApp")
    BaseAdminResult saveOpenApiApp(@Body("callback_url") String callbackUrl, @Body("url_token") String urlToken, @Body("callback_aeskey") String aesKey,
                                   @Body("report_approval_event") boolean reportApprovalEvent, @Body("customer_event") boolean customerEvent,
                                   @Body("app_id") String appId, @Body("callback_host") String callbackHost, @Body("report_loc_flag") Integer reportLocFlag,
                                   @Body("is_report_enter") boolean isReportEnter, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 保存应用配置-设置登录回调
     *
     * @param redirectDomain2  登录回调域名
     * @param bundleid         iOs的bundleId: 没配置默认""
     * @param signatureAndroid 安卓应用签名：没配置默认""
     * @param packagename      安卓应用包名：没配置默认""
     * @param appId            应用内部ID
     * @param appFlag          应用Flag
     * @param bIos             是否iOs：因为我们是修改web端所以使用false
     * @param bAndroid         是否安卓：因为我们是修改web端所以使用false
     * @return
     */
    @Post(url = "/apps/saveOpenApiApp")
    BaseAdminResult saveOpenApiApp(@Body("redirect_domain2") String redirectDomain2, @Body("bundleid") String bundleid, @Body("signature_android") String signatureAndroid,
                                   @Body("packagename") String packagename, @Body("app_id") String appId, @Body("app_flag") Integer appFlag,
                                   @Body("b_ios") boolean bIos, @Body("b_android") boolean bAndroid, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 保存应用配置-设置网页授权及JS-SDK
     *
     * @param appId                     应用内部ID
     * @param redirectDomain            可作为应用OAuth2.0网页授权功能的回调域名可信域名
     * @param isCheckDomainOwnership    通过后台接口分析出来：false
     * @param miniprogramDomains        可调用JS-SDK、跳转小程序的可信域名（最多10个，需完成域名校验）
     * @param miniprogramDomainsOperate 通过后台接口分析出来：true
     * @return
     */
    @Post(url = "/apps/saveOpenApiApp")
    BaseAdminResult saveOpenApiApp(@Body("app_id") String appId, @Body("redirect_domain") String redirectDomain,
                                   @Body("is_check_domain_ownership") boolean isCheckDomainOwnership, @Body("miniprogram_domains[]") String miniprogramDomains,
                                   @Body("miniprogram_domains_operate") boolean miniprogramDomainsOperate, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 开启通讯录应用API接口同步
     *
     * @param appId
     * @param appOpenId
     * @param appOpen
     * @param qrcodeKey
     * @return
     */
    @Post(url = "/apps/saveOpenApiApp")
    BaseAdminResult openMailListApi(@Body("app_id") String appId, @Body("app_open_id") Integer appOpenId, @Body("app_open") Boolean appOpen, @Header("qrcodeKey") String qrcodeKey);


    /**
     * 开启通讯录应用：手动编辑权限
     *
     * @param appId         通讯录ID
     * @param archRwFlag    读写权限标识： 1开启，0关闭
     * @param bOpenSyncEdit 手动编辑权限，true开启
     * @param appOpen       应用开启 true开启
     * @param qrcodeKey     二维码KEy用于拦截器选择启用
     * @return {@link BaseAdminResult}
     */
    @Post(url = "/apps/saveOpenApiApp")
    BaseAdminResult openMailListRWPermission(@Body("app_id") String appId, @Body("arch_rw_flag") Integer archRwFlag, @Body("b_open_sync_edit") Boolean bOpenSyncEdit, @Body("app_open") Boolean appOpen, @Header("qrcodeKey") String qrcodeKey);


    /**
     * 检测回调地址
     *
     * @param url   回调地址
     * @param appid 应用ID
     * @param type  类型：url
     * @return
     */
    @Get(url = "/apps/checkCustomAppURL")
    BaseAdminResult<WeCheckCustomAppUrlResp> checkCustomAppURL(@Query("url") String url, @Query("appid") String appid, @Query("type") String type, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 获取域名校验文件
     *
     * @return
     */
    @Get(url = "/apps/getDomainOwnershipVerifyInfo")
    DomainOwnershipVerifyInfoResp getDomainOwnershipVerifyInfo(@Header("qrcodeKey") String qrcodeKey);

    /**
     * 获取域名校验文件内容
     *
     * @return
     */
    @Get(url = "/apps/getDomainOwnershipVerifyInfo")
    String getDomainOwnershipVerifyInfo(@Query("action") String action, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 添加侧边栏菜单
     *
     * @param appid
     * @param appOpen
     * @param isThirdApp
     * @param isMiniApp
     * @param isBaseApp
     * @param id
     * @param logoimage
     * @param appName
     * @param smImgid
     * @param itemType
     * @param name
     * @param itemName
     * @param itemInfo
     * @return
     */
    @Post(url = "/customer/addChatMenu")
    BaseAdminResult addChatMenu(@Body("banner_list[0][corp_app][app_id]") String appid, @Body("banner_list[0][corpAppModel][app_open]") Integer appOpen,
                                @Body("banner_list[0][corpAppModel][isThirdApp]") Integer isThirdApp, @Body("banner_list[0][corpAppModel][isMiniApp]") Integer isMiniApp,
                                @Body("banner_list[0][corpAppModel][isBaseApp]") boolean isBaseApp, @Body("banner_list[0][corpAppModel][id]") String id,
                                @Body("banner_list[0][corpAppModel][logoimage]") String logoimage, @Body("banner_list[0][corpAppModel][name]") String appName,
                                @Body("banner_list[0][corpAppModel][sm_imgid]") String smImgid, @Body("banner_list[0][item_type]") Integer itemType,
                                @Body("banner_list[0][name]") String name, @Body("banner_list[0][item_name]") String itemName,
                                @Body("banner_list[0][item_info]") String itemInfo, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 获取所有已配置的侧边栏菜单
     *
     * @return
     */
    @Get(url = "/customer/getChatMenu")
    BaseAdminResult<WeGetChatMenuResp> getChatMenu( @Header("qrcodeKey") String qrcodeKey);

    /**
     * 将secert发送到客户端
     *
     * @param appid        内部应用ID
     * @param businessType 默认1
     * @param appType      默认1
     * @return
     */
    @Post("/two_factor_auth_operation/create")
    BaseAdminResult create(@Body("appid") String appid, @Body("business_type") Integer businessType, @Body("app_type") Integer appType, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 获取客户联系权限配置
     *
     * @return {@link BaseAdminResult<WeGetGroupListResp>}
     */
    @Get("/customer/group/getGroupList")
    BaseAdminResult<WeGetGroupListResp> getGroupList(@Header("qrcodeKey") String qrcodeKey);

    /**
     * 设置客户联系和客户群使用范围
     *
     * @param partyids 部门ID
     * @return {@link BaseAdminResult}
     */
    @Post("/customer/followers")
    BaseAdminResult followers(@Body("partyids[]") String partyids, @Header("qrcodeKey") String qrcodeKey);


    /**
     * 设置客户朋友圈使用范围
     *
     * @param partyids 部门ID
     * @return {@link BaseAdminResult}
     */
    @Post("/customer/group/setMomentsRange")
    BaseAdminResult setMomentsRange(@Body("partyids[]") String partyids, @Header("qrcodeKey") String qrcodeKey);

    /**
     * 验证手机短信二维码
     *
     * @param captcha   验证码
     * @param tlKey     tlKey 由获取验证码的网页返回
     * @param qrcodeKey
     * @return {@link BaseAdminResult}
     */
    @Post("/mobile_confirm/confirm_captcha")
    String confirmCaptcha(@Body("captcha") String captcha, @Body("tl_key") String tlKey, @Header("qrcodeKey") String qrcodeKey, @Header("referer") String referer);

    /**
     * 重新发送手机短信验证码
     *
     * @param tlKey tlKey 由获取验证码的网页返回
     * @return {@link BaseAdminResult}
     */
    @Post("/mobile_confirm/send_captcha")
    BaseAdminResult sendCaptcha(@Body("tl_key") String tlKey, @Header("qrcodeKey") String qrcodeKey, @Header("referer") String referer);

    /**
     * 选择企业（会302重定向到短信验证接口)
     *
     * @param tlKey tlKey 由登录接口如果需要短信验证则会返回
     * @return 响应
     */
    @Get("/login/choose_corp")
    String chooseCorp(@Query("tl_key") String tlKey, @Header("qrcodeKey") String qrcodeKey, @Header("referer") String referer);
}
