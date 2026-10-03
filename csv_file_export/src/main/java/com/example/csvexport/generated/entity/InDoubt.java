package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "in_doubt")
public class InDoubt extends AbstractInDoubt {

    /** */
    @Column(name = "transaction_name")
    public String transactionName;

    /** */
    @Column(name = "transaction_state")
    public String transactionState;
}
