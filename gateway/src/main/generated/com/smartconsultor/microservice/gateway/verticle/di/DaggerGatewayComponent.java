package com.smartconsultor.microservice.gateway.verticle.di;

import com.smartconsultor.microservice.gateway.adapter.service.PulsarService;
import com.smartconsultor.microservice.gateway.adapter.service.WebSocketManager;
import com.smartconsultor.microservice.gateway.adapter.web.handler.AuthHandler;
import com.smartconsultor.microservice.gateway.adapter.web.middle.ValidateAccessTokenHandler;
import com.smartconsultor.microservice.gateway.adapter.web.route.AuthRouter;
import com.smartconsultor.microservice.gateway.adapter.websocket.WebSocketHandler;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ExchangeCodeUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.LogoutUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.RefreshUseCase;
import com.smartconsultor.microservice.gateway.application.usecases.auth.ValidateAccessTokenUseCase;
import com.smartconsultor.microservice.gateway.domain.repository.AuthRepository;
import com.smartconsultor.microservice.gateway.infrastructure.datasources.remote.auth.AuthRemoteDataSource;
import com.smartconsultor.microservice.gateway.verticle.GatewayVerticle;
import com.smartconsultor.microservice.gateway.verticle.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Preconditions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import javax.annotation.processing.Generated;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

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
public final class DaggerGatewayComponent {
  private DaggerGatewayComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static GatewayComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder gatewayModule(GatewayModule gatewayModule) {
      Preconditions.checkNotNull(gatewayModule);
      return this;
    }

    public GatewayComponent build() {
      return new GatewayComponentImpl();
    }
  }

  private static final class GatewayComponentImpl implements GatewayComponent {
    private final GatewayComponentImpl gatewayComponentImpl = this;

    GatewayComponentImpl() {


    }

    @Override
    public void inject(Vertx vertx) {
    }

    @Override
    public void inject(GatewayVerticle gatewayVerticle) {
    }

    @Override
    public void inject(AppConfig appConfig) {
    }

    @Override
    public void inject(WebClient webClient) {
    }

    @Override
    public void inject(PulsarClient pulsarClient) {
    }

    @Override
    public void inject(Producer<byte[]> producer) {
    }

    @Override
    public void inject(WebSocketManager webSocketManager) {
    }

    @Override
    public void inject(WebSocketHandler webSocketHandler) {
    }

    @Override
    public void inject(PulsarService pulsarService) {
    }

    @Override
    public void inject(AuthRouter authRouter) {
    }

    @Override
    public void inject(AuthHandler authHandler) {
    }

    @Override
    public void inject(ExchangeCodeUseCase exchangeCodeUseCase) {
    }

    @Override
    public void inject(RefreshUseCase refreshUseCase) {
    }

    @Override
    public void inject(LogoutUseCase logoutUseCase) {
    }

    @Override
    public void inject(ValidateAccessTokenHandler validateAccessTokenHandler) {
    }

    @Override
    public void inject(ValidateAccessTokenUseCase validateAccessTokenUseCase) {
    }

    @Override
    public void inject(AuthRemoteDataSource authRemoteDataSource) {
    }

    @Override
    public void inject(AuthRepository authRepository) {
    }
  }
}
