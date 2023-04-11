package com.easyink.common.shorturl.service.impl;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.mapper.SysShortUrlMappingMapper;
import com.easyink.common.shorturl.model.SysShortUrlMapping;
import com.easyink.common.shorturl.service.ShortUrlService;
import com.easyink.common.utils.ConvertUrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 类名: 短链处理接口实现类 （不包含具体业务）
 *
 * @author : silver_chariot
 * @date : 2022/7/18 16:43
 **/
@Slf4j
@Service("shortUrlService")
public class ShortUrlServiceImpl implements ShortUrlService {
    @Autowired
    private  SysShortUrlMappingMapper sysShortUrlMappingMapper;
    @Autowired
    private  RedisCache redisCache;
    /**
     * 布隆过滤器
     */
    private static final BitMapBloomFilter BLOOM_FILTER = BloomFilterUtil.createBitMap(16);
    /**
     * 存储短链长链的redis映射
     */
    private static final String SHORT_URL_REDIS_KEY = "shortUrl:";
    /**
     * 如果已有重复的短链code,给长链接拼接额外字符再生成code所需要增加的字符串
     */
    private static final String DUPLICATE_SUB = "*";
    /**
     * 缓存存储时间 单位分钟
     */
    private static final int REDIS_TIME_OUT = 2;


    @Override
    public String createShortCode(String longUrl, Integer type, String createBy, String appendInfoStr) {
        if (StringUtils.isBlank(longUrl)) {
            log.info("[创建短链]长连接为空,创建失败 ");
            return null;
        }
        String code = null;
        // 用于生成短链的长链url,由于生成的短链可能重复,如果重复则需要添加占位符后重新生成
        String genLongUrl = BLOOM_FILTER.contains(longUrl) ? longUrl + DUPLICATE_SUB : longUrl;
        boolean success = false;
        do {
            try {
                // 生成短链的code
                code = ConvertUrlUtil.getShortCode(genLongUrl);
                // 保存映射
                SysShortUrlMapping mapping = SysShortUrlMapping.builder().appendInfo(appendInfoStr).type(type).shortCode(code).longUrl(longUrl).createBy(createBy).createTime(new Date()).build();
                success = sysShortUrlMappingMapper.insert(mapping) > 0;
                // 增加至布隆过滤器
                BLOOM_FILTER.add(code);
                log.info("[创建短链]创建成功,longUrl:{},shortCode:{},createBy:{}", longUrl, code, createBy);
            } catch (DuplicateKeyException e) {
                log.error("[创建短链]生成的code,db中已有重复的,重新生成,url:{},code:{}", longUrl, code);
                genLongUrl = genLongUrl + DUPLICATE_SUB;
            } catch (Exception e) {
                log.error("[创建短链]生成短链异常,url:{},e:{}", longUrl, ExceptionUtils.getStackTrace(e));
                throw new CustomException(ResultTip.TIP_ERROR_CREATE_SHORT_URL);
            }
        } while (!success);
        return code;
    }

    public static void main(String[] args) {
        String url = "http://lianluoyi.net?id=1314515&type=23&channel=1&userId=1414151faafa&faf=raewtatoraewt&ids=1414151515135515";
        String code = ConvertUrlUtil.getShortCode(url);
        System.out.println(code);
    }

    @Override
    public SysShortUrlMapping getUrlByMapping(String shortCode) {
        if (StringUtils.isBlank(shortCode)) {
            log.info("[获取长链]短链接code为空,获取失败");
            throw new CustomException(ResultTip.TIP_NO_SHORT_CODE);
        }
        // 先从缓存获取
        String redisKey = getRedisKey(shortCode);
        String cacheMappingStr = redisCache.getCacheObject(redisKey);
        if (StringUtils.isNotBlank(cacheMappingStr)) {
            SysShortUrlMapping cacheMapping = JSON.parseObject(cacheMappingStr, SysShortUrlMapping.class);
            redisCache.expire(redisKey, REDIS_TIME_OUT, TimeUnit.MINUTES);
            log.info("[获取长链]缓存中获取成功code:{},mapping:{}", shortCode, cacheMapping);
            return cacheMapping;
        }
        // 查询db中的映射
        SysShortUrlMapping mapping = sysShortUrlMappingMapper.selectOne(new LambdaQueryWrapper<SysShortUrlMapping>().eq(SysShortUrlMapping::getShortCode, shortCode).last(GenConstants.LIMIT_1));
        if (mapping == null || StringUtils.isBlank(mapping.getLongUrl())) {
            throw new CustomException(ResultTip.TIP_CANNOT_FIND_PAGE);
        }
        // 缓存并返回
        redisCache.setCacheObject(redisKey, JSON.toJSONString(mapping), REDIS_TIME_OUT, TimeUnit.MINUTES);
        log.info("[获取长链]获取成功code:{},mapping:{}", shortCode, mapping);
        return mapping;
    }


    /**
     * 生成完整的短链
     *
     * @param domain 域名
     * @param code   短链后缀的字符串
     * @return 完整的短链
     */
    public String genShortUrl(String domain, String code) {
        return domain + WeConstans.SLASH + code;
    }


    /**
     * redis获取
     *
     * @param shortCode 短链code
     * @return 获取长链的 redisKey
     */
    public String getRedisKey(String shortCode) {
        return SHORT_URL_REDIS_KEY + shortCode;
    }
}
