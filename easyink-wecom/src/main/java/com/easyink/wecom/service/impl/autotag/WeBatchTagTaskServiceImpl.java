package com.easyink.wecom.service.impl.autotag;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.easyink.common.annotation.DataScope;
import com.easyink.common.config.CosConfig;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.autotag.AutoTagConstants;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.common.enums.CustomerStatusEnum;
import com.easyink.common.enums.CustomerTrajectoryEnums;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.enums.batchtag.BatchTagDetailStatusEnum;
import com.easyink.common.exception.CustomException;
import com.easyink.common.redis.BatchTagRedisCache;
import com.easyink.common.utils.DictUtils;
import com.easyink.common.utils.ListShardUtil;
import com.easyink.common.utils.TagRecordUtil;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.poi.ExcelUtil;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.common.utils.sql.BatchInsertUtil;
import com.easyink.wecom.domain.WeCustomerTrajectory;
import com.easyink.wecom.domain.WeFlowerCustomerRel;
import com.easyink.wecom.domain.WeTag;
import com.easyink.wecom.domain.autotag.WeBatchTagTask;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskDetail;
import com.easyink.wecom.domain.autotag.WeBatchTagTaskRel;
import com.easyink.wecom.domain.dto.autotag.batchtag.BatchTagTaskDTO;
import com.easyink.wecom.domain.vo.autotag.BatchTagTaskVO;
import com.easyink.wecom.domain.vo.autotag.ImportBatchTagTaskVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.WeCustomerMapper;
import com.easyink.wecom.mapper.WeFlowerCustomerRelMapper;
import com.easyink.wecom.mapper.autotag.WeBatchTagTaskDetailMapper;
import com.easyink.wecom.mapper.autotag.WeBatchTagTaskMapper;
import com.easyink.wecom.mapper.autotag.WeBatchTagTaskRelMapper;
import com.easyink.wecom.service.WeCustomerTrajectoryService;
import com.easyink.wecom.service.WeTagService;
import com.easyink.wecom.service.autotag.WeBatchTagTaskDetailService;
import com.easyink.wecom.service.autotag.WeBatchTagTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.easyink.common.constant.autotag.AutoTagConstants.*;
import static com.easyink.common.utils.file.MimeTypeUtils.XLS;
import static com.easyink.common.utils.file.MimeTypeUtils.XLSX;

/**
 * 批量打标签（we_batch_tag_task）服务实现类
 *
 * @author lichaoyu
 * @date 2023/6/5 10:19
 */
@Service("WeBatchTagTaskService")
@Slf4j
public class WeBatchTagTaskServiceImpl extends ServiceImpl<WeBatchTagTaskMapper, WeBatchTagTask> implements WeBatchTagTaskService {
    /**
     * 批量打标签的并发数 ,目前暂定5 , 由企微官方论坛得 如果接口调用并发超过8可能会出现调用频繁的错误
     */
    private static final Integer BATCH_TAG_CONCURRENCY = 5;
    /**
     * 打标签的间隔时间
     */
    private static final long TAG_INTERVAL_TIME = 1000L;
    private static final int MAX_ERR_LENGTH = 50;
    private final WeTagService weTagService;

    private final WeBatchTagTaskDetailService weBatchTagTaskDetailService;

    private final WeBatchTagTaskMapper weBatchTagTaskMapper;

    private final WeBatchTagTaskRelMapper weBatchTagTaskRelMapper;

    private final WeCustomerMapper weCustomerMapper;
    private final WeFlowerCustomerRelMapper weFlowerCustomerRelMapper;
    private final WeBatchTagTaskDetailMapper weBatchTagTaskDetailMapper;

    private final WeCustomerTrajectoryService weCustomerTrajectoryService;

