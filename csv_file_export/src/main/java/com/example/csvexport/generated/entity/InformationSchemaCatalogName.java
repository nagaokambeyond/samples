package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "information_schema_catalog_name")
public class InformationSchemaCatalogName extends AbstractInformationSchemaCatalogName {

    /** */
    @Column(name = "catalog_name")
    public String catalogName;
}
