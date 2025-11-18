package com.easyink.common.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息处理类。
 *
 * @author admin
 */
public class ExceptionUtil {
    private ExceptionUtil(){}
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e) {
        return ExceptionUtils.getStackTrace(e);
    }

    public static String getRootErrorMseeage(Exception e) {
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null) {
            return "";
        }
        String msg = root.getMessage();
        if (msg == null) {
            return "null";
        }
        return StringUtils.defaultString(msg);
    }
}
