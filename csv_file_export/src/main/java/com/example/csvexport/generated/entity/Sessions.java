package com.example.csvexport.generated.entity;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "sessions")
public class Sessions extends AbstractSessions {

    /** */
    @Column(name = "session_id")
    public Integer sessionId;

    /** */
    @Column(name = "user_name")
    public String userName;

    /** */
    @Column(name = "server")
    public String server;

    /** */
    @Column(name = "client_addr")
    public String clientAddr;

    /** */
    @Column(name = "client_info")
    public String clientInfo;

    /** */
    @Column(name = "session_start")
    public String sessionStart;

    /** */
    @Column(name = "isolation_level")
    public String isolationLevel;

    /** */
    @Column(name = "executing_statement")
    public String executingStatement;

    /** */
    @Column(name = "executing_statement_start")
    public String executingStatementStart;

    /** */
    @Column(name = "contains_uncommitted")
    public Boolean containsUncommitted;

    /** */
    @Column(name = "session_state")
    public String sessionState;

    /** */
    @Column(name = "blocker_id")
    public Integer blockerId;

    /** */
    @Column(name = "sleep_since")
    public String sleepSince;
}
