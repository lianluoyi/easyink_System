package com.easyink.wecom.openapi.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.config.WeCrypt;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.bean.BeanUtils;
import com.easyink.common.utils.wecom.WxCryptUtil;
import com.easyink.wecom.openapi.dao.AppCallbackSettingMapper;
import com.easyink.wecom.openapi.domain.entity.AppCallbackSetting;
import com.easyink.wecom.openapi.dto.AddCallbackDTO;
import com.easyink.wecom.openapi.dto.EditCallbackDTO;
import com.easyink.wecom.openapi.service.AppCallbackSettingService;
import com.easyink.wecom.utils.JsoupUtil;
import jodd.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.RandomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 类名: API-消息订阅配置表业务层接口实现类
 *
 * @author : silver_chariot
 * @date : 2023/7/17 11:40
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class AppCallbackSettingServiceImpl extends ServiceImpl<AppCallbackSettingMapper, AppCallbackSetting> implements AppCallbackSettingService {
    /**
     * 最大回调地址设置数
     */
    private static final int MAX_CALLBACK_URL_CNT = 3;
    /**
     * 回调转发推送最大重试次数
     */
    private static final int MAX_RETRY_CNT = 3;
    /**
     * 回调转发推送最大超时时间
     */
    private static final int MAX_RETRY_TIMEOUT = 1000;
    /**
     * 回调推送成功的响应
     */
    private static final String SUCCESS_FLAG = "success";
    private final RuoYiConfig ruoyiConfig;

    @Override
    public AppCallbackSetting addUrl(AddCallbackDTO dto) {
        if (dto == null || StringUtils.isAnyBlank(dto.getCallbackUrl())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        AppCallbackSetting appCallbackSetting = new AppCallbackSetting();
        BeanUtils.copyProperties(dto, appCallbackSetting);
        List<AppCallbackSetting> list = this.list(new LambdaQueryWrapper<AppCallbackSetting>().eq(AppCallbackSetting::getCorpId, dto.getCorpId()));
        // 检验新增的回调地址
        if (!JsoupUtil.isValidUrl(appCallbackSetting.getCallbackUrl())) {
            throw new CustomException(ResultTip.TIP_URL_ERROR);
        }
        validateCallbackUrl(appCallbackSetting, list);
        // 设置token和aesKey(如果没有对应的传参)
        if (StringUtils.isAnyBlank(dto.getToken(), dto.getEncodingAesKey())) {
            WeCrypt weCrypt = ruoyiConfig.isThirdServer() ? ruoyiConfig.getProvider()
                                                                       .getCryptById(dto.getCorpId()) : ruoyiConfig.getSelfBuild();
            appCallbackSetting.setToken(weCrypt.getToken());
            appCallbackSetting.setEncodingAesKey(weCrypt.getEncodingAesKey());
        }
        //校验地址
        if (!checkCallback(dto.getCorpId(), dto.getCallbackUrl())) {
            throw new CustomException(ResultTip.CALLBACK_FAIL);
        }
        // 插入回调配置
        this.save(appCallbackSetting);
        return appCallbackSetting;
    }

    @Override
    public void editUrl(EditCallbackDTO dto) {
        if (dto == null || dto.getId() == null || StringUtils.isBlank(dto.getCallbackUrl())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        AppCallbackSetting appCallbackSetting = new AppCallbackSetting();
        BeanUtils.copyProperties(dto, appCallbackSetting);
        if (!JsoupUtil.isValidUrl(appCallbackSetting.getCallbackUrl())) {
            throw new CustomException(ResultTip.TIP_URL_ERROR);
        }
        //校验地址
        if (!checkCallback(dto.getCorpId(), dto.getCallbackUrl())) {
            throw new CustomException(ResultTip.CALLBACK_FAIL);
        } if (!this.updateById(appCallbackSetting)) {
            throw new CustomException(ResultTip.EDIT_URL_ERROR);
        }

    }

    @Override
    public void deleteUrl(Long id) {
        if (id == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        boolean res = this.removeById(id);
        if (!res) {
            throw new CustomException(ResultTip.DEL_URL_ERROR);
        }
    }

    @Override
    public void sendCallback(String corpId, String msg, String signature, String timestamp, String nonce) {
        // 查询出需要发送回调的地址
        List<AppCallbackSetting> list = list(new LambdaQueryWrapper<AppCallbackSetting>().eq(AppCallbackSetting::getCorpId, corpId));
        String callbackUrl;
        boolean shouldRetry;
        for (AppCallbackSetting setting : list) {
            // 创建HttpPost对象，并设置目标URL
            callbackUrl = setting.getCallbackUrl() + "?msg_signature=" + signature + "&timestamp=" + timestamp + "&nonce=" + nonce;
            String responseBody = "";
            int attemptCnt = 0;
            do {
                try {
                    responseBody = HttpUtil.post(callbackUrl, msg, MAX_RETRY_TIMEOUT);
                } catch (Exception e) {
                    log.error("[转发回调]发生异常,corpId:{},msg:{},signature:{},timestamp:{},nonce:{}, e:{}", corpId, msg, signature, timestamp, nonce, ExceptionUtils.getStackTrace(e));
                }
                attemptCnt++;
                // 回调地址返回success或者重试次数大于3 停止请求
                shouldRetry = StringUtils.isBlank(responseBody) && attemptCnt <= MAX_RETRY_CNT;
                log.info("[转发回调]响应结果,corpId:{}, url:{},attemptCnt :{} , res:{}", corpId, setting.getCallbackUrl(), attemptCnt, responseBody);
            } while (shouldRetry);
        }
    }

    @Override
    public boolean checkCallback(String corpId, String callbackUrl) {
        // 查询出需要发送回调的地址
        String timestamp = String.valueOf(new Date().getTime());
        String nonce = RandomUtil.randomNumbers(9);
        WeCrypt weCrypt = ruoyiConfig.isThirdServer() ? ruoyiConfig.getProvider()
                                                                   .getCryptById(corpId) : ruoyiConfig.getSelfBuild();
        WxCryptUtil wxCryptUtil = new WxCryptUtil(weCrypt.getToken(), weCrypt.getEncodingAesKey());
        //明文字符串由16个字节的随机字符串、4个字节的msg长度、明文msg和receiveid拼接组成。其中msg_len为msg的字节数，网络字节序；sReceiveId 在不同场景下有不同含义，见附注
        //https://developer.work.weixin.qq.com/document/path/90968#%E5%AF%86%E6%96%87%E8%A7%A3%E5%AF%86%E5%BE%97%E5%88%B0msg%E7%9A%84%E8%BF%87%E7%A8%8B
        String randomStr = RandomUtils.getRandomStr();
        String echoStr = wxCryptUtil.genEchoStr(randomStr,corpId);
        String signature = wxCryptUtil.encryptSign(timestamp, nonce, echoStr);
        // 创建HttpPost对象，并设置目标URL
        callbackUrl = callbackUrl + "?msg_signature=" + signature + "&timestamp=" + timestamp + "&nonce=" + nonce + "&echostr=" + URLEncoder.encode(echoStr);
        String responseBody = "";
        int attemptCnt = 0;
        do {
            try {
                responseBody = HttpUtil.get(callbackUrl, MAX_RETRY_TIMEOUT);
            } catch (Exception e) {
                log.error("[校验回调地址]发生异常,corpId:{},url:{},signature:{},timestamp:{},nonce:{}, e:{}", corpId, callbackUrl,signature, timestamp, nonce, ExceptionUtils.getStackTrace(e));
            }
            attemptCnt++;
            log.info("[校验回调地址]响应结果,corpId:{}, url:{},attemptCnt :{} , res:{}", corpId,callbackUrl, attemptCnt, responseBody);
            if(StringUtils.isNotBlank(responseBody)) {
                return randomStr.equals(responseBody);
            }
        } while (attemptCnt <= MAX_RETRY_CNT);
        return false;
    }


    /**
     * 校验回调地址url
     *
     * @param appCallbackSetting 回调地址实体
     * @param list               数据库里的回调地址列表
     */
    private void validateCallbackUrl(AppCallbackSetting appCallbackSetting, List<AppCallbackSetting> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() >= MAX_CALLBACK_URL_CNT) {
                throw new CustomException(ResultTip.TIP_MAX_CALLBACK_URL_CNT);
            }
            if (list.contains(appCallbackSetting)) {
                throw new CustomException(ResultTip.HAS_THIS_CALLBACK_URL);
            }
        }
    }
}
