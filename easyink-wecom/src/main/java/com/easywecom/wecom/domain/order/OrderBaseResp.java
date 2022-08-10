package com.easywecom.wecom.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类名: OrderBaseResp
 *
 * @author: 1*+
 * @date: 2021-12-13 18:23
 */
@Data
@NoArgsConstructor
public class OrderBaseResp<T> implements Serializable {


    private Integer code;

    private String message;

    private Integer timestamp;

    private T result;


}
