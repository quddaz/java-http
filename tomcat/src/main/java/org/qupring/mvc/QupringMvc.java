package org.qupring.mvc;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.mvc.handler.HandlerMapping;
import org.qupring.mvc.runner.ArgumentController;
import org.qupring.mvc.runner.ControllerResponseResolver;
import org.qupring.mvc.runner.RequestDispatcher;

public class QupringMvc {

    private final RequestDispatcher requestDispatcher;

    public QupringMvc(HandlerMapping handlerMapping) {
        ArgumentController argumentController = new ArgumentController();
        ControllerResponseResolver controllerResponseResolver = new ControllerResponseResolver();
        this.requestDispatcher = new RequestDispatcher(
                handlerMapping,
                argumentController,
                controllerResponseResolver
        );
    }

    public void run(HttpRequest request, HttpResponse response) {
        requestDispatcher.run(request, response);
    }

}
