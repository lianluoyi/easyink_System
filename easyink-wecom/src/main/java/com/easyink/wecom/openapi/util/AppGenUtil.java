package com.easyink.wecom.openapi.util;

import com.easyink.common.exception.openapi.AppGenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static cn.hutool.core.util.RandomUtil.randomInt;

/**
 * 类名: app_id和app_secret生成工具类
 *
 * @author : silver_chariot
 * @date : 2022/3/14 10:33
 */
@Component
@Slf4j
public class AppGenUtil {
    private AppGenUtil() {
    }

    /**
     * 票据key
     */
    private static final String TICKET_KEY = "ticket_key";
    /**
     * APP_ID key
     */
    private static final String APP_ID_KEY = "app_id_key";
    /**
     * JWT加密秘钥
     */
    private static final String JWT_SECRET = "Kgggoujo1151ihihg1o5h11";
    /**
     * 票据过期时间 默认7200s
     */
    private static final long TICKET_EX_TIME = 2 * 60 * 60 * 1000L;

    /**
     * 生成app_id 和app_secret 所需的参数
     */
    private static final String SERVER_NAME = "wechat_service_plus";
    private static final String[] CHARS = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    public static final int APP_ID_LENGTH = 8;
    public static final String RANDOM_CHAR_STR = "abcdefghijklmnopqrstuvwxyz";


    /**
     * 生成10位的app_id
     * <p>
     * 短8位UUID思想其实借鉴微博短域名的生成方式，但是其重复概率过高，而且每次生成4个，需要随即选取一个。
     * 本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，所以将UUID分成8组，每4个为一组，然后通过模62操作，结果作为索引取出字符，
     * </p>
     *
     * @return app_id
     */
    public static String getAppId() {
        StringBuilder stringBuilder = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 随机生成app
        for (int i = 0; i < APP_ID_LENGTH; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            stringBuilder.append(CHARS[x % 0x3E]);
        }
        // 生成2位前缀
        StringBuilder prefix = new StringBuilder()
                .append(randomChar())
                .append(randomChar());
        return prefix.append(stringBuilder).toString();
    }

    /**
     * 生成随机字符串
     *
     * @return 随机字符串
     */
    public static String randomChar() {
        return String.valueOf(RANDOM_CHAR_STR.charAt(randomInt(RANDOM_CHAR_STR.length())));
    }

    /**
     * 通过app_id 和 serverName 生成appSecret
     *
     * @param appId app_id
     * @return appSecret秘钥
     */
    public static String getAppSecret(String appId) {
        try {
            String[] array = new String[]{appId, SERVER_NAME};
            StringBuilder sb = new StringBuilder();
            // 字符串排序
            Arrays.sort(array);
            for (String s : array) {
                sb.append(s);
            }
            String str = sb.toString();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexStr = new StringBuilder();
            String shaHex;
            for (byte b : digest) {
                shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexStr.append(0);
                }
                hexStr.append(shaHex);
            }
            return hexStr.toString();
        } catch (NoSuchAlgorithmException e) {
            log.info("[开发参数生成]app_secret生成,不存在算法异常,appId:{},e:{}", appId, ExceptionUtils.getStackTrace(e));
            throw new AppGenException("不存在该解密算法");
        } catch (Exception e) {
            log.info("[开发参数生成]app_secret生成异常,appId:{},e:{}", appId, ExceptionUtils.getStackTrace(e));
            throw new AppGenException(e.getMessage());
        }
    }

    /**
     * 获取重置的app_secret
     *
     * @return 重置后的app 秘钥
     */
    public static String refreshSecret() {
        return getAppSecret(getAppId());
    }

    /**
     * 生成ticket
     *
     * @return ticket 用于返回给调用者的票据
     */
    public static String genTicket(String appId) {
        String token = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>(16);
        claims.put(TICKET_KEY, token);
        claims.put(APP_ID_KEY, appId);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TICKET_EX_TIME))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
    }

    /**
     * 根据ticket 获取appId
     *
     * @param ticket 票据
     * @return appId
     */
    public static String getAppIdByTicket(String ticket) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(ticket)
                .getBody();
        return (String) claims.get(APP_ID_KEY);
    }


}
