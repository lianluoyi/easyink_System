package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeMsgTlp;
import com.easywecom.wecom.domain.vo.welcomemsg.WeMsgTlpListVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 欢迎语模板Mapper接口
 *
 * @author admin
 * @date 2020-10-04
 */
@Repository
public interface WeMsgTlpMapper extends BaseMapper<WeMsgTlp> {

    /**
     * 查询欢迎语模板列表
     *
     * @param weMsgTlp 欢迎语模板
     * @return 欢迎语模板集合
     */
    List<WeMsgTlpListVO> selectWeMsgTlpList(WeMsgTlp weMsgTlp);

    /**
     * 删除欢迎语模板
     *
     * @param id 欢迎语模板ID
     * @return 结果
     */
    int deleteWeMsgTlpById(@Param("corpId") String corpId, @Param("id") Long id);

    /**
     * 查询当前员工最新的欢迎语
     *
     * @param userId 员工id
     * @param corpId 企业id
     */
    WeMsgTlp selectLatestByUserId(@Param("userId") String userId, @Param("corpId") String corpId);

    /**
     * 群欢迎语素材统计
     *
     * @param corpId
     * @return
     */
    Integer groupMaterialCount(@Param("corpId") String corpId);

}
