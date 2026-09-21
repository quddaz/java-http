package org.qupring.mvc.handler;

import java.lang.reflect.Method;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.mvc.argument.ArgumentController;
import org.qupring.mvc.response.ControllerResponse;
import org.qupring.mvc.response.ResponseResolverComposite;

public class RequestDispatcher {

    private final HandlerMapping handlerMapping;
    private final ArgumentController argumentController;
    private final ResponseResolverComposite responseResolverComposite;

    public RequestDispatcher(
            HandlerMapping handlerMapping,
            ArgumentController argumentController,
            ResponseResolverComposite responseResolverComposite
    ) {
        this.handlerMapping = handlerMapping;
        this.argumentController = argumentController;
        this.responseResolverComposite = responseResolverComposite;
    }

    public void run(HttpRequest request, HttpResponse response) {
        System.out.println("Request URL: " + request.getUrl() + ", Method: " + request.getHttpMethod());

        if (request.getUrl().equals("/")) {
            responseResolverComposite.resolve(
                    ControllerResponse.ok("Hello world!"),
                    response
            );
            return;
        }

        Method method = handlerMapping.getHandler(request.getUrl(), request.getHttpMethod());
        if (method == null) {
            responseResolverComposite.resolve(request.getUrl(), response);
            return;
        }

        Object controllerResponse = argumentController.execute(request, response, method);
        responseResolverComposite.resolve(controllerResponse, response);
    }

}
