package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "session_state")
public class SessionState extends AbstractSessionState {

    /** */
    @Column(name = "state_key")
    public String stateKey;

    /** */
    @Column(name = "state_command")
    public String stateCommand;
}
