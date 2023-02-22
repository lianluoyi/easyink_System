package com.easyink.wecom.domain.vo.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 表单分页VO
 *
 * @author tigger
 * 2023/1/10 14:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormPageVO {

    /**
     * 表单id
     */
    private Integer id;

    /**
     * 表单头图
     */
    private String headImageUrl;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 表单描述
     */
    private String description;

    /**
     * 启用禁用状态 true: 启用 false: 禁用
     */
    private Boolean enableFlag;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 提交人数
     */
    private Integer submitCount;

    /**
     * 是否绑定公众号标识
     */
    private Boolean bindWeChatPublicPlatformFlag;

}
