package com.easyink.wecom.openapi.util;

import com.easyink.common.exception.openapi.SignValidateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 类名: 签名生成器
 *
 * @author : silver_chariot
 * @date : 2022/3/14 21:01
 */
public class SignGenerator extends TreeMap<String, String> {

    /**
     * 添加组装签名的元素
     *
     * @param key   签名参数键key , 例： app_id
     * @param value 签名参数value , 例： app_id=1451515
     * @return {@link SignGenerator} 组装后的map
     */
    public SignGenerator add(String key, String value) {
        // map 规则 k: key v: key=value
        String content = key + "=" + value;
        this.put(key, content);
        return this;
    }

    /**
     * <p>
     * 生成签名： 需要先调用 add()方法把所有参数都加入到该生成器中
     * </p>
     * <p>
     * 规则： 把所需参数 app_id,app_secret,timestamp,nonce,ticket按
     * 已以下方式组装： plus_app_id=1515151&plus_app_secret=15151515....&plus_ticket=eathgahg2gaga
     * 然后 按照ASCII码 根据上面的key 重新排序 , 本方法是根据{@link TreeMap}实现排序
     * 再对字符串进行 md5加密(目前只使用md5,后续如需扩展可以增加多种类型,增加加密类型传参,使用策略模式加密) , 最后全部转换成大写, 得到最后的签名
     * </p>
     *
     * @return 服务端生成的签名
     */
    public String doGenerate() {
        if (this.isEmpty()) {
            throw new SignValidateException("generate sign error");
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : this.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                list.add(entry.getValue());
            }
        }
        String str = StringUtils.join(list, "&");
        return md5(str).toUpperCase();
    }

    /**
     * md5
     *
     * @param str 字符
     * @return 加密后的字符
     */
    public String md5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

}
