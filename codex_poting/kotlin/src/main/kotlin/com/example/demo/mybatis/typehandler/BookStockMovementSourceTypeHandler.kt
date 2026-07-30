package com.example.demo.mybatis.typehandler

import com.example.demo.data.domain.BookStockMovementSourceType
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@MappedTypes(BookStockMovementSourceType::class)
class BookStockMovementSourceTypeHandler : BaseTypeHandler<BookStockMovementSourceType?>() {
    @Throws(SQLException::class)
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: BookStockMovementSourceType?,
        jdbcType: JdbcType?
    ) {
        ps.setInt(i, parameter!!.value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String?): BookStockMovementSourceType? {
        val value = rs.getInt(columnName)
        return if (rs.wasNull()) null else BookStockMovementSourceType.of(value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): BookStockMovementSourceType? {
        val value = rs.getInt(columnIndex)
        return if (rs.wasNull()) null else BookStockMovementSourceType.of(value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): BookStockMovementSourceType? {
        val value = cs.getInt(columnIndex)
        return if (cs.wasNull()) null else BookStockMovementSourceType.of(value)
    }
}
