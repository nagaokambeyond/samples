package com.example.javaobjectmapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "benchmark")
public class BenchmarkProperties {

    private boolean autoRun = true;

    private int defaultRecordCount = 100_000;

    private int chunkSize = 1_000;

    public boolean isAutoRun() {
        return autoRun;
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    public int getDefaultRecordCount() {
        return defaultRecordCount;
    }

    public void setDefaultRecordCount(int defaultRecordCount) {
        this.defaultRecordCount = defaultRecordCount;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
