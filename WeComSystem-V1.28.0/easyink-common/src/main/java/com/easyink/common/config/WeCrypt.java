package com.easyink.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: WeCrypt
 *
 * @author: 1*+
 * @date: 2021-12-24 14:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeCrypt implements Serializable {

    private String token;
    private String encodingAesKey;

    /**
     * 获取默认服务商配置
     *
     * @return {@link WeCrypt}
     */
    public static WeCrypt getDefaultProviderWeCrypt() {
        return new WeCrypt("rz0oBayoJBLM2AHlSHmYjAqgDLe9", "kzOip1U1zjfei8ZYtCDBczQoVjBrXjSOep65jvmYtCq");
    }

    /**
     * 获取默认自建应用配置
     *
     * @return {@link WeCrypt}
     */
    public static WeCrypt getDefaultSelfBuildWeCrypt() {
        return new WeCrypt("JBd03DAvevMr2DxWlYUrr", "K2Xh8oYwOEG86Ee1Cg3s680VVYjYsDhRvnE3wtqw54b");
    }

}
