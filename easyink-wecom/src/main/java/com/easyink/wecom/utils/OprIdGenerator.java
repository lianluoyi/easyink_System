package com.easyink.wecom.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 类名: oprId 生成器 调用示例：OprIdGenerator.EXPORT.get()
 *
 * @author : silver_chariot
 * @date : 2022/10/13 14:31
 **/
@Slf4j
@AllArgsConstructor
@Getter
public enum OprIdGenerator {
    /**
     * 导出操作
     */
    EXPORT("export-"),

    ;
    /**
     * traceId 前缀
     */
    private final String prefix;

    /**
     * 生成随机uuid
     *
     * @return uuid
     */
    private  String genUuid() {
        return UUID.randomUUID()
                   .toString()
                   .replaceAll("-", "");
    }

    /**
     * 获取完整的traceId , 使用示例：TraceIdGenerator.RPA.get()
     *
     * @return TraceId
     */
    public String get() {
        return getPrefix() + genUuid();
    }


}
