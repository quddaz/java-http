package org.qupring.mvc.argument;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public class ArgumentController {

    private final List<ArgumentResolver> argumentResolvers;

    public ArgumentController() {
        this.argumentResolvers = List.of(
                new RequestParamResolver(),
                new RequestBodyResolver(),
                new RequestPathResolver()
        );
    }

    public Object execute(
            HttpRequest request,
            HttpResponse response,
            Method method
    ) {
        if (method == null) {
            throw new IllegalArgumentException("실행할 컨트롤러 메소드가 없습니다.");
        }

        try {
            Object controller = method
                    .getDeclaringClass()
                    .getDeclaredConstructor()
                    .newInstance();
            Object[] arguments = resolveArguments(method, request, response);
            return method.invoke(controller, arguments);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("컨트롤러 메소드를 실행하지 못했습니다.", exception);
        }
    }

    private Object[] resolveArguments(
            Method method,
            HttpRequest request,
            HttpResponse response
    ) {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveArgument(parameters[i], request, response);
        }

        return arguments;
    }

    private Object resolveArgument(
            Parameter parameter,
            HttpRequest request,
            HttpResponse response
    ) {
        if (parameter.getType() == HttpRequest.class) {
            return request;
        }

        if (parameter.getType() == HttpResponse.class) {
            return response;
        }

        return argumentResolvers.stream()
                .filter(resolver -> resolver.supportsParameter(parameter))
                .findFirst()
                .map(resolver -> resolver.resolveArgument(request, parameter))
                .orElseThrow(() -> new IllegalArgumentException(
                        "지원하지 않는 컨트롤러 인자: " + parameter
                ));
    }

}
