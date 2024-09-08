package com.smartconsultor.microservice.account.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * User account data object
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Account {

  private String id;
  private String username;
  private String fullname;
  private String phone;
  private String email;
  private Long birthdate;

  public Account() {
    // Empty constructor
  }

  public Account(JsonObject json) {
    AccountConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    AccountConverter.toJson(this, json);
    return json;
  }


  public String getId() {
    return id;
  }

  public Account setId(String id) {
    this.id = id;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public Account setUsername(String username) {
    this.username = username;
    return this;
  }
  public String getFullname() {
    return fullname;
  }

  public Account setFullname(String fullname) {
    this.fullname = fullname;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public Account setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public Account setEmail(String email) {
    this.email = email;
    return this;
  }

  public Long getBirthDate() {
    return birthdate;
  }

  public Account setBirthDate(Long birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  @Override
  public String toString() {
    return toJson().encodePrettily();
  }
}