package com.easyink.wecom.service.impl;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.http.ForestResponse;
import com.easyink.common.utils.OkHttpUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeThirdPartyPushClient;
import com.easyink.wecom.domain.WeCorpThirdPartyConfig;
import com.easyink.wecom.domain.WeThirdPartyPushLog;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushRequest;
import com.easyink.wecom.domain.dto.form.push.ThirdPartyPushSysData;
import com.easyink.wecom.domain.enums.form.push.FormRecordStatusEnum;
import com.easyink.wecom.service.WeCorpThirdPartyConfigService;
import com.easyink.wecom.service.WeFormSendRecordService;
import com.easyink.wecom.service.WeThirdPartyPushLogService;
import com.easyink.wecom.service.WeThirdPartyPushService;
import com.easyink.wecom.util.ThirdPartyPushDataConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 第三方推送服务实现类
 *
 * @author easyink
 */
@Slf4j
@Service
public class WeThirdPartyPushServiceImpl implements WeThirdPartyPushService {
    /**
     * 参数必填未通过错误msg
     */
    private static final String ERROR_MSG = "参数必填校验未通过";
    @Autowired
    private WeCorpThirdPartyConfigService configService;
    @Autowired
    private WeThirdPartyPushLogService pushLogService;
    @Autowired
    private WeThirdPartyPushClient weThirdPartyPushClient;
    @Autowired
    private WeFormSendRecordService formSendRecordService;
    @Autowired
    private ThirdPartyPushDataConverter converter;

    @Override
    public boolean pushFormSubmitData(ThirdPartyPushSysData pushData, Long recordId) {

        ThirdPartyPushRequest request = converter.convert(pushData);
        return push(pushData.getCorpId(), JSON.toJSONString(request.getContent()), pushData.getPushType(), recordId, request == null || request.isFastError());
    }


    @Override
    public boolean push(String corpId, String pushData, Integer pushType, Long recordId, boolean fastError) {
        if (StringUtils.isAnyBlank(corpId, pushData) || pushType == null) {
            log.warn("[表单推送] 推送数据失败：参数不完整");
            return false;
        }

        // 获取企业推送配置
        WeCorpThirdPartyConfig config = configService.getByCorpId(corpId);
        if (config == null || !FormRecordStatusEnum.PushSettingStatusEnum.ENABLED.getCode().equals(config.getStatus())) {
            log.warn("[表单推送] 企业{}未配置第三方推送或配置已禁用", corpId);
            return false;
        }

        // 创建推送日志
        WeThirdPartyPushLog pushLog = new WeThirdPartyPushLog();
        pushLog.setCorpId(corpId);
        pushLog.setRecordId(recordId);
        pushLog.setPushType(pushType);
        pushLog.setPushUrl(config.getPushUrl());
        pushLog.setPushData(pushData);
        pushLog.setPushStatus(FormRecordStatusEnum.PushLogStatusEnum.PUSHING.getCode());
        pushLog.setPushTime(new Date());

        boolean pushResult = false;
        long startTime = System.currentTimeMillis();
        Integer formPushType;
        try {

            if(fastError){
                pushLog.setPushStatus(FormRecordStatusEnum.PushLogStatusEnum.FAILED.getCode());
                formPushType = FormRecordStatusEnum.PushStatusEnum.PUSHED_FAILED.getCode();
                pushLog.setErrorMsg(ERROR_MSG);
                pushLog.setResponseTime(new Date());
                pushLog.setCostTime(System.currentTimeMillis() - startTime);
            }else{
                OkHttpUtil.HttpResponse httpResponse = OkHttpUtil.postJson(config.getPushUrl(), pushData);
                log.info("[表单推送] url:【{}】,result:【{}】", config.getPushUrl(), httpResponse.getBody());

//                ForestResponse<String> response;
//                response = weThirdPartyPushClient.pushData(config.getPushUrl(), pushData);

                // 处理响应
                pushLog.setHttpCode(httpResponse.getCode());
                pushLog.setPushResult(StringUtils.substring(httpResponse.getBody(),0, 1000));
                pushLog.setResponseTime(new Date());
                pushLog.setCostTime(System.currentTimeMillis() - startTime);

                if (httpResponse.isSuccess()) {
                    pushLog.setPushStatus(FormRecordStatusEnum.PushLogStatusEnum.SUCCESS.getCode());
                    formPushType = FormRecordStatusEnum.PushStatusEnum.PUSHED_SUCCESS.getCode();
                    pushResult = true;
                    log.info("[表单推送] 推送数据成功：corpId={}, pushType={}, statusCode={}", corpId, pushType, httpResponse.getCode());
                } else {
                    pushLog.setPushStatus(FormRecordStatusEnum.PushLogStatusEnum.FAILED.getCode());
                    formPushType = FormRecordStatusEnum.PushStatusEnum.PUSHED_FAILED.getCode();
                    pushLog.setErrorMsg("HTTP响应状态码：" + httpResponse.getCode());
                    log.warn("[表单推送] 推送数据失败：corpId={}, pushType={}, httpCode={}", corpId, pushType, httpResponse.getCode());
                }
            }
        } catch (Exception e) {
            pushLog.setPushStatus(FormRecordStatusEnum.PushLogStatusEnum.FAILED.getCode());
            formPushType = FormRecordStatusEnum.PushStatusEnum.PUSHED_FAILED.getCode();
            pushLog.setErrorMsg(e.getMessage());
            pushLog.setResponseTime(new Date());
            pushLog.setCostTime(System.currentTimeMillis() - startTime);
            log.error("[表单推送] 推送数据异常：corpId={}, pushType={}, error={}", corpId, pushType, e.getMessage(), e);
        }
        formSendRecordService.updatePushStatus(recordId, formPushType);
        // 保存推送日志
        pushLogService.save(pushLog);

        return pushResult;
    }

}
