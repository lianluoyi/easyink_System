package com.easyink.common.utils.xml;

import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

/**
 * 安全的XML工具类
 * 提供防止XXE攻击的XML解析功能
 *
 * @author security-team
 * @date 2023/12/07
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecureXmlUtil {

    /**
     * XML长度限制 - 1MB
     */
    private static final int MAX_XML_LENGTH = 1024 * 1024;

    /**
     * 恶意XML模式检测
     */
    private static final Pattern[] MALICIOUS_PATTERNS = {
        Pattern.compile("<!ENTITY", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<!DOCTYPE", Pattern.CASE_INSENSITIVE),
        Pattern.compile("SYSTEM\\s+[\"']", Pattern.CASE_INSENSITIVE),
        Pattern.compile("PUBLIC\\s+[\"']", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<!\\[CDATA\\[.*?javascript:", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        Pattern.compile("<!\\[CDATA\\[.*?<script", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    };

    /**
     * 创建安全的DocumentBuilder
     * 禁用所有可能导致XXE攻击的特性
     *
     * @return 安全的DocumentBuilder实例
     * @throws CustomException 当创建失败时抛出
     */
    public static DocumentBuilder createSecureDocumentBuilder() throws CustomException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            // 禁用DTD处理，防止XXE攻击
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            
            // 禁用外部实体引用
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            // 禁用外部DTD加载
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            
            // 禁用XInclude处理
            factory.setXIncludeAware(false);
            
            // 禁用实体引用扩展
            factory.setExpandEntityReferences(false);
            
            // 设置安全处理特性
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            
            return factory.newDocumentBuilder();
            
        } catch (ParserConfigurationException e) {
            log.error("创建安全DocumentBuilder失败", e);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "XML解析器配置失败");
        }
    }

    /**
     * 验证XML内容的安全性
     * 检查XML中是否包含可能导致安全问题的内容
     *
     * @param xmlContent XML内容
     * @throws CustomException 当发现安全风险时抛出
     */
    public static void validateXmlSecurity(String xmlContent) throws CustomException {
        if (StringUtils.isBlank(xmlContent)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "XML内容不能为空");
        }

        // 检查XML长度
        if (xmlContent.length() > MAX_XML_LENGTH) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, 
                "XML内容过大，超过最大限制: " + MAX_XML_LENGTH + " 字节");
        }

        // 基本格式验证
        String trimmedXml = xmlContent.trim();
        if (!trimmedXml.startsWith("<") || !trimmedXml.endsWith(">")) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "XML格式无效");
        }

        // 检查恶意模式
        for (Pattern pattern : MALICIOUS_PATTERNS) {
            if (pattern.matcher(xmlContent).find()) {
                log.warn("检测到可疑的XML内容，模式: {}, XML前100字符: {}", 
                    pattern.pattern(), 
                    xmlContent.length() > 100 ? xmlContent.substring(0, 100) + "..." : xmlContent);
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, 
                    "XML内容包含不安全的元素，可能存在XXE攻击风险");
            }
        }
    }

    /**
     * 安全地解析XML为Document
     * 使用安全的DocumentBuilder解析XML
     *
     * @param xmlContent XML内容
     * @return 解析后的Document对象
     * @throws CustomException 当解析失败时抛出
     */
    public static Document parseSecurely(String xmlContent) throws CustomException {
        validateXmlSecurity(xmlContent);
        
        try {
            DocumentBuilder builder = createSecureDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlContent)));
            
        } catch (SAXException e) {
            log.error("XML解析失败 - SAX异常，XML前100字符: {}", 
                xmlContent.length() > 100 ? xmlContent.substring(0, 100) + "..." : xmlContent, e);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "XML格式错误: " + e.getMessage());
            
        } catch (IOException e) {
            log.error("XML解析失败 - IO异常", e);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "XML读取失败");
            
        } catch (Exception e) {
            log.error("XML解析失败 - 未知异常", e);
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST, "XML解析失败");
        }
    }

    /**
     * 清理XML内容中的潜在危险字符
     * 移除或转义可能导致安全问题的字符
     *
     * @param xmlContent 原始XML内容
     * @return 清理后的XML内容
     */
    public static String sanitizeXmlContent(String xmlContent) {
        if (StringUtils.isBlank(xmlContent)) {
            return xmlContent;
        }

        return xmlContent
            // 移除XML声明中的编码声明，防止编码攻击
            .replaceAll("(?i)encoding\\s*=\\s*[\"'][^\"']*[\"']", "")
            // 移除潜在的脚本标签
            .replaceAll("(?i)<script[^>]*>.*?</script>", "")
            // 移除潜在的样式标签
            .replaceAll("(?i)<style[^>]*>.*?</style>", "")
            // 移除注释中的潜在恶意内容
            .replaceAll("<!--.*?-->", "")
            // 限制连续的空白字符，防止DoS攻击
            .replaceAll("\\s{1000,}", " ");
    }
} 