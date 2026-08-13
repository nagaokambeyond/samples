package com.example.demo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookRowLock implements AutoCloseable {
    private final Connection connection;
    private final PreparedStatement statement;
    private final ResultSet resultSet;

    private BookRowLock(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        this.connection = connection;
        this.statement = statement;
        this.resultSet = resultSet;
    }

    public static BookRowLock acquire(DataSource dataSource, Long id) throws SQLException {
        final var connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        final var statement = connection.prepareStatement("select id from book where id = ? for update");
        statement.setLong(1, id);
        final var resultSet = statement.executeQuery();
        resultSet.next();
        return new BookRowLock(connection, statement, resultSet);
    }

    @Override
    public void close() throws SQLException {
        try {
            resultSet.close();
            statement.close();
        } finally {
            connection.rollback();
            connection.close();
        }
    }
}
