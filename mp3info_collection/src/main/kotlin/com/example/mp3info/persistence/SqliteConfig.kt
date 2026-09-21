package com.example.mp3info.persistence

import org.seasar.doma.jdbc.Config
import org.seasar.doma.jdbc.dialect.SqliteDialect
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource
import org.seasar.doma.jdbc.tx.LocalTransactionManager
import org.seasar.doma.jdbc.tx.TransactionManager
import java.nio.file.Path
import javax.sql.DataSource

class SqliteConfig(databasePath: Path) : Config {
    private val dataSource = LocalTransactionDataSource("jdbc:sqlite:${databasePath.toAbsolutePath()}", "", "")
    private val transactionManager = LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()))

    override fun getDialect() = SqliteDialect()

    override fun getDataSource(): DataSource = dataSource

    override fun getTransactionManager(): TransactionManager = transactionManager
}
