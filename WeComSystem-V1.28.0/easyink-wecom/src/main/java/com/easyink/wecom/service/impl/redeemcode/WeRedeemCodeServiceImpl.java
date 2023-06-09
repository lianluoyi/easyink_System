package com.easyink.wecom.service.impl.redeemcode;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.config.CosConfig;
import com.easyink.common.config.RuoYiConfig;
import com.easyink.common.constant.redeemcode.RedeemCodeConstants;
import com.easyink.common.core.domain.entity.WeCorpAccount;
import com.easyink.common.core.page.PageDomain;
import com.easyink.common.core.page.TableSupport;
import com.easyink.common.enums.MessageType;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.common.lock.LockUtil;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.file.FileUploadUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.easyink.common.utils.sql.SqlUtil;
import com.easyink.wecom.client.WeMessagePushClient;
import com.easyink.wecom.domain.dto.WeMessagePushDTO;
import com.easyink.wecom.domain.dto.message.TextMessageDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeDeleteDTO;
import com.easyink.wecom.domain.dto.redeemcode.WeRedeemCodeImportDTO;
import com.easyink.wecom.domain.entity.redeemcode.WeRedeemCode;
import com.easyink.wecom.domain.vo.customer.WeCustomerVO;
import com.easyink.wecom.domain.vo.redeemcode.ImportRedeemCodeVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeActivityVO;
import com.easyink.wecom.domain.vo.redeemcode.WeRedeemCodeVO;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.mapper.redeemcode.WeRedeemCodeMapper;
import com.easyink.wecom.service.WeCorpAccountService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeActivityService;
import com.easyink.wecom.service.redeemcode.WeRedeemCodeService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.easyink.common.annotation.Excel.ColumnType.STRING;
import static com.easyink.common.utils.file.MimeTypeUtils.XLS;
import static com.easyink.common.utils.file.MimeTypeUtils.XLSX;

/**
 * ClassName： WeRedeemCodeServiceImpl
 *
 * @author wx
 * @date 2022/7/5 17:20
 */

@Slf4j
@Service
public class WeRedeemCodeServiceImpl extends ServiceImpl<WeRedeemCodeMapper, WeRedeemCode> implements WeRedeemCodeService {


    private final WeCustomerService weCustomerService;
    private final WeRedeemCodeMapper weRedeemCodeMapper;
    private final WeRedeemCodeActivityService weRedeemCodeActivityService;
    private final WeCorpAccountService corpAccountService;
    private final WeMessagePushClient messagePushClient;

    @Autowired
    public WeRedeemCodeServiceImpl(WeCustomerService weCustomerService, WeRedeemCodeMapper weRedeemCodeMapper, WeRedeemCodeActivityService weRedeemCodeActivityService, WeCorpAccountService corpAccountService, WeMessagePushClient messagePushClient) {
        this.weCustomerService = weCustomerService;
        this.weRedeemCodeMapper = weRedeemCodeMapper;
        this.weRedeemCodeActivityService = weRedeemCodeActivityService;
        this.corpAccountService = corpAccountService;
        this.messagePushClient = messagePushClient;
    }

    @Override
    public ImportRedeemCodeVO importRedeemCode(String corpId, MultipartFile file, String id) throws IOException {
        if (file.isEmpty()) {
            throw new CustomException(ResultTip.TIP_REDEEM_CODE_EMPTY_FILE);
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!(suffix.equals(XLSX) || suffix.equals(XLS))) {
            throw new CustomException(ResultTip.TIP_REDEEM_CODE_INPUT_EXCEL);
        }
        if (StringUtils.isBlank(id)) {
            throw new CustomException(ResultTip.TIP_REDEEM_CODE_ACTIVITY_ID_IS_EMPTY);
        }
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook Workbook = new XSSFWorkbook(inputStream);
        List<WeRedeemCodeImportDTO> redeemCodeImport = new ArrayList<>();
        List<WeRedeemCode> weRedeemCodeList = this.baseMapper.listWeRedeemCode(id);
        ImportRedeemCodeVO importRedeemCodeVO = buildImportResult(Workbook, redeemCodeImport, weRedeemCodeList, id);

        if (CollectionUtils.isNotEmpty(redeemCodeImport)) {
            this.baseMapper.batchInsert(redeemCodeImport);
        }
        return importRedeemCodeVO;
    }

