package com.smartconsultor.microservice.gateway.infrastructure.datasources.local;

import com.maxmind.geoip2.model.CityResponse;
import io.vertx.core.Future;

public interface GeoIPService extends AutoCloseable  {
    public Future<CityResponse> lookup(String ip);
    @Override
    public void close();
}
