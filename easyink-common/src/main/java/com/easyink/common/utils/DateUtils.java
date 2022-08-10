package com.easyink.common.utils;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeOperationsCenterSop;
import com.easyink.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间工具类
 *
 * @author admin
 */
@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    public static final String HH_MM = "HH:mm";

    public static final String MM = "mm";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String HH_MM_SS = "HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};
    /**
     * 开始时间后缀
     */
    public static final String BEGIN_TIME_SUFFIX = " 00:00:00";
    /**
     * 结束时间后缀
     */
    public static final String END_TIME_SUFFIX = " 23:59:59";

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }


    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * 转换日期格式
     * @param dateStr 日期
     * @param oldFormat 旧格式
     * @param newFormat 新格式
     * @return newFormat
     */
    public static final String timeFormatTrans(String dateStr, String oldFormat, String newFormat) {
        if (org.apache.commons.lang3.StringUtils.isBlank(dateStr)
                || org.apache.commons.lang3.StringUtils.isBlank(oldFormat)
                || org.apache.commons.lang3.StringUtils.isBlank(newFormat)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (!isMatchFormat(dateStr, oldFormat)) {
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        Date date = dateTime(oldFormat, dateStr);
        return parseDateToStr(newFormat, date);
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }


    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = (long) 1000 * 24 * 60 * 60;
        long nh = (long) 1000 * 60 * 60;
        long nm = (long) 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 计算两个时间差
     */
    public static long diffTime(Date endDate, Date nowDate) {
        return endDate.getTime() - nowDate.getTime();
    }

    /**
     * 获取时间段内所有日期
     *
     * @param dBegin
     * @param dEnd
     * @return
     */
    public static List<Date> findDates(Date dBegin, Date dEnd) {
        List lDate = new ArrayList();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }

    public static long getMillionSceondsBydate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return sdf.parse(date).getTime();
    }


    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;//当前日期在生日之前，年龄减一
                }
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;

    }

    /**
     * 判断日期字符是否符合某个日期格式
     *
     * @param dateStr 日期字符
     * @param format  日期格式
     * @return true 是 false 否
     */
    public static Boolean isMatchFormat(String dateStr, String format) {
        // 样式和字符串长度不等则不匹配
        if (StringUtils.isBlank(dateStr)
                || StringUtils.isBlank(format)
                || format.length() != dateStr.length()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        sdf.parse(dateStr, pos);
        // pos.index==0 则解析失败
        return pos.getIndex() != 0;
    }

    /**
     * 把 传入开始时间的日期转换成 yyyy-MM-dd 00:00:00格式
     *
     * @param dayTime 传入日期字符串，正确格式为:2020-10-01 或 2021-10
     * @return 获取开始时间字符串, 格式：2020-10-01 00:00:00
     */
    public static String parseBeginDay(String dayTime) {
        // 如果格式是yyyy-MM 需要后面1号,转换成yyyy-MM-dd 格式
        if (isMatchFormat(dayTime, YYYY_MM)) {
            //每个月第一天
            String firstDayOfMonth = "-01";
            dayTime = dayTime + firstDayOfMonth;
        }
        return getTargetDate(dayTime, BEGIN_TIME_SUFFIX);
    }

    /**
     * 把 传入结束时间的日期转换成 yyyy-MM-dd 23:59:59格式
     *
     * @param dayTime 传入日期字符串，正确格式为:2020-10-01 或 2021-10
     * @return 获取截止时间字符串, 格式：2020-10-01 23:59:59
     */
    public static String parseEndDay(String dayTime) {
        // 如果是yyyy-MM 需要先获取当月最后一天并转换成 yyyy-MM-dd 格式
        if (isMatchFormat(dayTime, YYYY_MM)) {
            try {
                Date lastDayOfMonth = getLastDayOfMonth(dayTime);
                dayTime = parseDateToStr(YYYY_MM_DD, lastDayOfMonth);
            } catch (ParseException e) {
                // 如果转换格式异常则不处理
                log.warn("获取指定月份最后一天:日期格式转换有误,e:{}", ExceptionUtils.getStackTrace(e));
            }
        }
        return getTargetDate(dayTime, END_TIME_SUFFIX);
    }

    /**
     * 检查传入日期格式是否正确
     *
     * @param dayTime 传入日期字符串，正确格式为:2020-10-01
     * @return 检查结果
     */
    private static boolean checkDayTime(String dayTime) {
        boolean checkResult = true;
        if (org.apache.commons.lang3.StringUtils.isBlank(dayTime)) {
            checkResult = false;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
            try {
                simpleDateFormat.parse(dayTime);
            } catch (ParseException e) {
                checkResult = false;
                log.warn("时间格式不正确：{}", ExceptionUtil.getExceptionMessage(e));
            }
        }

        return checkResult;
    }

    /**
     * 获取传入日期的目标Date
     *
     * @param dayTime  传入日期字符串，正确格式为:2020-10-01
     * @param hourTime 传入的小时，格式为：（此处考虑mysql的datetime精度为秒，使用23:59:59足够）
     *                 com.easyink.common.utils.DateUtils#START_TIME
     *                 com.easyink.common.utils.DateUtils#END_TIME
     * @return 目标日期字符串, 格式  “2020-10-01 00:00:00” ,格式错误则返回null
     */
    private static String getTargetDate(String dayTime, String hourTime) {
        if (isMatchFormat(dayTime, YYYY_MM_DD)) {
            return dayTime + hourTime;
        }
        if (isMatchFormat(dayTime, YYYY_MM_DD_HH_MM_SS)) {
            return dayTime;
        }
        return null;
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dateStr 月份的日期字符串 格式yyyy-MM
     * @return 对应月的最后一天的日期字符串 格式
     * @throws ParseException 如传传入的日期字符串有误，会抛出格式转换异常
     */
    public static Date getLastDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM);
        Date date = sdf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 校验当前时间是否在指定的时间范围内
     *
     * @param effectTimeOpen  开始时间 HH:mm
     * @param effectTimeClose 结束时间HH:mm
     * @return Boolean
     */
    public static Boolean verifyCurrTimeEithinTimeRange(String effectTimeOpen, String effectTimeClose) {
        if (org.apache.commons.lang3.StringUtils.isBlank(effectTimeOpen) || org.apache.commons.lang3.StringUtils.isBlank(effectTimeClose)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (!isMatchFormat(effectTimeOpen, HH_MM)
                || !isMatchFormat(effectTimeClose, HH_MM)) {
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        String[] split = effectTimeOpen.split(":");
        if (split.length < 2) {
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        Integer effectTimeOpenHH = Integer.valueOf(split[0]);
        Integer effectTimeOpenMM = Integer.valueOf(split[1]);
        LocalDateTime localDateStart = LocalDateTime.now().withHour(effectTimeOpenHH).withMinute(effectTimeOpenMM);

        split = effectTimeClose.split(":");
        if (split.length < 2) {
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        Integer effectTimeCloseHH = Integer.valueOf(split[0]);
        Integer effectTimeCloseMM = Integer.valueOf(split[1]);
        LocalDateTime localDateEnd = LocalDateTime.now().withHour(effectTimeCloseHH).withMinute(effectTimeCloseMM);

        //校验开始时间和结束时间是否在合理范围（即：开始时间要小于结束时间）
        if(localDateStart.isAfter(localDateEnd)){
            throw new CustomException(ResultTip.TIP_TIME_RANGE_FORMAT_ERROR);
        }
        LocalDateTime now = LocalDateTime.now();
        if (localDateStart.isBefore(now) && localDateEnd.isAfter(now)) {
            return true;
        }
        return false;
    }
    /**
     * 把unix时间戳转换成Date
     *
     * @param unixTimestamp
     * @return {@link Date}
     */
    public static Date unix2Date(long unixTimestamp) {
        return new Date(unixTimestamp * 1000);
    }

    /**
     * 日期减几天
     */
    public static Date dateSubDay(Date date, Integer subDay) {
        Calendar rightNow = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间。
        rightNow.setTime(date);
        // 日期减
        rightNow.add(Calendar.DAY_OF_YEAR, -subDay);
        //返回一个表示此 Calendar 时间值的 Date 对象。
        return rightNow.getTime();
    }

    /**
     * 日期减几小时
     */
    public static Date dateSubHour(Date date, Integer subHour) {
        Calendar rightNow = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间。
        rightNow.setTime(date);
        // 日期减
        rightNow.add(Calendar.HOUR, -subHour);
        //返回一个表示此 Calendar 时间值的 Date 对象。
        return rightNow.getTime();
    }


    /**
     * 校验SOP时间是否符合条件
     *
     * @param nowDate    当前时间
     * @param createTime 加入sop的时间
     * @param alertType  提醒类型
     * @param alertData1 alertData1
     * @param alertData2 alertData2
     * @return boolean
     */
    public static boolean isConformTime(Date nowDate, Date createTime, Integer alertType, Integer alertData1, String alertData2) {
        LocalDateTime now = nowDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withSecond(0);
        int hour;
        int minute;
        boolean flag = false;
        LocalDateTime createLocalTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime finishTime;
        switch (WeOperationsCenterSop.AlertTypeEnum.getAlertTypeEnumByType(alertType)) {
            case TYPE_0:
                finishTime = createLocalTime.plusHours(alertData1).plusMinutes(Integer.parseInt(alertData2));
                flag = finishTime.getYear() == now.getYear() && finishTime.getMonth() == now.getMonth() && finishTime.getDayOfMonth() == now.getDayOfMonth()
                        && finishTime.getHour() == now.getHour() && finishTime.getMinute() == now.getMinute();
                break;
            case TYPE_1:{

                LocalDateTime localDateTime = createLocalTime.plusDays(alertData1 - 1);
                int alertHH = Integer.parseInt(alertData2.split(":")[0]);
                int alertMM = Integer.parseInt(alertData2.split(":")[1]);

                flag = localDateTime.getYear() == now.getYear() && localDateTime.getMonthValue() == now.getMonthValue() && localDateTime.getDayOfMonth() == now.getDayOfMonth()
                        && now.getHour() == alertHH && now.getMinute() == alertMM;
            }
            break;
            case TYPE_2:
                hour = Integer.parseInt(alertData2.split(":")[0]);
                minute = Integer.parseInt(alertData2.split(":")[1]);
                flag = now.getHour() == hour && now.getMinute() == minute;
                break;
            case TYPE_3:
                if (now.getDayOfWeek().getValue() != alertData1) {
                    break;
                }
                hour = Integer.parseInt(alertData2.split(":")[0]);
                minute = Integer.parseInt(alertData2.split(":")[1]);
                flag = now.getHour() == hour && now.getMinute() == minute;
                break;
            case TYPE_4:
                if (now.getDayOfMonth() != alertData1) {
                    break;
                }
                hour = Integer.parseInt(alertData2.split(":")[0]);
                minute = Integer.parseInt(alertData2.split(":")[1]);
                flag = now.getHour() == hour && now.getMinute() == minute;
                break;
            case TYPE_5:
                Date date = dateTime(YYYY_MM_DD_HH_MM, alertData2);
                LocalDateTime activeTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withSecond(0);
                flag = now.getYear() == activeTime.getYear() && now.getMonth() == activeTime.getMonth() && now.getDayOfMonth() == activeTime.getDayOfMonth()
                        && now.getHour() == activeTime.getHour() && now.getMinute() == activeTime.getMinute();
                break;
            case TYPE_6:
                //生日提前发送
                if (alertData1>0){
                    alertData1 = Math.negateExact(alertData1);
                }
            case TYPE_7: {
                if (alertData1 > 0){
                    alertData1 -= 1;
                }
                LocalDateTime birthTime = createLocalTime.plusDays(alertData1);
                int alertHH = Integer.parseInt(alertData2.split(":")[0]);
                int alertMM = Integer.parseInt(alertData2.split(":")[1]);

                flag =  birthTime.getMonthValue() == now.getMonthValue() && birthTime.getDayOfMonth() == now.getDayOfMonth()
                        && now.getHour() == alertHH && now.getMinute() == alertMM;
            }
            break;
        }
        return flag;
    }

    /**
     * 给时间拼上HHmm (且设置second=0)
     *
     * @param alertDate yyyy-MM-dd
     * @param hour      HH
     * @param minute    mm
     * @return Date
     */
    public static Date getSopTaskDateStart(String alertDate, Integer hour, Integer minute) {
        if (org.apache.commons.lang3.StringUtils.isBlank(alertDate) || hour == null || minute == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (!isMatchFormat(alertDate, YYYY_MM_DD)) {
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        Date date = dateTime(YYYY_MM_DD, alertDate);
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(hour).withMinute(minute).withSecond(0);
        Date start = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return start;
    }

    /**
     * 给时间拼上HHmm (且设置second=59)
     *
     * @param alertDate yyyy-MM-dd
     * @param hour      HH
     * @param minute    mm
     * @return Date
     */
    public static Date getSopTaskDateEnd(String alertDate,Integer  hour,Integer minute){
        if(org.apache.commons.lang3.StringUtils.isBlank(alertDate)||hour==null||minute==null){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if(!isMatchFormat(alertDate,YYYY_MM_DD)){
            throw new CustomException(ResultTip.TIP_TIME_FORMAT_ERROR);
        }
        Date date = dateTime(YYYY_MM_DD,alertDate);
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withHour(hour).withMinute(minute).withSecond(59);
        Date end = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return end;
    }
}
