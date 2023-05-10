package com.easyink.common.token;

import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.utils.ServletUtils;
import com.easyink.common.utils.ip.AddressUtils;
import com.easyink.common.utils.ip.IpUtils;
import com.easyink.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 *
 * @author admin
 */
@Component
@Slf4j
public class TokenService {
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认24小时）
    @Value("${token.expireTime}")
    @Getter
    private int expireTime;

    @Autowired
    private RuoYiConfig ruoYiConfig;

    protected static final long MILLIS_SECOND = 1000;

    public static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 获取用户身份信息 (http请求）
     *
     * @return 登录用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取REDIS缓存登录用户的键
        String userKey = getUserKey(request);
        return this.getLoginUserByUserKey(userKey);
    }

    /**
     * 获取用户身份信息 (userKey）
     *
     * @return 登录用户信息
     */
    public LoginUser getLoginUserByUserKey(String userKey) {
        if (StringUtils.isBlank(userKey)) {
            return null;
        }
        return redisCache.getCacheObject(userKey);
    }


    /**
     * 生成保存登录用户信息的redisKEY
     *
     * @return 用户登录信息的redis键
     */
    public String getUserKey(HttpServletRequest request) {
        // 获取TOKEN
        String tokenId = getTokenId(request);
        if (StringUtils.isNotBlank(tokenId)) {
            return getUserKey(tokenId);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取用户tokenId
     *
     * @param request
     * @return tokenId
     */
    public String getTokenId(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            // 从令牌中获取声明
            Claims claims = parseToken(token);
            // 从声明中获取登录的uuid
            return (String) claims.get(Constants.LOGIN_USER_KEY);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (null != loginUser && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getUserKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTimes = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTimes - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 刷新部门权限
        try {
            sysPermissionService.setRoleAndDepartmentDataScope(loginUser);
        } catch (Exception e) {
            log.info("【登录】:设置可见部门权限异常,loginUser:{},e:{}", loginUser, ExceptionUtils.getStackTrace(e));
        }
        // 根据uuid将loginUser缓存
        String userKey = getUserKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 刷新令牌weUser
     *
     * @param loginUser 登录信息
     */
    public void refreshWeUser(LoginUser loginUser){
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        String userKey = getUserKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }



    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        long nowTime = System.currentTimeMillis();
        long expTime = 360 * 24 * 3600 * 1000L;
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(nowTime + expTime))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = "";
        //移除匿名接口中请求头的token
        if (!PatternMatchUtils.simpleMatch(ruoYiConfig.getAnonUrl(), request.getRequestURI())) {
            token = request.getHeader(header);
            if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
                token = token.replace(Constants.TOKEN_PREFIX, "");
            }
        }


        return token;
    }

    /**
     * 根据token获取缓存登录用户的redis key
     *
     * @param token 登录token
     * @return key
     */
    public String getUserKey(String token) {
        return Constants.LOGIN_TOKEN_KEY + token;
    }


}
