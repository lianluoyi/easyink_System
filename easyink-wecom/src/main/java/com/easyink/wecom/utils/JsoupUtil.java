package com.easyink.wecom.utils;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.vo.WeWordsUrlVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类名： JsoupUtil
 *
 * @author 佚名
 * @date 2022/2/15 14:32
 */

@Slf4j
public class JsoupUtil {

    private static final Pattern URL_PATTERN = Pattern.compile("[a-zA-z]+://[^\\s]*");
    /**
     * 链接内的内容
     *
     * @param address 链接
     * @return {@link WeWordsUrlVO}
     */
    public static WeWordsUrlVO matchUrl(String address) {
        if (!isValidUrl(address)) {
            throw new CustomException(ResultTip.TIP_URL_ERROR);
        }
        WeWordsUrlVO weWordsUrlVO = new WeWordsUrlVO();
        Document doc;
        try {
            doc = Jsoup.connect(address)
                    .data("query", "Java")
                    .userAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                    .timeout(5000)
                    .get();
        } catch (IllegalArgumentException e) {
            throw new CustomException(ResultTip.TIP_URL_ERROR);
        } catch (UnknownHostException e) {
            throw new CustomException(ResultTip.TIP_URL_UNKNOWN_HOST);
        } catch (Exception e) {
            log.error("获取链接默认信息失败 url:{} , e:{}", address, ExceptionUtils.getStackTrace(e));
            throw new CustomException(ResultTip.TIP_URL_MATCH_ERROR);
        }
        //匹配标题
        weWordsUrlVO.setTitle(doc.title());
        if (StringUtils.isBlank(weWordsUrlVO.getTitle())){
            weWordsUrlVO.setTitle(getElementAttribute(doc,"meta[property=og:title]","content"));
        }
        //匹配图片
        weWordsUrlVO.setImage(getElementAttribute(doc,"link[rel~=(shortcut icon)|(icon)]","href"));
        buildUrlImage(weWordsUrlVO);
        //匹配摘要
        weWordsUrlVO.setDesc(getElementAttribute(doc,"meta[name=description]","content"));
        return weWordsUrlVO;
    }

    /**
     * 获得标签中的参数值
     * @param doc url文档
     * @param cssQuery 标签匹配规则
     * @param key 参数名称
     * @return 标签参数值
     */
    private static String getElementAttribute(Document doc, String cssQuery, String key){
        Elements desc = doc.select(cssQuery);
        if (desc.size()>0){
            return desc.get(0).attributes().get(key);
        }
        return StringUtils.EMPTY;
    }


    /**
     * 链接构建图片
     */
    private static void buildUrlImage(WeWordsUrlVO weWordsUrlVO) {
        if (isValidUrl(weWordsUrlVO.getImage())) {
            weWordsUrlVO.setIsUrl(Boolean.TRUE);
        } else {
            //有域名缺少协议的情况拼接协议
            if (weWordsUrlVO.getImage().startsWith("//")) {
                String newUrl = "https:" + weWordsUrlVO.getImage();
                weWordsUrlVO.setImage(newUrl);
                weWordsUrlVO.setIsUrl(Boolean.TRUE);
                return;
            }
            weWordsUrlVO.setIsUrl(Boolean.FALSE);
        }
    }

    /**
     * 是否为合法的url链接
     * 例子：https://www.sina.com.cn
     * @param url 链接
     * @return true是  false否
     */
    public static boolean isValidUrl(String url){
        Matcher urlMatcher = URL_PATTERN.matcher(url);
        return urlMatcher.find();
    }
}
