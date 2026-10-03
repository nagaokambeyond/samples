package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "constants")
public class Constants extends AbstractConstants {

    /** */
    @Column(name = "constant_catalog")
    public String constantCatalog;

    /** */
    @Column(name = "constant_schema")
    public String constantSchema;

    /** */
    @Column(name = "constant_name")
    public String constantName;

    /** */
    @Column(name = "value_definition")
    public String valueDefinition;

    /** */
    @Column(name = "data_type")
    public String dataType;

    /** */
    @Column(name = "character_maximum_length")
    public Long characterMaximumLength;

    /** */
    @Column(name = "character_octet_length")
    public Long characterOctetLength;

    /** */
    @Column(name = "character_set_catalog")
    public String characterSetCatalog;

    /** */
    @Column(name = "character_set_schema")
    public String characterSetSchema;

    /** */
    @Column(name = "character_set_name")
    public String characterSetName;

    /** */
    @Column(name = "collation_catalog")
    public String collationCatalog;

    /** */
    @Column(name = "collation_schema")
    public String collationSchema;

    /** */
    @Column(name = "collation_name")
    public String collationName;

    /** */
    @Column(name = "numeric_precision")
    public Integer numericPrecision;

    /** */
    @Column(name = "numeric_precision_radix")
    public Integer numericPrecisionRadix;

    /** */
    @Column(name = "numeric_scale")
    public Integer numericScale;

    /** */
    @Column(name = "datetime_precision")
    public Integer datetimePrecision;

    /** */
    @Column(name = "interval_type")
    public String intervalType;

    /** */
    @Column(name = "interval_precision")
    public Integer intervalPrecision;

    /** */
    @Column(name = "maximum_cardinality")
    public Integer maximumCardinality;

    /** */
    @Column(name = "dtd_identifier")
    public String dtdIdentifier;

    /** */
    @Column(name = "declared_data_type")
    public String declaredDataType;

    /** */
    @Column(name = "declared_numeric_precision")
    public Integer declaredNumericPrecision;

    /** */
    @Column(name = "declared_numeric_scale")
    public Integer declaredNumericScale;

    /** */
    @Column(name = "geometry_type")
    public String geometryType;

    /** */
    @Column(name = "geometry_srid")
    public Integer geometrySrid;

    /** */
    @Column(name = "remarks")
    public String remarks;
}
