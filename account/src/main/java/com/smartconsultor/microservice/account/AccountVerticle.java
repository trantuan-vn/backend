package com.smartconsultor.microservice.account;

import com.smartconsultor.microservice.account.model.Account;
import com.smartconsultor.microservice.account.service.AccountService;
import com.smartconsultor.microservice.account.service.impl.JdbcAccountServiceImpl;
import com.smartconsultor.microservice.common.RestAPIVerticle;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;    

public class AccountVerticle extends RestAPIVerticle {

  private static final Logger logger  = LoggerFactory.getLogger(AccountVerticle.class);
  private AccountService accountService;
  private static final String API_ADD = "/user";
  private static final String API_RETRIEVE = "/user/:id";
  private static final String API_RETRIEVE_ALL = "/user";
  private static final String API_UPDATE = "/user/:id";
  private static final String API_DELETE = "/user/:id";  
  // tag::config[]
  //private static final int HTTP_PORT = Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "0"));
  //private static final String POD_NAME = System.getenv().getOrDefault("POD_NAME", "unknown");
  // end::config[]
  
  // tag::start[]
  @Override
  public void start(Promise<Void> startPromise) {
    accountService = new JdbcAccountServiceImpl(vertx, config());
    Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.post(API_ADD).handler(this::apiAddUser);
    router.get(API_RETRIEVE).handler(this::apiRetrieveUser);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdateUser);
    router.delete(API_DELETE).handler(this::apiDeleteUser);
        
    registerConsumer();
    enableHealthReadiness(router);
    JsonObject jsonServer=config().getJsonObject("server");
    // Create and start the HTTP server
    createHttpServer(router, jsonServer.getInteger("port"),jsonServer.getString("host"))
      .onSuccess(server -> {
        logger.info("Account Server started and listening on port {}", server.actualPort());
        startPromise.complete(); // Complete the verticle start promise
      })
      .onFailure(cause -> {
        logger.error("Failed to start the Account Server", cause);
        startPromise.fail(cause); // Fail the verticle start promise
      });     
  }
  // end::start[]

  @Override
  public void stop(Promise<Void> stopPromise) {
    // Perform any custom shutdown logic here
    logger.info("Shutting down AccountVerticle...");
    // Complete the stop promise
    stopPromise.complete();
  }  

  private void apiAddUser(RoutingContext context) {
    Account account = new Account(context.body().asJsonObject());
    accountService.addAccount(account, resultVoidHandler(context, 201));
  }

  private void apiRetrieveUser(RoutingContext context) {
    String id = context.request().getParam("id");
    accountService.retrieveAccount(id, resultHandlerNonEmpty(context));
  }

  private void apiRetrieveAll(RoutingContext context) {
    accountService.retrieveAllAccounts(resultHandler(context, Json::encodePrettily));
  }

  private void apiUpdateUser(RoutingContext context) {
    //notImplemented(context);
    Account account = new Account(context.body().asJsonObject());
    accountService.updateAccount(account, resultHandlerNonEmpty(context));
  }

  private void apiDeleteUser(RoutingContext context) {
    String id = context.request().getParam("id");
    accountService.deleteAccount(id, deleteResultHandler(context));
  }  

  // tag::consumer[]
  private void registerConsumer() {
    vertx.eventBus().<String>consumer("account", msg -> { 
      String requestData = msg.body();
      JsonObject requestJson = new JsonObject(requestData);          
      // Xử lý yêu cầu
      String method = requestJson.getString("method");
      JsonObject params = requestJson.getJsonObject("params");
      JsonObject responseJson;
      Account account;
      switch (method) {
        case "GET":
          String strType= params.getString("type");
          switch (strType) {
            case "one":
              String strUserName= params.getString("username");
              accountService.retrieveByUsername(strUserName, resultHandlerNonEmptyEventBus(msg));
              break;
            case "all":
              accountService.retrieveAllAccounts(resultHandlerEventBus(msg, Json::encodePrettily));
              break;
            default:
              notImplementedEventBus(msg);
              break;
          }
          break;
        case "POST":
          account = new Account(params);
          accountService.addAccount(account, resultVoidHandlerEventBus(msg, 201));    
          break;
        case "PATCH":
          account = new Account(params);
          accountService.updateAccount(account, resultHandlerNonEmptyEventBus(msg));              
          break;
        case "DELETE":
          String strID= params.getString("id");
          accountService.deleteAccount(strID, deleteResultHandlerEventBus(msg));              
          break;
        default:
          responseJson = new JsonObject()
            .put("status", 405)
            .put("message", "Method Not Allowed");
            msg.reply(responseJson.encodePrettily());            
          break;      
      }
    }); 
  }
  // end::consumer[]
}
