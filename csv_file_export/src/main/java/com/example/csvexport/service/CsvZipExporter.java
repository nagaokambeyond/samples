package com.example.csvexport.service;

import com.example.csvexport.dao.OrderDao;
import com.example.csvexport.generated.entity.CsvExportJobs;
import com.example.csvexport.generated.entity.Orders;
import com.example.csvexport.model.OrderFilter;
import com.example.csvexport.storage.FileStorage;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class CsvZipExporter {
    // Excelで日本語CSVを開いたときの文字化けを防ぐためのUTF-8 BOM。
    private static final String UTF_8_BOM = "\uFEFF";

    private final OrderDao orders; private final FileStorage storage; private final ExportJobService jobs;
    private final TaskExecutor executor; private final int pageSize; private final int retentionHours;
    public CsvZipExporter(OrderDao orders, FileStorage storage, ExportJobService jobs, @Qualifier("exportExecutor") TaskExecutor executor,
                          @Value("${app.export.page-size}") int pageSize, @Value("${app.export.retention-hours}") int retentionHours) {
        this.orders = orders; this.storage = storage; this.jobs = jobs; this.executor = executor; this.pageSize = pageSize; this.retentionHours = retentionHours;
    }
    public void submit(CsvExportJobs job) { executor.execute(() -> run(job)); }
    private void run(CsvExportJobs job) {
        try {
            OrderFilter filter = jobs.readFilter(job);
            long count = writeZip(job, filter);
            jobs.complete(job, storage.complete(job.id), count, retentionHours);
        } catch (Exception e) { jobs.fail(job, e, retentionHours); }
    }
    private long writeZip(CsvExportJobs job, OrderFilter filter) throws Exception {
        long count = 0; java.time.LocalDateTime afterAt = null; Long afterId = null;
        String entry = "orders-" + job.snapshotAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".csv";
        try (var output = storage.createTemporary(job.id); var zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
            zip.putNextEntry(new ZipEntry(entry));
            var writer = new BufferedWriter(new OutputStreamWriter(zip, StandardCharsets.UTF_8));
            writer.write(UTF_8_BOM);
            CSVWriter csv = CsvRowWriter.create(writer);
            CsvRowWriter.writeHeader(csv);
            while (true) {
                List<Orders> page = orders.findNextForExport(filter, job.snapshotAt, afterAt, afterId, pageSize);
                if (page.isEmpty()) break;
                for (Orders order : page) { CsvRowWriter.writeOrder(csv, order); count++; }
                Orders last = page.getLast(); afterAt = last.orderedAt; afterId = last.id;
                csv.flush();
            }
            csv.flush(); zip.closeEntry();
        }
        return count;
    }
}
