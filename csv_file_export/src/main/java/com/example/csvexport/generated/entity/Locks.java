package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "locks")
public class Locks extends AbstractLocks {

    /** */
    @Column(name = "table_schema")
    public String tableSchema;

    /** */
    @Column(name = "table_name")
    public String tableName;

    /** */
    @Column(name = "session_id")
    public Integer sessionId;

    /** */
    @Column(name = "lock_type")
    public String lockType;
}
