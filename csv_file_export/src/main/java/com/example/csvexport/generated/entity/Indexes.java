package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "indexes")
public class Indexes extends AbstractIndexes {

    /** */
    @Column(name = "index_catalog")
    public String indexCatalog;

    /** */
    @Column(name = "index_schema")
    public String indexSchema;

    /** */
    @Column(name = "index_name")
    public String indexName;

    /** */
    @Column(name = "table_catalog")
    public String tableCatalog;

    /** */
    @Column(name = "table_schema")
    public String tableSchema;

    /** */
    @Column(name = "table_name")
    public String tableName;

    /** */
    @Column(name = "index_type_name")
    public String indexTypeName;

    /** */
    @Column(name = "nulls_distinct")
    public String nullsDistinct;

    /** */
    @Column(name = "is_generated")
    public Boolean isGenerated;

    /** */
    @Column(name = "remarks")
    public String remarks;

    /** */
    @Column(name = "index_class")
    public String indexClass;
}
