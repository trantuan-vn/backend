package com.smartconsultor.microservice.business.infrastructure.db;

import com.smartconsultor.microservice.business.application.port.UserRepository;
import com.smartconsultor.microservice.business.domain.model.User;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
import io.vertx.core.*;

public class VertxUserRepository implements UserRepository {
    private final PgPool client;

    public VertxUserRepository(PgPool client) {
        this.client = client;
    }

    @Override
    public Future<User> findById(String id) {
        Promise<User> promise = Promise.promise();

        client.preparedQuery("SELECT full_name, email FROM users WHERE id = $1")
              .execute(Tuple.of(id), ar -> {
                  if (ar.succeeded() && ar.result().size() > 0) {
                      Row row = ar.result().iterator().next();
                      User u = new User();
                      u.id = id;
                      u.fullName = row.getString("full_name");
                      u.email = row.getString("email");
                      promise.complete(u);
                  } else {
                      promise.fail("User not found: " + id);
                  }
              });

        return promise.future();
    }
}
