package com.example.demo.mybatis.typehandler

import com.example.demo.data.domain.BookStockMovementType
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@MappedTypes(BookStockMovementType::class)
class BookStockMovementTypeHandler : BaseTypeHandler<BookStockMovementType?>() {
    @Throws(SQLException::class)
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: BookStockMovementType?,
        jdbcType: JdbcType?
    ) {
        ps.setInt(i, parameter!!.value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String?): BookStockMovementType? {
        val value = rs.getInt(columnName)
        return if (rs.wasNull()) null else BookStockMovementType.of(value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): BookStockMovementType? {
        val value = rs.getInt(columnIndex)
        return if (rs.wasNull()) null else BookStockMovementType.of(value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): BookStockMovementType? {
        val value = cs.getInt(columnIndex)
        return if (cs.wasNull()) null else BookStockMovementType.of(value)
    }
}
