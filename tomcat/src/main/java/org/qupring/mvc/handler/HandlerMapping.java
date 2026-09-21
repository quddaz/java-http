package org.qupring.mvc.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpMethod;
import org.qupring.annotation.Route;

public class HandlerMapping {

    private final Map<MappingTarget, HandlerTarget> mappings = new HashMap<>();

    public void addMappings(
            Map<String, String> resourceMappings,
            List<Class<?>> controllerClasses
    ) {
        addResourceMappings(resourceMappings);
        addControllerMappings(controllerClasses);
    }

    private void addResourceMappings(Map<String, String> resourceMappings) {
        resourceMappings.forEach((path, resourcePath) ->
                mappings.put(
                        new MappingTarget(path, HttpMethod.GET),
                        HandlerTarget.staticResource(resourcePath)
                )
        );
    }

    private void addControllerMappings(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                Route route = method.getAnnotation(Route.class);
                if (route == null) {
                    continue;
                }
                mappings.put(
                        new MappingTarget(route.path(), route.method()),
                        HandlerTarget.controller(method)
                );
            }
        }
    }

    public HandlerTarget getHandler(String path, HttpMethod method) {
        return mappings.get(new MappingTarget(path, method));
    }

}
