package com.easyink.framework.web.domain.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统文件相关信息
 *
 * @author admin
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysFile {
    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 盘符类型
     */
    private String sysTypeName;

    /**
     * 文件类型
     */
    private String typeName;

    /**
     * 总大小
     */
    private String total;

    /**
     * 剩余大小
     */
    private String free;

    /**
     * 已经使用量
     */
    private String used;

    /**
     * 资源的使用率
     */
    private double usage;


    /**
     * 文件名
     */
    private String fileName;


    /**
     * 文件前缀
     */
    private String imgUrlPrefix;


}
