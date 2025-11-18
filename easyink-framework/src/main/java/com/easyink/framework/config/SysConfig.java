package com.easyink.framework.config;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : limeizi
 * @date : 2023/1/4 14:04
 */
@Data
@Configuration
@ConfigurationProperties(value = "system")
public class SysConfig {

    /**
     * 日志脱敏
     */
    private Integer selectMonthLimit;

    public static SysConfig getInstance() {
        return SpringUtil.getBean(SysConfig.class);
    }

}
