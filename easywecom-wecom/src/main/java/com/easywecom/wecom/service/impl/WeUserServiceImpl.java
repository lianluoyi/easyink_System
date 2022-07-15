package com.easywecom.wecom.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easywecom.common.annotation.DataScope;
import com.easywecom.common.config.RuoYiConfig;
import com.easywecom.common.constant.*;
import com.easywecom.common.core.domain.entity.SysRole;
import com.easywecom.common.core.domain.entity.WeCorpAccount;
import com.easywecom.common.core.domain.model.LoginUser;
import com.easywecom.common.core.domain.wecom.WeDepartment;
import com.easywecom.common.core.domain.wecom.WeUser;
import com.easywecom.common.core.redis.RedisCache;
import com.easywecom.common.enums.*;
import com.easywecom.common.exception.CustomException;
import com.easywecom.common.utils.file.FileUploadUtils;
import com.easywecom.common.utils.spring.SpringUtils;
import com.easywecom.wecom.client.WeAgentClient;
import com.easywecom.wecom.client.WeUserClient;
import com.easywecom.wecom.domain.*;
import com.easywecom.wecom.domain.dto.*;
import com.easywecom.wecom.domain.dto.group.GroupChatListReq;
import com.easywecom.wecom.domain.dto.group.GroupChatListResp;
import com.easywecom.wecom.domain.dto.transfer.GetUnassignedListReq;
import com.easywecom.wecom.domain.dto.transfer.GetUnassignedListResp;
import com.easywecom.wecom.domain.dto.transfer.TransferResignedUserListDTO;
import com.easywecom.wecom.domain.resp.GetAgentResp;
import com.easywecom.wecom.domain.vo.*;
import com.easywecom.wecom.domain.vo.transfer.TransferResignedUserVO;
import com.easywecom.wecom.login.util.LoginTokenService;
import com.easywecom.wecom.mapper.WeUserMapper;
import com.easywecom.wecom.mapper.WeUserRoleMapper;
import com.easywecom.wecom.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通讯录相关客户Service业务层处理
 *
 * @author admin
 * @date 2020-08-31
 */
@Service
@Slf4j
public class WeUserServiceImpl extends ServiceImpl<WeUserMapper, WeUser> implements WeUserService {
    private final WeUserMapper weUserMapper;
    private final RedisCache redisCache;
    private final WeUserClient weUserClient;
    private final WeDepartmentService weDepartmentService;
    private final WeUserRoleMapper weUserRoleMapper;
    private final WeFlowerCustomerRelService weFlowerCustomerRelService;
    private final We3rdAppService we3rdAppService;
    private final WeUserRoleService weUserRoleService;
    private final PageHomeService pageHomeService;
    private final WeGroupService weGroupService;
    private final WeMaterialService weMaterialService;
    private final RuoYiConfig ruoYiConfig;
    private final WeExternalUserMappingUserService weExternalUserMappingUserService;
    private final WeCorpAccountService weCorpAccountService;
    private final WeAgentClient weAgentClient;
    @Autowired
    private WeAuthCorpInfoService weAuthCorpInfoService;


    @Lazy
    @Autowired
    public WeUserServiceImpl(WeDepartmentService weDepartmentService, WeUserMapper weUserMapper, RedisCache redisCache, We3rdAppService we3rdAppService, WeUserClient weUserClient, WeCustomerService weCustomerService, WeUserRoleMapper weUserRoleMapper, WeFlowerCustomerRelService weFlowerCustomerRelService, WeUserRoleService weUserRoleService, PageHomeService pageHomeService, WeGroupService weGroupService, WeMaterialService weMaterialService, WeExternalUserMappingUserService weExternalUserMappingUserService, RuoYiConfig ruoYiConfig, WeCorpAccountService weCorpAccountService, WeAgentClient weAgentClient) {
        this.weDepartmentService = weDepartmentService;
        this.weUserMapper = weUserMapper;
        this.redisCache = redisCache;
        this.we3rdAppService = we3rdAppService;
        this.weUserClient = weUserClient;
        this.weUserRoleMapper = weUserRoleMapper;
        this.weFlowerCustomerRelService = weFlowerCustomerRelService;
        this.weUserRoleService = weUserRoleService;
        this.pageHomeService = pageHomeService;
        this.weGroupService = weGroupService;
        this.weMaterialService = weMaterialService;
        this.weExternalUserMappingUserService = weExternalUserMappingUserService;
        this.ruoYiConfig = ruoYiConfig;
        this.weCorpAccountService = weCorpAccountService;
        this.weAgentClient = weAgentClient;
    }

    /**
     * 更新员工角色关系，不存在则插入
     *
     * @param weUserRole 员工角色
     */
    @Override
    public void updateUserRole(WeUserRole weUserRole) {
        LambdaUpdateWrapper<WeUserRole> updateWrapper = new LambdaUpdateWrapper<>();
        int i = weUserRoleMapper.update(weUserRole, updateWrapper.eq(WeUserRole::getCorpId, weUserRole.getCorpId()).eq(WeUserRole::getUserId, weUserRole.getUserId()));
        if (i <= 0) {
            weUserRoleMapper.insertUserRole(weUserRole);
        }
    }

