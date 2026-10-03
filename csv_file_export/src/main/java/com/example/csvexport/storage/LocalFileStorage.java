package com.example.csvexport.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorage implements FileStorage {
    private final Path root;
    public LocalFileStorage(@Value("${app.export.storage-dir}") String storageDir) throws IOException {
        root = Path.of(storageDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }
    private Path temp(Long id) { return root.resolve("job-" + id + ".tmp"); }
    private Path finalPath(Long id) { return root.resolve("orders-export-" + id + ".zip"); }
    public OutputStream createTemporary(Long jobId) throws IOException { return Files.newOutputStream(temp(jobId)); }
    public String complete(Long jobId) throws IOException {
        Path destination = finalPath(jobId);
        Files.move(temp(jobId), destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return destination.getFileName().toString();
    }
    public InputStream open(String path) throws IOException { return Files.newInputStream(root.resolve(path).normalize()); }
    public void delete(String path) throws IOException { Files.deleteIfExists(root.resolve(path).normalize()); }
}
