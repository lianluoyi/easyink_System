package com.easywecom.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easywecom.wecom.domain.WeExternalUserMappingUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名: WeExternalUserMappingUserMapper
 *
 * @author: 1*+
 * @date: 2021-11-30 14:31
 */
@Repository
@Mapper
public interface WeExternalUserMappingUserMapper extends BaseMapper<WeExternalUserMappingUser> {


    void saveOrUpdateBatch(List<WeExternalUserMappingUser> list);


}
