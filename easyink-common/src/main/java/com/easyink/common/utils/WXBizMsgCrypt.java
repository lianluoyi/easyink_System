package com.easyink.common.utils;

import com.easyink.common.exception.AesException;
import me.chanjar.weixin.common.util.crypto.PKCS7Encoder;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.easyink.common.utils.wecom.SHA1.getSHA1;

/**
 * 提供接收和推送给公众平台消息的加解密接口(UTF8编码的字符串).
 * <ol>  * <li>第三方回复加密消息给公众平台</li>  * <li>第三方收到公众平台发送的消息，验证消息的安全性，并对消息进行解密。</li>
 * </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol>
 * <li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：  *
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 * <li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * <li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * <li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 *
 * </ol>
 */
public class WXBizMsgCrypt {
    private static final Charset CHARSET;
    private static final ThreadLocal<DocumentBuilder> BUILDER_LOCAL;

    static {
        CHARSET = StandardCharsets.UTF_8;
        BUILDER_LOCAL = ThreadLocal.withInitial(() -> {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                dbf.setXIncludeAware(false);
                dbf.setExpandEntityReferences(false);
                return dbf.newDocumentBuilder();
            } catch (ParserConfigurationException var1) {
                throw new IllegalArgumentException(var1);
            }
        });
    }

    Base64 base64 = new Base64();
    byte[] aesKey;
    String token;
    String appId;

    /**
     * 构造函数
     * @param token 公众平台上，开发者设置的token
     * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
     * @param appId 公众平台appid
     *
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public WXBizMsgCrypt(String token, String encodingAesKey, String appId) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(AesException.IllegalAesKey);
        }

        this.token = token;
        this.appId = appId;
        aesKey = Base64.decodeBase64(encodingAesKey + "=");
    }

    // 还原4个字节的网络字节序
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    /**
     * 对密文进行解密.
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws AesException aes解密失败
     */
    String decrypt(String text) throws AesException {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new AesException(AesException.DecryptAESError);
        }

        String xmlContent, from_appid;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和AppId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            from_appid =
                    new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            throw new AesException(AesException.IllegalBuffer);
        }

        // appid不相同的情况
        if (!from_appid.equals(appId)) {
            throw new AesException(AesException.ValidateSignatureError);
        }
        return xmlContent;

    }

    /**
     *  * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取xml中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param postData 密文，对应POST请求的数据
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(String msgSignature, String timeStamp, String nonce, String postData)
            throws AesException {

        // 密钥，公众账号的app secret
        // 提取密文
        String encrypt = extractEncryptPart(postData);

        // 验证安全签名
        String signature = getSHA1(token, timeStamp, nonce, encrypt);

        // 和URL中的签名比较是否相等
        if (!signature.equals(msgSignature)) {
            throw new AesException(AesException.ValidateSignatureError);
        }

        // 解密
        return decrypt(encrypt);
    }


    /**
     * 提取出xml数据包中的加密消息
     * @param xmltext 待提取的xml字符串
     * @return 提取出的加密消息字符串
     * @throws AesException
     */
    public static String extractEncryptPart(String xmltext) throws AesException {
        try {
            DocumentBuilder db = BUILDER_LOCAL.get();
            Document document = db.parse(new InputSource(new StringReader(xmltext)));

            Element root = document.getDocumentElement();
            return root.getElementsByTagName("Encrypt").item(0).getTextContent();
        } catch (Exception e) {
            throw new AesException(AesException.ParseXmlError);
        }
    }


}
