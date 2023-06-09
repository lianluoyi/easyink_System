package com.easyink.wecom.domain.vo;

import lombok.Data;

/**
 * @author 佚名
 * @ClassName WeCorpInfoVO
 * @date 2021/8/9 18:49
 * @Version 1.0
 */
@Data
public class WeCorpInfoVO {
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 应用id
     */
    private String agentId;
    /**
     * token
     */
    private String token;
}
