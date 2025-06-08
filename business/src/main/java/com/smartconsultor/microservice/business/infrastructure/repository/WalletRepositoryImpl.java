package com.smartconsultor.microservice.business.infrastructure.repository;

import com.smartconsultor.microservice.business.domain.model.UserWallet;
import com.smartconsultor.microservice.business.domain.repository.WalletRepository;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.core.Promise;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

@Singleton
public class WalletRepositoryImpl implements WalletRepository {

    private final PgPool client;

    @Inject
    public WalletRepositoryImpl(PgPool client) {
        this.client = client;
    }

    @Override
    public UserWallet findByUserId(String userId, String currency) {
        Promise<UserWallet> promise = Promise.promise();

        client.preparedQuery("SELECT user_id, currency, balance FROM user_wallet WHERE user_id = $1 AND currency = $2")
            .execute(Tuple.of(userId, currency), ar -> {
                if (ar.succeeded()) {
                    RowSet<Row> rows = ar.result();
                    if (rows == null || !rows.iterator().hasNext()) {
                        // Không có kết quả => trả về wallet mặc định (hoặc throw exception tuỳ nghiệp vụ)
                        promise.complete(new UserWallet(userId, currency, BigDecimal.ZERO));
                    } else {
                        Row row = rows.iterator().next();
                        UserWallet wallet = new UserWallet(
                            row.getString("user_id"),
                            row.getString("currency"),
                            row.getBigDecimal("balance")
                        );
                        promise.complete(wallet);
                    }
                } else {
                    promise.fail(ar.cause());
                }
            });

        // Chuyển sang blocking-style để tương thích với interface sync hiện tại
        CountDownLatch latch = new CountDownLatch(1);
        final UserWallet[] result = new UserWallet[1];
        final Throwable[] error = new Throwable[1];

        promise.future().onComplete(ar -> {
            if (ar.succeeded()) {
                result[0] = ar.result();
            } else {
                error[0] = ar.cause();
            }
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for DB result", e);
        }

        if (error[0] != null) throw new RuntimeException("Failed to query wallet", error[0]);
        return result[0];
    }

    @Override
    public void save(UserWallet wallet) {
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] error = new Throwable[1];

        client.preparedQuery("""
            INSERT INTO user_wallet (user_id, currency, balance)
            VALUES ($1, $2, $3)
            ON CONFLICT (user_id, currency)
            DO UPDATE SET balance = EXCLUDED.balance
        """)
        .execute(Tuple.of(wallet.getUserId(), wallet.getCurrency(), wallet.getBalance()), ar -> {
            if (ar.failed()) {
                error[0] = ar.cause();
            }
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while saving wallet", e);
        }

        if (error[0] != null) throw new RuntimeException("Failed to save wallet", error[0]);
    }
}

