package com.smartconsultor.microservice.business.infrastructure.config;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.core.Vertx;

public class DbConfig {
    private Vertx vertx;
    private PgPool pgClient;

    public DbConfig(Vertx vertx) {
        this.vertx = vertx;
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("postgres-service")
            .setDatabase("mydb")
            .setUser("myuser")
            .setPassword("mypassword");

        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        this.pgClient = PgPool.pool(vertx, connectOptions, poolOptions);
    }

    public PgPool getPgClient() {
        return pgClient;
    }
}
