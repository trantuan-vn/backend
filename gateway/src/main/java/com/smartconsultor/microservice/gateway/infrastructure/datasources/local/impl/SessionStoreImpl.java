package com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl;

import com.google.common.hash.Hashing;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SessionStore;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.verticle.config.RocksDBConfig;
import io.vertx.core.*;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStoreImpl implements SessionStore {
    private static final Logger log = LoggerFactory.getLogger(SessionStoreImpl.class);

    private final Vertx vertx;
    private final Map<String, RocksDB> dbs = new ConcurrentHashMap<>();
    private final WriteOptions writeOptions;
    private final ReadOptions readOptions;
    private final Options options;
    private final SlotManager slotManager;
    private final AppConfig appConfig;

    @Inject
    public SessionStoreImpl(Vertx vertx, AppConfig appConfig, SlotManager slotManager) {
        this.vertx = vertx;
        this.appConfig = appConfig;
        this.slotManager = slotManager;

        RocksDBConfig config = appConfig.getRocksdb();
        this.options = buildOptions(config);
        this.writeOptions = new WriteOptions()
                .setSync(false)
                .setDisableWAL(false); 
        this.readOptions = new ReadOptions()
                .setVerifyChecksums(false); // Tăng tốc độ đọc
    }

    private Options buildOptions(RocksDBConfig config) {
        BlockBasedTableConfig tableConfig = new BlockBasedTableConfig()
                .setBlockCacheSize(config.getBlockCacheSizeMB() * 1024L * 1024L)
                .setCacheIndexAndFilterBlocks(true);
        return new Options()
                .setCreateIfMissing(true)
                .setUseFsync(false)
                .setMaxOpenFiles(config.getMaxOpenFiles())
                .setWriteBufferSize(config.getWriteBufferSizeMB() * 1024L * 1024L)
                .setMaxWriteBufferNumber(3)
                .setMaxBackgroundFlushes(config.getMaxBackgroundFlushes())
                .setMaxBackgroundCompactions(config.getMaxBackgroundCompactions())
                .setCompactionStyle(CompactionStyle.UNIVERSAL)
                .setLevelCompactionDynamicLevelBytes(true)
                .setTableFormatConfig(tableConfig);
    }

    private RocksDB initDB(String topic) {
        String dbPath = appConfig.getRocksdb().getBasePath() + "/" + topic;
        File dbDir = new File(dbPath);
        if (!dbDir.exists() && !dbDir.mkdirs()) {
            log.error("Cannot create RocksDB path: {}", dbPath);
            throw new SessionStoreException("Failed to create database directory for topic: " + topic);
        }
        try {
            RocksDB db = RocksDB.open(options, dbPath);
            dbs.putIfAbsent(topic, db);
            log.info("Initialized RocksDB for topic: {}", topic);
            return db;
        } catch (RocksDBException e) {
            log.error("Failed to open RocksDB for topic: {}", topic, e);
            throw new SessionStoreException("Failed to initialize database for topic: " + topic, e);
        }
    }

    private Future<RocksDB> getDB(String userId) {
        validateInput(userId);
        return slotManager.getTopicOfUser(userId).compose(topic -> {
            RocksDB db = dbs.computeIfAbsent(topic, this::initDB);
            return Future.succeededFuture(db);
        });
    }

    private void validateInput(String input) {
        if (input == null || input.isEmpty() || !input.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid input: " + input);
        }
    }

    private byte[] key(String userId, String deviceId, String appId, long connIndex, long seqId) {
        validateInput(userId);
        validateInput(deviceId);
        validateInput(appId);
        String hashedUserId = Hashing.sha256().hashString(userId, StandardCharsets.UTF_8).toString();
        return (hashedUserId + ":" + deviceId + ":" + appId + ":" + connIndex + ":" + seqId)
                .getBytes(StandardCharsets.UTF_8);
    }

    private String composeKeyPrefix(String userId, String deviceId, String appId, long connIndex) {
        validateInput(userId);
        validateInput(deviceId);
        validateInput(appId);
        String hashedUserId = Hashing.sha256().hashString(userId, StandardCharsets.UTF_8).toString();
        return hashedUserId + ":" + deviceId + ":" + appId + ":" + connIndex + ":";
    }

    @Override
    public Future<Void> save(String userId, String deviceId, String appId, long connIndex, long seqId, byte[] stateData) {
        if (stateData == null) {
            return Future.failedFuture(new IllegalArgumentException("State data cannot be null"));
        }
        return getDB(userId).compose(db ->
                executeBlockingVoid(() -> {
                    db.put(writeOptions, key(userId, deviceId, appId, connIndex, seqId), stateData);
                    log.debug("Saved state for userId: {}, deviceId: {}, appId: {}, connIndex: {}, seqId: {}",
                            userId, deviceId, appId, connIndex, seqId);
                })
        );
    }

    @Override
    public Future<byte[]> get(String userId, String deviceId, String appId, long connIndex, long seqId) {
        return getDB(userId).compose(db ->
                executeBlocking(() -> {
                    byte[] result = db.get(readOptions, key(userId, deviceId, appId, connIndex, seqId));
                    log.debug("Retrieved state for userId: {}, deviceId: {}, appId: {}, connIndex: {}, seqId: {}",
                            userId, deviceId, appId, connIndex, seqId);
                    return result;
                })
        );
    }

    @Override
    public Future<Void> delete(String userId, String deviceId, String appId, long connIndex, long seqId) {
        return getDB(userId).compose(db ->
                executeBlockingVoid(() -> {
                    db.delete(writeOptions, key(userId, deviceId, appId, connIndex, seqId));
                    log.debug("Deleted state for userId: {}, deviceId: {}, appId: {}, connIndex: {}, seqId: {}",
                            userId, deviceId, appId, connIndex, seqId);
                })
        );
    }

    @Override
    public Future<Void> saveBatch(String userId, String deviceId, String appId, long connIndex, Map<Long, byte[]> batchData) {
        if (batchData == null || batchData.isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("Batch data cannot be null or empty"));
        }
        return getDB(userId).compose(db ->
                executeBlockingVoid(() -> {
                    try (WriteBatch batch = new WriteBatch()) {
                        for (Map.Entry<Long, byte[]> entry : batchData.entrySet()) {
                            byte[] key = key(userId, deviceId, appId, connIndex, entry.getKey());
                            batch.put(key, entry.getValue());
                        }
                        db.write(writeOptions, batch);
                        log.debug("Saved batch for userId: {}, deviceId: {}, appId: {}, connIndex: {}, size: {}",
                                userId, deviceId, appId, connIndex, batchData.size());
                    }
                })
        );
    }

    @Override
    public Future<Void> deleteAllForConnection(String userId, String deviceId, String appId, long connIndex) {
        return getDB(userId).compose(db ->
                executeBlockingVoid(() -> {
                    String prefix = composeKeyPrefix(userId, deviceId, appId, connIndex);
                    byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);

                    try (RocksIterator iter = db.newIterator(readOptions)) {
                        for (iter.seek(prefixBytes); iter.isValid(); iter.next()) {
                            String keyStr = new String(iter.key(), StandardCharsets.UTF_8);
                            if (!keyStr.startsWith(prefix)) break;
                            db.delete(writeOptions, iter.key());
                        }
                        log.debug("Deleted all states for userId: {}, deviceId: {}, appId: {}, connIndex: {}",
                                userId, deviceId, appId, connIndex);
                    }
                })
        );
    }

    private <T> Future<T> executeBlocking(Callable<T> blockingCode) {
        Promise<T> promise = Promise.promise();
        vertx.executeBlocking(fut -> {
            try {
                fut.complete(blockingCode.call());
            } catch (Exception e) {
                log.error("Blocking operation failed", e);
                fut.fail(new SessionStoreException("Blocking operation failed", e));
            }
        }, false, promise);
        return promise.future();
    }

    private Future<Void> executeBlockingVoid(BlockingAction blockingCode) {
        return executeBlocking(() -> {
            blockingCode.run();
            return null;
        });
    }

    @Override
    public void close() {
        dbs.values().forEach(db -> {
            try {
                if (db != null) {
                    db.close();
                    log.info("Closed RocksDB instance");
                }
            } catch (Exception e) {
                log.error("Failed to close RocksDB instance", e);
            }
        });
        try {
            if (options != null) {
                options.close();
                log.info("Closed RocksDB options");
            }
            if (writeOptions != null) {
                writeOptions.close();
                log.info("Closed RocksDB write options");
            }
            if (readOptions != null) {
                readOptions.close();
                log.info("Closed RocksDB read options");
            }
        } catch (Exception e) {
            log.error("Failed to close RocksDB options", e);
        }
        dbs.clear();
        log.info("Cleared RocksDB instance map");
    }

    @FunctionalInterface
    private interface BlockingAction {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface Callable<T> {
        T call() throws Exception;
    }

    public static class SessionStoreException extends RuntimeException {
        public SessionStoreException(String message) {
            super(message);
        }

        public SessionStoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}