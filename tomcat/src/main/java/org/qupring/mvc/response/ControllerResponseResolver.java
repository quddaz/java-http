package org.qupring.mvc.response;

import org.apache.http.response.HttpResponse;

public class ControllerResponseResolver implements ResponseResolver {

    @Override
    public boolean supports(Object controllerResponse) {
        return controllerResponse instanceof ControllerResponse;
    }

    @Override
    public void resolve(Object controllerResponse, HttpResponse response) {
        ControllerResponse resolvedResponse = (ControllerResponse) controllerResponse;

        response.setStatus(resolvedResponse.getStatus());
        resolvedResponse.getHeaders().forEach(response::setHeader);

        if (resolvedResponse.getBody() != null) {
            response.setBody(resolvedResponse.getBody().toString());
        }
    }
}
