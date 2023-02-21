package com.easyink.wecom.domain.model.form;

import lombok.Data;

/**
 * 提交表单的问题与答案Model
 *
 * @author wx
 * 2023/1/30 9:25
 **/
@Data
public class FormResultModel {

    /**
     * id 唯一标识 防止问题名称重复
     */
    private String id;

    /**
     *  问题
     */
    private String question;

    /**
     * 答案
     */
    private String answer;
}
