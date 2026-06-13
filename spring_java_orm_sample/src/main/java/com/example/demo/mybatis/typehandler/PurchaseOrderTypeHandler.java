package com.example.demo.mybatis.typehandler;

import com.example.demo.data.domain.PurchaseOrderType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(PurchaseOrderType.class)
@MappedJdbcTypes(value = JdbcType.INTEGER, includeNullJdbcType = true)
public class PurchaseOrderTypeHandler extends BaseTypeHandler<PurchaseOrderType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PurchaseOrderType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public PurchaseOrderType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : PurchaseOrderType.of(value);
    }

    @Override
    public PurchaseOrderType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : PurchaseOrderType.of(value);
    }

    @Override
    public PurchaseOrderType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : PurchaseOrderType.of(value);
    }
}
