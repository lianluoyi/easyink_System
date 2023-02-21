package com.easyink.wecom.domain.query.form;

import com.easyink.common.constant.Constants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.utils.DateUtils;
import com.easyink.wecom.domain.enums.form.FormSourceType;
import com.easyink.wecom.login.util.LoginTokenService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 表单查询query
 *
 * @author tigger
 * 2023/1/10 14:14
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormQuery {
    /**
     * 所属分组id， 如果不传默认不过滤
     */
    private Integer groupId;
    /**
     *
     */
    private List<Integer> searchGroupIdList;

    /**
     * 部门id，用于过滤部门表单 如果不传默认不过滤
     */
    private Long departmentId;
    /**
     * 当前登录账号id， 用户过滤个人分组表单
     */
    private String userId;
    /**
     * 表单所属分类 {@link FormSourceType}
     */
    @NotNull(message = "请选择表单所属分类")
    @Range(min = 1, max = 3, message = "请选择正确的表单分类")
    private Integer sourceType;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单id列表
     */
    private List<Integer> formIdList;

    /**
     * 启用禁用标识 true：启用 false：禁用
     */
    private Boolean enableFlag;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;


    /**
     * 不需要子表单标识 true:不需要 false: 需要
     * (== null || !unNeedChildFlag) 标识需要子分组,反之则不需要
     */
    private Boolean unNeedChildFlag;


    /**
     * 判断搜索条件
     */
    public void fillSearchValue() {
        if (FormSourceType.PERSONAL.getCode().equals(this.sourceType)) {
            LoginUser loginUser = LoginTokenService.getLoginUser();
            boolean superAdmin = loginUser.isSuperAdmin();
            if (superAdmin) {
                this.userId = Constants.SUPER_ADMIN;
            } else {
                this.userId = loginUser.getUserId();
            }
        }

    }

    /**
     * 校验参数
     */
    public void initAndValid() {
        FormSourceType.validCode(this.sourceType);
        if (this.getUnNeedChildFlag() == null) {
            this.unNeedChildFlag = false;
        }
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = DateUtils.parseBeginDay(beginTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = DateUtils.parseEndDay(endTime);
    }
}
