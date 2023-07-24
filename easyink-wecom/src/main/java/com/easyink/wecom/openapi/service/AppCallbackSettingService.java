package com.easyink.wecom.openapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.openapi.domain.entity.AppCallbackSetting;
import com.easyink.wecom.openapi.dto.AddCallbackDTO;
import com.easyink.wecom.openapi.dto.EditCallbackDTO;

/**
 * 类名: API-消息订阅配置表业务层接口
 *
 * @author : silver_chariot
 * @date : 2023/7/17 11:38
 **/
public interface AppCallbackSettingService extends IService<AppCallbackSetting> {
    /**
     * 新增回调订阅地址
     *
     * @param dto {@link AddCallbackDTO} 新增所需参数
     * @return
     */
    AppCallbackSetting addUrl(AddCallbackDTO dto);

    /**
     * 编辑回调地址
     *
     * @param dto {@link EditCallbackDTO}  编辑所需参数
     */
    void editUrl(EditCallbackDTO dto);

    /**
     * 删除回调地址
     *
     * @param id 回调地址id
     */
    void deleteUrl(Long id);

    /**
     * 发送回调POST  （按原请求转发）
     *
     * @param corpId corpId
     * @param msg        消息体
     * @param signature  签名
     * @param timestamp  时间戳
     * @param nonce      随机字符串
     */
    void sendCallback(String corpId, String msg, String signature, String timestamp, String nonce);

    /**
     * 校验回调地址
     * @param corpId 企业ID
     * @param callbackUrl 回调地址
     * @return true 校验成功
     */
    boolean checkCallback(String corpId, String callbackUrl);
}
