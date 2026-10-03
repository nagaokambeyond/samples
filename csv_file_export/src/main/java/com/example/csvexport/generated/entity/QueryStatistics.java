package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "query_statistics")
public class QueryStatistics extends AbstractQueryStatistics {

    /** */
    @Column(name = "sql_statement")
    public String sqlStatement;

    /** */
    @Column(name = "execution_count")
    public Integer executionCount;

    /** */
    @Column(name = "min_execution_time")
    public Double minExecutionTime;

    /** */
    @Column(name = "max_execution_time")
    public Double maxExecutionTime;

    /** */
    @Column(name = "cumulative_execution_time")
    public Double cumulativeExecutionTime;

    /** */
    @Column(name = "average_execution_time")
    public Double averageExecutionTime;

    /** */
    @Column(name = "std_dev_execution_time")
    public Double stdDevExecutionTime;

    /** */
    @Column(name = "min_row_count")
    public Long minRowCount;

    /** */
    @Column(name = "max_row_count")
    public Long maxRowCount;

    /** */
    @Column(name = "cumulative_row_count")
    public Long cumulativeRowCount;

    /** */
    @Column(name = "average_row_count")
    public Double averageRowCount;

    /** */
    @Column(name = "std_dev_row_count")
    public Double stdDevRowCount;
}
