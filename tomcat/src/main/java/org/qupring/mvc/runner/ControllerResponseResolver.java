package org.qupring.mvc.runner;

import java.util.Map;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.handler.HandlerTarget;

public class ControllerResponseResolver {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String NOT_FOUND_PAGE = "static/404.html";

    public void resolveResponse(Object controllerResponse, HttpResponse response) {
        System.out.println("Controller Response: " + controllerResponse);
        if (controllerResponse == null) {
            return;
        }

        if (controllerResponse instanceof String) {
            String path =  "/"+ ((String) controllerResponse) +".html";
            response.setBody(HtmlReader.read(path));
            return;
        }

        if(controllerResponse instanceof ControllerResponse) {
            ControllerResponse controllerResponseObj = (ControllerResponse) controllerResponse;
            response.setStatus(controllerResponseObj.getStatus());
            response.setBody((String) controllerResponseObj.getBody());

            for (Map.Entry<String, String> entry : controllerResponseObj.getHeaders().entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }

    }

    public void resolveStaticResourceResponse(HandlerTarget handlerTarget, HttpResponse response) {
        response.setBody(HtmlReader.read(handlerTarget.staticResourcePath()));
        contentType(handlerTarget.staticResourcePath(),response);
    }

    public void setNotFoundPage(HttpResponse response) {
        response.setStatus(404);
        response.setBody(HtmlReader.read(NOT_FOUND_PAGE));
        response.setHeader("Content-Type", DEFAULT_CONTENT_TYPE);
    }


    private void contentType(String path, HttpResponse response) {

        if (path.endsWith(".css")) {
            response.setHeader("Content-Type", "text/css;charset=utf-8");
            return;
        }

        if (path.endsWith(".js")) {
            response.setHeader("Content-Type", "application/javascript;charset=utf-8");
            return;
        }

        response.setHeader("Content-Type", DEFAULT_CONTENT_TYPE);
    }
}
