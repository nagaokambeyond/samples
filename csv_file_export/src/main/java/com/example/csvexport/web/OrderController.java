package com.example.csvexport.web;

import com.example.csvexport.dao.OrderDao;
import com.example.csvexport.generated.entity.CsvExportJobs;
import com.example.csvexport.model.OrderFilter;
import com.example.csvexport.service.ExportJobService;
import com.example.csvexport.storage.FileStorage;
import java.io.InputStream;
import java.security.Principal;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
    private final OrderDao orders; private final ExportJobService jobs; private final FileStorage storage;
    public OrderController(OrderDao orders, ExportJobService jobs, FileStorage storage) { this.orders = orders; this.jobs = jobs; this.storage = storage; }
    @GetMapping("/")
    public String index(@ModelAttribute OrderFilter filter, Principal principal, Model model) {
        filter = filter == null ? OrderFilter.empty() : filter;
        model.addAttribute("filter", filter);
        model.addAttribute("orders", orders.findPreview(filter));
        model.addAttribute("jobs", jobs.ownedJobs(principal.getName()));
        return "index";
    }
    @PostMapping("/exports")
    public String createExport(@ModelAttribute OrderFilter filter, Principal principal) {
        jobs.create(principal.getName(), filter);
        return "redirect:/";
    }
    @GetMapping("/api/exports/{id}") @ResponseBody
    public ResponseEntity<ExportJobResponse> job(@PathVariable Long id, Principal principal) {
        CsvExportJobs job = jobs.ownedJob(id, principal.getName());
        return job == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(ExportJobResponse.from(job));
    }
    @GetMapping("/exports/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id, Principal principal) throws Exception {
        CsvExportJobs job = jobs.ownedJob(id, principal.getName());
        if (job == null || !"COMPLETED".equals(job.status) || job.filePath == null) return ResponseEntity.notFound().build();
        InputStream stream = storage.open(job.filePath);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("orders-export-" + id + ".zip").build().toString())
                .body(new InputStreamResource(stream));
    }
}
