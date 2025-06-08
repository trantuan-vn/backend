package com.smartconsultor.microservice.gateway.infrastructure.datasources.local.impl;

import com.smartconsultor.microservice.gateway.infrastructure.datasources.local.SlotManager;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import com.smartconsultor.microservice.gateway.verticle.config.RocksDBConfig;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SlotManager implementation with RocksDB persistence.
 */
public class SlotManagerImpl implements SlotManager {

    private static final Logger logger = LoggerFactory.getLogger(SlotManagerImpl.class);

    private final AppConfig appConfig;
    private final RocksDB db;
    private final List<String> managedTopics;
    private final Map<String, String> userToTopicMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> topicToUserSet = new ConcurrentHashMap<>();
    private final Vertx vertx;
    private final Options options;
    private final WriteOptions writeOptions;

    @Inject
    public SlotManagerImpl(Vertx vertx, AppConfig appConfig) {
        Objects.requireNonNull(vertx, "Vertx must not be null");
        Objects.requireNonNull(appConfig, "AppConfig must not be null");
        Objects.requireNonNull(appConfig.getEnv(), "Environment config must not be null");
        Objects.requireNonNull(appConfig.getServer(), "Server config must not be null");

        this.vertx = vertx;
        this.appConfig = appConfig;
        this.managedTopics = Collections.unmodifiableList(generateTopicsForPod(String.valueOf(appConfig.getEnv().getPodId())));

        RocksDBConfig config = appConfig.getRocksdb();
        this.options = buildOptions(config);
        this.writeOptions = new WriteOptions()
                            .setSync(false)
                            .setDisableWAL(false); 

        this.db = initDB("pod" + appConfig.getEnv().getPodId() + "-userToTopicMap", config);
        initializeTopicUserSets();
        loadFromDB();
    }

    private void initializeTopicUserSets() {
        for (String topic : managedTopics) {
            topicToUserSet.put(topic, new HashSet<>()); // Use HashSet for memory efficiency
        }
    }

