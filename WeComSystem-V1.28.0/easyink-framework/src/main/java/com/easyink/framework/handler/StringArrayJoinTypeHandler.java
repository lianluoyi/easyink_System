package com.easyink.framework.handler;

import com.easyink.common.utils.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @description: Mybatis数组转字符串a, b, c, d, e
 * @author admin
 * @create: 2020-09-08 17:19
 **/
@MappedTypes(value = {Integer[].class, Short[].class, Long[].class, String[].class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class StringArrayJoinTypeHandler extends BaseTypeHandler<String[]> {

    String split = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType)
            throws SQLException {

        ps.setString(i, StringUtils.join(parameter, split));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String reString = rs.getString(columnName);
        return getStrings(reString);
    }

    /**
     * 获取字符串数组 为空返回null
     * @param reString 目标字符串
     * @return 数组或null
     */
    private String[] getStrings(String reString){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(reString)){
            return reString.split(split);
        }
        return new String[0];
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String reString = rs.getString(columnIndex);
        return getStrings(reString);
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String reString = cs.getString(columnIndex);
        return getStrings(reString);
    }

}

