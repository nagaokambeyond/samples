package com.example.demo.doma.generator.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import org.seasar.doma.jdbc.NoCacheSqlFileRepository;
import org.seasar.doma.jdbc.SqlFile;
import org.seasar.doma.jdbc.SqlFileRepository;
import org.seasar.doma.jdbc.dialect.Dialect;

/**
 * 
 */
public class BookGenreDaoTest {

    /** */
    protected SqlFileRepository repository;

    /** */
    protected Dialect dialect;

    /** */
    protected String url;

    /** */
    protected String user;

    /** */
    protected String password;

    @BeforeEach
    protected void setUp() {
        repository = new NoCacheSqlFileRepository();
        dialect = new org.seasar.doma.jdbc.dialect.H2Dialect();
        url = "jdbc:h2:mem:doma_codegen;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'src/main/resources/generator-schema.sql'";
        user = "sa";
        password = "";
    }

    /**
     * Executes a SQL file against the code generation schema.
     *
     * @param sqlFile SQL file to execute
     * @throws Exception when database access fails
     */
    protected void execute(SqlFile sqlFile) throws Exception {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlFile.getSql());
            }
            connection.rollback();
        }
    }

    /**
     * Returns a connection to the code generation schema.
     *
     * @return database connection
     * @throws Exception when database access fails
     */
    protected Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Verifies the selectById SQL file.
     *
     * @param testInfo JUnit test metadata
     * @throws Exception when SQL execution fails
     */
    @Test
    public void testSelectById(TestInfo testInfo) throws Exception {
        SqlFile sqlFile = repository.getSqlFile(testInfo.getTestMethod().orElseThrow(), "META-INF/com/example/demo/doma/generator/dao/BookGenreDao/selectById.sql", dialect);
        execute(sqlFile);
    }

    /**
     * Verifies the selectByIdAndVersion SQL file.
     *
     * @param testInfo JUnit test metadata
     * @throws Exception when SQL execution fails
     */
    @Test
    public void testSelectByIdAndVersion(TestInfo testInfo) throws Exception {
        SqlFile sqlFile = repository.getSqlFile(testInfo.getTestMethod().orElseThrow(), "META-INF/com/example/demo/doma/generator/dao/BookGenreDao/selectByIdAndVersion.sql", dialect);
        execute(sqlFile);
    }

}
