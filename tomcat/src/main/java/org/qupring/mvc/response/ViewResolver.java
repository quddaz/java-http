package org.qupring.mvc.response;

import java.util.Map;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;

public class ViewResolver implements ResponseResolver {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String NOT_FOUND_PAGE = "static/404.html";

    private final Map<String, String> viewMappings;

    public ViewResolver(Map<String, String> viewMappings) {
        this.viewMappings = viewMappings;
    }

    @Override
    public boolean supports(Object controllerResponse) {
        return controllerResponse instanceof String;
    }

    @Override
    public void resolve(Object controllerResponse, HttpResponse response) {
        String viewName = (String) controllerResponse;
        String viewPath = findViewPath(viewName);

        if (viewPath == null) {
            response.setStatus(404);
            response.setBody(HtmlReader.read(NOT_FOUND_PAGE));
            response.setHeader("Content-Type", DEFAULT_CONTENT_TYPE);
            return;
        }

        response.setBody(HtmlReader.read(viewPath));
        response.setHeader("Content-Type", contentType(viewPath));
    }

    private String findViewPath(String viewName) {
        String viewPath = viewMappings.get(viewName);
        if (viewPath != null) {
            return viewPath;
        }

        String requestPath = viewName.startsWith("/") ? viewName : "/" + viewName;
        viewPath = viewMappings.get(requestPath);
        if (viewPath != null || requestPath.endsWith(".html")) {
            return viewPath;
        }

        return viewMappings.get(requestPath + ".html");
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return DEFAULT_CONTENT_TYPE;
    }
}
