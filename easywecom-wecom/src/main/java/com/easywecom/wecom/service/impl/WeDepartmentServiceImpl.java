package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.constant.WeConstans;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.enums.WeExceptionTip;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.StringUtils;
import com.easywecom.wecom.client.WeDepartMentClient;
import com.easywecom.wecom.domain.dto.WeDepartMentDTO;
import com.easywecom.wecom.domain.dto.WeResultDTO;
import com.easywecom.wecom.domain.vo.sop.DepartmentVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.WeDepartmentMapper;
import com.easywecom.wecom.service.WeDepartmentService;
import com.easywecom.wecom.service.WeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业微信组织架构相关Service业务层处理
 *
 * @author admin
 * @date 2020-09-01
 */
@Service
@Slf4j
public class WeDepartmentServiceImpl extends ServiceImpl<WeDepartmentMapper, WeDepartment> implements WeDepartmentService {


    @Autowired
    private WeDepartMentClient weDepartMentClient;


    @Autowired
    private WeUserService weUserService;


    /**
     * 查询企业微信组织架构相关列表
     *
     * @param corpId     公司ID
     * @param isActivate 成员的激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除
     * @return 企业微信组织架构相关
     */
    @Override
    public List<WeDepartment> selectWeDepartmentList(String corpId, Integer isActivate) {
        if (StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        return this.selectWeDepartmentDetailList(corpId, isActivate);
    }

    /**
     * 根据用户ID获取部门名字 ,隔开
     *
     * @param corpId 企业id
     * @param userId 用户id
     * @return
     */
    @Override
    public String selectNameByUserId(String corpId, String userId) {
        return baseMapper.selectNameByUserId(corpId, userId);
    }

    @Override
    public List<WeDepartment> selectWeDepartmentDetailList(String corpId, Integer isActivate) {
        List<WeDepartment> list = this.baseMapper.selectWeDepartmentList(corpId);
        list.forEach(d -> {
            //查询出该部门和其所有下级部门ID
            List<WeDepartment> deptAndChildren = this.baseMapper.selectDepartmentAndChildList(d);
            //查询所有用户数
            if (CollectionUtils.isEmpty(deptAndChildren)) {
                d.setTotalUserCount(0);
            } else {
                d.setTotalUserCount(this.baseMapper.selectTotalUserCount(corpId, deptAndChildren, isActivate));
            }
        });
        return list;
    }

    /**
     * 新增企业微信组织架构相关
     *
     * @param weDepartment 企业微信组织架构相关
     * @return true新增成功 false 新增失败
     */
    @Override
    public Boolean insertWeDepartment(WeDepartment weDepartment) {

        WeResultDTO weDepartMent = weDepartMentClient.createWeDepartMent(
                new WeDepartMentDTO().new DeartMentDto(weDepartment), weDepartment.getCorpId()
        );


        if (WeConstans.WE_SUCCESS_CODE.equals(weDepartMent.getErrcode()) && weDepartMent.getId() != null) {

            weDepartment.setId(weDepartMent.getId());
            return this.baseMapper.insertWeDepartment(weDepartment) > 0;
        }
        return Boolean.FALSE;
    }

    @Override
    public int insertWeDepartmentNoToWeCom(WeDepartment weDepartment) {
        WeDepartment department = this.baseMapper.selectWeDepartmentById(weDepartment.getCorpId(), weDepartment.getId());
        if (department != null) {
            return 0;
        }
        return this.baseMapper.insertWeDepartment(weDepartment);
    }

    /**
     * 修改企业微信组织架构相关
     *
     * @param weDepartment 企业微信组织架构相关
     * @return 结果
     */
    @Override
    public void updateWeDepartment(WeDepartment weDepartment) {

        WeResultDTO weDepartMent = weDepartMentClient.updateWeDepartMent(
                new WeDepartMentDTO().new DeartMentDto(weDepartment), weDepartment.getCorpId()
        );

        if (WeConstans.WE_SUCCESS_CODE.equals(weDepartMent.getErrcode())) {
            this.baseMapper.update(weDepartment, new LambdaUpdateWrapper<WeDepartment>()
                    .eq(WeDepartment::getCorpId, weDepartment.getCorpId())
                    .eq(WeDepartment::getId, weDepartment.getId())
            );
        }

    }


    /**
     * 同步部门
     *
     * @param corpId
     * @param userKey
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WeDepartment> synchWeDepartment(String corpId, String userKey) {
        List<WeDepartment> weDepartments = synchWeDepartment(corpId);
        // 刷新可见部门 权限
        LoginTokenService.refreshDataScope(userKey);
        return weDepartments;
    }

    /**
     * 同步部门 不刷新可见部门权限
     *
     * @param corpId 公司ID
     * @return 同步部门集合
     */
    @Override
    public List<WeDepartment> synchWeDepartment(String corpId) {
        log.info("开始同步部门,corpId:{}", corpId);
        if (StringUtils.isBlank(corpId)) {
            log.info("corpId为空,无法同步部门");
            return Collections.emptyList();
        }
        List<WeDepartment> weDepartments = weDepartMentClient.weAllDepartMents(corpId).findWeDepartments(corpId);
        List<WeDepartment> localDepartments = baseMapper.selectDepartmentByCorpId(corpId);
        //删除不存在的部门
        if (CollUtil.isNotEmpty(localDepartments)) {
            List<Long> ids = new ArrayList<>();
            //拉取到的部门集合,通过corpId+departmentId组成唯一标识来区分不同部门
            List<Long> pullList = weDepartments.stream().map(WeDepartment::getId).collect(Collectors.toList());
            for (WeDepartment localDepartment : localDepartments) {
                if (!pullList.contains(localDepartment.getId())) {
                    ids.add(localDepartment.getId());
                }
            }
            if (CollUtil.isNotEmpty(ids)) {
                this.baseMapper.delete(
                        new LambdaUpdateWrapper<WeDepartment>()
                                .eq(WeDepartment::getCorpId, corpId)
                                .in(WeDepartment::getId, ids)
                );
            }
        }
        //更新部门
        if (CollUtil.isNotEmpty(weDepartments)) {
            this.baseMapper.batchInsertWeDepartment(weDepartments);
            log.info("同步部门完成,corpId:{},本次同步部门数:{}", corpId, weDepartments.size());
        }
        return weDepartments;
    }


    /**
     * 根据部门id删除部门
     *
     * @param corpId
     * @param ids
     */
    @Override
    public void deleteWeDepartmentByIds(String corpId, String[] ids) {

        //查询当前部门下所有的子部门,如果存在,则不可以删除
        List<WeDepartment> weDepartments = this.list(new LambdaQueryWrapper<WeDepartment>()
                .eq(WeDepartment::getCorpId, corpId)
                .in(WeDepartment::getParentId, ids));
        if (CollUtil.isNotEmpty(weDepartments)) {
            //抛出异常，请删除此部门下的成员或子部门后，再删除此部门
            throw new CustomException(WeExceptionTip.WE_EXCEPTION_TIP_60006.getTipMsg(), WeExceptionTip.WE_EXCEPTION_TIP_60006.getCode());
        }

        List<WeUser> weUsers = weUserService.selectWeUserList(WeUser.builder()
                .corpId(corpId)
                .department(ids)
                .build());
        if (CollUtil.isNotEmpty(weUsers)) {
            //该部门存在成员无法删除
            throw new CustomException(WeExceptionTip.WE_EXCEPTION_TIP_60005.getTipMsg(), WeExceptionTip.WE_EXCEPTION_TIP_60005.getCode());
        }

        //删除数据库中数据
        if (this.remove(new LambdaUpdateWrapper<WeDepartment>()
                .eq(WeDepartment::getCorpId, corpId)
                .in(WeDepartment::getId, ids)
        )) {
            for (String id : ListUtil.toList(ids)) {
                //移除微信端
                weDepartMentClient.deleteWeDepartMent(id, corpId);
            }
        }

    }


    @Override
    public List<DepartmentVO> getDeparmentDetailByIds(String corpId, List<String> departmentIdList) {
        if (StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return Collections.emptyList();
        }
        return this.baseMapper.getDepartmentDetails(corpId, departmentIdList);
    }


}