    private void loadFromDB() {
        try (RocksIterator iterator = db.newIterator()) {
            Map<String, String> batch = new HashMap<>();
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                String userId = bytesToString(iterator.key());
                String topic = bytesToString(iterator.value());
                if (managedTopics.contains(topic)) {
                    batch.put(userId, topic);
                }
            }
            userToTopicMap.putAll(batch);
            batch.forEach((userId, topic) -> topicToUserSet.get(topic).add(userId));
        } catch (Exception e) {
            logger.error("Failed to load data from RocksDB for podId: {}", appConfig.getEnv().getPodId(), e);
            throw new IllegalStateException("Failed to load data from RocksDB", e);
        }
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
                .setTableFormatConfig(tableConfig)
                .setOptimizeFiltersForHits(true);
    }

    private RocksDB initDB(String name, RocksDBConfig config) {
        String dbPath = config.getBasePath() + "/" + name;
        File dbDir = new File(dbPath);
        if (!dbDir.exists() && !dbDir.mkdirs()) {
            logger.error("Cannot create RocksDB path: {}", dbPath);
            throw new IllegalStateException("Cannot create RocksDB path: " + dbPath);
        }
        try {
            return RocksDB.open(options, dbPath);
        } catch (RocksDBException e) {
            logger.error("Failed to open RocksDB at path: {}", dbPath, e);
            throw new IllegalStateException("Failed to open RocksDB at path: " + dbPath, e);
        }
    }

    private List<String> generateTopicsForPod(String podId) {
        List<String> topics = new ArrayList<>();
        int numTopics = appConfig.getServer().getNumTopics();
        for (int i = 0; i < numTopics; i++) {
            topics.add("pod" + podId + "-topic" + i);
        }
        return topics;
    }

    private byte[] stringToBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Future<Void> putToDB(String userId, String topic) {
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(future -> {
            try (WriteBatch batch = new WriteBatch()) {
                batch.put(stringToBytes(userId), stringToBytes(topic));
                db.write(writeOptions, batch);
                future.complete();
            } catch (RocksDBException e) {
                logger.error("Failed to write to RocksDB for userId: {}, topic: {}", userId, topic, e);
                future.fail(e);
            }
        }, false, promise);
        return promise.future();
    }

    private Future<Void> deleteFromDB(String userId) {
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(future -> {
            try {
                db.delete(writeOptions, stringToBytes(userId));
                future.complete();
            } catch (RocksDBException e) {
                logger.error("Failed to delete from RocksDB for userId: {}", userId, e);
                future.fail(e);
            }
        }, false, promise);
        return promise.future();
    }

    @Override
    public Future<String> assignTopicToUser(String userId) {
        Objects.requireNonNull(userId, "UserId must not be null");
        Promise<String> promise = Promise.promise();
        vertx.runOnContext(v -> {
            String existingTopic = userToTopicMap.get(userId);
            if (existingTopic != null) {
                promise.complete(existingTopic);
                return;
            }

            synchronized (this) {
                String selectedTopic = null;
                int minSize = Integer.MAX_VALUE;

                for (String topic : managedTopics) {
                    Set<String> users = topicToUserSet.get(topic);
                    int size = users.size();
                    if (size < appConfig.getServer().getMaxUsersPerTopic() && size < minSize) {
                        minSize = size;
                        selectedTopic = topic;
                    }
                }

                final String finalSelectedTopic = selectedTopic; // Tạo biến final
                if (finalSelectedTopic != null) {
                    topicToUserSet.get(finalSelectedTopic).add(userId);
                    userToTopicMap.put(userId, finalSelectedTopic);
                    putToDB(userId, finalSelectedTopic)
                            .onFailure(e -> {
                                logger.error("Failed to persist user {} to topic {} in RocksDB", userId, finalSelectedTopic, e);
                                promise.fail(e);
                            })
                            .onSuccess(v1 -> promise.complete(finalSelectedTopic));
                } else {
                    logger.warn("No available topic for userId: {}", userId);
                    promise.complete(null);
                }
            }
        });
        return promise.future();
    }

    @Override
    public Future<Void> removeUser(String userId) {
        Objects.requireNonNull(userId, "UserId must not be null");
        Promise<Void> promise = Promise.promise();
        vertx.runOnContext(v -> {
            synchronized (this) {
                String topic = userToTopicMap.remove(userId);
                if (topic != null) {
                    Set<String> users = topicToUserSet.get(topic);
                    if (users != null) {
                        users.remove(userId);
                    }
                    deleteFromDB(userId)
                            .onFailure(e -> {
                                logger.error("Failed to delete user {} from RocksDB", userId, e);
                                promise.fail(e);
                            })
                            .onSuccess(v1 -> promise.complete());
                } else {
                    promise.complete();
                }
            }
        });
        return promise.future();
    }

    @Override
    public Future<String> getTopicOfUser(String userId) {
        Objects.requireNonNull(userId, "UserId must not be null");
        return Future.succeededFuture(userToTopicMap.get(userId));
    }

    @Override
    public Future<Map<String, Integer>> getTopicLoad() {
        Map<String, Integer> load = new HashMap<>();
        for (String topic : managedTopics) {
            load.put(topic, topicToUserSet.get(topic).size());
        }
        return Future.succeededFuture(load);
    }

    @Override
    public Future<Set<String>> getAllUsers() {
        return Future.succeededFuture(new HashSet<>(userToTopicMap.keySet()));
    }

    @Override
    public List<String> getManagedTopics() {
        return managedTopics;
    }

    @Override
    public String getPodId() {
        return String.valueOf(appConfig.getEnv().getPodId());
    }

    @Override
    public void close() {
        try {
            if (writeOptions != null) {
                writeOptions.close();
            }
            if (db != null ) {
                db.close();
            }
            if (options != null) {
                options.close();
            }
            topicToUserSet.clear();
            userToTopicMap.clear();
        } catch (Exception e) {
            logger.error("Failed to close SlotManagerImpl for podId: {}", appConfig.getEnv().getPodId(), e);
        }
    }
}