    @Resource(name = "batchTagRedisCache")
    private BatchTagRedisCache batchTagRedisCache;
    @Resource(name = "batchTagExecutor")
    private ThreadPoolTaskExecutor batchTagExecutor;

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public WeBatchTagTaskServiceImpl(WeTagService weTagService, WeBatchTagTaskDetailService weBatchTagTaskDetailService, WeBatchTagTaskMapper weBatchTagTaskMapper, WeBatchTagTaskRelMapper weBatchTagTaskRelMapper, WeCustomerMapper weCustomerMapper, WeFlowerCustomerRelMapper weFlowerCustomerRelMapper, WeBatchTagTaskDetailMapper weBatchTagTaskDetailMapper, WeCustomerTrajectoryService weCustomerTrajectoryService) {
        this.weTagService = weTagService;
        this.weBatchTagTaskDetailService = weBatchTagTaskDetailService;
        this.weBatchTagTaskMapper = weBatchTagTaskMapper;
        this.weBatchTagTaskRelMapper = weBatchTagTaskRelMapper;
        this.weCustomerMapper = weCustomerMapper;
        this.weFlowerCustomerRelMapper = weFlowerCustomerRelMapper;
        this.weBatchTagTaskDetailMapper = weBatchTagTaskDetailMapper;
        this.weCustomerTrajectoryService = weCustomerTrajectoryService;
    }


