package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.CosConfig;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.constant.WeConstans;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.MessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.WeWordsCategoryTypeEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.WeWordsCategory;
import com.easyink.wecom.domain.WeWordsDetailEntity;
import com.easyink.wecom.domain.WeWordsGroupEntity;
import com.easyink.wecom.domain.dto.*;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.vo.FindExistWordsCategoryNameList;
import com.easyink.wecom.domain.vo.WeWordsImportVO;
import com.easyink.wecom.domain.vo.WeWordsUrlVO;
import com.easyink.wecom.domain.vo.WeWordsVO;
import com.easyink.wecom.mapper.WeWordsDetailMapper;
import com.easyink.wecom.mapper.WeWordsGroupMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeWordsCategoryService;
import com.easyink.wecom.service.WeWordsDetailService;
import com.easyink.wecom.service.WeWordsGroupService;
import com.easyink.wecom.utils.JsoupUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 类名： 话术库主表接口
 *
 * @author 佚名
 * @date 2021/10/27 17:56
 */
@Service("weWordsGroupService")
@Slf4j
public class WeWordsGroupServiceImpl extends ServiceImpl<WeWordsGroupMapper, WeWordsGroupEntity> implements WeWordsGroupService {
    private final WeWordsDetailService weWordsDetailService;
    private final WeWordsGroupMapper weWordsGroupMapper;
    private final WeWordsDetailMapper weWordsDetailMapper;
    private final WeWordsCategoryService weWordsCategoryService;
    private final WeMessagePushClient messagePushClient;
    private final WeCorpAccountService corpAccountService;
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Autowired
    public WeWordsGroupServiceImpl(WeWordsDetailService weWordsDetailService, WeWordsGroupMapper weWordsGroupMapper, WeWordsDetailMapper weWordsDetailMapper, WeWordsCategoryService weWordsCategoryService, WeMessagePushClient messagePushClient, WeCorpAccountService corpAccountService) {
        this.weWordsDetailService = weWordsDetailService;
        this.weWordsGroupMapper = weWordsGroupMapper;
        this.weWordsDetailMapper = weWordsDetailMapper;
        this.weWordsCategoryService = weWordsCategoryService;
        this.messagePushClient = messagePushClient;
        this.corpAccountService = corpAccountService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(WeWordsDTO weWordsDTO) {
        StringUtils.checkCorpId(weWordsDTO.getCorpId());
        //参数校验
        checkWordsDTO(weWordsDTO);
        //构造话术主体
        WeWordsGroupEntity weWordsGroupEntity = new WeWordsGroupEntity();
        BeanUtils.copyProperties(weWordsDTO, weWordsGroupEntity);
        weWordsGroupEntity.setSeq(new String[]{});
        if (weWordsGroupEntity.getIsPush() == null) {
            weWordsGroupEntity.setIsPush(Boolean.TRUE);
        }
        if (weWordsGroupEntity.getTitle() == null) {
            weWordsGroupEntity.setTitle(StringUtils.EMPTY);
        }
        //保存话术主表
        weWordsGroupMapper.insert(weWordsGroupEntity);
        //设置排序号
        weWordsGroupEntity.setSort(weWordsGroupEntity.getId());
        List<WeWordsDetailEntity> weWordsDetailEntities = weWordsDTO.getWeWordsDetailList();
        //设置主表id
        weWordsDetailEntities.forEach(weWordsDetailEntity -> {
            weWordsDetailEntity.setGroupId(weWordsGroupEntity.getId());
            weWordsDetailEntity.setCorpId(weWordsGroupEntity.getCorpId());
        });
        wordsHandler(weWordsGroupEntity, weWordsDetailEntities);
        //发消息通知员工
        if (weWordsGroupEntity.getIsPush()) {
            sendToUser(weWordsDTO);
        }
    }

    @Override
    public WeWordsGroupEntity get(Integer id) {
        return weWordsGroupMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WeWordsDTO weWordsDTO) {
        //参数校验
        checkWordsDTO(weWordsDTO);
        WeWordsGroupEntity weWordsGroupEntity = new WeWordsGroupEntity();
        BeanUtils.copyProperties(weWordsDTO, weWordsGroupEntity);
        StringUtils.checkCorpId(weWordsGroupEntity.getCorpId());

        List<WeWordsDetailEntity> weWordsDetailEntities = weWordsDTO.getWeWordsDetailList();
        weWordsDetailEntities.forEach(weWordsDetailEntity -> {
            weWordsDetailEntity.setCorpId(weWordsGroupEntity.getCorpId());
            weWordsDetailEntity.setGroupId(weWordsDTO.getId());
        });
        wordsHandler(weWordsGroupEntity, weWordsDetailEntities);
        //删除多余的附件
        if (CollectionUtils.isNotEmpty(weWordsDTO.getWordsDetailIds())) {
            weWordsDetailMapper.deleteBatchIds(weWordsDTO.getWordsDetailIds());
        }
        //发消息通知员工
        if (weWordsGroupEntity.getIsPush()) {
            sendToUser(weWordsDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (CollectionUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_GROUP_ID);
        }
        //删除主表
        weWordsGroupMapper.deleteBatchIds(ids, corpId);
        //删除附件
        weWordsDetailMapper.deleteByGroupIds(ids, corpId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCategoryId(List<Long> categoryIds, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (CollectionUtils.isEmpty(categoryIds)) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_CATEGORY_ID);
        }
        List<Long> groupIds = weWordsGroupMapper.listOfCategoryId(categoryIds, corpId);
        if (CollectionUtils.isNotEmpty(groupIds)) {
            delete(groupIds, corpId);
        }
    }

    /**
     * 查询话术库
     *
     * @param weWordsQueryDTO 话术dto
     * @return {@link List<WeWordsVO>}
     */
    @Override
    public List<WeWordsVO> listOfWords(WeWordsQueryDTO weWordsQueryDTO) {
        if (weWordsQueryDTO == null || CollectionUtils.isEmpty(weWordsQueryDTO.getCategoryIds())) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_CATEGORY_ID);
        }
        String corpId = weWordsQueryDTO.getCorpId();
        List<Long> searchGroupIdList = new ArrayList<>();
        // 若标题、内容查询条件不为空，则先查询符合条件的话术id
        if (StringUtils.isNotBlank(weWordsQueryDTO.getContent())) {
            String content = weWordsQueryDTO.getContent();
            List<WeWordsDetailEntity> wordsDetailEntities = weWordsDetailMapper.selectList(Wrappers.lambdaQuery(WeWordsDetailEntity.class).select(WeWordsDetailEntity::getGroupId)
                                                                               .eq(WeWordsDetailEntity::getCorpId, corpId)
                                                                               .gt(WeWordsDetailEntity::getGroupId, Constants.DEFAULT_ID)
                                                                               .like(WeWordsDetailEntity::getTitle, content)
                                                                               .or()
                                                                               .like(WeWordsDetailEntity::getContent, content));
            searchGroupIdList = wordsDetailEntities.stream().map(WeWordsDetailEntity::getGroupId).collect(Collectors.toList());
        }
        // 启动分页
        PageInfoUtil.startPage();
        // 根据条件查询
        List<WeWordsVO> resultList = weWordsGroupMapper.listOfWords(weWordsQueryDTO, searchGroupIdList);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        // 补充话术对应的附件信息
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        for (WeWordsVO weWordsVO : resultList) {
            CompletableFuture<Void> addWordsDetailFuture = CompletableFuture.runAsync(() -> {
                List<WeWordsDetailEntity> weWordsDetailList = weWordsDetailMapper.selectWordsDetailByGroupId(corpId, Math.toIntExact(weWordsVO.getId()));
                weWordsVO.setWeWordsDetailList(weWordsDetailList);
            }, threadPoolTaskExecutor);
            completableFutures.add(addWordsDetailFuture);
        }
        CompletableFuture<Void> finalFuture = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
        // 异常捕获处理
        try {
            finalFuture.exceptionally(ex -> {
                log.error("[查询话术库附件列表] 出现异常，异常原因：{}，corpId：{}", ExceptionUtils.getStackTrace(ex), corpId);
                return null;
            }).get();
        } catch (Exception e) {
            log.error("[查询话术库附件列表] 出现异常，异常原因：{}，corpId：{}", ExceptionUtils.getStackTrace(e), corpId);
        }
        return resultList;
    }

