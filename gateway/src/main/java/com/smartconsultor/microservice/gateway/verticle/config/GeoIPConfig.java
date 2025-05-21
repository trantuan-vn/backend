package com.smartconsultor.microservice.gateway.verticle.config;

public class GeoIPConfig {
  private String dbPath;
  private int maximumSize;
  private int expireAfterWrite; // phút

  public GeoIPConfig() {}

  public String getDbPath() {
    return dbPath;
  }

  public void setDbPath(String dbPath) {
    this.dbPath = dbPath;
  }

  public int getMaximumSize() {
    return maximumSize;
  }

  public void setMaximumSize(int maximumSize) {
    this.maximumSize = maximumSize;
  }

  public int getExpireAfterWrite() {
    return expireAfterWrite;
  }

  public void setExpireAfterWrite(int expireAfterWrite) {
    this.expireAfterWrite = expireAfterWrite;
  }
}
