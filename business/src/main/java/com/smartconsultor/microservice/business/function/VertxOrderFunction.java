package com.smartconsultor.microservice.business.function;

import com.smartconsultor.microservice.business.domain.model.Order;
import com.smartconsultor.microservice.business.domain.service.OrderService;
import com.smartconsultor.microservice.business.infrastructure.db.VertxUserRepository;
import com.smartconsultor.microservice.business.application.port.UserRepository;

import io.vertx.core.Vertx;
import io.vertx.core.Promise;
import io.vertx.pgclient.*;
import io.vertx.sqlclient.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;

public class VertxOrderFunction implements Function<String, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    private Vertx vertx;
    private PgPool pgClient;
    private UserRepository userRepo;
    private OrderService orderService;

    @Override
    public void initialize(Context context) throws Exception {
        vertx = Vertx.vertx();
        
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("postgres-service")
            .setDatabase("mydb")
            .setUser("myuser")
            .setPassword("mypassword");

        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        pgClient = PgPool.pool(vertx, connectOptions, poolOptions);

        userRepo = new VertxUserRepository(pgClient);
        orderService = new OrderService();
    }

    @Override
    public String process(String input, Context context) throws Exception {
        Order order = mapper.readValue(input, Order.class);
        Promise<String> promise = Promise.promise();

        userRepo.findById(order.userId)
                .onSuccess(user -> {
                    String log = orderService.generateLog(order, user);
                    context.getLogger().info(log);
                    promise.complete(log);
                })
                .onFailure(err -> {
                    context.getLogger().warn(err.getMessage());
                    promise.complete("User not found: " + order.userId);
                });

        return promise.future().toCompletionStage().toCompletableFuture().get();
    }

    @Override
    public void close() throws Exception {
        if (pgClient != null) pgClient.close();
        if (vertx != null) vertx.close();
    }
}