    /**
     * 查询员工信息
     *
     * @param queryUserDTO 查询条件
     * @return vo
     */
    @Override
    @DataScope
    public List<WeUserVO> listOfUser(QueryUserDTO queryUserDTO) {
        if (StringUtils.isEmpty(queryUserDTO.getDepartments())) {
            return Collections.emptyList();
        }
        return weUserMapper.listOfUser(queryUserDTO);
    }

    /**
     * 查询员工信息
     *
     * @param corpId          企业ID
     * @param queryUserIdList 查询指定员工信息
     * @return {@link List < WeUserVO >}
     */
    @Override
    public List<WeUserVO> listOfUser(String corpId, List<String> queryUserIdList) {
        if (StringUtils.isBlank(corpId)) {
            return new ArrayList<>();
        }
        List<WeUserVO> list = weUserMapper.listOfUser1(corpId, queryUserIdList);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * 可查出员工信息包含离职员工
     *
     * @param corpId
     * @param userId 员工id
     * @return vo
     */
    @Override
    public WeUserVO getUser(String corpId, String userId) {
        return weUserMapper.getUser(corpId, userId);
    }

    @Override
    public List<String> listOfUserId(String corpId, String[] departments) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if(departments.length > 0){
            return weUserMapper.listOfUserId(corpId, departments);
        }
        return Collections.emptyList();
    }