    @Override
    public void updateCategory(Long categoryId, List<Long> ids, String corpId) {
        StringUtils.checkCorpId(corpId);
        if (categoryId == null || CollectionUtils.isEmpty(ids)) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_PARAMETER);
        }
        weWordsGroupMapper.updateCategory(categoryId, ids, corpId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeWordsImportVO importWords(MultipartFile file, LoginUser loginUser, Integer type) {
        ExcelUtil<WeWordsImportDTO> util = new ExcelUtil<>(WeWordsImportDTO.class);
        List<WeWordsGroupEntity> weWordsGroupEntities = new ArrayList<>();
        List<WeWordsDetailEntity> weWordsDetailEntities = new ArrayList<>();
        List<WeWordsImportDTO> words = new ArrayList<>();
        List<WeWordsImportDTO> wordsImport = new ArrayList<>();
        try {
            words = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            log.error("话术库导入话术异常：ex={}", ExceptionUtils.getStackTrace(e));
        }

        //构造导入话术返回结果
        WeWordsImportVO wordsImportVO = buildImportResult(words, wordsImport);
        List<String> nameList = wordsImport.stream().map(WeWordsImportDTO::getCategoryName).collect(Collectors.toList());
        //为空直接返回结果
        if (CollectionUtils.isEmpty(nameList)) {
            return wordsImportVO;
        }
        //判断文件夹名称，不存在就创建
        String useRange = buildUseRange(type, loginUser);
        List<FindExistWordsCategoryNameList> wordsCategory = weWordsCategoryService.findAndAddWordsCategory(loginUser.getCorpId(), type, nameList, useRange);
        Map<String, Long> wordsMap = wordsCategory.stream().collect(Collectors.toMap(FindExistWordsCategoryNameList::getName, FindExistWordsCategoryNameList::getId));
        //构造列表
        buildWords(weWordsGroupEntities, weWordsDetailEntities, wordsImport, loginUser.getCorpId(), wordsMap);
        //查询出最大sort
        WeWordsGroupEntity weWordsGroupEntity = weWordsGroupMapper.selectOne(new LambdaQueryWrapper<WeWordsGroupEntity>().eq(WeWordsGroupEntity::getCorpId, loginUser.getCorpId()).orderByDesc(WeWordsGroupEntity::getSort).last(GenConstants.LIMIT_1));
        Long sort = weWordsGroupEntity == null ? 0 : weWordsGroupEntity.getSort();
        for (WeWordsGroupEntity weWordsEntity : weWordsGroupEntities) {
            sort++;
            weWordsEntity.setSort(sort);
        }
        //批量新增话术主表
        weWordsGroupMapper.batchInsert(weWordsGroupEntities);

        //设置主表id
        for (int i = 0; i < weWordsDetailEntities.size(); i++) {
            weWordsDetailEntities.get(i).setGroupId(weWordsGroupEntities.get(i).getId());
        }
        //新增附件
        weWordsDetailService.saveOrUpdate(weWordsDetailEntities);
        //更新主表seq字段
        for (int i = 0; i < weWordsGroupEntities.size(); i++) {
            weWordsGroupEntities.get(i).setSeq(ArrayUtils.toArray(weWordsDetailEntities.get(i).getId().toString()));
        }
        weWordsGroupMapper.batchUpdateSeq(weWordsGroupEntities, loginUser.getCorpId());
        return wordsImportVO;
    }

    @Override
    public WeWordsUrlVO matchUrl(String address){
        return JsoupUtil.matchUrl(address);
    }

    /**
     * 构造导入话术返回结果
     *
     * @param words       话术
     * @param wordsImport 可导入的话术
     * @return {@link WeWordsImportVO}
     */
    private WeWordsImportVO buildImportResult(List<WeWordsImportDTO> words, List<WeWordsImportDTO> wordsImport) {
        final String overSize = "导入失败,每次最多导入1000条话术";
        final String contentEmpty = "分组名或内容为空";
        final int contentSize = 1500;
        final int titleSize = 64;
        final int categorySize = 12;
        final int wordsSize = 1000;
        StringBuilder failMsg = new StringBuilder();
        WeWordsImportVO wordsImportVO = new WeWordsImportVO();
        //空行数量
        int emptyCount = 0;
        if (words.size() > wordsSize) {
            failMsg.append(overSize);
        }
        for (int i = 0; i < words.size(); i++) {
            WeWordsImportDTO weWordsImportDTO = words.get(i);
            // 判空
            if (StringUtils.isBlank(weWordsImportDTO.getCategoryName()) || StringUtils.isBlank(weWordsImportDTO.getContent())) {
                if (StringUtils.isBlank(weWordsImportDTO.getTitle())) {
                    emptyCount++;
                    continue;
                }
                failMsg.append("第 ").append(i + 2).append(" 行,").append(contentEmpty).append("\r\n");
                continue;
            }
            //超出部分截取
            if (weWordsImportDTO.getCategoryName().length() > categorySize) {
                weWordsImportDTO.setCategoryName(weWordsImportDTO.getCategoryName().substring(0, categorySize));
            }
            if (weWordsImportDTO.getTitle().length() > titleSize) {
                weWordsImportDTO.setTitle(weWordsImportDTO.getTitle().substring(0, titleSize));
            }
            if (weWordsImportDTO.getContent().length() > contentSize) {
                weWordsImportDTO.setContent(weWordsImportDTO.getContent().substring(0, contentSize));
            }
            wordsImport.add(weWordsImportDTO);
        }

        wordsImportVO.setSuccessNum(wordsImport.size());
        wordsImportVO.setFailNum(words.size() - wordsImport.size() - emptyCount);
        if (wordsImportVO.getFailNum() > 0) {
            String suffix = "txt";
            String fileName = System.currentTimeMillis() + new Random().nextInt(wordsSize) + ".txt";
            RuoYiConfig ruoyiConfig = SpringUtils.getBean(RuoYiConfig.class);
            CosConfig cosConfig = ruoyiConfig.getFile().getCos();
            try {
                String url = FileUploadUtils.upload2Cos(new ByteArrayInputStream(failMsg.toString().getBytes(StandardCharsets.UTF_8)), fileName, suffix, cosConfig);
                String imgUrlPrefix = ruoyiConfig.getFile().getCos().getCosImgUrlPrefix();
                wordsImportVO.setUrl(imgUrlPrefix + url);
            } catch (IOException e) {
                log.error("话术库导入失败报告上传异常：ex:{}", ExceptionUtils.getStackTrace(e));
            }
        }
        return wordsImportVO;
    }


    /**
     * 构造话术（导入附件时使用）
     *
     * @param words 导入话术实体
     */
    private void buildWords(List<WeWordsGroupEntity> weWordsGroupEntities, List<WeWordsDetailEntity> weWordsDetailEntities, List<WeWordsImportDTO> words, String corpId, Map<String, Long> wordsMap) {

        words.forEach(weWordsImportDTO -> {
            WeWordsGroupEntity weWordsGroupEntity = new WeWordsGroupEntity(weWordsImportDTO.getTitle(), corpId, wordsMap.get(weWordsImportDTO.getCategoryName()));
            weWordsGroupEntities.add(weWordsGroupEntity);
            WeWordsDetailEntity weWordsDetailEntity = new WeWordsDetailEntity(corpId, WeConstans.WE_WORDS_DETAIL_MEDIATYPE_TEXT, weWordsImportDTO.getContent());
            weWordsDetailEntities.add(weWordsDetailEntity);
        });
    }

    /**
     * 构造文件使用范围
     *
     * @param type      类型
     * @param loginUser 登录用户
     * @return 使用范围（企业话术：存入根部门1，部门话术：主部门，我的话术：员工id）
     */
    private String buildUseRange(Integer type, LoginUser loginUser) {
        if (WeWordsCategoryTypeEnum.CORP.getType().equals(type)) {
            return WeConstans.ROOT_DEPARTMENT;
        } else if (WeWordsCategoryTypeEnum.DEPARTMENT.getType().equals(type)) {
            return loginUser.getWeUser().getMainDepartment().toString();
        } else {
            return loginUser.getWeUser().getUserId();
        }
    }

    /**
     * 处理话术主表附件关系
     *
     * @param weWordsGroupEntity    主表
     * @param weWordsDetailEntities 附件
     */
    private void wordsHandler(WeWordsGroupEntity weWordsGroupEntity, List<WeWordsDetailEntity> weWordsDetailEntities) {
        //保存或更新附件
        weWordsDetailService.saveOrUpdate(weWordsDetailEntities);
        //获得话术库附件id
        List<String> ids = weWordsDetailEntities.stream().map(weWordsDetailEntity -> weWordsDetailEntity.getId().toString()).collect(Collectors.toList());
        weWordsGroupEntity.setSeq(ids.toArray(new String[]{}));

        //更新主表
        weWordsGroupMapper.update(weWordsGroupEntity);
    }

    /**
     * 参数校验
     *
     * @param weWordsDTO 话术
     */
    private void checkWordsDTO(WeWordsDTO weWordsDTO) {
        //参数校验
        if (weWordsDTO == null || CollectionUtils.isEmpty(weWordsDTO.getWeWordsDetailList())) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_PARAMETER);
        }
    }

    /**
     * 给员工发送应用消息
     *
     * @param weWordsDTO 话术信息
     */
    private void sendToUser(WeWordsDTO weWordsDTO) {
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        WeWordsCategory wordsCategory = weWordsCategoryService.getById(weWordsDTO.getCategoryId());
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(weWordsDTO.getCorpId());
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        StringBuilder content = new StringBuilder();
        //设置发送者 发送给企业所有员工
        if (WeWordsCategoryTypeEnum.CORP.getType().equals(wordsCategory.getType())) {
            pushDto.setTouser("@all");
            content.append("企业话术库已更新");
            if (StringUtils.isNotBlank(weWordsDTO.getTitle())) {
                content.append("“").append(weWordsDTO.getTitle()).append("”");
            }
            text.setContent(content.toString());
        } else if (WeWordsCategoryTypeEnum.DEPARTMENT.getType().equals(wordsCategory.getType())) {
            //设置发送者 发送给部门所有员工
            pushDto.setToparty(weWordsDTO.getMainDepartment().toString());
            content.append("部门话术库已更新");
            if (StringUtils.isNotBlank(weWordsDTO.getTitle())) {
                content.append("“").append(weWordsDTO.getTitle()).append("”");
            }
            text.setContent(content.toString());
        } else {
            //没命中说明是员工文件夹不发消息
            return;
        }
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.debug("发送话术变更信息：toUser:{},toParty:{}", pushDto.getTouser(), pushDto.getToparty());
        messagePushClient.sendMessageToUser(pushDto, agentId, weWordsDTO.getCorpId());
    }


    @Override
    public int changeSort(WeWordsSortDTO sortDTO) {
        if (sortDTO == null || CollectionUtils.isEmpty(sortDTO.getWordsChangeSortDTOList())) {
            throw new CustomException(ResultTip.TIP_MISS_WORDS_SORT_INFO);
        }
        StringUtils.checkCorpId(sortDTO.getCorpId());
        return weWordsGroupMapper.changeSort(sortDTO.getCorpId(), sortDTO.getWordsChangeSortDTOList());
    }

}