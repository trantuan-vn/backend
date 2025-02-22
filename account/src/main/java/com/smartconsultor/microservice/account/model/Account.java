package com.smartconsultor.microservice.account.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.Map;
import java.util.HashMap;

/**
 * User account data object with customizable attributes
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Account {

  private Map<String, Object> attributes = new HashMap<>();

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

  public Object getAttribute(String key) {
    return attributes.get(key);
  }

  public Account setAttribute(String key, Object value) {
    attributes.put(key, value);
    return this;
  }

  @Override
  public String toString() {
    return toJson().encodePrettily();
  }
}
