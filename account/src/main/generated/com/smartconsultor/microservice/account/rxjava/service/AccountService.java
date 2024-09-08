/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.smartconsultor.microservice.account.rxjava.service;

import rx.Observable;
import rx.Single;
import io.vertx.rx.java.RxHelper;
import io.vertx.rx.java.WriteStreamSubscriber;
import io.vertx.rx.java.SingleOnSubscribeAdapter;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;

/**
 * A service interface managing user accounts.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link com.smartconsultor.microservice.account.service.AccountService original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(com.smartconsultor.microservice.account.service.AccountService.class)
public class AccountService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountService that = (AccountService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<AccountService> __TYPE_ARG = new TypeArg<>(    obj -> new AccountService((com.smartconsultor.microservice.account.service.AccountService) obj),
    AccountService::getDelegate
  );

  private final com.smartconsultor.microservice.account.service.AccountService delegate;
  
  public AccountService(com.smartconsultor.microservice.account.service.AccountService delegate) {
    this.delegate = delegate;
  }

  public AccountService(Object delegate) {
    this.delegate = (com.smartconsultor.microservice.account.service.AccountService)delegate;
  }

  public com.smartconsultor.microservice.account.service.AccountService getDelegate() {
    return delegate;
  }

  /**
   * Delete all user accounts from the persistence
   * @param resultHandler the result handler will be called as soon as the users have been removed. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService deleteAllAccounts(io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.deleteAllAccounts(resultHandler));
    return ret;
  }

  /**
   * Update user account info.
   * @param account a account entity that we want to update
   * @param resultHandler the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService updateAccount(com.smartconsultor.microservice.account.model.Account account, io.vertx.core.Handler<io.vertx.core.AsyncResult<com.smartconsultor.microservice.account.model.Account>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.updateAccount(account, resultHandler));
    return ret;
  }

  /**
   * Retrieve all user accounts.
   * @param resultHandler the result handler will be called as soon as the users have been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService retrieveAllAccounts(io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<com.smartconsultor.microservice.account.model.Account>>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.retrieveAllAccounts(resultHandler));
    return ret;
  }

  /**
   * Retrieve the user account with certain `username`.
   * @param username username
   * @param resultHandler the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService retrieveByUsername(java.lang.String username, io.vertx.core.Handler<io.vertx.core.AsyncResult<com.smartconsultor.microservice.account.model.Account>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.retrieveByUsername(username, resultHandler));
    return ret;
  }

  /**
   * Retrieve the user account with certain `id`.
   * @param id user account id
   * @param resultHandler the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService retrieveAccount(java.lang.String id, io.vertx.core.Handler<io.vertx.core.AsyncResult<com.smartconsultor.microservice.account.model.Account>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.retrieveAccount(id, resultHandler));
    return ret;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService initializePersistence(io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.initializePersistence(resultHandler));
    return ret;
  }

  /**
   * Add a account to the persistence.
   * @param account a account entity that we want to add
   * @param resultHandler the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService addAccount(com.smartconsultor.microservice.account.model.Account account, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.addAccount(account, resultHandler));
    return ret;
  }

  /**
   * Delete a user account from the persistence
   * @param id user account id
   * @param resultHandler the result handler will be called as soon as the user has been removed. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public com.smartconsultor.microservice.account.rxjava.service.AccountService deleteAccount(java.lang.String id, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    com.smartconsultor.microservice.account.rxjava.service.AccountService ret = com.smartconsultor.microservice.account.rxjava.service.AccountService.newInstance((com.smartconsultor.microservice.account.service.AccountService)delegate.deleteAccount(id, resultHandler));
    return ret;
  }

  /**
   * The address on which the service is published.
   */
  public static final java.lang.String SERVICE_ADDRESS = com.smartconsultor.microservice.account.service.AccountService.SERVICE_ADDRESS;
  /**
   * The name of the event bus service.
   */
  public static final java.lang.String SERVICE_NAME = com.smartconsultor.microservice.account.service.AccountService.SERVICE_NAME;
  public static AccountService newInstance(com.smartconsultor.microservice.account.service.AccountService arg) {
    return arg != null ? new AccountService(arg) : null;
  }

}
