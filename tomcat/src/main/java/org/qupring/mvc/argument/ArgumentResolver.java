package org.qupring.mvc.argument;

import java.lang.reflect.Parameter;
import org.apache.http.request.HttpRequest;

public interface ArgumentResolver {
    boolean supportsParameter(Parameter parameter);

    Object resolveArgument(HttpRequest request, Parameter parameter);
}
