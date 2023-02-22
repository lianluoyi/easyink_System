package com.easyink.common.service;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 自定义二维码处理类
 *
 * @author wx
 * 2023/1/16 11:36
 **/
@Component
public class QRCodeHandler {
    @Resource
    private QrConfig qrConfig;

    /**
     * 生成到本地文件
     *
     * @param content   二维码绑定的内容
     * @param file      file
     */
    public void generateFile(String content, File file){
        QrCodeUtil.generate(content, qrConfig, file);
    }

    /**
     * 输出到流
     *
     * @param content       二维码绑定的内容
     * @param response      响应
     * @throws IOException
     */
    public void generateStream(String content, HttpServletResponse response) throws IOException {
        QrCodeUtil.generate(content, qrConfig,"png",response.getOutputStream());
    }

    public String generateBase64(String content) {
        return QrCodeUtil.generateAsBase64(content, qrConfig,"png");
    }

}
