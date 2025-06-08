package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AuthModule_ProvideAuthRouterFactory implements Factory<AuthRouter> {
  private final AuthModule module;

  private final Provider<Vertx> vertxProvider;

  private final Provider<AuthHandler> authHandlerProvider;

  public AuthModule_ProvideAuthRouterFactory(AuthModule module, Provider<Vertx> vertxProvider,
      Provider<AuthHandler> authHandlerProvider) {
    this.module = module;
    this.vertxProvider = vertxProvider;
    this.authHandlerProvider = authHandlerProvider;
  }

  @Override
  public AuthRouter get() {
    return provideAuthRouter(module, vertxProvider.get(), authHandlerProvider.get());
  }

  public static AuthModule_ProvideAuthRouterFactory create(AuthModule module,
      Provider<Vertx> vertxProvider, Provider<AuthHandler> authHandlerProvider) {
    return new AuthModule_ProvideAuthRouterFactory(module, vertxProvider, authHandlerProvider);
  }

  public static AuthRouter provideAuthRouter(AuthModule instance, Vertx vertx,
      AuthHandler authHandler) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthRouter(vertx, authHandler));
  }
}
