package com.easyink.wecom.annotation.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.core.domain.RootEntity;
import com.easyink.common.core.domain.entity.SysRole;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.enums.DataScopeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.user.NoLoginTokenException;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeDepartmentService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据过滤处理
 *
 * @author admin
 */
@Aspect
@Component
public class DataScopeAspect {
    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    private final WeDepartmentService weDepartmentService;

    public DataScopeAspect(WeDepartmentService weDepartmentService) {
        this.weDepartmentService = weDepartmentService;
    }

    @Pointcut("@annotation(com.easyink.common.annotation.DataScope)")
    public void dataScopePointCut() {
        // 配置织入点

    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) {
        handleDataScope(point);
    }

    protected void handleDataScope(final JoinPoint joinPoint) {
        // 获得注解
        DataScope controllerDataScope = getAnnotationLog(joinPoint);
        if (controllerDataScope == null) {
            return;
        }
        // 获取当前的用户 (若非页面的内部请求则会抛出无登录token异常,不进行数据范围过滤处理)
        LoginUser loginUser;
        try {
            loginUser = LoginTokenService.getLoginUser();
        } catch (NoLoginTokenException e) {
            return;
        }
        // 如果是超级管理员 则不用过滤
        if (loginUser != null && !loginUser.isSuperAdmin()) {
            dataScopeFilter(joinPoint, loginUser, controllerDataScope.userAlias());
            setDataScopeUserIds(joinPoint, loginUser);
        }
    }

    /**
     * 设置数据权限下的userId列表
     *
     * @param joinPoint 切点
     * @param loginUser 登录用户实体
     */
    public void setDataScopeUserIds(JoinPoint joinPoint, LoginUser loginUser) {
        SysRole role = loginUser.getRole();
        WeUser weUser = loginUser.getWeUser();
        String corpId = loginUser.getCorpId();
        if (ObjectUtil.isNull(role) || ObjectUtil.isNull(weUser)) {
            throw new CustomException(ResultTip.TIP_DATA_SCOPE_ERROR);
        }
        // 当前登录员工的部门id列表
        List<String> departmentScopeList = Arrays.asList(loginUser.getDepartmentDataScope().split(DictUtils.SEPARATOR));
        // 当前登录员工的id
        List<String> userIdList = new ArrayList<>();
        userIdList.add(weUser.getUserId());
        // 数据权限下的员工id列表
        List<String> dataScopeUserIds = new ArrayList<>();
        switch (DataScopeEnum.getDataScope(role.getDataScope())) {
            case CUSTOM:
                dataScopeUserIds = weDepartmentService.getDataScopeUserIdList(departmentScopeList, userIdList, corpId);
                break;
            case SELF_DEPT:
                List<String> mainDepartMentList = new ArrayList<>();
                mainDepartMentList.add(String.valueOf(weUser.getMainDepartment()));
                dataScopeUserIds = weDepartmentService.getDataScopeUserIdList(mainDepartMentList, null, corpId);
                break;
            case DEPT_AND_CHILD:
                dataScopeUserIds = weDepartmentService.getDataScopeUserIdList(departmentScopeList, null, corpId);
                break;
            case SELF:
                dataScopeUserIds = weDepartmentService.getDataScopeUserIdList(null, userIdList, corpId);
                break;
            default:
                break;
        }
        RootEntity rootEntity = (RootEntity) joinPoint.getArgs()[0];
        rootEntity.setDataScopeUserIds(dataScopeUserIds);
    }

    /**
     * 过滤数据范围
     *
     * @param joinPoint 切点
     * @param user      登录用户实体
     * @param userAlias 用户表查询 别名
     */
    public static void dataScopeFilter(JoinPoint joinPoint, LoginUser user, String userAlias) {
        SysRole role = user.getRole();
        WeUser weUser = user.getWeUser();
        if (ObjectUtil.isNull(role) || ObjectUtil.isNull(weUser)) {
            throw new CustomException("数据权限获取异常");
        }
        StringBuilder sqlString = new StringBuilder();
        // 根据角色所属的数据范围拼接查询条件
        switch (DataScopeEnum.getDataScope(role.getDataScope())) {
            case CUSTOM:
                sqlString.append(
                        StringUtils.format(" {}.main_department IN ({}) OR {}.user_id = '{}' ", userAlias, user.getDepartmentDataScope(), userAlias, weUser.getUserId())
                );
                break;
            case SELF_DEPT:
                sqlString.append(
                        StringUtils.format(" {}.main_department = '{}' ", userAlias, weUser.getMainDepartment())
                );
                break;
            case DEPT_AND_CHILD:
                sqlString.append(
                        StringUtils.format(" {}.main_department IN ({})", userAlias, user.getDepartmentDataScope())
                );
                break;
            case SELF:
                sqlString.append(
                        StringUtils.format(" {}.user_id = '{}' ", userAlias, weUser.getUserId())
                );
                break;
            default:
                break;
        }
        // 组装SQL
        if (StringUtils.isNotBlank(sqlString.toString())) {
            RootEntity rootEntity = (RootEntity) joinPoint.getArgs()[0];
            rootEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.toString() + ")");
        }

    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private DataScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(DataScope.class);
        }
        return null;
    }
}
