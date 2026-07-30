package com.example.demo

import java.lang.AutoCloseable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

class BookRowLock private constructor(
    private val connection: Connection,
    private val statement: PreparedStatement,
    private val resultSet: ResultSet
) : AutoCloseable {
    @Throws(SQLException::class)
    override fun close() {
        try {
            resultSet.close()
            statement.close()
        } finally {
            connection.rollback()
            connection.close()
        }
    }

    companion object {
        @Throws(SQLException::class)
        fun acquire(dataSource: DataSource, id: Long): BookRowLock {
            val connection = dataSource.getConnection()
            connection.setAutoCommit(false)
            val statement = connection.prepareStatement("select id from book where id = ? for update")
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()
            resultSet.next()
            return BookRowLock(connection, statement, resultSet)
        }
    }
}
