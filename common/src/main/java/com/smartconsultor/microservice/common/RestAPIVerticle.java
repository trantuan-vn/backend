package com.smartconsultor.microservice.common;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.cluster.infinispan.ClusterHealthCheck;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.smartconsultor.microservice.common.utils.RequestUtil;

/**
 * An abstract base verticle that provides several helper methods for REST API.
 *
 * @author Eric Zhao
 */
public abstract class RestAPIVerticle extends AbstractVerticle {

  protected Future<HttpServer> createHttpServer(Router router, int port, String host) {
    Promise<HttpServer> promise = Promise.promise();
    
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port,host)
      .onSuccess(server -> {
        promise.complete(server); // Complete the promise with the server instance
      })
      .onFailure(promise::fail); // Fail the promise if an error occurs
    
    return promise.future();
  }
  /**
   * Enable HealthReadiness.
   *
   * @param router router instance
   */  
  protected void enableHealthReadiness(Router router) {
    /*
    // Route cho /health chỉ cho phép IP nội bộ
    router.get("/health").handler(rc -> {
      String ipAddress = rc.request().remoteAddress().host();
      if (RequestUtil.isInternalIp(ipAddress)) {
          rc.response().end("OK");
      } else {
          rc.response().setStatusCode(403).end("Forbidden: Access is only allowed from internal IPs.");
      }
    });
    Handler<Promise<Status>> procedure = ClusterHealthCheck.createProcedure(vertx, false);
    HealthChecks checks = HealthChecks.create(vertx).register("cluster-health", procedure);
    // Route cho /readiness chỉ cho phép IP nội bộ
    router.get("/readiness").handler(rc -> {
      String ipAddress = rc.request().remoteAddress().host();
      if (RequestUtil.isInternalIp(ipAddress)) {
          HealthCheckHandler healthCheckHandler = HealthCheckHandler.createWithHealthChecks(checks);
          healthCheckHandler.handle(rc);
      } else {
          rc.response().setStatusCode(403).end("Forbidden: Access is only allowed from internal IPs.");
      }
    });
    */
    router.get("/health").handler(rc -> rc.response().end("OK"));
    Handler<Promise<Status>> procedure = ClusterHealthCheck.createProcedure(vertx, false);
    HealthChecks checks = HealthChecks.create(vertx).register("cluster-health", procedure);
    router.get("/readiness").handler(HealthCheckHandler.createWithHealthChecks(checks)); 
  }   
  /**
   * Enable CORS support.
   *
   * @param router router instance
   */
  protected void enableCorsSupport(Router router) {
    Set<String> allowHeaders = new HashSet<>();
    allowHeaders.add("x-requested-with");
    allowHeaders.add("Access-Control-Allow-Origin");
    allowHeaders.add("Access-Control-Allow-Method");
    allowHeaders.add("origin");
    allowHeaders.add("Content-Type");
    allowHeaders.add("accept");
    Set<HttpMethod> allowMethods = new HashSet<>();
    allowMethods.add(HttpMethod.GET);
    allowMethods.add(HttpMethod.PUT);
    allowMethods.add(HttpMethod.OPTIONS);
    allowMethods.add(HttpMethod.POST);
    allowMethods.add(HttpMethod.DELETE);
    allowMethods.add(HttpMethod.PATCH);

    router.route().handler(CorsHandler.create()
      .allowedHeaders(allowHeaders)
      .allowedMethods(allowMethods));
  }

  /**
   * Enable local session storage in requests.
   *
   * @param router router instance
   */
  protected void enableLocalSession(Router router) {
    router.route().handler(SessionHandler.create(
      LocalSessionStore.create(vertx, "shopping.user.session")));
  }

  /**
   * Enable clustered session storage in requests.
   *
   * @param router router instance
   */
  protected void enableClusteredSession(Router router) {
    router.route().handler(SessionHandler.create(
      ClusteredSessionStore.create(vertx, "shopping.user.session")));
  }

  // Auth helper method

  /**
   * Validate if a user exists in the request scope.
   */
  protected void requireLogin(RoutingContext context, BiConsumer<RoutingContext, JsonObject> biHandler) {
    Optional<JsonObject> principal = Optional.ofNullable(context.request().getHeader("user-principal"))
      .map(JsonObject::new);
    if (principal.isPresent()) {
      biHandler.accept(context, principal.get());
    } else {
      context.response()
        .setStatusCode(401)
        .end(new JsonObject().put("message", "need_auth").encode());
    }
  }

  // helper result handler within a request context

  /**
   * This method generates handler for async methods in REST APIs.
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
    return res -> {
      if (res.succeeded()) {
        handler.handle(res.result());
      } else {
        internalError(context, res.cause());
        res.cause().printStackTrace();
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * Use the result directly and invoke `toString` as the response. The content type is JSON.
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        context.response()
          .putHeader("content-type", "application/json")
          .end(res == null ? "{}" : res.toString());
      } else {
        internalError(context, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * Use the result directly and use given {@code converter} to convert result to string
   * as the response. The content type is JSON.
   *
   * @param context   routing context instance
   * @param converter a converter that converts result to a string
   * @param <T>       result type
   * @return generated handler
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Function<T, String> converter) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        if (res == null) {
          serviceUnavailable(context, "invalid_result");
        } else {
          context.response()
            .putHeader("content-type", "application/json")
            .end(converter.apply(res));
        }
      } else {
        internalError(context, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * The result requires non-empty. If empty, return <em>404 Not Found</em> status.
   * The content type is JSON.
   *
   * @param context routing context instance
   * @param <T>     result type
   * @return generated handler
   */
  protected <T> Handler<AsyncResult<T>> resultHandlerNonEmpty(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        if (res == null) {
          notFound(context);
        } else {
          context.response()
            .putHeader("content-type", "application/json")
            .end(res.toString());
        }
      } else {
        internalError(context, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * The content type is originally raw text.
   *
   * @param context routing context instance
   * @param <T>     result type
   * @return generated handler
   */
  protected <T> Handler<AsyncResult<T>> rawResultHandler(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        context.response()
          .end(res == null ? "" : res.toString());
      } else {
        internalError(context, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }

  protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, JsonObject result) {
    return resultVoidHandler(context, result, 200);
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * The result is not needed. Only the state of the async result is required.
   *
   * @param context routing context instance
   * @param result  result content
   * @param status  status code
   * @return generated handler
   */
  protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, JsonObject result, int status) {
    return ar -> {
      if (ar.succeeded()) {
        context.response()
          .setStatusCode(status == 0 ? 200 : status)
          .putHeader("content-type", "application/json")
          .end(result.encodePrettily());
      } else {
        internalError(context, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }

  protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, int status) {
    return ar -> {
      if (ar.succeeded()) {
        context.response()
          .setStatusCode(status == 0 ? 200 : status)
          .putHeader("content-type", "application/json")
          .end();
      } else {
        internalError(context, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }

  /**
   * This method generates handler for async methods in REST DELETE APIs.
   * Return format in JSON (successful status = 204):
   * <code>
   * {"message": "delete_success"}
   * </code>
   *
   * @param context routing context instance
   * @return generated handler
   */
  protected Handler<AsyncResult<Void>> deleteResultHandler(RoutingContext context) {
    return res -> {
      if (res.succeeded()) {
        context.response().setStatusCode(204)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", "delete_success").encodePrettily());
      } else {
        internalError(context, res.cause());
        res.cause().printStackTrace();
      }
    };
  }

  // helper method dealing with failure

  protected void badRequest(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(400)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }

  protected void notFound(RoutingContext context) {
    context.response().setStatusCode(404)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("message", "not_found").encodePrettily());
  }

  protected void internalError(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(500)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }

  protected void notImplemented(RoutingContext context) {
    context.response().setStatusCode(501)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("message", "not_implemented").encodePrettily());
  }

  protected void badGateway(Throwable ex, RoutingContext context) {
    ex.printStackTrace();
    context.response()
      .setStatusCode(502)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", "bad_gateway")
        //.put("message", ex.getMessage())
        .encodePrettily());
  }

  protected void serviceUnavailable(RoutingContext context) {
    context.fail(503);
  }

  protected void serviceUnavailable(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(503)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }

  protected void serviceUnavailable(RoutingContext context, String cause) {
    context.response().setStatusCode(503)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", cause).encodePrettily());
  }

  protected <T> Handler<AsyncResult<T>> resultHandlerEventBus(Message<String> message, Handler<T> handler) {
    return res -> {
      if (res.succeeded()) {
        handler.handle(res.result());
      } else {
        internalErrorEventBus(message, res.cause());
        res.cause().printStackTrace();
      }
    };
  }
  
  protected <T> Handler<AsyncResult<T>> resultHandlerEventBus(Message<String> message) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        message.reply(res == null ? "{}" : res.toString());
      } else {
        internalErrorEventBus(message, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }
  
  protected <T> Handler<AsyncResult<T>> resultHandlerEventBus(Message<String> message, Function<T, String> converter) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        if (res == null) {
          serviceUnavailableEventBus(message, "invalid_result");
        } else {
          message.reply(converter.apply(res));
        }
      } else {
        internalErrorEventBus(message, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }
  
  protected <T> Handler<AsyncResult<T>> resultHandlerNonEmptyEventBus(Message<String> message) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        if (res == null) {
          notFoundEventBus(message);
        } else {
          message.reply(res.toString());
        }
      } else {
        internalErrorEventBus(message, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }
  
  protected <T> Handler<AsyncResult<T>> rawResultHandlerEventBus(Message<String> message) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        message.reply(res == null ? "" : res.toString());
      } else {
        internalErrorEventBus(message, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }
  
  protected Handler<AsyncResult<Void>> resultVoidHandlerEventBus(Message<String> message, JsonObject result) {
    return resultVoidHandlerEventBus(message, result, 200);
  }
  
  protected Handler<AsyncResult<Void>> resultVoidHandlerEventBus(Message<String> message, JsonObject result, int status) {
    return ar -> {
      if (ar.succeeded()) {
        message.reply(result.encodePrettily());
      } else {
        internalErrorEventBus(message, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }
  
  protected Handler<AsyncResult<Void>> resultVoidHandlerEventBus(Message<String> message, int status) {
    return ar -> {
      if (ar.succeeded()) {
        message.reply("");
      } else {
        internalErrorEventBus(message, ar.cause());
        ar.cause().printStackTrace();
      }
    };
  }
  
  protected Handler<AsyncResult<Void>> deleteResultHandlerEventBus(Message<String> message) {
    return res -> {
      if (res.succeeded()) {
        message.reply(new JsonObject().put("message", "delete_success").encodePrettily());
      } else {
        internalErrorEventBus(message, res.cause());
        res.cause().printStackTrace();
      }
    };
  }
  
  protected void badRequestEventBus(Message<String> message, Throwable ex) {
    message.fail(400, new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }
  
  protected void notFoundEventBus(Message<String> message) {
    message.fail(404, new JsonObject().put("message", "not_found").encodePrettily());
  }
  
  protected void internalErrorEventBus(Message<String> message, Throwable ex) {
    message.fail(500, new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }
  
  protected void notImplementedEventBus(Message<String> message) {
    message.fail(501, new JsonObject().put("message", "not_implemented").encodePrettily());
  }
  
  protected void badGatewayEventBus(Throwable ex, Message<String> message) {
    ex.printStackTrace();
    message.fail(502, new JsonObject().put("error", "bad_gateway").encodePrettily());
  }
  
  protected void serviceUnavailableEventBus(Message<String> message) {
    message.fail(503, "Service Unavailable");
  }
  
  protected void serviceUnavailableEventBus(Message<String> message, Throwable ex) {
    message.fail(503, new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }
  
  protected void serviceUnavailableEventBus(Message<String> message, String cause) {
    message.fail(503, new JsonObject().put("error", cause).encodePrettily());
  }
  
}