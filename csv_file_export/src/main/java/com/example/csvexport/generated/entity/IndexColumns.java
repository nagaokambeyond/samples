package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "index_columns")
public class IndexColumns extends AbstractIndexColumns {

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
    @Column(name = "column_name")
    public String columnName;

    /** */
    @Column(name = "ordinal_position")
    public Integer ordinalPosition;

    /** */
    @Column(name = "ordering_specification")
    public String orderingSpecification;

    /** */
    @Column(name = "null_ordering")
    public String nullOrdering;

    /** */
    @Column(name = "is_unique")
    public Boolean isUnique;
}