    /**
     * 导入批量打标签任务
     *
     * @param file     excel文件
     * @param tagIds   企业ID
     * @param taskName
     * @return 结果 {@link ImportBatchTagTaskVO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportBatchTagTaskVO importBatchTagTask(MultipartFile file, List<String> tagIds, String taskName) throws IOException {
        if (file.isEmpty()) {
            throw new CustomException(ResultTip.TIP_BATCH_TAG_TASK_NOT_IMPORT_EXCEL);
        }
        LoginUser loginUser = LoginTokenService.getLoginUser();
        String corpId = loginUser.getCorpId();
        String createByUserId = loginUser.getUserId();
        String createUserName = loginUser.getUsername();
        // 校验和获取Excel
        XSSFWorkbook excel = verifyAndGetExcel(file);
        // 校验模板属性值
        verifyFieldValues(excel);
        ImportBatchTagTaskVO importBatchTagTaskVO = new ImportBatchTagTaskVO();
        // 创建打标签任务
        WeBatchTagTask weBatchTagTask = new WeBatchTagTask();
        weBatchTagTask.setCorpId(corpId);
        weBatchTagTask.setName(taskName);
        weBatchTagTask.setTagName(getTagName(tagIds));
        weBatchTagTask.setCreateBy(createByUserId);
        weBatchTagTask.setCreateTime(new Date());
        this.save(weBatchTagTask);
        // 转换excel列表数据
        List<WeBatchTagTaskDetail> weBatchTagTaskDetails = convertColumnToList(excel.getSheetAt(0), importBatchTagTaskVO, weBatchTagTask.getId());
        if (CollectionUtils.isEmpty(weBatchTagTaskDetails)) {
            throw new CustomException(ResultTip.TIP_REDEEM_CODE_FILE_DATA_IS_EMPTY);
        }
        // 分批插入详情数据
        BatchInsertUtil.doInsert(weBatchTagTaskDetails, list -> weBatchTagTaskDetailMapper.batchInsert(list));
        weBatchTagTaskRelMapper.saveBatch(weBatchTagTask.getId(), tagIds);
        // 异步执行批量打标签的任务
        threadPoolTaskExecutor.execute(
                ()  -> executeTask(corpId, weBatchTagTask.getId(), tagIds, weBatchTagTaskDetails, createUserName, createByUserId));
        return importBatchTagTaskVO;
    }

    /**
     * 校验和获取Excel
     *
     * @param file {@link MultipartFile}
     * @return 工作簿
     * @throws IOException
     */
    public XSSFWorkbook verifyAndGetExcel(MultipartFile file) throws IOException {
        // 格式判断
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!(suffix.equals(XLSX) || suffix.equals(XLS))) {
            throw new CustomException(ResultTip.TIP_BATCH_TAG_TASK_NOT_EXCEL);
        }
        // 获取Excel文件
        InputStream inputStream = file.getInputStream();
        // 使用XSSFWorkbook类来读取Excel文件，并将其存储在Workbook对象
        XSSFWorkbook excel = new XSSFWorkbook(inputStream);
        if (excel == null) {
            throw new CustomException(ResultTip.TIP_FAILED_TO_COVERT_EXCEL);
        }
        return excel;
    }

    /**
     * 获取标签名称，以,分隔
     *
     * @param tagIds 标签ID列表
     * @return 标签名称
     */
    public String getTagName(List<String> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            throw new CustomException(ResultTip.TIP_NO_EFFECTIVE_TAG);
        }
        // 根据标签ID获取标签名列表
        List<String> tagNamelist = weTagService.list(new LambdaQueryWrapper<WeTag>()
                        .select(WeTag::getName)
                        .in(WeTag::getTagId, tagIds))
                .stream().map(WeTag::getName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagNamelist) || tagNamelist.size() != tagIds.size()) {
            throw new CustomException(ResultTip.TIP_NO_EFFECTIVE_TAG);
        }
        // 将标签名以","分隔
        return tagNamelist.stream().collect(Collectors.joining(DictUtils.SEPARATOR));
    }

    /**
     * 校验模板属性值
     *
     * @param excel {@link XSSFWorkbook}
     * @return
     */
    public void verifyFieldValues(XSSFWorkbook excel) {
        if (excel == null) {
            throw new CustomException(ResultTip.TIP_FAILED_TO_COVERT_EXCEL);
        }
        int firstSheet = 0;
        int firstRow = 0;
        int externalUseridColumn = 0;
        int unionIdColumn = 1;
        int mobileColumn = 2;
        // 对A1、B1、C1 列第一行字段校验
        String externalUserid = (String) ExcelUtil.getRowValue(excel, firstSheet, firstRow, externalUseridColumn);
        String unionId = (String) ExcelUtil.getRowValue(excel, firstSheet, firstRow, unionIdColumn);
        String mobile = (String) ExcelUtil.getRowValue(excel, firstSheet, firstRow, mobileColumn);
        Boolean verifyColumn = AutoTagConstants.FIRST_FIELD_VALUE.equals(externalUserid) && AutoTagConstants.SECOND_FIELD_VALUE.equals(unionId) && AutoTagConstants.THIRD_FIELD_VALUE.equals(mobile);
        if (!verifyColumn) {
            throw new CustomException(ResultTip.TIP_TEMPLATE_MISMATCH);
        }
    }

    /**
     * 转换excel列表数据
     *
     * @param sheet                {@link XSSFSheet}
     * @param importBatchTagTaskVO {@link ImportBatchTagTaskVO}
     * @return 结果
     */
    public List<WeBatchTagTaskDetail> convertColumnToList(XSSFSheet sheet, ImportBatchTagTaskVO importBatchTagTaskVO, Long taskId) {
        // 导入失败消息体
        StringBuilder failMsg = new StringBuilder();
        // 返回的结果
        int successNum = 0;
        int failNum = 0;
        // 最后一行行数
        int lastRowNum = sheet.getLastRowNum();
        lastRowNum = lastRowNum > AutoTagConstants.MAX_TASK_SIZE ? AutoTagConstants.MAX_TASK_SIZE : lastRowNum;
        // 数据从第一行开始获取，排除第0行的模板属性值
        int firstRowNum = 1;
        // 处理任务详情
        CopyOnWriteArrayList<WeBatchTagTaskDetail> resultList = new CopyOnWriteArrayList<>();
        // 遍历表格
        for (int currentRowNum = firstRowNum; currentRowNum <= lastRowNum; currentRowNum++) {
            XSSFRow row = sheet.getRow(currentRowNum);
            int externalUseridColumn = 0;
            int unionIdColumn = 1;
            int mobileColumn = 2;
            // 获取当前行第一列数据
            String externalUserid = getCellValue(row, externalUseridColumn);
            // 获取当前行第二列数据
            String unionId = getCellValue(row, unionIdColumn);
            // 获取当前行第三列数据
            String mobile = getCellValue(row, mobileColumn);
            // 三个值为空或为空字符串则表示该行无数据，跳过此行
            if (StringUtils.isBlank(externalUserid) && StringUtils.isBlank(unionId) && StringUtils.isBlank(mobile)) {
                continue;
            }
            // 判断字段是否超长，超长记录并跳过此行
            Boolean overLength = calculateLength(externalUserid, unionId, mobile);
            if (overLength) {
                appendFailMsg(failMsg, currentRowNum, AutoTagConstants.COLUMN_EXCEED_64_LENGTH);
                failNum++;
                continue;
            }
            // 有数据时
            if (CollectionUtils.isNotEmpty(resultList)) {
                Boolean isRepeated = false;
                // 判断是否有重复的数据，并将数据添加到错误消息中。
                for (WeBatchTagTaskDetail item : resultList) {
                    if (!StringUtils.isBlank(externalUserid) && !StringUtils.isBlank(item.getImportExternalUserid())) {
                        if (item.getImportExternalUserid().equals(externalUserid)) {
                            appendFailMsg(failMsg, currentRowNum, AutoTagConstants.EXTERNAL_USER_ID_NOT_BE_REPEATED);
                            failNum++;
                            isRepeated = true;
                            break;
                        }
                    }
                    if (!StringUtils.isBlank(unionId) && !StringUtils.isBlank(item.getImportUnionId())) {
                        if (item.getImportUnionId().equals(unionId)) {
                            appendFailMsg(failMsg, currentRowNum, AutoTagConstants.UNION_ID_NOT_BE_REPEATED);
                            failNum++;
                            isRepeated = true;
                            break;
                        }
                    }
                    if (!StringUtils.isBlank(mobile) && !StringUtils.isBlank(item.getImportMobile())) {
                        if (item.getImportMobile().equals(mobile)) {
                            appendFailMsg(failMsg, currentRowNum, AutoTagConstants.MOBILE_NOT_BE_REPEATED);
                            failNum++;
                            isRepeated = true;
                            break;
                        }
                    }
                }
                if (!isRepeated) {
                    resultList.add(new WeBatchTagTaskDetail(taskId, externalUserid, unionId, mobile));
                    successNum++;
                }
            } else {
                resultList.add(new WeBatchTagTaskDetail(taskId, externalUserid, unionId, mobile));
                successNum++;
            }
        }
        // 设置导入成功和失败的数量
        importBatchTagTaskVO.setSuccessNum(successNum);
        importBatchTagTaskVO.setFailNum(failNum);
        // 上传文件并设置下载路径
        uploadFailFileAndSetUrl(importBatchTagTaskVO, failMsg);
        return resultList;
    }

    /**
     * 计算当前行数据总长度是否符合规则
     *
     * @param externalUserid 客户ID
     * @param unionId 客户unionid
     * @param mobile 手机号
     * @return false：不符合，true：符合
     */
    private Boolean calculateLength(String externalUserid, String unionId, String mobile) {
        int externalLength = StringUtils.isBlank(externalUserid) ? 0 : externalUserid.length();
        int unionIdLength = StringUtils.isBlank(unionId) ? 0 : unionId.length();
        int mobileLength = StringUtils.isBlank(mobile) ? 0 : mobile.length();
        // 任意一个大于64字符，表示该行不合法
        return externalLength > MAX_COLUMN_LENGTH || unionIdLength > MAX_COLUMN_LENGTH || mobileLength > MAX_COLUMN_LENGTH;
    }

    /**
     * 添加失败原因信息
     *
     * @param failMsg       所有失败的信息
     * @param currentRowNum 当前行数
     * @param info          原因信息
     */
    public void appendFailMsg(StringBuilder failMsg, int currentRowNum, String info) {
        failMsg.append("第 ").append(currentRowNum + 1).append(" 行,").append(info).append("\r\n");
    }

    /**
     * 获取指定单元格（列）的值
     *
     * @param row       当前行
     * @param columnNum 单元格列数
     * @return 单元格值
     */
    public String getCellValue(XSSFRow row, int columnNum) {
        if (row.getCell(columnNum) == null) {
            return null;
        }
        // 设置单元格类型，获取单元格值
        if (row.getCell(columnNum).getCellTypeEnum() == CellType.NUMERIC) {
            row.getCell(columnNum).setCellType(CellType.STRING);
        }
        return row.getCell(columnNum).getStringCellValue();
    }

    /**
     * 上传文件并设置下载路径
     *
     * @param importBatchTagTaskVO
     * @param failMsg
     */
    public void uploadFailFileAndSetUrl(ImportBatchTagTaskVO importBatchTagTaskVO, StringBuilder failMsg) {
        if (importBatchTagTaskVO.getFailNum() > 0) {
            // 导入失败报告文件名
            String fileName = AutoTagConstants.EXPORT_FAIL_FILE_NAME + AutoTagConstants.EXPORT_FAIL_FILE_TYPE;
            // 读取配置
            RuoYiConfig ruoyiConfig = SpringUtils.getBean(RuoYiConfig.class);
            CosConfig cosConfig = ruoyiConfig.getFile().getCos();
            try {
                // 通过流进行文件上传
                String url = FileUploadUtils.upload2Cos(new ByteArrayInputStream(failMsg.toString().getBytes(StandardCharsets.UTF_8)), fileName, AutoTagConstants.URL_FILE_SUFFIX, cosConfig);
                // 获取COS URL前缀
                String imgUrlPrefix = ruoyiConfig.getFile().getCos().getCosImgUrlPrefix();
                // 设置URL
                importBatchTagTaskVO.setUrl(imgUrlPrefix + url);
            } catch (IOException e) {
                log.error("导入失败报告异常：ex:{}", ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * 根据任务ID批量删除任务详情（逻辑删除）
     *
     * @param corpId  企业ID
     * @param taskIds 任务ID
     * @return 结果
     */
    @Override
    public Integer deleteBatchTaskByIds(String corpId, Long[] taskIds) {
        if (StringUtils.isBlank(corpId) || ArrayUtils.isEmpty(taskIds)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 设置任务终止状态
        batchTagRedisCache.batchStopTask(taskIds);
        return weBatchTagTaskMapper.deleteBatchTaskByIds(corpId, taskIds);
    }


    /**
     * 查询任务列表
     *
     * @param dto {@link BatchTagTaskDTO}
     * @return 结果
     */
    @Override
    @DataScope
    public List<BatchTagTaskVO> selectBatchTaskList(BatchTagTaskDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        List<WeBatchTagTaskRel> taskRels;
        List<Long> taskIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getTagIds())) {
            // 查询标签关联表对应的关联信息
            taskRels = weBatchTagTaskRelMapper.selectList(new LambdaQueryWrapper<WeBatchTagTaskRel>().in(WeBatchTagTaskRel::getTagId, dto.getTagIds()).select(WeBatchTagTaskRel::getTaskId));
            if (CollectionUtils.isEmpty(taskRels)) {
                return new ArrayList<>();
            }
            taskIds = taskRels.stream().map(WeBatchTagTaskRel::getTaskId).collect(Collectors.toList());
        }
        // 获取分页结果
        List<BatchTagTaskVO> resultList = weBatchTagTaskMapper.selectBatchTaskList(dto, taskIds);
        if (CollectionUtils.isEmpty(resultList)) {
            return new ArrayList<>();
        }
        taskIds = resultList.stream().map(BatchTagTaskVO::getId).collect(Collectors.toList());
        // 根据taskId查出详情
        QueryWrapper<WeBatchTagTaskDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("task_id", taskIds);
        List<WeBatchTagTaskDetail> weBatchTagTaskDetails = weBatchTagTaskDetailService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(weBatchTagTaskDetails)) {
            // 获取每个任务下的客户数
            Map<Long, Long> customerCntMap = weBatchTagTaskDetails.stream().collect(Collectors.groupingBy(WeBatchTagTaskDetail::getTaskId, Collectors.counting()));
            // 设置客户数量
            for (BatchTagTaskVO batchTagTaskVO : resultList) {
                if (customerCntMap.get(batchTagTaskVO.getId()) != null) {
                    batchTagTaskVO.setCustomerCnt(Math.toIntExact(customerCntMap.get(batchTagTaskVO.getId())));
                }
            }
        }
        return resultList;
    }

    /**
     * 执行批量标签任务
     *
     * @param corpId     企业ID
     * @param taskId     批量打标签任务ID
     * @param tagIdList  需要打上的标签ID 集合
     * @param detailList 执行打标签的任务详情列表  {@link WeBatchTagTaskDetail} ,包含需要打标签的 客户external_userid
     * @param createUserName 创建人姓名
     * @param createByUserId 创建人ID
     */
    public void executeTask(String corpId, Long taskId, List<String> tagIdList, List<WeBatchTagTaskDetail> detailList, String createUserName, String createByUserId) {
        if (StringUtils.isBlank(corpId) || CollectionUtils.isEmpty(tagIdList) || CollectionUtils.isEmpty(detailList)) {
            throw new CustomException(ResultTip.TIP_PARAM_MISSING);
        }
        // 根据EXCEL 表格中的填写的数据 ,匹配对应的客户id 和 员工的userId
        matchCustomer(corpId, detailList);
        // 把任务按照最大并发数把任务分组
        List<List<WeBatchTagTaskDetail>> splitDetailList = ListShardUtil.split2MaxSize(detailList, BATCH_TAG_CONCURRENCY);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        log.info("[批量打标签]开始执行,corpId:{}, taskId:{} ,总数 :{} , 分批数 :{}", corpId, taskId, detailList.size(), splitDetailList.size());
        // 分组并行执行打标签操作
        splitDetailList.forEach(details -> futureList.add(
                CompletableFuture.runAsync(() -> tagSingleTask(corpId, taskId, details, tagIdList, createUserName, createByUserId), batchTagExecutor)
        ));
        // 待所有线程都执行完, 更新任务状态和详情
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                         .whenComplete((v, th) -> finishTaskHandle(taskId))
                         .exceptionally(e -> {
                             log.error("[批量打标签]执行异常,corpId:{}, taskId:{},e :{} ", corpId, taskId, ExceptionUtils.getStackTrace(e));
                             return null;
                         })
                         .join();
        log.info("[批量打标签] 任务执行完成,corpId:{} ,taskId:{}", corpId, taskId);
    }

    /**
     * 批量打标签 任务完成处理
     *
     * @param taskId 主任务ID
     */
    private void finishTaskHandle(Long taskId) {
        if (taskId == null) {
            return;
        }
        // 批量更新任务详情
        this.updateById(WeBatchTagTask.builder()
                                      .id(taskId)
                                      .executeFlag(true)
                                      .build());

    }

    /**
     * 执行整个标签任务下的单条数据
     *
     * @param corpId    企业ID
     * @param taskId    批量标签任务ID
     * @param details   标签任务列表 {@link WeBatchTagTask}
     * @param tagIdList 需要打上的标签ID 列表
     * @param createUserName 创建人姓名
     * @param createByUserId 创建人ID
     */
    public void tagSingleTask(String corpId, Long taskId, List<WeBatchTagTaskDetail> details, List<String> tagIdList, String createUserName, String createByUserId) {
        // 成功打上标签的客户关系列表
        Set<WeFlowerCustomerRel> relList = new HashSet<>();
        for (WeBatchTagTaskDetail detail : details) {
            // 设置打标签的员工,多个则用,隔开
            detail.setTagUserId(StringUtils.join(detail.getTagUserIds(), ","));
            if (detail.notMatchCustomer()) {
                detail.setTagExternalUserid(StringUtils.EMPTY);
                detail.fail(NOT_CUSTOMER);
                continue;
            }
            // 判断任务是否需要中断
            if (batchTagRedisCache.isStopped(taskId)) {
                log.info("[批量打标签]任务中断,corpId:{}, taskId:{}" ,corpId,taskId);
                taskStopHandle(details);
                break;
            }
            // 依次调用打标签API,客户的每个员工都需要打上标签,一个员工打上标签此任务详情则算成功
            for (WeFlowerCustomerRel rel : detail.getRelSet()) {
                try {
                    weTagService.addTag(corpId, rel, tagIdList);
                    // 构建记录信息动态对象,并添加到列表
                    relList.add(rel);
                    detail.success();
                    ThreadUtil.safeSleep(TAG_INTERVAL_TIME);
                } catch (ForestRuntimeException | CustomException e) {
                    // 调用企微接口异常处理
                    detail.fail(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage().substring(0,MAX_ERR_LENGTH) : "");
                } catch (Exception e) {
                    detail.fail(SYS_ERROR);
                    log.error("[批量打标签]为单个客户打标签出现系统异常,corpId:{},taskId:{},e:{}",corpId, taskId, ExceptionUtils.getStackTrace(e));
                }
            }
        }
        // 批量更新任务详情(每批更新一次)
        weBatchTagTaskDetailMapper.batchInsertOrUpdate(details);
        // 记录信息动态
        recordTrajectory(corpId, createByUserId, relList, createUserName, tagIdList);
    }

    /**
     * 构建记录信息动态对象
     *
     * @param corpId 企业ID
     * @param createByUserId 创建人ID
     * @param relList 成功被打标签的客户关系
     * @param createUserName 创建人姓名
     * @param tagIdList 标签ID
     * @return 记录动态结果列表
     */
    private void recordTrajectory(String corpId, String createByUserId, Set<WeFlowerCustomerRel> relList, String createUserName, List<String> tagIdList) {
        // 最终记录的文案内容列表
        List<WeCustomerTrajectory> recordList = new ArrayList<>();
        // 因为一整批的标签和创建人都是相同的，所以在此处构建文案和操作详情
        TagRecordUtil tagRecordUtil = new TagRecordUtil();
        try {
            // 构建文案内容
            String content = tagRecordUtil.buildEditTagContent(createUserName, CustomerTrajectoryEnums.TagType.BATCH_TAG_TASK.getType());
            // 去除列表中标签为空的
            tagIdList.removeAll(Collections.singleton(null));
            // 获取有效标签名称
            List<String> tagNameList = weTagService.getTagNameByIds(tagIdList);
            if (CollectionUtils.isEmpty(tagNameList)) {
                log.info("[批量打标签] 标签名称为空：tagIdList:{}", tagIdList);
                return;
            }
            // 构建操作详情
            String oprDetail = String.join(DictUtils.SEPARATOR, tagNameList);
            // 为每个被打上标签的客户记录信息动态
            for (WeFlowerCustomerRel rel : relList) {
                recordList.add(WeCustomerTrajectory.builder()
                        .corpId(corpId)
                        .userId(rel.getUserId())
                        .externalUserid(rel.getExternalUserid())
                        .createDate(new Date())
                        .trajectoryType(CustomerTrajectoryEnums.Type.INFO.getDesc())
                        .content(content)
                        .subType(CustomerTrajectoryEnums.SubType.EDIT_TAG.getType())
                        .detail(oprDetail)
                        .startTime(new Time(System.currentTimeMillis()))
                        .detailId(Constants.DEFAULT_ID)
                        .build());
            }
            // 批量更新信息动态(每批更新一次)
            weCustomerTrajectoryService.saveBatch(recordList);
        } catch (Exception e) {
            log.info("[批量打标签] 信息动态记录异常，corpId:{} , createUserName:{} , createByUserId:{} , relList:{}", corpId, createUserName, createByUserId, relList);
        }
    }


    /**
     * 任务中断处理
     *
     * @param details 批量打标签详情列表
     */
    private void taskStopHandle(List<WeBatchTagTaskDetail> details) {
        for (WeBatchTagTaskDetail detail : details) {
            if (detail.getStatus() == null || BatchTagDetailStatusEnum.TO_BE_EXECUTED.getStatus().equals(detail.getStatus())) {
                // 如果还没执行则直接 设为失败
                detail.fail(TASK_STOPPER_BY_DEL);
            }
            if(detail.getTagUserId() == null) {
                detail.setTagUserId(StringUtils.EMPTY);
            }
        }
    }


    /**
     * 为批量打标签任务 从db中匹配 对应的客户,
     * <p>
     * 匹配后的结果会更新到 detailList中;
     * 会把匹配到的客户 external_userid设置到 {@link WeBatchTagTaskDetail#getTagExternalUserid()} 字段中
     * 同时也会把匹配到客户对应的 user_id ,加入到 {@link WeBatchTagTaskDetail#getTagUserIds()} 中
     *
     * @param corpId     企业ID
     * @param detailList 批量打标签任务列表 {@link WeBatchTagTaskDetail}
     */
    private void matchCustomer(String corpId, List<WeBatchTagTaskDetail> detailList) {
        // 分别按照导入列表中的 external_userid ,union_id和手机号分成3组条件
        List<String> externalUserIds = new ArrayList<>();
        List<String> unionIds = new ArrayList<>();
        List<String> mobiles = new ArrayList<>();
        detailList.forEach(detail -> {
            if (StringUtils.isNotBlank(detail.getImportExternalUserid())) {
                externalUserIds.add(detail.getImportExternalUserid());
            }
            if (StringUtils.isNotBlank(detail.getImportUnionId())) {
                unionIds.add(detail.getImportUnionId());
            }
            if (StringUtils.isNotBlank(detail.getImportMobile())) {
                mobiles.add(detail.getImportMobile());
            }
        });
        // 根据external_userid条件匹配客户
        matchByExternalId(corpId, detailList, externalUserIds);
        // 根据union_id 条件匹配客户
        matchByUnionId(corpId, detailList, unionIds);
        // 根据 手机号Mobile 条件匹配客户
        matchByMobile(corpId, detailList, mobiles);
    }


    /**
     * 根据externalUserId 匹配打标签的客户
     *
     * @param corpId          企业ID
     * @param detailList      批量打标签任务列表 {@link WeBatchTagTaskDetail}
     * @param externalUserIds 导入EXCEL 的所有external_user_Id列表
     */
    private void matchByExternalId(String corpId, List<WeBatchTagTaskDetail> detailList, List<String> externalUserIds) {
        if (CollectionUtils.isEmpty(externalUserIds)) {
            return;
        }
        List<WeFlowerCustomerRel> relList = weFlowerCustomerRelMapper.selectList(new LambdaQueryWrapper<WeFlowerCustomerRel>().eq(WeFlowerCustomerRel::getCorpId, corpId)
                                                                                                                              .in(WeFlowerCustomerRel::getExternalUserid, externalUserIds)
                                                                                                                              .in(WeFlowerCustomerRel::getStatus, CustomerStatusEnum.NORMAL.getCode().toString(), CustomerStatusEnum.DRAIN.getCode().toString())
                                                                                                                              );
        if (CollectionUtils.isEmpty(relList)) {
            return;
        }
        log.info("[批量打标签]根据external_user_id匹配,导入{}个external_user_id,匹配到{}个客户,corpId:{}", externalUserIds.size(), relList.size(), corpId);
        for (WeFlowerCustomerRel rel : relList) {
            for (WeBatchTagTaskDetail detail : detailList) {
                if (StringUtils.isNotBlank(rel.getExternalUserid()) && rel.getExternalUserid()
                                                                          .equals(detail.getImportExternalUserid())) {
                    // 为匹配上的客户设置 实际的客户id和其他相关信息
                    detail.setTagExternalUserid(rel.getExternalUserid());
                    // 增加员工信息
                    detail.getTagUserIds().add(rel.getUserId());
                    detail.getRelSet().add(rel);
                }
            }
        }
    }

    /**
     * 根据客户导入的EXCEL 匹配对应的客户信息
     *
     * @param corpId     企业ID
     * @param detailList 批量打标签任务列表 {@link WeBatchTagTaskDetail}
     * @param unionIds   EXCEL导入的所有unionId列表
     */
    private void matchByUnionId(String corpId, List<WeBatchTagTaskDetail> detailList, List<String> unionIds) {
        if (CollectionUtils.isEmpty(unionIds)) {
            return;
        }
        // 根据union_id查询满足条件的客户
        List<WeFlowerCustomerRel> relList = weFlowerCustomerRelMapper.getByUnionIds(corpId, unionIds);
        if (CollectionUtils.isEmpty(relList)) {
            return;
        }
        log.info("[批量打标签]根据union_id匹配,导入{}个union_id,匹配到{}个客户,corpId:{}", unionIds.size(), relList.size(), corpId);
        // 进行匹配
        for (WeBatchTagTaskDetail detail : detailList) {
            if (StringUtils.isNotBlank(detail.getTagExternalUserid())) {
                // 如果已经根据external_user_id匹配上,则不继续匹配
                continue;
            }
            for (WeFlowerCustomerRel rel : relList) {
                if (rel.getUnionId() != null && rel.getUnionId().equals(detail.getImportUnionId())) {
                    // 为匹配上的客户设置external_userid和user_id 以及
                    detail.setTagExternalUserid(rel.getExternalUserid());
                    detail.getTagUserIds().add(rel.getUserId());
                    detail.getRelSet().add(rel);
                }
            }
        }
    }

    /**
     * 根据导入的手机号去db中匹配客户
     *
     * @param corpId     企业ID
     * @param detailList 批量打标签任务列表 {@link WeBatchTagTaskDetail}
     * @param mobiles    导入EXCEL 的所有手机号mobile 列表
     */
    private void matchByMobile(String corpId, List<WeBatchTagTaskDetail> detailList, List<String> mobiles) {
        if (CollectionUtils.isEmpty(mobiles)) {
            return;
        }
        // 根据手机号获取客户信息
        List<WeFlowerCustomerRel> relList = weFlowerCustomerRelMapper.getByMobiles(corpId, mobiles);
        if (CollectionUtils.isEmpty(relList)) {
            return;
        }
        log.info("[批量打标签]根据手机号mobile匹配,导入{}个手机号,匹配到{}个客户,corpId:{}", mobiles.size(), relList.size(), corpId);
        //根据手机号,  匹配查出的客户
        for (WeBatchTagTaskDetail detail : detailList) {
            if (StringUtils.isNotBlank(detail.getTagExternalUserid())) {
                // 如果之前已经根据union_id和external_user_id匹配上则不继续匹配
                continue;
            }
            for (WeFlowerCustomerRel rel : relList) {
                if (rel.getRemarkMobiles() != null && rel.getRemarkMobiles().equals(detail.getImportMobile())) {
                    // 为匹配上的客户设置external_userid和user_id 以及
                    detail.setTagExternalUserid(rel.getExternalUserid());
                    detail.getTagUserIds().add(rel.getUserId());
                    detail.getRelSet().add(rel);
                }
            }
        }
    }
}
