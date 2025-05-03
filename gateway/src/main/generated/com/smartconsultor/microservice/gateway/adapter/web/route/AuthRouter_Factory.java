package com.smartconsultor.microservice.gateway.adapter.web.route;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AuthRouter_Factory implements Factory<AuthRouter> {
  private final Provider<Vertx> vertxProvider;

  private final Provider<AuthHandler> authHandlerProvider;

  public AuthRouter_Factory(Provider<Vertx> vertxProvider,
      Provider<AuthHandler> authHandlerProvider) {
    this.vertxProvider = vertxProvider;
    this.authHandlerProvider = authHandlerProvider;
  }

  @Override
  public AuthRouter get() {
    return newInstance(vertxProvider.get(), authHandlerProvider.get());
  }

  public static AuthRouter_Factory create(Provider<Vertx> vertxProvider,
      Provider<AuthHandler> authHandlerProvider) {
    return new AuthRouter_Factory(vertxProvider, authHandlerProvider);
  }

  public static AuthRouter newInstance(Vertx vertx, AuthHandler authHandler) {
    return new AuthRouter(vertx, authHandler);
  }
}
