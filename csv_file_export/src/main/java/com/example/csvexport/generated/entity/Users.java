package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "users")
public class Users extends AbstractUsers {

    /** */
    @Column(name = "user_name")
    public String userName;

    /** */
    @Column(name = "is_admin")
    public Boolean isAdmin;

    /** */
    @Column(name = "remarks")
    public String remarks;
}
