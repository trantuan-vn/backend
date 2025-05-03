package com.smartconsultor.microservice.gateway.common.error;

import java.util.function.Consumer;

import io.vavr.control.Either;

public interface Result<T> extends Either<Failure, T> {

    static <T> Result<T> success(T value) {
        return (Result<T>) Either.<Failure, T>right(value);
    }

    static <T> Result<T> failure(Failure failure) {
        return (Result<T>) Either.<Failure, T>left(failure);
    }
    public static <L> void foldVoid(Result<L> result, Consumer<L> onSuccess, Consumer<Failure> onFailure) {
        result.fold(
            failure -> {
                onFailure.accept(failure);
                return null;  // Không cần phải trả về gì
            },
            onSuccessValue -> {
                onSuccess.accept(onSuccessValue);
                return null;  // Không cần phải trả về gì
            }
        );
    }    
}
