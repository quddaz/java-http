package org.qupring.mvc.response;

import java.util.List;
import org.apache.http.response.HttpResponse;

public class ResponseResolverComposite {

    private final List<ResponseResolver> responseResolvers;

    public ResponseResolverComposite(List<ResponseResolver> responseResolvers) {
        this.responseResolvers = responseResolvers;
    }

    public void resolve(Object controllerResponse, HttpResponse response) {
        if (controllerResponse == null) {
            return;
        }

        responseResolvers.stream()
                .filter(resolver -> resolver.supports(controllerResponse))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "지원하지 않는 응답 타입: " + controllerResponse.getClass().getName()
                ))
                .resolve(controllerResponse, response);
    }
}
