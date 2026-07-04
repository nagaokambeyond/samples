package com.example.demo.mybatis.typehandler;

import com.example.demo.data.domain.BookStockMovementSourceType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(BookStockMovementSourceType.class)
public class BookStockMovementSourceTypeHandler extends BaseTypeHandler<BookStockMovementSourceType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BookStockMovementSourceType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public BookStockMovementSourceType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final var value = rs.getInt(columnName);
        return rs.wasNull() ? null : BookStockMovementSourceType.of(value);
    }

    @Override
    public BookStockMovementSourceType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final var value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : BookStockMovementSourceType.of(value);
    }

    @Override
    public BookStockMovementSourceType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final var value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : BookStockMovementSourceType.of(value);
    }
}
