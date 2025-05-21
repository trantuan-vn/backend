package com.smartconsultor.microservice.gateway.verticle.config;

public class PulsarConfig {
    private String url;
    private String adminUrl;  // 🆕 Thêm dòng này
    private int connectionsPerBroker;
    private int numIoThreads;
    private int numListenerThreads;
    private int batchingMaxMessagesPerBatch;
    private int batchingMaxPublishDelay;
    private int operationTimeout;
    private int keepAliveInterval;
    private int receiverQueueSize;
    private int rateLimit;
    private int backpressureThreshold;
    

    public PulsarConfig() {}
    public String getUrl() { return url; }
    public String getAdminUrl() { return adminUrl; }  // 🆕 Getter cho adminUrl
    public int getConnectionsPerBroker() { return connectionsPerBroker; }
    public int getNumIoThreads() { return numIoThreads; }
    public int getNumListenerThreads() { return numListenerThreads; }
    public int getBatchingMaxMessagesPerBatch() { return batchingMaxMessagesPerBatch; }
    public int getBatchingMaxPublishDelay() { return batchingMaxPublishDelay; }
    public int getOperationTimeout() { return operationTimeout; }
    public int getKeepAliveInterval() { return keepAliveInterval; }
    public int getReceiverQueueSize() { return receiverQueueSize; }
    public int getRateLimit() { return rateLimit;}
    public int getBackpressureThreshold() { return backpressureThreshold;}

}
