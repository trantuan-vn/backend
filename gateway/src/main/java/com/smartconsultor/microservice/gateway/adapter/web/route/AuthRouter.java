package com.smartconsultor.microservice.gateway.adapter.web.route;

import javax.inject.Inject;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class AuthRouter {
    private final AuthHandler authHandler;
    private final Vertx vertx;
    @Inject
    public AuthRouter(Vertx vertx, AuthHandler authHandler) {    
        this.vertx = vertx;
        this.authHandler = authHandler;
    }
       
    public void mount(Router rootRouter) {
        Router authRouter = Router.router(vertx);
        authRouter.post("/token").handler(authHandler::exchangeCode);
        authRouter.post("/refresh").handler(authHandler::refresh);
        authRouter.post("/logout").handler(authHandler::logout);
        // Mount subrouter
        rootRouter.route("/auth/*").subRouter(authRouter);
    }
}

