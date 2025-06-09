package com.smartconsultor.microservice.business.adapter.config;

import com.smartconsultor.microservice.business.adapter.dto.gateway.business.common.EventType;
import com.smartconsultor.microservice.business.application.usecase.common.BusinessUseCase;
import com.smartconsultor.microservice.business.adapter.annotation.HandlesEvent;
import com.smartconsultor.microservice.business.adapter.dispatcher.BusinessDispatcher;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class BusinessDispatcherFactory {

    public static BusinessDispatcher createDispatcher() {
        Map<EventType, BusinessUseCase> handlers = new EnumMap<>(EventType.class);

        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .forPackage("com.smartconsultor.microservice.business.application.usecase")
            .addScanners(Scanners.SubTypes));

        Set<Class<? extends BusinessUseCase>> useCaseClasses =
            reflections.getSubTypesOf(BusinessUseCase.class);

        for (Class<? extends BusinessUseCase> clazz : useCaseClasses) {
            HandlesEvent annotation = clazz.getAnnotation(HandlesEvent.class);
            if (annotation == null) continue;

            try {
                BusinessUseCase useCase = clazz.getDeclaredConstructor().newInstance();
                handlers.put(annotation.value(), useCase);
            } catch (Exception e) {
                throw new RuntimeException("Cannot instantiate use case: " + clazz.getName(), e);
            }
        }

        return new BusinessDispatcher(handlers);
    }
}
