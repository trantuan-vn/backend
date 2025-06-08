package com.smartconsultor.microservice.gateway.infrastructure.datasources.local;

import io.vertx.core.Future;
import java.util.Map;

public interface SessionStore  extends AutoCloseable {

    Future<Void> save(String userId, String deviceId, String appId, long connIndex, long seqId, byte[] stateData);

    Future<byte[]> get(String userId, String deviceId, String appId, long connIndex, long seqId);

    Future<Void> delete(String userId, String deviceId, String appId, long connIndex, long seqId);

    Future<Void> saveBatch(String userId, String deviceId, String appId, long connIndex, Map<Long, byte[]> batchData);

    Future<Void> deleteAllForConnection(String userId, String deviceId, String appId, long connIndex);

    @Override
    public void close();
}
