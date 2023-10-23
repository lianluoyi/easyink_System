package com.easyink.wecom.domain.vo.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: 导出操作VO
 *
 * @author : silver_chariot
 * @date : 2023/9/20 9:33
 **/
@Data
@Builder
@AllArgsConstructor
public class ExportOprVO {
    /**
     * 操作id
     */
    private String oprId;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 是否已完成
     */
    private Boolean hasFinished;
}
