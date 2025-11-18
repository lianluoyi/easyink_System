package com.easyink.common.encrypt;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类名：PatternRuleUtil
 *
 * @author lixiaolin
 * @date 2022-11-16 18:36
 */
public class PatternRuleUtil {


    private static final int ID_SIZE = 14;
    private static final int CUT_SIZE = 6;
    private static final int CUT_SIZE_LICENSE = 3;
    private static final int PHONE_RIGHT_SIZE = 4;
    private static final int PHONE_LEFT_SIZE = 3;
    private static final int PHONE_CENTER_SIZE = 8;


    private static final int USER_RIGHT_SIZE = 4;
    private static final int USER_LEFT_SIZE = 4;
    private static final int USER_CENTER_SIZE = 12;

    /**
     * 邮箱后面保留明文位数
     */
    private static final int MAIL_RIGHT_SIZE = 4;
    /**
     * 邮箱前面保留明文位数
     */
    private static final int MAIL_LEFT_SIZE = 4;

    /**
     * 银行卡后面保留明文位数
     */
    private static final int BANK_CARD_RIGHT_SIZE = 4;
    /**
     * 银行卡前面保留明文位数
     */
    private static final int BANK_CARD_LEFT_SIZE = 4;

    private static final char ASTERISK = '*';

    private static final String ASTERISK_STR = "*";

    /**
     * 日志脱敏关键字电话
     */
    private static final Pattern phoneReg = Pattern.compile("1(3[0-9]|4[01456879]|5[0-3,5-9]|6[2567]|7[0-8]|8[0-9]|9[0-3,5-9])\\d{8}");
    /**
     * 日志脱敏关键字车牌
     */
    private static final Pattern licensePlateReg = Pattern.compile("[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]{1}[A-Z]{1}[-]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}");
    /**
     * 日志脱敏关键字证件
     */
    private static final Pattern certificateReg = Pattern.compile("([1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx])|([1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2})");
    /**
     * 16为数值的企微号或好友userId
     */
    private static final Pattern userIdPeg = Pattern.compile("(168|788)\\d{13}");
    /**
     * 匹配托管账号uin和微信好友userName以及R:开头的群聊id, 目前最长看到17位数字
     */
    private static final Pattern keywordReg = Pattern.compile("(168|788)\\d{13}|R:\\d{0,17}");
    /**
     * 匹配邮箱
     */
    private static final Pattern mailReg = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+");
    /**
     * 匹配银行卡
     * 16位：Visa、Mastercard、Discover等品牌的信用卡通常是16位数字。
     * 17位：某些国际发行的银行卡可能具有17位的长度。
     * 19位：中国部分银行的借记卡（储蓄卡）可能具有19位的长度
     */
    private static final Pattern bankCardReg = Pattern.compile("([1-9])(\\d{18}|\\d{16}|\\d{15})");

    /**
     * 日志脱敏关键字地址
     */
    private static final Pattern provinceReg = Pattern.compile("(?:北京市|天津市|上海市|重庆市|河北省|山西省|辽宁省|吉林省|黑龙江省|江苏省|浙江省|安徽省|福建省|江西省|山东省|河南省|湖北省|湖南省|广东省|海南省|四川省|贵州省|云南省|陕西省|甘肃省|青海省|台湾省|内蒙古自治区|广西壮族自治区|宁夏回族自治区|新疆维吾尔自治区|香港特别行政区|澳门特别行政区|海南自由贸易港|[\\u4e00-\\u9fa5]{2}市|[\\u4e00-\\u9fa5]{2}区)");
    /**
     * 详细地址脱敏
     */
    private static final Pattern addressDetailReg = Pattern.compile("1(3[0-9]|4[01456879]|5[0-3,5-9]|6[2567]|7[0-8]|8[0-9]|9[0-3,5-9])\\d{8}|(\\d{1,5}[-－—]?\\d{0,4}[号]?室?|[一二三四五六七八九十\\d]{1,3}[层楼](?!小区|大厦)|[A-Za-z][-－—]?\\d{1,5}[号楼栋幢座室]?|[A-Za-z]?\\d{1,4}室?\\b)");



    public static String desensitizationMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return message;
        }
        List<String> keywords = extractKeyWords(message);
        StringBuilder sb = new StringBuilder(message);
        //处理userId、roomId
