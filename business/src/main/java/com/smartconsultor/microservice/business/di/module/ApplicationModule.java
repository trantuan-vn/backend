package com.smartconsultor.microservice.business.di.module;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import javax.inject.Singleton;

import com.smartconsultor.microservice.business.application.config.AppConfig;
import com.smartconsultor.microservice.business.application.config.PostgresConfig;
import com.smartconsultor.microservice.business.application.usecase.DepositUseCase;
import com.smartconsultor.microservice.business.domain.repository.WalletRepository;
import com.smartconsultor.microservice.business.infrastructure.repository.WalletRepositoryImpl;

@Module
public class ApplicationModule {

    @Singleton
    @Provides
    public AppConfig provideAppConfig() {
        return new AppConfig();
    }
        
    @Singleton
    @Provides
    public Vertx provideVertx() {
        return Vertx.vertx(); // tạo Vertx instance dùng chung
    }

    @Singleton
    @Provides
    public PgPool providePgPool(Vertx vertx, AppConfig appConfig) {
        PostgresConfig pgConfig = appConfig.getPostgresConfig();

        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(pgConfig.getPort())  // nên thêm port trong PostgresConfig nếu chưa có, hoặc mặc định 5432
            .setHost(pgConfig.getHost())
            .setDatabase(pgConfig.getDatabase())
            .setUser(pgConfig.getUser())
            .setPassword(pgConfig.getPassword());

        PoolOptions poolOptions = new PoolOptions().setMaxSize(10);

        return PgPool.pool(vertx, connectOptions, poolOptions);
    }    

    @Singleton
    @Provides
    public WalletRepository provideWalletRepository(PgPool pgPool) {
        return new WalletRepositoryImpl(pgPool);
    }

    @Singleton
    @Provides
    public DepositUseCase provideDepositUseCase(WalletRepository walletRepository) {
        return new DepositUseCase(walletRepository);
    }

}
