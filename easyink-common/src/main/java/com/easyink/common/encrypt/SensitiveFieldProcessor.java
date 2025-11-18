package com.easyink.common.encrypt;

import com.easyink.common.annotation.EncryptField;
import com.easyink.common.annotation.EncryptFields;
import com.easyink.common.core.page.TableDataInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 敏感字段处理工具类
 * 提供加密、解密、脱敏等操作的统一处理
 * 
 * @author java-backend-expert
 * @date 2024-12-19
 */
@Slf4j
public class SensitiveFieldProcessor {

    /**
     * 加密字段后缀
     */
    private static final String ENCRYPT_SUFFIX = "Encrypt";

    /**
     * 对对象进行解密处理
     * 将加密字段解密后覆盖原字段
     * 
     * @param obj 待处理对象
     */
    public static void decrypt(Object obj) {
        if (obj == null) {
            return;
        }

        try {
            if (obj instanceof Collection) {
                // 处理集合类型
                Collection<?> collection = (Collection<?>) obj;
                for (Object item : collection) {
                    decrypt(item);
                }
            } else if (obj instanceof TableDataInfo) {
                // 处理分页数据
                TableDataInfo tableData = (TableDataInfo) obj;
                if (tableData.getRows() != null) {
                    decrypt(tableData.getRows());
                }
            } else if (obj instanceof Map) {
                // 处理Map类型
                Map<?, ?> map = (Map<?, ?>) obj;
                for (Object value : map.values()) {
                    decrypt(value);
                }
            } else {
                // 处理普通对象
                decryptObject(obj);
            }
        } catch (Exception e) {
            log.error("解密处理失败", e);
        }
    }

    /**
     * 对对象进行加密处理
     * 生成加密字段并保持原字段为脱敏状态
     * 
     * @param obj 待处理对象
     */
    public static void encrypt(Object obj) {
        if (obj == null) {
            return;
        }

        try {
            if (obj instanceof Collection) {
                // 处理集合类型
                Collection<?> collection = (Collection<?>) obj;
                for (Object item : collection) {
                    encrypt(item);
                }
            } else if (obj instanceof TableDataInfo) {
                // 处理分页数据
                TableDataInfo tableData = (TableDataInfo) obj;
                if (tableData.getRows() != null) {
                    encrypt(tableData.getRows());
                }
            } else if (obj instanceof Map) {
                // 处理Map类型
                Map<?, ?> map = (Map<?, ?>) obj;
                for (Object value : map.values()) {
                    encrypt(value);
                }
            } else {
                // 处理普通对象
                encryptObject(obj);
            }
        } catch (Exception e) {
            log.error("加密处理失败", e);
        }
    }

    /**
     * 解密单个对象
     * 
     * @param obj 待解密对象
     */
    private static void decryptObject(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        
        // 检查类是否需要嵌套处理
        EncryptFields encryptFields = clazz.getAnnotation(EncryptFields.class);
        if (encryptFields != null && !encryptFields.value()) {
            return;
        }

        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null) {
                // 获取加密字段
                String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);
                