//        patternUserId(sb);
        //处理车牌
        licensePlate(sb);
        // 处理电话字符串
        mobilePhone(sb);
        //处理邮箱
        mailReg(sb);
        //处理银行卡
        bankCard(sb);
        //处理地址
        address(sb);
        //处理证件
        message = sb.toString();
        message = idCardNum(message);
        message = StringUtils.substring(message, 0, 2000);
        return message.concat("=====>").concat(keywords.toString());
    }


    /**
     * [身份证号] 前4后4明文，中间脱敏
     *
     * @param idCardNum 身份证号
     * @return 身份证号
     */
    public static String genMarkIdNum(String idCardNum) {
        if (StringUtils.isBlank(idCardNum)) {
            return StringUtils.EMPTY;
        }
        String rightNum = StringUtils.right(idCardNum, CUT_SIZE);
        rightNum = StringUtils.leftPad(rightNum, ID_SIZE, ASTERISK);

        return StringUtils.left(idCardNum, CUT_SIZE) + rightNum;
    }

    public static String idCardNum(String message) {
        if (StringUtils.isBlank(message)) {
            return StringUtils.EMPTY;
        }
        Matcher matcher = certificateReg.matcher(message);
        while (matcher.find()) {
            message = message.replace(matcher.group(0), genMarkIdNum(matcher.group(0)));
        }
        return message;
    }

    /**
     * 前3后2明文，中间脱敏
     *
     * @return 电话号码
     */
    public static boolean mobilePhone(StringBuilder sb) {
        if (StringUtils.isBlank(sb)) {
            return false;
        }
        Matcher matcher = phoneReg.matcher(sb);
        boolean match = false;
        while (matcher.find()) {
            match = true;
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start + PHONE_LEFT_SIZE; i < end - PHONE_RIGHT_SIZE; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
        return match;
    }

    /**
     * 匹配邮箱前四后四
     * @param sb
     */
    public static void mailReg(StringBuilder sb){
        if (StringUtils.isBlank(sb)) {
            return;
        }
        Matcher matcher = mailReg.matcher(sb);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start + MAIL_LEFT_SIZE; i < end - MAIL_RIGHT_SIZE; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
    }


    /**
     * 匹配邮箱前四后四
     * @param sb
     */
    public static void bankCard(StringBuilder sb) {
        if (StringUtils.isBlank(sb)) {
            return;
        }
        Matcher matcher = bankCardReg.matcher(sb);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start + BANK_CARD_LEFT_SIZE; i < end - BANK_CARD_RIGHT_SIZE; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
    }

    public static void address(StringBuilder sb) {
        if (StringUtils.isBlank(sb)) {
            return;
        }
        Matcher matcher = provinceReg.matcher(sb);
        //对省份全脱敏
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start; i < end; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
    }

    public static void addressExt(StringBuilder sb) {
        if (StringUtils.isBlank(sb)) {
            return;
        }
        Matcher matcher = addressDetailReg.matcher(sb);
        //对省份全脱敏
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start; i < end; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
    }


    /**
     * UserId匹配 前3后2明文
     *
     * @param sb
     */
    public static void patternUserId(StringBuilder sb) {
        if (StringUtils.isBlank(sb)) {
            return;
        }
        Matcher matcher = userIdPeg.matcher(sb);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start + USER_LEFT_SIZE; i < end - USER_RIGHT_SIZE; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
    }


    /**
     * 处理车牌
     * 后4位脱敏
     *
     * @param sb
     */
    public static void licensePlate(StringBuilder sb) {
        if (StringUtils.isBlank(sb)) {
            return;
        }
        Matcher matcher = licensePlateReg.matcher(sb);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            for (int i = start + CUT_SIZE_LICENSE; i < end; i++) {
                sb.setCharAt(i, ASTERISK);
            }
        }
    }

    /**
     * 匹配url中的filename
     *
     * @param url url
     * @return filename
     */
    public static String getFilename(String url) {
        Pattern pattern = Pattern.compile("^http.*download.*[fname,filename]=");
        Matcher matcher = pattern.matcher(url);
        String s = "";
        if (matcher.find()) {
            s = matcher.replaceFirst("");
        }
        return s;
    }

    /**
     * 获取匹配到的关键词列表
     * @param formattedMessage
     * @return
     */
    public static List<String> extractKeyWords(String formattedMessage) {
        return matchUserIdOrRoomId(formattedMessage);
    }

    private static List<String> matchUserIdOrRoomId(String formattedMessage) {
        if (StringUtils.isBlank(formattedMessage)) {
            return new ArrayList<>();
        }
        List<String> matchs = new ArrayList<>();
        Matcher matcher = keywordReg.matcher(formattedMessage);
        while (matcher.find()) {
            matchs.add(matcher.group());
        }
        return matchs;
    }
}
