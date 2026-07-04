package com.example.demo.mybatis.typehandler;

import com.example.demo.data.domain.BookStockMovementType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(BookStockMovementType.class)
public class BookStockMovementTypeHandler extends BaseTypeHandler<BookStockMovementType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BookStockMovementType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public BookStockMovementType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final var value = rs.getInt(columnName);
        return rs.wasNull() ? null : BookStockMovementType.of(value);
    }

    @Override
    public BookStockMovementType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final var value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : BookStockMovementType.of(value);
    }

    @Override
    public BookStockMovementType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final var value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : BookStockMovementType.of(value);
    }
}