    /**
     * 解析上传的Excel
     *
     * @param workbook         上传的Excel
     * @param redeemCodeImport 文件中符合条件的兑换码
     * @param weRedeemCodeList 数据库中原有的兑换码
     * @return
     */
    private ImportRedeemCodeVO buildImportResult(XSSFWorkbook workbook, List<WeRedeemCodeImportDTO> redeemCodeImport, List<WeRedeemCode> weRedeemCodeList, String activityId) {
        final String overSize = "导入失败,每次最多导入1000条兑换码";
        final String repeatCode = "兑换码重复导入, 或与原有兑换码重复, 请保证其唯一性";
        final String contentEmpty = "提货码/兑换码为空";
        final String formatError = "时间格式错误, 有效期格式为2022/7/1";
        final String effectiveTimeError = "有效日期小于当前日期";
        final String codeLengthError = "兑换码长度过长，最长不超过20字符";

        final int codesSize = 1000;
        StringBuilder failMsg = new StringBuilder();
        ImportRedeemCodeVO importRedeemCodeVO = new ImportRedeemCodeVO();
        //空行数量
        int emptyCount = 0;

        //校验重复code
        Map<String, String> verifyCode = weRedeemCodeList.stream().collect(Collectors.toMap(WeRedeemCode::getCode, WeRedeemCode::getActivityId));

        //读取第一张sheet
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum > codesSize) {
            failMsg.append(overSize);
        }
        //遍历每一行Excel内容
        for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            //兑换码判空
            if (ObjectUtil.isEmpty(row.getCell(0))) {
                emptyCount++;
                failMsg.append("第 ").append(rowNum + 1).append(" 行,").append(contentEmpty).append("\r\n");
                continue;
            }
            row.getCell(0).setCellType(CellType.STRING);
            String code = row.getCell(0).getStringCellValue();
            //判断兑换码长度，不能超过20
            if (code.length() > 20) {
                failMsg.append("第 ").append(rowNum + 1).append(" 行,").append(codeLengthError).append("\r\n");
                continue;
            }
            String effectiveTime;

