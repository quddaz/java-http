package org.qupring.mvc.resolver;

import java.lang.reflect.Parameter;
import org.apache.http.request.HttpRequest;
import org.qupring.annotation.RequestBody;

public class RequestBodyResolver implements ArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolveArgument(HttpRequest request, Parameter parameter) {
        return RequestBodyConverter.convert(
                request.getBodys(),
                parameter.getType()
        );
    }
}
