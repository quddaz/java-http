package org.qupring.mvc.handler;

import java.lang.reflect.Method;
import org.qupring.mvc.resolver.HandlerType;

public record HandlerTarget(
        HandlerType handlerType,
        Method controllerMethod,
        String staticResourcePath
) {
    public static HandlerTarget controller(Method method) {
        return new HandlerTarget(
                HandlerType.CONTROLLER,
                method,
                null
        );
    }

    public static HandlerTarget staticResource(String resourcePath) {
        return new HandlerTarget(
                HandlerType.STATIC_RESOURCE,
                null,
                resourcePath
        );
    }
}
