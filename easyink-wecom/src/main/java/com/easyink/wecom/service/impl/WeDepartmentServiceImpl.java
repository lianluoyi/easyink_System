package com.easyink.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.core.domain.wecom.WeDepartment;
import com.easyink.common.core.domain.wecom.WeUser;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.DataScopeEnum;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeExceptionTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.service.ISysDeptService;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.sql.SqlUtil;
import com.easyink.wecom.client.WeDepartMentClient;
import com.easyink.wecom.domain.dto.WeDepartMentDTO;
import com.easyink.wecom.domain.dto.WeResultDTO;
import com.easyink.wecom.domain.vo.OrganizationVO;
import com.easyink.wecom.domain.vo.sop.DepartmentVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeDepartmentMapper;
import com.easyink.wecom.service.WeDepartmentService;
import com.easyink.wecom.service.WeUserService;
import com.easyink.wecom.utils.DepartmentCacheUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.easyink.common.constant.Constants.ONE_NUM;

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
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询企业微信组织架构相关列表
     *
     * @param corpId     公司ID
     * @param isActivate 成员的激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业,6=删除
     * @param loginUser
     * @return 企业微信组织架构相关
     */
    @Override
    public List<WeDepartment> selectWeDepartmentList(String corpId, Integer isActivate, LoginUser loginUser) {
        if (StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        return this.selectWeDepartmentDetailList(corpId, isActivate, loginUser);
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
    public List<WeDepartment> selectWeDepartmentDetailList(String corpId, Integer isActivate, LoginUser loginUser) {
        List<WeDepartment> list = this.baseMapper.selectWeDepartmentList(corpId);
        final List<WeDepartment> departments = sysDeptService.filterDepartmentDataScope(list, loginUser);
        if (!loginUser.isSuperAdmin() && DataScopeEnum.SELF.getCode().equals(loginUser.getRole().getDataScope())) {
            departments.get(0).setTotalUserCount(ONE_NUM);
            return departments;
        }
        //key:departmentId value: departmentId
        final Map<Long, Long> map = departments.stream().collect(Collectors.toMap(WeDepartment::getId, WeDepartment::getId));
        departments.forEach(d -> {
            //查询出该部门和其所有下级部门ID
            List<WeDepartment> deptAndChildren = this.baseMapper.selectDepartmentAndChildList(d).stream().filter(a -> map.containsKey(a.getId())).collect(Collectors.toList());
            //查询所有用户数
            if (CollectionUtils.isEmpty(deptAndChildren)) {
                d.setTotalUserCount(0);
            } else {
                d.setTotalUserCount(this.baseMapper.selectTotalUserCount(corpId, deptAndChildren, isActivate));
            }
        });
        return departments;
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
        DepartmentCacheUtils.clearAndSet(corpId, weDepartments);
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

    @Override
    public Long selectDepartmentIdByUserId(String userId, String corpId) {
        String departmentIds = this.baseMapper.selectDepartmentIdByUserId(userId, corpId);
        if (StringUtils.isBlank(departmentIds)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String[] split = departmentIds.split(StrUtil.COMMA);
        // 取最后一个子部门id
        return Long.valueOf(split[split.length - 1]);
    }

    @Override
    public List<Long> getDepartmentAndChildList(Integer departmentId, String corpId) {
        if (departmentId == null) {
            return new ArrayList<>();
        }
        if(StringUtils.isBlank(corpId)){
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeDepartment weDepartment = this.getBaseMapper().selectOne(new LambdaQueryWrapper<WeDepartment>()
                .eq(WeDepartment::getCorpId, corpId)
                .eq(WeDepartment::getId, departmentId)
        );
        List<WeDepartment> weDepartments = baseMapper.selectDepartmentAndChildList(weDepartment);
        return weDepartments.stream().map(WeDepartment::getId).collect(Collectors.toList());
    }

    @Override
    public OrganizationVO getOrganization(WeUser weUser) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        List<WeDepartment> departments = new ArrayList<>();
        // 该员工的数据权限为个人
        if (loginUser.isSelfDataScope()) {
            // 当前登录的账号为部门不在可见范围内的员工
            if (isOtherUser(loginUser)) {
                return new OrganizationVO(departments, getOtherUsers(weUser, loginUser));
            }
            return new OrganizationVO(departments, getUsers(weUser, loginUser));
        }
        departments = this.selectWeDepartmentList(loginUser.getCorpId(), weUser.getIsActivate(), loginUser);
        return new OrganizationVO(departments, getOtherUsers(weUser, loginUser));
    }

    /**
     * 获取数据权限下的员工id列表，若是包含根部们，则返回空列表
     *
     * @param departmentIds 部门id列表
     * @param userIds       员工id列表
     * @param corpId        企业id
     * @return 数据权限下的员工id列表
     */
    @Override
    public List<String> getDataScopeUserIdList(List<String> departmentIds, List<String> userIds, String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        // 使用hashSet去重
        HashSet<String> userIdList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(departmentIds)) {
            // 部门下的员工id列表
            List<String> userIdsByDepartment = weUserService.listOfUserId(corpId, departmentIds.toArray(new String[0]));
            if (CollectionUtils.isNotEmpty(userIdsByDepartment)) {
                userIdList.addAll(userIdsByDepartment);
            }
        }
        if (CollectionUtils.isNotEmpty(userIds)) {
            userIdList.addAll(userIds);
        }
        return new ArrayList<>(userIdList);
    }

    /**
     * 判断是否存在根部们
     *
     * @param departmentIds 部门id列表
     * @return true 存在，false 不存在
     */
    private boolean isHaveRootDepartment(List<String> departmentIds) {
        if (CollectionUtils.isEmpty(departmentIds)) {
            return false;
        }
        for (String departmentId : departmentIds) {
            if (WeConstans.ROOT_DEPARTMENT.equals(departmentId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前登录员工是部门不在可见范围的员工
     *
     * @param loginUser {@link LoginUser} 当前登录员工
     * @return
     */
    private boolean isOtherUser(LoginUser loginUser) {
        if (loginUser == null || loginUser.getWeUser() == null) {
            return false;
        }
        return !loginUser.isSuperAdmin() && WeConstans.OTHER_USER_DEPARTMENT.equals(StringUtils.join(loginUser.getWeUser().getDepartment()));
    }

    /**
     * 获取员工list 员工部门在可见范围内
     *
     * @param weUser    {@link WeUser}
     * @param loginUser {@link LoginUser}
     * @return
     */
    private List<WeUser> getUsers(WeUser weUser, LoginUser loginUser) {
        if (weUser == null || loginUser == null) {
            return new ArrayList<>();
        }
        weUser.setCorpId(loginUser.getCorpId());
        startPage();
        return weUserService.selectWeUserList(weUser);
    }

    /**
     * 获取部门不在可见范围的员工
     *
     * @param weUser    {@link WeUser}
     * @param loginUser {@link LoginUser}
     * @return
     */
    private List<WeUser> getOtherUsers(WeUser weUser, LoginUser loginUser) {
        if (weUser == null || loginUser == null) {
            return new ArrayList<>();
        }
        weUser.setCorpId(loginUser.getCorpId());
        weUser.setOtherUserFlag(true);
        startPage();
        return weUserService.selectWeUserList(weUser);
    }


    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }


}
