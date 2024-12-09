package com.easyink.wecom.openapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.easyink.wecom.openapi.req.GetExternalUserIdReq;
import com.easyink.wecom.openapi.req.GetUserIdReq;
import com.easyink.wecom.openapi.res.DecryptExternalUserIdSelfBuildRes;
import com.easyink.wecom.openapi.res.DecryptUserIdSelfBuildRes;
import com.easyink.wecom.openapi.service.LockSelfBuildApiService;
import com.easyink.wecom.openapi.util.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * lock自建应用配置
 * @author tigger
 * 2024/11/19 16:22
 **/
@Slf4j
@Service
public class LockSelfBuildApiServiceImpl implements LockSelfBuildApiService {

    @Override
    public String decryptExternalUserId(String externalUserid, String agentId, String selfBuildUrl) {

        String reqStr = JSONObject.toJSONString(new GetExternalUserIdReq(externalUserid, Integer.valueOf(agentId)));
        String resStr = OkHttpClientUtil.doPost(selfBuildUrl, reqStr.getBytes(StandardCharsets.UTF_8));
        if(resStr == null){
            log.info("[解密external_userid] 响应为空,  req:{}, selfBuildUrl: {}, externalUserid: {}, agentId: {}", reqStr, selfBuildUrl, externalUserid, agentId);
            return null;
        }
        DecryptExternalUserIdSelfBuildRes res = JSONObject.parseObject(resStr, DecryptExternalUserIdSelfBuildRes.class);
        if(res == null || !res.isSuccess()){
            log.info("[解密external_userid] 失败,  res:{}, externalUserid: {}, agentId: {}", resStr, externalUserid, agentId);
            return null;
        }
        return res.getData();
    }

    @Override
    public DecryptUserIdSelfBuildRes.GetUserIdResData decryptUserId(List<String> useridList, String agentId, String url) {


        String reqStr = JSONObject.toJSONString(new GetUserIdReq(useridList, Integer.valueOf(agentId)));
        String resStr = OkHttpClientUtil.doPost(url, reqStr.getBytes(StandardCharsets.UTF_8));
        if(resStr == null){

            log.info("[解密Userid] 响应为空,  req:{}, url: {}, useridList: {}, agentId: {}", reqStr, url, useridList, agentId);
            return null;
        }
        DecryptUserIdSelfBuildRes res = JSONObject.parseObject(resStr, DecryptUserIdSelfBuildRes.class);
        if(res == null || !res.isSuccess()){
            log.info("[解密Userid] 失败,  res:{}, useridList: {}, agentId: {}", resStr, useridList, agentId);
            return null;
        }
        return res.getData();
    }
}
