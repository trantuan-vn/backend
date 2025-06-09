package com.smartconsultor.microservice.business.adapter.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import com.smartconsultor.microservice.business.adapter.dto.gateway.business.common.EventType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HandlesEvent {
    EventType value();
}