package com.easyink.wecom.domain.vo;

import lombok.Data;

/**
 * 类名：WeEmplyCodeDownloadVO 员工活码下载所需的数据
 *
 * @author Society my sister Li
 * @date 2021-11-09 15:51
 */
@Data
public class WeEmplyCodeDownloadVO {

    /**
     * 员工活码ID
     */
    private Long id;

    /**
     * 活码链接
     */
    private String qrCode;

    /**
     * 活动场景
     */
    private String scenario;
}
