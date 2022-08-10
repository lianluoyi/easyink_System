package com.easyink.common.utils;


/**
 * 操作系统相关判断
 */
public class OsUtils {

    public static boolean isWindows() {
        String osName = System.getProperties().getProperty("os.name");
        return osName.toUpperCase().indexOf("WINDOWS") != -1;
    }
}
