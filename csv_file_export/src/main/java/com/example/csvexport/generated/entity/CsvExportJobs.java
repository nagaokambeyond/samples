package com.example.csvexport.generated.entity;

import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 * 
 */
@Entity(metamodel = @Metamodel)
@Table(name = "csv_export_jobs")
public class CsvExportJobs extends AbstractCsvExportJobs {

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    /** */
    @Column(name = "owner_username")
    public String ownerUsername;

    /** */
    @Column(name = "status")
    public String status;

    /** */
    @Column(name = "filter_json")
    public String filterJson;

    /** */
    @Column(name = "snapshot_at")
    public LocalDateTime snapshotAt;

    /** */
    @Column(name = "file_path")
    public String filePath;

    /** */
    @Column(name = "record_count")
    public Long recordCount;

    /** */
    @Column(name = "error_message")
    public String errorMessage;

    /** */
    @Column(name = "created_at")
    public LocalDateTime createdAt;

    /** */
    @Column(name = "started_at")
    public LocalDateTime startedAt;

    /** */
    @Column(name = "completed_at")
    public LocalDateTime completedAt;

    /** */
    @Column(name = "expires_at")
    public LocalDateTime expiresAt;
}
