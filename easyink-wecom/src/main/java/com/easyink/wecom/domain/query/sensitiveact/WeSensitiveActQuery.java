package com.easyink.wecom.domain.query.sensitiveact;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 敏感词查询
 * @author tigger
 * 2024/8/1 16:19
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeSensitiveActQuery {


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;


    /**
     * 审计对象id
     */
    @ApiModelProperty(value = "审计范围id")
    private String auditScopeId;

    /**
     * 敏感行为名称
     */
    private String actName;


}
