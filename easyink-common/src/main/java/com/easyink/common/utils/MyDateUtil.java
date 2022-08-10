package com.easyink.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 类名： MyDateUtil
 * 这个是获取时间的开始和结束
 *
 * @author 佚名
 * @date 2021/9/1 23:14
 */
@Slf4j
public class MyDateUtil {
    /**
     *
     * @param days  第几天 如：-1代表昨天
     * @param type  0代表一天的开始，1代表一天的结束
     * @return
     */
    public static Long strToDate(int days, Integer type) {
        Long time = null;
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.DATE, days);
        String tarday = new SimpleDateFormat("yyyy-MM-dd").format(cale.getTime());
        if (type.equals(0)) {
            tarday += " 00:00:00";
        } else {
            tarday += " 23:59:59";
        }
        // String转Date
        try {
            date = format2.parse(tarday);
            time = date.getTime() / 1000;
        } catch (ParseException e) {
            log.error("字符串转日期异常 ex:【{}】", ExceptionUtils.getStackTrace(e));
        }
        return time;
    }
}
