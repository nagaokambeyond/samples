package com.example.csvexport.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileStorage {
    OutputStream createTemporary(Long jobId) throws IOException;
    String complete(Long jobId) throws IOException;
    InputStream open(String path) throws IOException;
    void delete(String path) throws IOException;
}
