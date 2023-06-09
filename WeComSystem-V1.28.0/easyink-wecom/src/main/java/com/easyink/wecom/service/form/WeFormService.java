package com.easyink.wecom.service.form;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.dto.common.AttachmentParam;
import com.easyink.wecom.domain.dto.form.*;
import com.easyink.wecom.domain.entity.form.WeForm;
import com.easyink.wecom.domain.query.form.FormQuery;
import com.easyink.wecom.domain.vo.form.*;
import com.easyink.wecom.domain.vo.form.FormContentVO;
import com.easyink.wecom.domain.vo.form.FormDetailViewVO;
import com.easyink.wecom.domain.vo.form.FormPageVO;
import com.easyink.wecom.domain.vo.form.FormTotalView;
import com.easyink.wecom.domain.vo.form.PromotionalVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 表单表(WeForm)表服务接口
 *
 * @author tigger
 * @since 2023-01-09 15:00:46
 */
public interface WeFormService extends IService<WeForm> {

    /**
     * 根据分组id删除表单
     *
     * @param idList 分组id列表
     * @param corpId 企业id
     */
    void deleteFormByGroupId(List<Integer> idList, String corpId);

    /**
     * 批量删除表单
     *
     * @param deleteIdList 表单列表(全量数据)
     */
    void deleteBatchForm(List<Long> deleteIdList);

    /**
     * 新增表单
     *
     * @param addDTO dto
     * @param corpId 企业id
     */
    void saveForm(FormAddRequestDTO addDTO, String corpId);

    /**
     * 插入表单
     *
     * @param form   新增表单DTO
     * @param corpId 企业id
     * @return 表单主键id
     */
    Long saveFormReturnId(FormAddDTO form, String corpId);

    /**
     * 更新表单
     *
     * @param updateDTO 更新表单DTO
     * @param corpId    企业id
     */
    void updateForm(FormUpdateRequestDTO updateDTO, String corpId);

    /**
     * 删除表单
     *
     * @param idList 表单id列表
     * @param corpId
     */
    void deleteForm(List<Long> idList, String corpId);

    /**
     * 表单分页
     *
     * @param formQuery 查询QUERY
     * @param corpId
     * @return PageInfo
     */
    List<FormPageVO> getPage(FormQuery formQuery, String corpId);

    /**
     * 表单分页
     *
     * @param formQuery 查询QUERY
     * @param corpId    企业id
     * @param pageFlag  是否分页
     * @return PageInfo
     */
    List<FormPageVO> getPage(FormQuery formQuery, String corpId, Boolean pageFlag);


    /**
     * 表单列表
     *
     * @param formQuery 查询QUERY
     * @param corpId
     * @return PageInfo
     */
    List<FormPageVO> getList(FormQuery formQuery, String corpId);

    /**
     * 表单详情
     *
     * @param id     表单id
     * @param corpId 企业id
     * @return 详情VO
     */
    FormDetailViewVO getDetail(Long id, String corpId);

    /**
     * 批量修改分组
     *
     * @param batchDTO batchDTO
     * @param corpId   企业id
     */
    void updateBatchGroup(BatchUpdateGroupDTO batchDTO, String corpId);

    /**
     * 启用禁用表单
     *
     * @param id         表单id
     * @param enableFlag 启用禁用标识
     * @param corpId     企业id
     */
    void enableForm(Long id, Boolean enableFlag, String corpId);

    /**
     * 表单数据总览
     *
     * @param id     表单id
     * @param corpId 企业id
     * @return FormTotalView
     */
    FormTotalView totalView(Long id, String corpId);

    /**
     * 获取编辑详情
     *
     * @param id     表单id
     * @param corpId 企业id
     * @return
     */
    WeFormEditDetailVO getEditDetail(Long id, String corpId);


    /**
     * 生成表单url
     *
     * @param formId        表单id
     * @param corpId        企业id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    String genFormUrl(Long formId, String corpId, String userId, Integer channelType);

    /**
     * 侧边栏获取表单链接
     *
     * @param formId        表单id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    String sideBarGenFormUrl(Long formId, String userId, Integer channelType);

    /**
     * 生成表单附件
     *
     * @param formId        表单id
     * @param corpId        企业id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    AttachmentParam getFormAttachment(Long formId, String corpId, String userId, Integer channelType);

    /**
     * 生成短链
     *
     * @param url           表单url
     * @param formId        表单id
     * @param corpId        企业id
     * @param userId        员工id
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return
     */
    String genShortUrl(String url, Long formId, String corpId, String userId, Integer channelType);

    /**
     * 中间页获取表单内容
     *
     * @param formId        表单id
     * @param userId        用户id
     * @param openId        公众号用户openId
     * @param channelType   {@link com.easyink.wecom.domain.enums.form.FormChannelEnum}
     * @return {@link FormContentVO}
     */
    FormContentVO getContent(Long formId, String userId, String openId, Integer channelType);

    /**
     * 提交表单
     *
     * @param formCommitDTO {@link FormCommitDTO}
     */
    void commit(FormCommitDTO formCommitDTO);

    /**
     * 推广
     *
     * @param formId        表单id
     * @param response      响应
     * @return
     */
    PromotionalVO promotion(Long formId, HttpServletResponse response);

}

