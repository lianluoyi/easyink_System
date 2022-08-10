package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeMyApplication;
import com.easywecom.wecom.domain.vo.MyApplicationIntroductionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: WeMyApplicationMapper
 *
 * @author: 1*+
 * @date: 2021-09-10 9:23
 */
@Repository
public interface WeMyApplicationMapper extends BaseMapper<WeMyApplication> {


    /**
     * 获取企业我的应用列表
     *
     * @param corpId 企业ID
     * @return {@link List<MyApplicationIntroductionVO>}
     */
    List<MyApplicationIntroductionVO> listOfMyApplication(@Param("corpId") String corpId);

    /**
     * 获取企业我的应用列表
     * 增加了sidebar_redirect_url 不为空的判断
     *
     * @param corpId 企业ID
     * @return {@link List<MyApplicationIntroductionVO>}
     */
    List<MyApplicationIntroductionVO> listOfMyApplication2Sidebar(@Param("corpId") String corpId);


    /**
     * 获取我的应用详情
     *
     * @param corpId 企业ID
     * @param appid  应用ID
     * @return {@link MyApplicationIntroductionVO}
     */
    MyApplicationIntroductionVO getMyApplication(@Param("corpId") String corpId, @Param("appid") Integer appid);

    /**
     * 获取企业我的应用列表
     *
     * @param appid 应用ID
     * @return {@link List<MyApplicationIntroductionVO>}
     */
    List<MyApplicationIntroductionVO> listOfMyApplicationByAppid(@Param("appid") Integer appid);


}
