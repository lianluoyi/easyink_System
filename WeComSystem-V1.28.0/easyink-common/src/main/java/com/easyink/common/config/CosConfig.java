package com.easyink.common.config;

import com.easyink.common.constant.WeConstans;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
@Data
public class CosConfig {

    private String secretId;

    private String secretKey;

    private String region;

    private String bucketName;

    private String cosImgUrlPrefix;

    public String getCosImgUrlPrefix() {
        //判断是否以“/”结尾
        if(!cosImgUrlPrefix.endsWith(WeConstans.SLASH)){
            cosImgUrlPrefix += WeConstans.SLASH;
        }
        return cosImgUrlPrefix;
    }
}
