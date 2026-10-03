package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "roles")
public class Roles extends AbstractRoles {

    /** */
    @Column(name = "role_name")
    public String roleName;

    /** */
    @Column(name = "remarks")
    public String remarks;
}
