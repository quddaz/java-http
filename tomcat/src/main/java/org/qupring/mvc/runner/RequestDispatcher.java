package org.qupring.mvc.runner;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.handler.HandlerMapping;
import org.qupring.mvc.handler.HandlerTarget;
import org.qupring.mvc.resolver.HandlerType;

public class RequestDispatcher {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private final HandlerMapping handlerMapping;
    private final ArgumentController argumentController;
    private final ControllerResponseResolver controllerResponseResolver;

    public RequestDispatcher(HandlerMapping handlerMapping, ArgumentController argumentController,
                             ControllerResponseResolver controllerResponseResolver) {
        this.handlerMapping = handlerMapping;
        this.argumentController = argumentController;
        this.controllerResponseResolver = controllerResponseResolver;
    }

    public void run(HttpRequest request, HttpResponse response) {
        System.out.println("Request URL: " + request.getUrl() + ", Method: " + request.getHttpMethod());

        if (request.getUrl().equals("/")) {
            response.setBody("Hello world!");
            return ;
        }

        HandlerTarget handlerTarget = handlerMapping.getHandler(request.getUrl(), request.getHttpMethod());
        if (handlerTarget == null) {
            controllerResponseResolver.setNotFoundPage(response);
            return ;
        }

        if (handlerTarget.handlerType() == HandlerType.STATIC_RESOURCE) {
            controllerResponseResolver.resolveStaticResourceResponse(handlerTarget, response);
            return ;
        }

        Object controllerResponse = argumentController.execute(request, response,handlerTarget);
        controllerResponseResolver.resolveResponse(controllerResponse, response);
    }

}
