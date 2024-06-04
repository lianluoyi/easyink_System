package com.easyink.wecom.openapi.service.impl;


import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.openapi.OpenApiException;
import com.easyink.wecom.openapi.dao.AppIdInfoMapper;
import com.easyink.wecom.openapi.domain.entity.AppIdInfo;
import com.easyink.wecom.openapi.domain.vo.AppIdGenVO;
import com.easyink.wecom.openapi.service.AppIdInfoService;
import com.easyink.wecom.openapi.util.AppGenUtil;
import com.easyink.wecom.openapi.util.AppIdCache;
import com.easyink.wecom.openapi.util.AppInfoRedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 类名: 开发参数生成接口实现类
 *
 * @author : silver_chariot
 * @date : 2022/3/14 13:37
 */
@Service("appIdInfoService")
@Slf4j
public class AppIdInfoServiceImpl implements AppIdInfoService {

    private final AppIdInfoMapper appIdInfoMapper;
    private final AppInfoRedisClient appInfoRedisClient;
    private final AppIdCache appIdCache;

    public AppIdInfoServiceImpl(@NotNull AppIdInfoMapper appIdInfoMapper, AppInfoRedisClient appInfoRedisClient, AppIdCache appIdCache) {
        this.appIdInfoMapper = appIdInfoMapper;
        this.appInfoRedisClient = appInfoRedisClient;
        this.appIdCache = appIdCache;
    }


    @Override
    public AppIdInfo get(String corpId) {
        if (corpId == null) {
            throw new CustomException("获取账号信息失败");
        }
        return appIdInfoMapper.get(corpId);
    }

    @Override
    public AppIdGenVO getVO(String corpId) {
        AppIdInfo appIdInfo = get(corpId);
        if (appIdInfo == null) {
            return new AppIdGenVO();
        }
        return new AppIdGenVO(appIdInfo);
    }

    @Override
    public AppIdGenVO create(String corpId) {
        // 查询是否已存在配置
        AppIdInfo appIdInfo = this.get(corpId);
        if (appIdInfo != null) {
            throw new CustomException("已存在开发配置,请勿重复申请获取");
        }
        // 初始化appId 和 appSecret
        String appId = AppGenUtil.getAppId();
        String appSecret = AppGenUtil.getAppSecret(appId);
        appIdInfo = AppIdInfo.builder()
                .corpId(corpId)
                .appId(appId)
                .appSecret(appSecret)
                .createTime(new Date())
                .build();
        int res = appIdInfoMapper.insert(appIdInfo);
        if (res == 0) {
            throw new CustomException("获取开发配置异常");
        }
        appIdCache.put(appId, appIdInfo);
        return new AppIdGenVO(appIdInfo);
    }

    @Override
    public AppIdGenVO refreshSecret(String corpId) {
        // 获取当前配置
        AppIdInfo appIdInfo = this.get(corpId);
        if (appIdInfo == null) {
            throw new CustomException("开发参数未初始化,请先获取");
        }
        String appId = appIdInfo.getAppId();
        // 重置、更新秘钥 (需保障appId不被修改,只修改appSecret)
        appIdInfo = AppIdInfo.builder()
                .corpId(corpId)
                .appSecret(AppGenUtil.refreshSecret())
                .updateTime(new Date())
                .build();
        int res = appIdInfoMapper.update(appIdInfo);
        if (res == 0) {
            throw new CustomException("重置失败");
        }
        appIdCache.put(appId, appIdInfo);
        return new AppIdGenVO(appIdInfo);
    }

    @Override
    public String getTicket(String appId, String appSecret) {
        if (StringUtils.isAnyBlank(appId, appSecret)) {
            throw new OpenApiException("param missing");
        }
        // 判断appId是否存在
        AppIdInfo appIdInfo = appIdCache.get(appId);
        if (appIdInfo == null) {
            throw new OpenApiException("invalid appId");
        }
        // 判断appSecret是否正确
        if (!appSecret.equals(appIdInfo.getAppSecret())) {
            throw new OpenApiException("invalid secret");
        }
        // 生成票据
        String ticket;
        try {
            ticket = AppGenUtil.genTicket(appId);
        } catch (Exception e) {
            throw new OpenApiException("create ticket error");
        }
        appInfoRedisClient.setTicket(appId, ticket);
        return ticket;
    }

}
