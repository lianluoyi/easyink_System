package com.easyink.wecom.annotation;

import java.lang.annotation.*;

/**
 * 标记需要加密的字段
 * 如果方法是传入实体类的话需要在实体中需要加密的字段上加上该注解 eg: {@link com.easyink.wecom.domain.dto.customer.EditCustomerDTO}
 * !!!传入实体需要在第一个参数位置 eg: {@link com.easyink.wecom.service.impl.WeCustomerServiceImpl#editCustomer}
 *
 * @author wx
 * 2023/3/14 15:26
 **/

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cipher {
    /**
     * 需要加密的userId
     *
     * @return
     */
    boolean isUserId() default false;

    /**
     * 需要加密的externalUserId
     *
     * @return
     */
    boolean isExUserId() default false;
}
