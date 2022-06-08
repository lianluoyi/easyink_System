package com.easywecom.wecom.domain.vo;

import com.easywecom.common.enums.EmployCodeSourceEnum;
import com.easywecom.wecom.domain.dto.AddWeMaterialDTO;
import lombok.Data;

import java.util.List;

/**
 * 类名：SelectWeEmplyCodeWelcomeMsgVO
 *
 * @author Society my sister Li
 * @date 2021-11-05 09:50
 */
@Data
public class SelectWeEmplyCodeWelcomeMsgVO {

    /**
     * 员工活码ID
     */
    private String empleCodeId;

    /**
     * 欢迎语-文本
     */
    private String welcomeMsg;

    /**
     * 欢迎语-素材（员工活码使用）
     */
    private List<AddWeMaterialDTO> materialList;

    /**
     * 素材排序
     */
    private String[] materialSort;

    /**
     * 数据来源 {@link EmployCodeSourceEnum}
     */
    private Integer source;

    /**
     * 新客进群使用的群活码链接
     */
    private String groupCodeUrl;

    /**
     * 是否打标签 0:否,1:是
     */
    private Boolean tagFlag;

    /**
     * 备注类型：0：不备注，1：在昵称前，2：在昵称后
     */
    private Integer remarkType;

    /**
     * 备注名
     */
    private String remarkName;
}
