package com.easyink.framework.config;

import cn.hutool.extra.qrcode.QrConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.awt.*;

/**
 * 生成二维码配置类
 *
 * @author wx
 * 2023/1/16 11:27
 **/
@Configuration
public class QRCodeConfig {
    @Bean
    public QrConfig qrConfig(){
        QrConfig qrConfig = new QrConfig();
        qrConfig.setBackColor(Color.white.getRGB());
        qrConfig.setForeColor(Color.black.getRGB());
        return qrConfig;
    }
}
