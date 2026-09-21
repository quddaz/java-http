package org.qupring.mvc.response;

import org.apache.http.response.HttpResponse;

public interface ResponseResolver {

    boolean supports(Object controllerResponse);

    void resolve(Object controllerResponse, HttpResponse response);
}
