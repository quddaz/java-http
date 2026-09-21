package org.qupring.mvc.resolver;

import java.lang.reflect.Parameter;
import org.apache.http.request.HttpRequest;
import org.qupring.annotation.RequestParam;

public class RequestParamResolver implements ArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class)
                && ArgumentConverter.supports(parameter.getType());
    }

    @Override
    public Object resolveArgument(HttpRequest request, Parameter parameter) {
        String name = parameter.getAnnotation(RequestParam.class).value();
        String value = request.getQueryParams().get(name);

        return ArgumentConverter.convert(value, parameter.getType(), name);
    }
}
