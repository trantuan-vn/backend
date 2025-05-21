package com.smartconsultor.microservice.business.application.port;

import com.smartconsultor.microservice.business.domain.model.User;
import io.vertx.core.Future; 

public interface UserRepository {
    Future<User> findById(String id);
}
