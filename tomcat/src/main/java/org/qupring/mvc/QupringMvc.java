package org.qupring.mvc;

import java.util.List;
import java.util.Map;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.mvc.argument.ArgumentController;
import org.qupring.mvc.handler.HandlerMapping;
import org.qupring.mvc.handler.RequestDispatcher;
import org.qupring.mvc.response.ControllerResponseResolver;
import org.qupring.mvc.response.ResponseResolverComposite;
import org.qupring.mvc.response.ViewResolver;

public class QupringMvc {

    private final RequestDispatcher requestDispatcher;

    public QupringMvc(
            HandlerMapping handlerMapping,
            Map<String, String> viewMappings
    ) {
        ArgumentController argumentController = new ArgumentController();
        ResponseResolverComposite responseResolverComposite = new ResponseResolverComposite(
                List.of(
                        new ControllerResponseResolver(),
                        new ViewResolver(viewMappings)
                )
        );
        this.requestDispatcher = new RequestDispatcher(
                handlerMapping,
                argumentController,
                responseResolverComposite
        );
    }

    public void run(HttpRequest request, HttpResponse response) {
        requestDispatcher.run(request, response);
    }

}