    /**
     * 查询权限下的员工，重载上述方法
     *
     * @param corpId
     * @param departments
     * @return
     */
    @Override
    public List<String> listOfUserId(String corpId, String departments) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (StringUtils.isBlank(departments)) {
            return Collections.emptyList();
        }
        String[] departmentsArr = departments.split(StrUtil.COMMA);
        return weUserMapper.listOfUserId(corpId, departmentsArr);
    }

    /**
     * 查询权限下的员工，重载上述方法
     *
     * @param corpId
     * @param departments
     * @return
     */
    @Override
    public List<String> listOfUserId(String corpId, List<Long> departments) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (CollectionUtils.isEmpty(departments)) {
            return Collections.emptyList();
        }
        String[] departmentsArr = departments.stream().map(x -> x + "").toArray(String[]::new);
        return weUserMapper.listOfUserId(corpId, departmentsArr);
    }

    /**
     * 查询通讯录相关客户
     *
     * @param corpId
     * @param userId 通讯录相关客户ID
     * @return 通讯录相关客户
     */
    @Override
    public WeUser selectWeUserById(String corpId, String userId) {
        return weUserMapper.selectWeUserById(corpId, userId);
    }

    @Override
    public WeUser getUserDetail(String corpId, String userId) {
        return weUserMapper.getUserDetail(corpId, userId);
    }

    /**
     * 查询员工列表（ 校验数据范围)
     *
     * @param weUser 通讯录相关客户
     * @return 通讯录相关客户
     */
    @Override
    @DataScope
    public List<WeUser> selectWeUserList(WeUser weUser) {
        return this.selectBaseList(weUser);

    }

    /**
     * 查询员工简略信息(名字和id)列表 (不校验数据范围)
     *
     * @param weUser
     * @return
     */
    @Override
    public List<WeUserBriefInfoVO> selectWeUserBriefInfo(WeUser weUser) {
        List<WeUser> list = this.selectBaseList(weUser);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(WeUserBriefInfoVO::new).collect(Collectors.toList());
    }

    /**
     * 查询员工列表
     *
     * @param weUser
     * @return
     */
    private List<WeUser> selectBaseList(WeUser weUser) {
        String[] department = weUser.getDepartment();
        if (ArrayUtil.isNotEmpty(department)) {
            weUser.setDepartmentStr(StringUtils.join(department, ","));
        }
        return weUserMapper.selectWeUserList(weUser);

    }

    /**
     * 新增通讯录相关客户
     *
     * @param weUser 通讯录相关客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertWeUser(WeUser weUser) {
        if (weUser == null || StringUtils.isAnyBlank(weUser.getCorpId(), weUser.getUserId()) || weUser.getRoleId() == null || weUser.getMainDepartment() == null) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 1. 判断角色是否存在
        SysRole role = weUserRoleMapper.selectByRoleId(weUser.getCorpId(), weUser.getRoleId());
        if (role == null) {
            throw new CustomException(ResultTip.TIP_ROLE_NOT_EXIST);
        }
        // 设置上级关系
        String isLeader = WeConstans.corpUserEnum.IS_DEPARTMENT_SUPERIOR_NO.getKey().toString();
        if (role.getRoleType() != null && RoleTypeEnum.SYS_ADMIN.getType().equals(role.getRoleType())) {
            isLeader = WeConstans.corpUserEnum.IS_DEPARTMENT_SUPERIOR_YES.getKey().toString();
        }
        String[] isLeaderArr = new String[]{isLeader};
        weUser.setIsLeaderInDept(isLeaderArr);
        // 2. 判断部门是否存在
        WeDepartment department = weDepartmentService.getOne(new LambdaQueryWrapper<WeDepartment>().eq(WeDepartment::getCorpId, weUser.getCorpId()).eq(WeDepartment::getId, weUser.getMainDepartment()));
        if (department == null) {
            throw new CustomException(ResultTip.TIP_DEPARTMENT_NOT_EXIST);
        }
        String[] deptArr = new String[]{String.valueOf(weUser.getMainDepartment())};
        weUser.setDepartment(deptArr);
        // 3.上传头像到临时素材库
        WeUserDTO createUserReq = new WeUserDTO(weUser);
        if (StringUtils.isNotBlank(weUser.getAvatarMediaid())) {
            String fileName = weUser.getName() + "headImg";
            WeMediaDTO resp = weMaterialService.uploadTemporaryMaterial(weUser.getAvatarMediaid(), GroupMessageType.IMAGE.getMessageType(), fileName, weUser.getCorpId());
            if (resp == null || StringUtils.isBlank(resp.getMedia_id())) {
                throw new CustomException(ResultTip.TIP_ERROR_UPLOAD_HEAD_IMG);
            }
            // 设置请求参数的media id
            createUserReq.setAvatar_mediaid(resp.getMedia_id());
            // 设置用户的头像url
            weUser.setAvatarMediaid(resp.getUrl());
        }
        // 4. 构建员工-角色实体
        WeUserRole weUserRole = WeUserRole.builder().corpId(weUser.getCorpId()).userId(weUser.getUserId()).roleId(weUser.getRoleId()).build();
        // 5.插入员工和角色信息,调用企微API创建员工
        if (this.insertWeUserNoToWeCom(weUser) > 0 && weUserRoleMapper.insertUserRole(weUserRole) > 0) {
            weUserClient.createUser(createUserReq, weUser.getCorpId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWeUserNoToWeCom(WeUser weUser) {
        WeUser weUserInfo = weUserMapper.selectWeUserById(weUser.getCorpId(), weUser.getUserId());
        initRoleByDepartmentAndLeader(weUser);
        if (weUserInfo != null) {
            Date dimissionTime = weUserInfo.getDimissionTime();
            //是否离职过 离职时间和离职继承
            boolean hasLeft = StaffActivateEnum.DELETE.getCode().equals(weUserInfo.getIsActivate()) || StaffActivateEnum.RETIRE.getCode().equals(weUserInfo.getIsActivate());
            if (hasLeft && dimissionTime != null) {
                weUser.setDimissionTime(null);
            }
            return weUserMapper.updateWeUser(weUser);
        }
        return weUserMapper.insertWeUser(weUser);

    }

    /**
     * 修改员工
     *
     * @param weUser 员工
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeUser(WeUser weUser) {
        if (this.updateWeUserNoToWeCom(weUser) > 0) {
            //如果是私有云服务器(自建应用)才能修改企业微信的员工信息
            CheckCorpIdVO checkCorpIdVO = weAuthCorpInfoService.isDkCorp(weUser.getCorpId());
            if (checkCorpIdVO != null && !checkCorpIdVO.isDkCorp()) {
                weUserClient.updateUser(new WeUserDTO(weUser), weUser.getCorpId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWeUserNoToWeCom(WeUser weUser) {
        WeUser weUserInfo = weUserMapper.selectWeUserById(weUser.getCorpId(), weUser.getUserId());
        if (weUserInfo == null) {
            return weUserMapper.insertWeUser(weUser);
        } else {
            return weUserMapper.updateWeUser(weUser);
        }
    }

    /**
     * 从企业微信获取员工数据更新到本地
     *
     * @param userId 用户ID
     * @param corpId 企业ID
     * @return 受影响行
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWeUserDataFromWeCom(String userId, String corpId) {
        WeUserDTO weUserDTO = weUserClient.getUserByUserId(userId, corpId);
        WeUser weUser = weUserDTO.transferToWeUser();
        weUser.setCorpId(corpId);
        return this.updateWeUserNoToWeCom(weUser);
    }


    /**
     * 启用或禁用用户
     *
     * @param weUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(WeUser weUser) {
        if (WeConstans.WE_USER_STOP.equals(weUser.getEnable())) {
            weUser.setIsActivate(WeConstans.WE_USER_IS_FORBIDDEN);
        } else {
            weUser.setIsActivate(WeConstans.WE_USER_IS_ACTIVATE);
        }
        this.updateWeUser(weUser);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void syncWeUser(String corpId, LoginUser loginUser) {
        syncWeUser(corpId);
        try {
            //如果三方用户信息
            if (ruoYiConfig.isThirdServer() && loginUser.getWeUser() != null && loginUser.getWeUser().isExternalUser()) {
                WeExternalUserMappingUser mapping = weExternalUserMappingUserService.getMappingByExternal(loginUser.getWeUser().getExternalCorpId(), loginUser.getWeUser().getExternalUserId());
                if (mapping != null && StringUtils.isNoneBlank(mapping.getCorpId(), mapping.getUserId())) {
                    WeUser currentLoginUser = weUserMapper.getUserDetail(mapping.getCorpId(), mapping.getUserId());
                    if (currentLoginUser != null) {
                        currentLoginUser.setExternalUserId(loginUser.getWeUser().getExternalUserId());
                        currentLoginUser.setExternalCorpId(loginUser.getWeUser().getExternalCorpId());
                        loginUser.setWeUser(currentLoginUser);
                        // 刷新当前用户
                        LoginTokenService.refreshWeUser(loginUser);
                        // 刷新数据权限
                        LoginTokenService.refreshDataScope(loginUser);
                    }
                }
            }
        } catch (Exception e) {
            log.error("三方应用刷新当前登录人信息出现异常，企业ID：{}，用户：{}，异常原因：{}", loginUser.getCorpId(), loginUser.getUsername(), ExceptionUtils.getStackTrace(e));
        }
        // 同步员工后刷新数据概览页数据
        pageHomeService.getUserData(corpId);
    }

    @Override
    public void syncWeUser(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            log.info("corpId为空,无法同步企业成员");
            return;
        }
        // 先同步离职成员(由于如果成员离职后再进入企业,并且存在待分配的离职客户时,拉取离职员工的时候会拉取到该成员并修改状态为离职,所以此处先同步离职员工后,再同步在职成员)
        try {
            SpringUtils.getBean(WeUserService.class).syncWeLeaveUserV2(corpId);
        } catch (Exception e) {
            log.error("同步离职员工异常corpId:{},E:{}", corpId, ExceptionUtils.getStackTrace(e));
        }
        log.info("开始同步成员,corpId:{}", corpId);
        List<WeUser> visibleUser = this.getVisibleUser(corpId);
        if (CollUtil.isEmpty(visibleUser)) {
            log.info("[同步员工]该企业没有可见的部门和员工,corpId:{}", corpId);
            return;
        }
        List<WeUser> exitsUsers = weUserMapper.selectList(new LambdaQueryWrapper<WeUser>().eq(WeUser::getCorpId, corpId).ne(WeUser::getIsActivate, WeConstans.WE_USER_IS_LEAVE));
        //删除已离职的员工
        Map<String, String> userMap = visibleUser.stream().collect(Collectors.toMap(WeUser::getUserId, WeUser::getUserId));
        //提前创建映射关系创建内外部应用员工映射关系
        weExternalUserMappingUserService.createMapping(corpId, new ArrayList<>(userMap.keySet()));
        List<String> delIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(exitsUsers)) {
            for (WeUser eUser : exitsUsers) {
                if (!userMap.containsKey(eUser.getUserId())) {
                    delIds.add(eUser.getUserId());
                }
            }
        }
        deleteUsersNoToWeCom(corpId, delIds);
        //添加或更新员工
        for (WeUser weUser : visibleUser) {
            weUser.setCorpId(corpId);
            insertWeUserNoToWeCom(weUser);
        }
        log.info("同步成员完成,corpId:{},本次同步成员数：{}", corpId, visibleUser.size());
        // 同步员工后刷新数据概览页数据
        pageHomeService.getUserData(corpId);
    }

    @Override
    public List<WeUser> getVisibleUser(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return Collections.emptyList();
        }
        // 获取agentId
        WeCorpAccount corpAccount = weCorpAccountService.findValidWeCorpAccount(corpId);
        if (corpAccount == null || StringUtils.isBlank(corpAccount.getAgentId())) {
            return Collections.emptyList();
        }
        Set<WeUser> visibleUsers = new HashSet<>();
        // 获取应用详情（可见员工+ 可见部门)
        GetAgentResp resp = weAgentClient.getAgent(corpAccount.getAgentId(), corpId);
        // 根据可见部门获取员工
        if (CollectionUtils.isNotEmpty(resp.getAllow_partys().getPartyid())) {
            for (Integer party : resp.getAllow_partys().getPartyid()) {
                List<WeUser> tempList = weUserClient.list(Long.valueOf(party), WeConstans.DEPARTMENT_SUB_WEUSER, corpId).getWeUsers();
                if (CollectionUtils.isNotEmpty(tempList)) {
                    visibleUsers.addAll(tempList);
                }
            }
        }
        // 根据可见员工去获取员工详情
        if (CollectionUtils.isNotEmpty(resp.getAllow_userinfos().getUser())) {
            for (GetAgentResp.User user : resp.getAllow_userinfos().getUser()) {
                WeUserDTO dto = weUserClient.getUserByUserId(user.getUserid(), corpId);
                if (dto != null) {
                    visibleUsers.add(dto.transferToWeUser());
                }
            }
        }
        return new ArrayList<>(visibleUsers);
    }


    @Override
    public void syncWeLeaveUserV2(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            log.info("[同步离职员工]缺失企业ID,corpId:{}", corpId);
            return;
        }
        log.info("同步离职待分配员工数据V2开始,corpId:{}", corpId);
        // 1. 通过企微API获取待分配客户群 (过滤条件：状态为待继承)
        GroupChatListReq groupReq = GroupChatListReq.builder().status_filter(GroupConstants.OWNER_LEAVE).build();
        GroupChatListResp groupResp = (GroupChatListResp) groupReq.executeTillNoNextPage(corpId);
        List<String> chatIdList = groupResp.getChatIdList();
        // 2.更新客户群状态为:离职待分配
        if (CollectionUtils.isNotEmpty(chatIdList)) {
            WeGroup entity = new WeGroup();
            entity.setStatus(GroupConstants.OWNER_LEAVE);
            weGroupService.update(entity, new LambdaUpdateWrapper<WeGroup>().eq(WeGroup::getCorpId, corpId).in(WeGroup::getChatId, chatIdList));
        }
        // 3. 获取所有离职员工的待分配客户和员工详情
        GetUnassignedListReq req = new GetUnassignedListReq();
        GetUnassignedListResp resp = (GetUnassignedListResp) req.executeTillNoNextPage(corpId);
        resp.handleData(corpId);
        if (CollectionUtils.isEmpty(resp.getTotalList())) {
            log.info("[同步离职员工]本次没有同步到离职待分配员工corpId:{},resp:{}", corpId, resp);
            return;
        }
        // 4.更新离职员工状态和离职时间
        List<WeUser> updateUserList = resp.getUpdateUserList();
        if (CollectionUtils.isNotEmpty(updateUserList)) {
            weUserMapper.batchUpdateWeUser(updateUserList);
        }
        // 5. 更新离职员工的客户关系状态
        List<WeFlowerCustomerRel> relList = resp.getRelList();
        if (CollectionUtils.isNotEmpty(relList)) {
            weFlowerCustomerRelService.batchUpdateStatus(relList);
        }
        log.info("同步离职员工数据V2结束,corpId:{}", corpId);
    }


    /**
     * 删除用户
     *
     * @param corpId
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String corpId, String[] ids) {
        if (StringUtils.isBlank(corpId) || ArrayUtil.isEmpty(ids)) {
            return;
        }
        List<WeUser> weUsers = new ArrayList<>();
        CollUtil.newArrayList(ids).forEach(id -> weUsers.add(WeUser.builder().corpId(corpId).userId(id).isActivate(WeConstans.WE_USER_IS_LEAVE).dimissionTime(new Date()).build()));

        if (this.baseMapper.batchUpdateWeUser(weUsers) > 0) {
            weUsers.forEach(weUser -> weUserClient.deleteUserByUserId(weUser.getUserId(), corpId));
        }
        // 删除后过一段时间 自动同步离职成员
        redisCache.setCacheObject(RedisKeyConstants.DELETE_USER_KEY + corpId, corpId, 30, TimeUnit.SECONDS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserNoToWeCom(String userId, String corpId) {
        if (StringUtils.isAnyBlank(userId, corpId)) {
            log.error("客户id和企业id不能为空");
            return 0;
        }
        WeUser weUser = WeUser.builder().userId(userId).isActivate(WeConstans.WE_USER_IS_LEAVE).corpId(corpId).dimissionTime(new Date()).build();
        return weUserMapper.update(weUser, new LambdaQueryWrapper<WeUser>().eq(WeUser::getUserId, userId).eq(WeUser::getCorpId, corpId));
    }

    private Map<String, WeFlowerCustomerRel> getWeCustomerMap(String corpId, WeUser weUser) {
        if (StringUtils.isBlank(corpId) || weUser == null) {
            return new HashMap<>(1);
        }
        //获取所有数据不管是否流失
        List<WeFlowerCustomerRel> weFlowerCustomers = weFlowerCustomerRelService.list(new LambdaQueryWrapper<WeFlowerCustomerRel>().eq(WeFlowerCustomerRel::getCorpId, corpId).eq(WeFlowerCustomerRel::getUserId, weUser.getUserId()));

        Map<String, WeFlowerCustomerRel> map = new HashMap<>(weFlowerCustomers.size());
        for (WeFlowerCustomerRel weFlowerCustomerRel : weFlowerCustomers) {
            if (map.containsKey(weFlowerCustomerRel.getExternalUserid())) {
                continue;
            }
            map.put(weFlowerCustomerRel.getExternalUserid(), weFlowerCustomerRel);
        }
        return map;
    }


    @Override
    public void deleteUsersNoToWeCom(String corpId, List<String> ids) {
        List<WeUser> weUsers = new ArrayList<>();
        ids.forEach(id -> weUsers.add(WeUser.builder().corpId(corpId).userId(id).isActivate(WeConstans.WE_USER_IS_LEAVE).dimissionTime(new Date()).build()));
        if (CollUtil.isNotEmpty(weUsers)) {
            weUserMapper.batchUpdateWeUser(weUsers);
        }
    }

    @Override
    public WeUserInfoVO getUserInfo(String code, String agentId, String corpId) {
        String cacheKey = "USER_INFO:" + corpId + ":" + agentId + ":" + code;
        WeUserInfoDTO getuserinfo = redisCache.getCacheObject(cacheKey);
        if (ObjectUtils.isEmpty(getuserinfo)) {
            getuserinfo = weUserClient.getuserinfo(code, agentId, corpId);
            redisCache.setCacheObject(cacheKey, getuserinfo, 5, TimeUnit.MINUTES);
        }
        return WeUserInfoVO.builder().userId(getuserinfo.getUserId()).deviceId(getuserinfo.getDeviceId()).externalUserId(getuserinfo.getExternal_userid()).openId(getuserinfo.getOpenId()).build();

    }

    @Override
    public List<WeCustomerAddUser> findWeUserByCutomerId(String corpId, String externalUserid) {
        return this.baseMapper.findWeUserByCutomerId(corpId, externalUserid);
    }

    @Override
    public void batchInitRoleByDepartmentAndLeader(List<WeUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        // 公司ID
        String corpId = userList.get(0).getCorpId();
        if (StringUtils.isBlank(corpId)) {
            log.info("初始化员工角色:corpId为空,无法初始化{}", userList.get(0));
            return;
        }
        // 默认角色id的映射(key: 角色KEY , VALUE: 角色ID )
        // 有员工需要初始化时再主动获取并做缓存,避免每次同步员工都要查询3个默认角色id
        HashMap<String, Long> roleIdMap = new HashMap<>(16);
        // 是否是根部门的员工
        boolean isInRootDepartment;
        // 是否是领导
        boolean isLeader;
        List<WeUserRole> needToInitRoleList = new ArrayList<>();
        for (WeUser user : userList) {
            // 已有角色的员工无需初始化 , corpId异常的员工不处理
            if (ObjectUtil.isNotNull(user.getRoleId()) || !corpId.equals(user.getCorpId())) {
                continue;
            }
            Long initRoleId;
            isInRootDepartment = isInRootDepartment(user.getMainDepartment());
            isLeader = isLeaderInMainDepartment(user.getDepartment(), user.getIsLeaderInDept(), user.getMainDepartment());
            // 根据部门和是否是上级初始化角色
            // 在根部门并且是上级初始化 成管理员角色：非根部门并且是上级初始化成部门管理员 ; 其他情况皆初始化成员工角色
            if (isInRootDepartment && isLeader) {
                initRoleId = getInitRoleIdByKey(corpId, roleIdMap, UserConstants.INIT_ADMIN_ROLE_KEY);
            } else if (isLeader) {
                initRoleId = getInitRoleIdByKey(corpId, roleIdMap, UserConstants.INIT_DEPARTMENT_ADMIN_ROLE_KEY);
            } else {
                initRoleId = getInitRoleIdByKey(corpId, roleIdMap, UserConstants.INIT_EMPLOYEE_ROLE_KEY);
            }
            //这边增加三方应用初始化管理员判断
            if (ruoYiConfig.isThirdServer()) {
                initRoleId = initRole4ThirdServer(corpId, user, roleIdMap);
            }
            if (initRoleId != null) {
                needToInitRoleList.add(new WeUserRole(user.getCorpId(), user.getUserId(), initRoleId));
            }
        }
        //批量插入 用户-角色表
        if (CollUtil.isNotEmpty(needToInitRoleList)) {
            weUserRoleMapper.batchInsertUserRole(needToInitRoleList);
            log.info("为员工初始化角色完成,成功个数:{},corpId:{}", needToInitRoleList.size(), corpId);
        }
    }

    /**
     * 为三方用户初始化角色
     *
     * @param corpId    企业id
     * @param user      企微用户
     * @param roleIdMap 映射 (key: 角色KEY , VALUE: 角色ID )
     * @return 初始化的角色id
     */
    public Long initRole4ThirdServer(String corpId, WeUser user, HashMap<String, Long> roleIdMap) {
        if (StringUtils.isBlank(user.getExternalCorpId())) {
            WeCorpAccount weCorpAccount = weCorpAccountService.findValidWeCorpAccount(user.getCorpId());
            user.setExternalCorpId(weCorpAccount.getExternalCorpId());
        }
        Map<String, Integer> adminMap = new HashMap<>();
        if (StringUtils.isNotBlank(user.getExternalCorpId())) {
            adminMap = we3rdAppService.getAdminList(user.getExternalCorpId());
        }
        WeExternalUserMappingUser weExternalUserMappingUser = weExternalUserMappingUserService.getMappingByInternal(user.getCorpId(), user.getUserId());
        if (weExternalUserMappingUser != null && StringUtils.isNoneBlank(weExternalUserMappingUser.getExternalCorpId(), weExternalUserMappingUser.getCorpId(), weExternalUserMappingUser.getUserId(), weExternalUserMappingUser.getExternalUserId())) {
            user.setExternalCorpId(weExternalUserMappingUser.getExternalCorpId());
            user.setCorpId(weExternalUserMappingUser.getCorpId());
            user.setUserId(weExternalUserMappingUser.getUserId());
            user.setExternalUserId(weExternalUserMappingUser.getExternalUserId());
        }
        if (ObjectUtil.isNotEmpty(adminMap) && adminMap.containsKey(user.getExternalUserId())) {
            return getInitRoleIdByKey(corpId, roleIdMap, UserConstants.INIT_ADMIN_ROLE_KEY);
        }
        return getInitRoleIdByKey(corpId, roleIdMap, UserConstants.INIT_EMPLOYEE_ROLE_KEY);
    }

    /**
     * 根据roleKey获取roleId ,
     * 如果映射中存在则直接返回 ,如果映射中不存在则查询数据库后返回并缓存
     *
     * @param corpId    公司ID
     * @param roleIdMap 角色ID映射Map
     * @param roleKey   角色Key
     * @return 对应的角色ID
     */
    private Long getInitRoleIdByKey(String corpId, HashMap<String, Long> roleIdMap, String roleKey) {
        return roleIdMap.computeIfAbsent(roleKey, key -> weUserRoleService.selectRoleIdByCorpIdAndRoleKey(corpId, roleKey));
    }

    /**
     * 判断成员是否在根部门
     *
     * @param mainDepartment 用户的根部门
     * @return true 是根部门 false 不是根部门
     */
    private boolean isInRootDepartment(Long mainDepartment) {
        return !ObjectUtil.isNull(mainDepartment) && WeConstans.WE_ROOT_DEPARMENT_ID.equals(mainDepartment);
    }

    /**
     * 判断是否是主部门的管理员
     *
     * @param department     部门ID数组 包含员工所有部门ID, 例： [7,1,8]
     * @param isLeaderInDept 是否是部门管理员数组(1是0否) 与部门数组一一对应,例：[0,0,0]
     * @param mainDepartment 主部门ID
     * @return true 是主部门的管理员  false 不是主部门管理员
     */
    private boolean isLeaderInMainDepartment(String[] department, String[] isLeaderInDept, Long mainDepartment) {
        // 参数校验
        if (ObjectUtil.isNull(mainDepartment) || ArrayUtil.isEmpty(department) || ArrayUtil.isEmpty(isLeaderInDept)) {
            return false;
        }
        // 若部门和上级数组长度不等 表明数据异常,不做判断处理
        if (department.length != isLeaderInDept.length) {
            return false;
        }
        // 单部门情况：直接通过isLeaderInDept数组的第一个元素  判断是否是上级
        if (department.length == 1) {
            return WeConstans.corpUserEnum.IS_DEPARTMENT_SUPERIOR_YES.getKey().toString().equals(isLeaderInDept[0]);
        }
        // 多部门情况：遍历所有部门,并判断当部门为主部门时 对应索引的isLeader数组值是否为1 (是否是上级)
        for (int i = 0; i < department.length; i++) {
            if (!mainDepartment.toString().equals(department[i])) {
                continue;
            }
            return WeConstans.corpUserEnum.IS_DEPARTMENT_SUPERIOR_YES.getKey().toString().equals(isLeaderInDept[i]);
        }
        return false;
    }

    @Override
    public void initRoleByDepartmentAndLeader(WeUser weUser) {
        List<WeUser> list = new ArrayList<>();
        list.add(weUser);
        batchInitRoleByDepartmentAndLeader(list);
    }

    @Override
    public List<WeUser> getUserInDataScope(LoginUser loginUser) {
        if (ObjectUtil.isNull(loginUser) || StringUtils.isBlank(loginUser.getCorpId()) || StringUtils.isBlank(loginUser.getDepartmentDataScope())) {
            return Collections.emptyList();
        }
        String corpId = loginUser.getCorpId();
        // 获取登录用户缓存中该用户的所有部门权限(格式：1,2,3)
        String dataScope = loginUser.getDepartmentDataScope();
        // 缺省部门ID集合
        String[] array = {"-1"};
        try {
            array = dataScope.split(",");
        } catch (Exception e) {
            log.info("获取当前登录用户可操作的所有企业成员:可见部门格式错误,user:{},e:{}", loginUser, ExceptionUtils.getStackTrace(e));
        }
        // 根据用户有权限的所有部门 查询该企业下指定部门的所有成员
        return weUserMapper.getUserByDepartmentList(corpId, array);
    }

    @Override
    public String getJoinQrCode(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        GetJoinQrCodeResp resp = weUserClient.getJoinQrCode(corpId);
        if (resp == null || StringUtils.isBlank(resp.getJoin_qrcode())) {
            throw new CustomException(ResultTip.TIP_FAIL_GET_JOIN_CORP_QRCODE);
        }
        return resp.getJoin_qrcode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchUpdateUserInfoVO batchUpdateUserInfo(BatchUpdateUserInfoDTO updateUserInfo) {
        //校验请求参数
        verifyParam(updateUserInfo);
        Integer type = updateUserInfo.getType();
        String corpId = updateUserInfo.getCorpId();
        if (BatchUpdateUserInfoTypeEnum.ROLE.getType().equals(type)) {
            return batchUpdateUserRole(corpId, updateUserInfo.getUserIdList(), updateUserInfo.getRoleId());
        } else if (BatchUpdateUserInfoTypeEnum.POSITION.getType().equals(type)) {
            return batchUpdateUserInfo(corpId, updateUserInfo.getUserIdList(), updateUserInfo.getPosition(), null);
        } else {
            return batchUpdateUserInfo(corpId, updateUserInfo.getUserIdList(), null, updateUserInfo.getDepartment());
        }
    }

    /**
     * 批量修改员工职位
     *
     * @param corpId     企业ID
     * @param userIdList 员工列表
     * @param position   职位
     * @return BatchUpdateUserInfoVO
     */
    private BatchUpdateUserInfoVO batchUpdateUserInfo(String corpId, List<UpdateUserInfoDetailDTO> userIdList, String position, Long department) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(userIdList) || StringUtils.isBlank(position) && department == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        int successCount = 0;
        int failCount = 0;
        WeResultDTO weResultDTO;
        StringBuilder failMsg = new StringBuilder();
        WeUserDTO weUserDTO;
        String[] departArr = department != null ? new String[]{department.toString()} : null;
        List<String> successUserIdList = new ArrayList<>();
        for (UpdateUserInfoDetailDTO detailDTO : userIdList) {
            if (StringUtils.isBlank(detailDTO.getUserId())) {
                continue;
            }
            weUserDTO = new WeUserDTO();
            weUserDTO.setUserid(detailDTO.getUserId());
            if (StringUtils.isNotBlank(position)) {
                weUserDTO.setPosition(position);
            } else {
                weUserDTO.setDepartment(departArr);
            }
            try {
                weUserClient.updateUser(weUserDTO, corpId);
                successUserIdList.add(detailDTO.getUserId());
                successCount++;
            } catch (Exception e) {
                failCount++;
                weResultDTO = JSONObject.parseObject(e.getMessage(), WeResultDTO.class);
                failMsg.append("员工：").append(detailDTO.getUserName()).append(", 失败原因：").append(weResultDTO.getErrmsg()).append("\n");
            }
        }
        //批量更新员工职位/部门
        if (CollectionUtils.isNotEmpty(successUserIdList)) {
            weUserMapper.batchUpdateWeUserPositionOrDepartment(corpId, successUserIdList, position, department);
        }

        //上传文件
        String fileUrl = getFileMsgUrl(failMsg.toString());
        return new BatchUpdateUserInfoVO(successCount, failCount, fileUrl);
    }

    /**
     * 上传到云文件
     *
     * @param failMsg 异常信息
     * @return 文件链接
     */
    private String getFileMsgUrl(String failMsg) {
        String fileUrl = null;
        //上传文件
        if (StringUtils.isNotBlank(failMsg)) {
            String fileName = System.currentTimeMillis() + "修改员工信息异常报告." + GenConstants.SUFFIX_TXT;
            try {
                String url = FileUploadUtils.upload2Cos(new ByteArrayInputStream(failMsg.getBytes(StandardCharsets.UTF_8)), fileName, GenConstants.SUFFIX_TXT, ruoYiConfig.getFile().getCos());
                fileUrl = ruoYiConfig.getFile().getCos().getCosImgUrlPrefix() + url;
            } catch (Exception e) {
                log.error("batchUpdateUserInfoPosition error! {}", ExceptionUtils.getStackTrace(e));
            }
        }
        return fileUrl;
    }


    /**
     * 批量更新员工角色
     *
     * @param corpId     企业ID
     * @param userIdList 员工列表
     * @param roleId     角色ID
     * @return BatchUpdateUserInfoVO
     */
    private BatchUpdateUserInfoVO batchUpdateUserRole(String corpId, List<UpdateUserInfoDetailDTO> userIdList, Long roleId) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(userIdList) || roleId == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        List<WeUserRole> list = new ArrayList<>();
        WeUserRole weUserRole;
        for (UpdateUserInfoDetailDTO detailDTO : userIdList) {
            if (StringUtils.isBlank(detailDTO.getUserId())) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
            weUserRole = new WeUserRole(corpId, detailDTO.getUserId(), roleId);
            list.add(weUserRole);
        }
        weUserRoleMapper.batchInsertOrUpdateUserRole(list);
        return new BatchUpdateUserInfoVO(list.size(), 0, null);
    }

    /**
     * 校验请求参数
     *
     * @param batchUpdateUserInfoDTO batchUpdateUserInfoDTO
     */
    private void verifyParam(BatchUpdateUserInfoDTO batchUpdateUserInfoDTO) {
        if (batchUpdateUserInfoDTO == null || StringUtils.isBlank(batchUpdateUserInfoDTO.getCorpId()) || batchUpdateUserInfoDTO.getType() == null || CollectionUtils.isEmpty(batchUpdateUserInfoDTO.getUserIdList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        //校验不同操作类型的请求字段值
        Integer type = batchUpdateUserInfoDTO.getType();
        if (BatchUpdateUserInfoTypeEnum.ROLE.getType().equals(type)) {
            if (batchUpdateUserInfoDTO.getRoleId() == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        } else if (BatchUpdateUserInfoTypeEnum.POSITION.getType().equals(type)) {
            if (StringUtils.isBlank(batchUpdateUserInfoDTO.getPosition())) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        } else if (BatchUpdateUserInfoTypeEnum.DEPARTMENT.getType().equals(type)) {
            if (batchUpdateUserInfoDTO.getDepartment() == null) {
                throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
            }
        } else {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
    }

    @Override
    public void validateActiveUser(String corpId, String userId) {
        WeUser user = this.getOne(new LambdaQueryWrapper<WeUser>().eq(WeUser::getCorpId, corpId).eq(WeUser::getUserId, userId).last(GenConstants.LIMIT_1));
        if (user == null || !StaffActivateEnum.ACTIVE.getCode().equals(user.getIsActivate())) {
            throw new CustomException(ResultTip.TIP_USER_NOT_ACTIVE);
        }
    }

    @Override
    @DataScope
    public List<TransferResignedUserVO> leaveUserListV3(TransferResignedUserListDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId()) || dto.getIsAllocate() == null) {
            throw new CustomException(ResultTip.TIP_GENERAL_PARAM_ERROR);
        }
        List<TransferResignedUserVO> list = weUserMapper.leaveUserListV3(dto);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().filter(a -> a.getAllocateGroupNum() > 0 || a.getAllocateCustomerNum() > 0).collect(Collectors.toList());

    }

    /**
     * 通过员工映射关系获得员工
     *
     * @param externalUserId 外部员工id
     * @param externalCorpId 外部企业id
     * @return {@link WeUser}
     */
    @Override
    public WeUser getWeUserByExternalMapping(String externalUserId, String externalCorpId) {
        WeExternalUserMappingUser weExternalUserMappingUser = weExternalUserMappingUserService.getMappingByExternal(externalCorpId, externalUserId);
        if (weExternalUserMappingUser != null && StringUtils.isNoneBlank(weExternalUserMappingUser.getUserId(), weExternalUserMappingUser.getCorpId())) {
            WeUserDTO weUserDTO = weUserClient.getUserByUserId(weExternalUserMappingUser.getUserId(), weExternalUserMappingUser.getCorpId());
            if (weUserDTO == null) {
                return null;
            }
            WeUser weUser = weUserDTO.transferToWeUser();
            weUser.setExternalCorpId(externalCorpId);
            weUser.setExternalUserId(externalUserId);
            weUser.setCorpId(weExternalUserMappingUser.getCorpId());
            this.insertWeUserNoToWeCom(weUser);
            weUser.setDepartmentName(weDepartmentService.selectNameByUserId(weExternalUserMappingUser.getCorpId(), weExternalUserMappingUser.getUserId()));
            return weUser;
        } else {
            return null;
        }
    }

}
