package com.smartconsultor.microservice.account.model;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link com.smartconsultor.microservice.account.model.Account}.
 * NOTE: This class has been automatically generated from the {@link com.smartconsultor.microservice.account.model.Account} original class using Vert.x codegen.
 */
public class AccountConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Account obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "birthDate":
          if (member.getValue() instanceof Number) {
            obj.setBirthDate(((Number)member.getValue()).longValue());
          }
          break;
        case "email":
          if (member.getValue() instanceof String) {
            obj.setEmail((String)member.getValue());
          }
          break;
        case "fullname":
          if (member.getValue() instanceof String) {
            obj.setFullname((String)member.getValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof String) {
            obj.setId((String)member.getValue());
          }
          break;
        case "phone":
          if (member.getValue() instanceof String) {
            obj.setPhone((String)member.getValue());
          }
          break;
        case "username":
          if (member.getValue() instanceof String) {
            obj.setUsername((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Account obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Account obj, java.util.Map<String, Object> json) {
    if (obj.getBirthDate() != null) {
      json.put("birthDate", obj.getBirthDate());
    }
    if (obj.getEmail() != null) {
      json.put("email", obj.getEmail());
    }
    if (obj.getFullname() != null) {
      json.put("fullname", obj.getFullname());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getPhone() != null) {
      json.put("phone", obj.getPhone());
    }
    if (obj.getUsername() != null) {
      json.put("username", obj.getUsername());
    }
  }
}
