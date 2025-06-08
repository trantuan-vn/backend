package com.smartconsultor.microservice.gateway.infrastructure.service.pulsar;

import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BacklogManager {
    private static final Logger logger = LoggerFactory.getLogger(BacklogManager.class);
    private final Semaphore rateLimiter;
    private final AtomicInteger pendingMessages = new AtomicInteger(0);
    private final AtomicLong lastRateLimitWindow = new AtomicLong(System.currentTimeMillis());
    private final AtomicInteger currentRate = new AtomicInteger(0);
    private final int rateLimit;
    private final int backpressureThreshold;
    private volatile boolean backpressureActive = false;
    private long backlogMonitorTimerId = -1;
    private final AtomicLong totalMessagesProcessed = new AtomicLong(0);
    private final AtomicLong totalMessagesFailed = new AtomicLong(0);
    private final Vertx vertx;

    @Inject
    public BacklogManager(Vertx vertx, AppConfig appConfig) {
        this.vertx = Objects.requireNonNull(vertx, "Vertx cannot be null");
        this.rateLimit = Math.max(1, appConfig.getPulsar().getRateLimit() > 0 ?
            appConfig.getPulsar().getRateLimit() : Constants.DEFAULT_RATE_LIMIT);
        this.backpressureThreshold = Math.max(1, appConfig.getPulsar().getBackpressureThreshold() > 0 ?
            appConfig.getPulsar().getBackpressureThreshold() : Constants.DEFAULT_BACKPRESSURE_THRESHOLD);
        this.rateLimiter = new Semaphore(rateLimit, true);

        startBacklogMonitor();
        startMetricsCollector();
    }

    private void startMetricsCollector() {
        vertx.setPeriodic(60000, id -> {
            logger.info("Metrics - Processed: {}, Failed: {}, Pending: {}, Backpressure: {}",
                totalMessagesProcessed.get(),
                totalMessagesFailed.get(),
                pendingMessages.get(),
                backpressureActive ? "Active" : "Inactive");
        });
    }

    private void startBacklogMonitor() {
        backlogMonitorTimerId = vertx.setPeriodic(5000, id -> {
            vertx.executeBlocking(promise -> {
                try {
                    monitorAndAdjust();
                    promise.complete();
                } catch (Exception e) {
                    logger.error("Backlog monitoring error", e);
                    promise.fail(e);
                }
            }, false, ar -> {
                if (ar.failed()) {
                    logger.error("Failed to monitor backlog", ar.cause());
                }
            });
        });
    }

    private void monitorAndAdjust() {
        int pending = pendingMessages.get();
        double utilization = (double) pending / backpressureThreshold;

        if (utilization > 0.8 && !backpressureActive) {
            logger.warn("High system load detected ({}% utilization), enabling backpressure", (int)(utilization * 100));
            backpressureActive = true;
        } else if (utilization < 0.5 && backpressureActive) {
            logger.info("System load normalized ({}% utilization), disabling backpressure", (int)(utilization * 100));
            backpressureActive = false;
        }

        long now = System.currentTimeMillis();
        if (now - lastRateLimitWindow.get() > 1000) {
            lastRateLimitWindow.set(now);
            currentRate.set(0);
        }
    }

    public Semaphore getRateLimiter() {
        return rateLimiter;
    }

    public void incrementPendingMessages() {
        pendingMessages.incrementAndGet();
    }

    public void decrementPendingMessages() {
        pendingMessages.decrementAndGet();
    }

    public void incrementProcessedMessages() {
        totalMessagesProcessed.incrementAndGet();
    }

    public void incrementFailedMessages() {
        totalMessagesFailed.incrementAndGet();
    }

    public boolean isBackpressureActive() {
        return backpressureActive;
    }

    public boolean checkRateLimit() {
        return currentRate.incrementAndGet() <= rateLimit;
    }

    public void shutdown() {
        if (backlogMonitorTimerId != -1) {
            vertx.cancelTimer(backlogMonitorTimerId);
            backlogMonitorTimerId = -1;
        }
    }
}