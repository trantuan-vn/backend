package com.smartconsultor.microservice.gateway.verticle.config;

public class RocksDBConfig {

    private String basePath;           // Đường dẫn gốc chứa RocksDB instance cho từng topic
    private int maxOpenFiles = 512;    // Hạn chế số file mở
    private int blockCacheSizeMB = 64; // Cache đọc block trong RAM (MB)
    private int writeBufferSizeMB = 16; // Viết vào memtable trước khi flush (MB)
    private int maxBackgroundFlushes = 2; // Số thread nền để flush memtable
    private int maxBackgroundCompactions = 4; // Thread compaction

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public int getMaxOpenFiles() {
        return maxOpenFiles;
    }

    public void setMaxOpenFiles(int maxOpenFiles) {
        this.maxOpenFiles = maxOpenFiles;
    }

    public int getBlockCacheSizeMB() {
        return blockCacheSizeMB;
    }

    public void setBlockCacheSizeMB(int blockCacheSizeMB) {
        this.blockCacheSizeMB = blockCacheSizeMB;
    }

    public int getWriteBufferSizeMB() {
        return writeBufferSizeMB;
    }

    public void setWriteBufferSizeMB(int writeBufferSizeMB) {
        this.writeBufferSizeMB = writeBufferSizeMB;
    }

    public int getMaxBackgroundFlushes() {
        return maxBackgroundFlushes;
    }

    public void setMaxBackgroundFlushes(int maxBackgroundFlushes) {
        this.maxBackgroundFlushes = maxBackgroundFlushes;
    }

    public int getMaxBackgroundCompactions() {
        return maxBackgroundCompactions;
    }

    public void setMaxBackgroundCompactions(int maxBackgroundCompactions) {
        this.maxBackgroundCompactions = maxBackgroundCompactions;
    }
}