                if (encryptFieldObj != null) {
                    encryptFieldObj.setAccessible(true);
                    String encryptedValue = (String) encryptFieldObj.get(obj);
                    
                    if (StringUtils.isNotBlank(encryptedValue)) {
                        // 解密并设置到原字段
                        String decryptedValue = StrategyCryptoUtil.decrypt(encryptedValue);
                        field.set(obj, decryptedValue);
                    }
                }
            } else if (field.getAnnotation(EncryptFields.class) != null) {
                // 递归处理嵌套对象
                Object nestedObj = field.get(obj);
                if (nestedObj != null) {
                    decrypt(nestedObj);
                }
            }
        }
    }

    /**
     * 加密单个对象
     * 
     * @param obj 待加密对象
     */
    private static void encryptObject(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        
        // 检查类是否需要嵌套处理
        EncryptFields encryptFields = clazz.getAnnotation(EncryptFields.class);
        if (encryptFields != null && !encryptFields.value()) {
            return;
        }

        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null) {
                String originalValue = (String) field.get(obj);
                
                if (StringUtils.isNotBlank(originalValue)) {
                    // 获取加密字段
                    String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                    Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);
                    
                    if (encryptFieldObj != null) {
                        encryptFieldObj.setAccessible(true);
                        
                        // 加密原值并设置到加密字段
                        String encryptedValue = StrategyCryptoUtil.encrypt(originalValue);
                        encryptFieldObj.set(obj, encryptedValue);
                        
                        // 脱敏原字段
                        String desensitizedValue = null;
                        if(encryptField.value() == EncryptField.FieldType.ADDRESS){
                            desensitizedValue = StrategyCryptoUtil.esensitizationAllAddress(originalValue);
                        }else{
                            desensitizedValue = StrategyCryptoUtil.esensitization(originalValue);
                        }
                        field.set(obj, desensitizedValue);
                    }
                }else if(originalValue == null){
                    // 原值为空, 则设置默认的加密字段
                    // 获取加密字段
                    String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                    Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);

                    if (encryptFieldObj != null) {
                        encryptFieldObj.setAccessible(true);

                        // 设置默认的加密值
                        encryptFieldObj.set(obj, encryptField.nullValue().getValue());
                    }
                }else{
                    // 原值为空, 则设置默认的加密字段
                    // 获取加密字段
                    String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                    Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);

                    if (encryptFieldObj != null) {
                        encryptFieldObj.setAccessible(true);

                        // 设置默认的加密值
                        encryptFieldObj.set(obj, encryptField.emptyValue());
                    }
                }
            } else if (field.getAnnotation(EncryptFields.class) != null) {
                // 递归处理嵌套对象
                Object nestedObj = field.get(obj);
                if (nestedObj != null) {
                    encrypt(nestedObj);
                }
            }
        }
    }

    /**
     * 处理保存前的数据
     * 对明文字段进行加密并生成密文字段，同时对明文字段进行脱敏处理
     * 
     * @param obj 待处理对象
     */
    public static void processForSave(Object obj) {
        if (obj == null) {
            return;
        }

        try {
            if (obj instanceof Collection) {
                // 处理集合类型
                Collection<?> collection = (Collection<?>) obj;
                for (Object item : collection) {
                    processForSave(item);
                }
            } else if (obj instanceof TableDataInfo) {
                // 处理分页数据
                TableDataInfo tableData = (TableDataInfo) obj;
                if (tableData.getRows() != null) {
                    processForSave(tableData.getRows());
                }
            } else if (obj instanceof Map) {
                // 处理Map类型
                Map<?, ?> map = (Map<?, ?>) obj;
                for (Object value : map.values()) {
                    processForSave(value);
                }
            } else {
                // 处理普通对象
                processObjectForSave(obj);
            }
        } catch (Exception e) {
            log.info("保存前处理失败: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 处理单个对象的保存前操作
     * 
     * @param obj 待处理对象
     */
    private static void processObjectForSave(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        
        // 检查类是否需要嵌套处理
        EncryptFields encryptFields = clazz.getAnnotation(EncryptFields.class);
        if (encryptFields != null && !encryptFields.value()) {
            return;
        }

        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            
            EncryptField encryptField = field.getAnnotation(EncryptField.class);
            if (encryptField != null) {
                Object o = field.get(obj);
                String originalValue = o == null ? StringUtils.EMPTY :String.valueOf(o) ;
                
                if (StringUtils.isNotBlank(originalValue)) {
                    // 获取加密字段
                    String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                    Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);
                    
                    if (encryptFieldObj != null) {
                        encryptFieldObj.setAccessible(true);
                        
                        // 如果原值不是脱敏数据，则进行加密和脱敏处理
                        if (!isDesensitized(originalValue, encryptField.value())) {
                            // 加密原值并设置到加密字段
                            String encryptedValue = StrategyCryptoUtil.encrypt(originalValue);
                            encryptFieldObj.set(obj, encryptedValue);
                            
                            // 脱敏原字段
                            String desensitizedValue = null;
                            if(encryptField.value() == EncryptField.FieldType.ADDRESS){
                                desensitizedValue = StrategyCryptoUtil.esensitizationAllAddress(originalValue);
                            }else{
                                desensitizedValue = StrategyCryptoUtil.esensitization(originalValue);
                            }
                            field.set(obj, desensitizedValue);
                            
                            log.debug("字段 {} 已加密并脱敏处理", field.getName());
                        }
                    }
                }else if(originalValue == null){
                    // 原值为空, 则设置默认的加密字段
                    // 获取加密字段
                    String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                    Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);

                    if (encryptFieldObj != null) {
                        encryptFieldObj.setAccessible(true);

                        // 设置默认的加密值
                        encryptFieldObj.set(obj, encryptField.nullValue().getValue());
                    }
                }else{
                    // 原值为空, 则设置默认的加密字段
                    // 获取加密字段
                    String encryptFieldName = field.getName() + ENCRYPT_SUFFIX;
                    Field encryptFieldObj = getFieldByName(clazz, encryptFieldName);

                    if (encryptFieldObj != null) {
                        encryptFieldObj.setAccessible(true);

                        // 设置默认的加密值
                        encryptFieldObj.set(obj, encryptField.emptyValue());
                    }
                }
            } else if (field.getAnnotation(EncryptFields.class) != null) {
                // 递归处理嵌套对象
                Object nestedObj = field.get(obj);
                if (nestedObj != null) {
                    processForSave(nestedObj);
                }
            }
        }
    }

    /**
     * 判断字段值是否已经是脱敏数据
     * 
     * @param value 字段值
     * @param fieldType 字段类型
     * @return 是否已脱敏
     */
    private static boolean isDesensitized(String value, EncryptField.FieldType fieldType) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        // 都先进行脱敏
        return false;
//        switch (fieldType) {
//            case MOBILE:
//                // 手机号脱敏格式：138****1234
//                return value.contains("*") && value.length() >= 8;
//            case ADDRESS:
//                // 地址脱敏格式：北京市****区
//                return value.contains("*");
//            case TELEPHONE:
//                // 座机脱敏格式：010-****1234
//                return value.contains("*");
//            default:
//                // 通用脱敏格式：包含*号
//                return value.contains("*");
//        }
    }

    /**
     * 获取类及其父类的所有字段
     * 
     * @param clazz 类对象
     * @return 所有字段列表
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        
        // 递归获取当前类及其父类的所有字段
        while (clazz != null && clazz != Object.class) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        
        return fields;
    }

    /**
     * 根据字段名获取字段对象
     * 
     * @param clazz 类对象
     * @param fieldName 字段名
     * @return 字段对象
     */
    private static Field getFieldByName(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 尝试从父类查找
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return getFieldByName(superClass, fieldName);
            }
            return null;
        }
    }
}