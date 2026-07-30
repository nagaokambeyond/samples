package com.example.demo.mybatis.typehandler

import com.example.demo.data.domain.PurchaseInvoiceType
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.MappedJdbcTypes
import org.apache.ibatis.type.MappedTypes
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@MappedTypes(PurchaseInvoiceType::class)
@MappedJdbcTypes(value = [JdbcType.INTEGER], includeNullJdbcType = true)
class PurchaseInvoiceTypeHandler : BaseTypeHandler<PurchaseInvoiceType?>() {
    @Throws(SQLException::class)
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: PurchaseInvoiceType?,
        jdbcType: JdbcType?
    ) {
        ps.setInt(i, parameter!!.value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String?): PurchaseInvoiceType? {
        val value = rs.getInt(columnName)
        return if (rs.wasNull()) null else PurchaseInvoiceType.of(value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): PurchaseInvoiceType? {
        val value = rs.getInt(columnIndex)
        return if (rs.wasNull()) null else PurchaseInvoiceType.of(value)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): PurchaseInvoiceType? {
        val value = cs.getInt(columnIndex)
        return if (cs.wasNull()) null else PurchaseInvoiceType.of(value)
    }
}
