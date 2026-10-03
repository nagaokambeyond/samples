package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "enum_values")
public class EnumValues extends AbstractEnumValues {

    /** */
    @Column(name = "object_catalog")
    public String objectCatalog;

    /** */
    @Column(name = "object_schema")
    public String objectSchema;

    /** */
    @Column(name = "object_name")
    public String objectName;

    /** */
    @Column(name = "object_type")
    public String objectType;

    /** */
    @Column(name = "enum_identifier")
    public String enumIdentifier;

    /** */
    @Column(name = "value_name")
    public String valueName;

    /** */
    @Column(name = "value_ordinal")
    public String valueOrdinal;
}