            if (ObjectUtil.isNotEmpty(row.getCell(1))) {
                //如果是字符类型
                if (STRING.name().equals(row.getCell(1).getCellTypeEnum().name())) {
                    row.getCell(1).setCellType(CellType.STRING);
                    effectiveTime = row.getCell(1).getStringCellValue();
                } else if (DateUtil.isCellDateFormatted(row.getCell(1))) {
                    if (ObjectUtil.isEmpty(row.getCell(1).getDateCellValue())) {
                        effectiveTime = RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME;
                    } else {
                        Date date = row.getCell(1).getDateCellValue();
                        effectiveTime = DateFormatUtils.format(date, "yyyy/M/d");
                    }
                } else {
                    effectiveTime = row.getCell(1).getStringCellValue();
                }
                if (!RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME.equals(effectiveTime)) {
                    //验证时间格式
                    if (Boolean.TRUE.equals(!DateUtils.isMatchFormat(effectiveTime, "yyyy/MM/dd")
                            && !DateUtils.isMatchFormat(effectiveTime, "yyyy/MM/d")
                            && !DateUtils.isMatchFormat(effectiveTime, "yyyy/M/dd")
                            && !DateUtils.isMatchFormat(effectiveTime, "yyyy/M/d"))) {
                        failMsg.append("第 ").append(rowNum + 1).append(" 行,").append(formatError).append("\r\n");
                        continue;
                    }
                }
            } else {
                effectiveTime = RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME;
            }
            //验证code重复
            if (StringUtils.isNotEmpty(verifyCode.get(code))) {
                failMsg.append("第 ").append(rowNum + 1).append(" 行,").append(repeatCode).append("\r\n");
                continue;
            }
            verifyCode.put(code, activityId);
            WeRedeemCodeImportDTO weRedeemCodeImportDTO = WeRedeemCodeImportDTO.builder()
                    .activityId(Long.valueOf(activityId))
                    .code(code)
                    .effectiveTime(effectiveTime).build();
            redeemCodeImport.add(weRedeemCodeImportDTO);
        }

        if (lastRowNum == emptyCount) {
            throw new CustomException(ResultTip.TIP_REDEEM_CODE_FILE_DATA_IS_EMPTY);
        }

        importRedeemCodeVO.setSuccessNum(redeemCodeImport.size());
        importRedeemCodeVO.setFailNum(lastRowNum - redeemCodeImport.size());
        if (importRedeemCodeVO.getFailNum() > 0) {
            String suffix = "txt";
            String fileName = System.currentTimeMillis() + new Random().nextInt(codesSize) + ".txt";
            RuoYiConfig ruoyiConfig = SpringUtils.getBean(RuoYiConfig.class);
            CosConfig cosConfig = ruoyiConfig.getFile().getCos();
            try {
                String url = FileUploadUtils.upload2Cos(new ByteArrayInputStream(failMsg.toString().getBytes(StandardCharsets.UTF_8)), fileName, suffix, cosConfig);
                String imgUrlPrefix = ruoyiConfig.getFile().getCos().getCosImgUrlPrefix();
                importRedeemCodeVO.setUrl(imgUrlPrefix + url);
            } catch (IOException e) {
                log.error("兑换码导入失败报告上传异常：ex:{}", ExceptionUtils.getStackTrace(e));
            }
        }
        return importRedeemCodeVO;
    }

    /**
     * 新增兑换码
     *
     * @param weRedeemCodeDTO
     */
    @Override
    public void saveRedeemCode(WeRedeemCodeDTO weRedeemCodeDTO) {
        if (ObjectUtil.isNull(weRedeemCodeDTO)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        WeRedeemCode weRedeemCode = weRedeemCodeDTO.setAddOrUpdateWeRedeemCode();
        weRedeemCode.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        if (ObjectUtil.isNotNull(this.baseMapper.selectOne(weRedeemCode))) {
            throw new CustomException(ResultTip.TIP_REDEEM_CODE_REPEAT);
        }
        this.baseMapper.insertWeRedeemCode(weRedeemCode);
    }

    /**
     * 编辑修改兑换码
     *
     * @param weRedeemCodeDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRedeemCode(WeRedeemCodeDTO weRedeemCodeDTO) {
        if (ObjectUtil.isNull(weRedeemCodeDTO)) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        String corpId = weRedeemCodeDTO.getCorpId();
        WeRedeemCode weRedeemCode = weRedeemCodeDTO.setAddOrUpdateWeRedeemCode();
        if (StringUtils.isBlank(weRedeemCode.getEffectiveTime())) {
            weRedeemCode.setEffectiveTime(RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME);
        }
        if (StringUtils.isNotBlank(weRedeemCode.getReceiveUserId())) {
            //判断该客户是否有参与过活动, 且该活动是否限制再次参与
            WeRedeemCode getWeRedeemCode = WeRedeemCode.builder().activityId(String.valueOf(weRedeemCodeDTO.getActivityId())).receiveUserId(weRedeemCodeDTO.getReceiveUserId()).build();
            final WeRedeemCode selectWeRedeemCode = weRedeemCodeMapper.selectOne(getWeRedeemCode);
            final WeRedeemCodeActivityVO redeemCodeActivity = weRedeemCodeActivityService.getRedeemCodeActivity(corpId, Long.valueOf(weRedeemCode.getActivityId()));
            if (RedeemCodeConstants.REDEEM_CODE_ACTIVITY_LIMITED.equals(redeemCodeActivity.getEnableLimited()) && ObjectUtil.isNotEmpty(selectWeRedeemCode)) {
                throw new CustomException(ResultTip.TIP_REDEEM_CODE_ACTIVITY_LIMIT_ADD_USER);
            }
            //该客户符合分配兑换码条件
            RLock rLock = null;
            boolean isHaveLock = false;
            try {
                final String redeemCodeKey = RedeemCodeConstants.getRedeemCodeKey(corpId, weRedeemCode.getActivityId());
                rLock = LockUtil.getLock(redeemCodeKey);
                isHaveLock = rLock.tryLock(RedeemCodeConstants.CODE_WAIT_TIME, RedeemCodeConstants.CODE_LEASE_TIME, TimeUnit.SECONDS);
                if (isHaveLock) {
                    weRedeemCode.setStatus(RedeemCodeConstants.REDEEM_CODE_RECEIVED);
                    weRedeemCode.setRedeemTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()));
                    this.baseMapper.updateWeRedeemCode(weRedeemCode);
                }
            } catch (InterruptedException e) {
                log.error("[兑换码更新]兑换码更新兑换人获取锁失败,e:{},活动id:{},corpId:{}", ExceptionUtils.getStackTrace(e), weRedeemCodeDTO.getActivityId(), corpId);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("[兑换码更新]兑换码分配兑换人失败,e:{},活动id:{},corpId:{}", ExceptionUtils.getStackTrace(e), weRedeemCodeDTO.getActivityId(), corpId);
            }finally {
                if(rLock != null){
                    rLock.unlock();
                }
            }
            //告警员工
            alarmUser(corpId, weRedeemCodeDTO.getActivityId());
        } else {
            weRedeemCode.setStatus(RedeemCodeConstants.REDEEM_CODE_NOT_RECEIVED);
            weRedeemCode.setRedeemTime(RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME);
            this.baseMapper.updateWeRedeemCode(weRedeemCode);
        }
    }


    /**
     * 告警
     *
     * @param corpId
     * @param activityId
     */
    private void alarmUser(String corpId, Long activityId) {
        //查找活动，查看限制和库存已经告警员工
        WeRedeemCodeActivityVO redeemCodeActivity = weRedeemCodeActivityService.getRedeemCodeActivity(corpId, activityId);
        if (ObjectUtil.isNull(redeemCodeActivity)
                || ObjectUtil.isNull(redeemCodeActivity.getActivityName())
                || ObjectUtil.isNull(redeemCodeActivity.getRemainInventory())
                || ObjectUtil.isNull(redeemCodeActivity.getEnableLimited())) {
            log.info("【兑换码活动告警】,兑换码活动查询错误，活动id为{}", activityId);
            return;
        }
        if (RedeemCodeConstants.REDEEM_CODE_USER_ALARM.equals(redeemCodeActivity.getEnableAlarm())) {
            //等于限制发送告警员工
            final Integer remainInventory = redeemCodeActivity.getRemainInventory();
            final Integer alarmThreshold = redeemCodeActivity.getAlarmThreshold();
            log.info("兑换码活动id:{},剩余库存:{},告警阈值为{}", redeemCodeActivity.getId(), remainInventory, alarmThreshold);
            if (remainInventory.equals(alarmThreshold)) {
                log.info("兑换码活动{},库存不足,正在向员工告警", redeemCodeActivity.getId());
                if (CollectionUtils.isEmpty(redeemCodeActivity.getAlarmUserList())) {
                    log.info("[活码回调兑换码库存告警,员工为空]");
                } else {
                    String alarmMsg;
                    alarmMsg = RedeemCodeConstants.REDEEM_CODE_ALARM_MESSAGE_INFO.replaceAll(RedeemCodeConstants.REDEEM_CODE_ACTIVITY_NAME, redeemCodeActivity.getActivityName())
                            .replaceAll(RedeemCodeConstants.REDEEM_CODE_REAMIN_INVENTORY, String.valueOf(remainInventory));
                    redeemCodeActivity.getAlarmUserList().forEach(item -> {
                        toAlarmUser(corpId, item.getTargetId(), alarmMsg);
                    });
                }
            }
        }
    }

    /**
     * 发送消息给告警员工
     *
     * @param corpId
     * @param userId
     * @param msg
     */
    private void toAlarmUser(String corpId, String userId, String msg) {
        WeMessagePushDTO pushDto = new WeMessagePushDTO();
        WeCorpAccount validWeCorpAccount = corpAccountService.findValidWeCorpAccount(corpId);
        String agentId = validWeCorpAccount.getAgentId();
        // 文本消息
        TextMessageDTO text = new TextMessageDTO();
        StringBuilder content = new StringBuilder();
        //设置发送者 发送给企业员工
        pushDto.setTouser(userId);
        content.append(msg);
        text.setContent(content.toString());
        pushDto.setAgentid(Integer.valueOf(agentId));
        pushDto.setText(text);
        pushDto.setMsgtype(MessageType.TEXT.getMessageType());
        // 请求消息推送接口，获取结果 [消息推送 - 发送应用消息]
        log.debug("发送兑换码活动员工提醒信息：toUser:{}", userId);
        messagePushClient.sendMessageToUser(pushDto, agentId, corpId);
    }

    /**
     * 批量删除兑换码
     *
     * @param deleteDTO
     * @return
     */
    @Override
    public int batchRemoveRedeemCode(WeRedeemCodeDeleteDTO deleteDTO) {

        if (ObjectUtil.isNull(deleteDTO.getActivityId()) || CollectionUtils.isEmpty(deleteDTO.getCodeList())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        return this.baseMapper.delete(new LambdaQueryWrapper<WeRedeemCode>()
                .eq(WeRedeemCode::getActivityId, deleteDTO.getActivityId())
                .in(WeRedeemCode::getCode, deleteDTO.getCodeList()));
    }

    /**
     * 分页查询兑换码列表
     *
     * @param weRedeemCodeDTO
     * @return
     */
    @Override
    public List<WeRedeemCodeVO> getReemCodeList(WeRedeemCodeDTO weRedeemCodeDTO) {
        if (ObjectUtil.isNull(weRedeemCodeDTO) || ObjectUtil.isNull(weRedeemCodeDTO.getActivityId())) {
            throw new CustomException(ResultTip.TIP_GENERAL_BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(weRedeemCodeDTO.getReceiveName())) {
            List<WeCustomerVO> customers = weCustomerService.getCustomer(weRedeemCodeDTO.getCorpId(), weRedeemCodeDTO.getReceiveName());
            List<String> externalUsers = customers.stream().map(WeCustomerVO::getExternalUserid).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(externalUsers)) {
                return Collections.emptyList();
            }
            weRedeemCodeDTO.setExternalUserIdList(externalUsers);
        }
        startPage();
        List<WeRedeemCodeVO> weRedeemCodeList = this.baseMapper.selectWeRedeemCodeList(weRedeemCodeDTO);
        weRedeemCodeList.forEach(item -> {
            if (item.getRedeemTime().contains(RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME)) {
                item.setRedeemTime(StringUtils.EMPTY);
            }
            if (item.getEffectiveTime().contains(RedeemCodeConstants.REDEEM_CODE_EMPTY_TIME)) {
                item.setEffectiveTime(StringUtils.EMPTY);
            }
        });
        return weRedeemCodeList;
    }

    /**
     * 分页
     */
    public void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (com.easyink.common.utils.StringUtils.isNotNull(pageNum) && com.easyink.common.utils.StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

}
