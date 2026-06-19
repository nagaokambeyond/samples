package com.example.demo.mybatis.typehandler;

import com.example.demo.data.domain.PurchaseInvoiceType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(PurchaseInvoiceType.class)
@MappedJdbcTypes(value = JdbcType.INTEGER, includeNullJdbcType = true)
public class PurchaseInvoiceTypeHandler extends BaseTypeHandler<PurchaseInvoiceType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PurchaseInvoiceType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public PurchaseInvoiceType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : PurchaseInvoiceType.of(value);
    }

    @Override
    public PurchaseInvoiceType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : PurchaseInvoiceType.of(value);
    }

    @Override
    public PurchaseInvoiceType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : PurchaseInvoiceType.of(value);
    }
}
