package com.easywecom.common.utils;

import cn.hutool.core.lang.hash.MurmurHash;

/**
 * 类名: 短链转换工具
 *
 * @author : silver_chariot
 * @date : 2022/7/18 18:10
 **/

public class ConvertUrlUtil {

    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static final int SIZE = CHARS.length;
    /**
     * 短链code长度
     */
    private static final int CODE_SIZE = 6;
    /**
     * 占位符
     */
    private static final char SUB = '0';

    /**
     * 转62进制
     *
     * @param num 数字
     * @return 62进制字符
     */
    private static String convert2Base62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int i = (int) (num % SIZE);
            sb.append(CHARS[i]);
            num /= SIZE;
        }
        return sb.reverse().toString();
    }

    /**
     * 字符串 转成 base62 （先通过murhash 再转 62进制）
     *
     * @param str 字符串
     * @return base62进制字符
     */
    public static String url2hash2Base62(String str) {
        int i = MurmurHash.hash32(str);
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
        return convert2Base62(num);
    }

    /**
     * 根据长链获取短链code
     *
     * @param str 长链
     * @return 短链code
     */
    public static String getShortCode(String str) {
        String code = url2hash2Base62(str);
        return StringUtils.leftPad(code, CODE_SIZE, SUB);

    }

}


