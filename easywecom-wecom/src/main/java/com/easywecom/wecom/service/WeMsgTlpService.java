package com.easywecom.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easywecom.wecom.domain.WeMsgTlp;
import com.easywecom.wecom.domain.vo.welcomemsg.WeEmployMaterialVO;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgAddDTO;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgUpdateEmployDTO;
import com.easywecom.wecom.domain.dto.welcomemsg.WelComeMsgUpdateGroupDTO;
import com.easywecom.wecom.domain.vo.welcomemsg.WeMsgTlpListVO;
import com.easywecom.wecom.domain.vo.welcomemsg.WelcomeMsgGroupMaterialCountVO;

import java.util.List;

/**
 * 欢迎语模板Service接口
 *
 * @author admin
 * @date 2020-10-04
 */
public interface WeMsgTlpService extends IService<WeMsgTlp> {

    /**
     * 查询欢迎语模板列表
     *
     * @param weMsgTlp 欢迎语模板
     * @return 欢迎语模板集合
     */
    List<WeMsgTlpListVO> selectWeMsgTlpList(WeMsgTlp weMsgTlp);

    /**
     * 新增员工欢迎语模板
     *
     * @param welComeMsgAddDTO 欢迎语模板
     * @return 结果
     */
    void insertWeMsgTlpWithEmploy(WelComeMsgAddDTO welComeMsgAddDTO);

    /**
     * 新增群欢迎语模板
     *
     * @param welComeMsgAddDTO
     */
    void insertWeMsgTlpWithGroup(WelComeMsgAddDTO welComeMsgAddDTO);

    /**
     * 详情
     *
     * @param weMsgTlp 默认欢迎语id
     * @return
     */
    WeMsgTlpListVO detail(WeMsgTlp weMsgTlp);

    /**
     * 修改好友欢迎语
     *
     * @param welComeMsgUpdateDTO
     */
    void updateWeMsgTlpWithEmploy(WelComeMsgUpdateEmployDTO welComeMsgUpdateDTO);

    /**
     * 修改群欢迎语
     *
     * @param welComeMsgUpdateDTO
     */
    void updateWeMsgTlpWithGroup(WelComeMsgUpdateGroupDTO welComeMsgUpdateDTO);

    /**
     * 查询欢迎语素材
     *
     * @param userId 员工id
     * @param corpId 企业id
     * @return
     */
    WeEmployMaterialVO selectMaterialByUserId(String userId, String corpId);

    /**
     * 群欢迎语素材统计
     *
     * @param corpId 企业id
     * @return
     */
    WelcomeMsgGroupMaterialCountVO groupCount(String corpId);

    /**
     * 删除好友欢迎语模板信息
     *
     * @param corpId 企业id
     * @param ids    欢迎语ids
     */
    void deleteEmployWeMsgTlpById(String corpId, List<Long> ids);

    /**
     * 删除好友欢迎语模板信息
     *
     * @param corpId 企业id
     * @param ids    欢迎语ids
     */
    void deleteGroupWeMsgTlpById(String corpId, List<Long> ids);
}
