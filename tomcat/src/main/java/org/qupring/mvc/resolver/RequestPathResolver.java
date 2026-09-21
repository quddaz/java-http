package org.qupring.mvc.resolver;

import java.lang.reflect.Parameter;
import org.apache.http.request.HttpRequest;
import org.qupring.annotation.RequestPath;

public class RequestPathResolver implements ArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestPath.class)
                && parameter.getType() == String.class;
    }

    @Override
    public Object resolveArgument(HttpRequest request, Parameter parameter) {
        return request.getUrl();
    }
}
