package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "rights")
public class Rights extends AbstractRights {

    /** */
    @Column(name = "grantee")
    public String grantee;

    /** */
    @Column(name = "granteetype")
    public String granteetype;

    /** */
    @Column(name = "grantedrole")
    public String grantedrole;

    /** */
    @Column(name = "rights")
    public String rights;

    /** */
    @Column(name = "table_schema")
    public String tableSchema;

    /** */
    @Column(name = "table_name")
    public String tableName;
}
