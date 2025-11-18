package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeCorpThirdPartyConfig;
import com.easyink.wecom.domain.dto.form.push.WeCorpThirdPartyConfigDTO;
import com.easyink.wecom.mapper.WeCorpThirdPartyConfigMapper;
import com.easyink.wecom.service.WeCorpThirdPartyConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 企业第三方服务推送配置Service业务层处理
 *
 * @author easyink
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeCorpThirdPartyConfigServiceImpl extends ServiceImpl<WeCorpThirdPartyConfigMapper, WeCorpThirdPartyConfig> implements WeCorpThirdPartyConfigService {


    /**
     * URL连接超时时间（毫秒）
     */
    private static final int CONNECTION_TIMEOUT = 5000;

    /**
     * URL读取超时时间（毫秒）
     */
    private static final int READ_TIMEOUT = 10000;


    @Override
    public WeCorpThirdPartyConfig getByCorpId(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        return baseMapper.selectByCorpId(corpId);
    }

    @Override
    public boolean saveOrUpdateConfig(WeCorpThirdPartyConfigDTO dto, LoginUser loginUser) {
        WeCorpThirdPartyConfig config = new WeCorpThirdPartyConfig();
        BeanUtils.copyProperties(dto, config);
        // 校验地址是否可访问, 不为空才校验
        if (StringUtils.isNotBlank(dto.getPushUrl())) {
            boolean accessibility = validateUrlAccessibility(dto.getPushUrl());
            if (!accessibility) {
                throw new CustomException(ResultTip.FORM_PUSH_URL_REQ_FAILED);
            }
        }

        config.setCreateBy(loginUser.getUserId());
        config.setUpdateBy(loginUser.getUserId());
        return this.saveOrUpdate(config);
    }

    /**
     * 验证URL是否可访问
     *
     * @param urlString 要验证的URL字符串
     * @return true表示URL可访问，false表示不可访问
     */
    private boolean validateUrlAccessibility(String urlString) {
        if (StringUtils.isBlank(urlString)) {
            log.warn("URL为空，无法进行可访问性验证");
            return false;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为HEAD，减少网络传输
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            // 设置常见的HTTP头信息
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setRequestProperty("Accept", "*/*");

            // 获取响应码
            int responseCode = connection.getResponseCode();
            connection.disconnect();

            // 只要能获取到响应码就认为服务器可达，不关心具体的状态码
            boolean isAccessible = responseCode > 0;

            if (isAccessible) {
                log.info("URL验证成功，服务器可达: {} - 响应码: {}", urlString, responseCode);
            } else {
                log.warn("URL验证失败，无法获取响应码: {}", urlString);
            }

            return isAccessible;

        } catch (IOException e) {
            log.error("URL连接异常: {}", urlString, e);
            return false;
        } catch (Exception e) {
            log.error("URL验证过程中发生未知异常: {}", urlString, e);
            return false;
        }
    }


    @Override
    public boolean deleteConfig(List<String> ids, String corpId) {
        LambdaQueryWrapper<WeCorpThirdPartyConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WeCorpThirdPartyConfig::getCorpId, ids);
        return this.remove(wrapper);
    }

    @Override
    public boolean updateStatus(String corpId, Integer status) {
        return baseMapper.updateStatusByCorpId(corpId, status) > 0;
    }
}
