package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "synonyms")
public class Synonyms extends AbstractSynonyms {

    /** */
    @Column(name = "synonym_catalog")
    public String synonymCatalog;

    /** */
    @Column(name = "synonym_schema")
    public String synonymSchema;

    /** */
    @Column(name = "synonym_name")
    public String synonymName;

    /** */
    @Column(name = "synonym_for")
    public String synonymFor;

    /** */
    @Column(name = "synonym_for_schema")
    public String synonymForSchema;

    /** */
    @Column(name = "type_name")
    public String typeName;

    /** */
    @Column(name = "status")
    public String status;

    /** */
    @Column(name = "remarks")
    public String remarks;
}
