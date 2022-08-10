package com.easywecom.wecom.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import com.easywecom.common.enums.ResultTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.wecom.domain.WeMyApplicationUseScopeEntity;
import com.easywecom.wecom.domain.WeUserRole;
import com.easywecom.wecom.domain.dto.SetApplicationUseScopeDTO;
import com.easywecom.wecom.domain.vo.WeUserVO;
import com.easywecom.wecom.mapper.WeMyApplicationUseScopeMapper;
import com.easywecom.wecom.service.WeDepartmentService;
import com.easywecom.wecom.service.WeMyApplicationUseScopeService;
import com.easywecom.wecom.service.WeUserRoleService;
import com.easywecom.wecom.service.WeUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WeMyApplicationUseScopeServiceImpl extends ServiceImpl<WeMyApplicationUseScopeMapper, WeMyApplicationUseScopeEntity> implements WeMyApplicationUseScopeService {

    @Autowired
    private WeUserRoleService weUserRoleService;
    @Autowired
    private WeUserService weUserService;
    @Autowired
    private WeDepartmentService weDepartmentService;
    /**
     * 设置我的应用使用范围
     *
     * @param corpId       企业ID
     * @param appid        应用ID
     * @param useScopeList 使用范围
     */
    @Override
    public void setMyApplicationUseScope(String corpId, Integer appid, List<SetApplicationUseScopeDTO.UseScope> useScopeList) {
        if (StringUtils.isBlank(corpId) || appid <= 0 ) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        LambdaQueryWrapper<WeMyApplicationUseScopeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeMyApplicationUseScopeEntity::getCorpId, corpId);
        queryWrapper.eq(WeMyApplicationUseScopeEntity::getAppid, appid);
        this.remove(queryWrapper);
        //指定员工（角色）为空不执行插入
        if (CollectionUtils.isEmpty(useScopeList) || StringUtils.isBlank(useScopeList.get(0).getVal())){
            return;
        }
        List<WeMyApplicationUseScopeEntity> list = new ArrayList<>();
        for (int i = 0; i < useScopeList.size(); i++) {
            SetApplicationUseScopeDTO.UseScope useScope = useScopeList.get(i);
            final int max = 3;
            final int min = 1;
            if (useScope.getType() < min || useScope.getType() > max) {
                continue;
            }

            WeMyApplicationUseScopeEntity entity = new WeMyApplicationUseScopeEntity();
            entity.setAppid(appid);
            entity.setCorpId(corpId);
            entity.setType(useScope.getType());
            entity.setVal(useScope.getVal());
            list.add(entity);
        }

        this.saveBatch(list);
    }

    /**
     * 获取我的应用使用员工集合
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link  List <String>}
     */
    @Override
    public List<String> getUseScopeUserList(String corpId, Integer appid) {
        if (StringUtils.isBlank(corpId) || appid <= 0) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        LambdaQueryWrapper<WeMyApplicationUseScopeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeMyApplicationUseScopeEntity::getCorpId, corpId);
        queryWrapper.eq(WeMyApplicationUseScopeEntity::getAppid, appid);
        List<WeMyApplicationUseScopeEntity> list = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<String> userList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();
        List<String> departmentIdList = new ArrayList<>();

        final Integer useScopeUser = 1;
        final Integer useScopeRole = 2;
        for (int i = 0; i < list.size(); i++) {
            WeMyApplicationUseScopeEntity entity = list.get(i);
            if (entity == null || StringUtils.isBlank(entity.getVal())) {
                continue;
            }
            if (useScopeUser.equals(entity.getType())) {
                userList.add(entity.getVal());
            }else if(useScopeRole.equals(entity.getType())){
                roleList.add(entity.getVal());
            }
            else {
                departmentIdList.add(entity.getVal());
            }
        }
        if(CollectionUtils.isNotEmpty(departmentIdList)){
            //部门下员工
            final List<String> userIds = weUserService.listOfUserId(corpId, StringUtils.join(departmentIdList,StrUtil.COMMA));
            if(CollectionUtils.isNotEmpty(userIds)){
                userList.addAll(userIds);
            }
        }
        if (CollectionUtils.isNotEmpty(roleList)) {
            List<WeUserRole> userRoleList = weUserRoleService.list(
                    new LambdaQueryWrapper<WeUserRole>()
                            .eq(WeUserRole::getCorpId, corpId)
                            .in(WeUserRole::getRoleId, roleList)
            );
            if (CollectionUtils.isNotEmpty(userRoleList)) {
                for (int i = 0; i < userRoleList.size(); i++) {
                    WeUserRole role = userRoleList.get(i);
                    if (role == null) {
                        continue;
                    }
                    userList.add(role.getUserId());
                }
            }

        }
        return userList;
    }

    /**
     * 获取我的应用使用
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link  List < SetApplicationUseScopeDTO.UseScope>}
     */
    @Override
    public List<SetApplicationUseScopeDTO.UseScope> getUseScope(String corpId, Integer appid) {
        if (StringUtils.isBlank(corpId) || appid <= 0) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }

        LambdaQueryWrapper<WeMyApplicationUseScopeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WeMyApplicationUseScopeEntity::getCorpId, corpId);
        queryWrapper.eq(WeMyApplicationUseScopeEntity::getAppid, appid);
        List<WeMyApplicationUseScopeEntity> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }

        List<SetApplicationUseScopeDTO.UseScope> scopeList = new ArrayList<>(list.size());
        final Integer useScopeUser = 1;
        final Integer useScopeDepartment = 3;
        for (int i = 0; i < list.size(); i++) {
            WeMyApplicationUseScopeEntity entity = list.get(i);
            if (entity == null) {
                continue;
            }
            SetApplicationUseScopeDTO.UseScope useScope = new SetApplicationUseScopeDTO.UseScope();
            useScope.setType(entity.getType());
            useScope.setVal(entity.getVal());
            if (useScopeUser.equals(entity.getType())) {
                WeUserVO weUser = weUserService.getUser(corpId, entity.getVal());
                if (weUser != null && StringUtils.isNotBlank(weUser.getUserName())) {
                    useScope.setName(weUser.getUserName());
                }
            }
            if (useScopeDepartment.equals(entity.getType())) {
                LambdaQueryWrapper<WeDepartment> departmentQueryWrapper = new LambdaQueryWrapper<>();
                departmentQueryWrapper.eq(WeDepartment::getCorpId, corpId);
                departmentQueryWrapper.eq(WeDepartment::getId, entity.getVal());
                final WeDepartment weDepartment = weDepartmentService.getOne(departmentQueryWrapper);
                useScope.setName(weDepartment.getName());
            }
            scopeList.add(useScope);
        }
        return scopeList;
    }

}