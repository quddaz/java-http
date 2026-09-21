package org.qupring.mvc.response;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ControllerResponse {

    private static final int OK = 200;
    private static final int NO_CONTENT = 204;

    private final int status;
    private final Map<String, String> headers;
    private final Object body;

    private ControllerResponse(
            int status,
            Map<String, String> headers,
            Object body
    ) {
        validateStatus(status);
        this.status = status;
        this.headers = new LinkedHashMap<>(headers);
        this.body = body;
    }

    public static ControllerResponse ok() {
        return new ControllerResponse(OK, Map.of(), null);
    }

    public static ControllerResponse ok(Object body) {
        return new ControllerResponse(OK, Map.of(), body);
    }

    public static ControllerResponse noContent() {
        return new ControllerResponse(NO_CONTENT, Map.of(), null);
    }

    public static ControllerResponse status(int status) {
        return new ControllerResponse(status, Map.of(), null);
    }

    private static void validateStatus(int status) {
        if (status < 100 || status > 599) {
            throw new IllegalArgumentException(
                    "잘못된 HTTP 상태 코드입니다: " + status
            );
        }
    }

    public ControllerResponse header(String name, String value) {
        Map<String, String> newHeaders = new LinkedHashMap<>(headers);
        newHeaders.put(name, value);

        return new ControllerResponse(status, newHeaders, body);
    }

    public ControllerResponse body(Object body) {
        return new ControllerResponse(status, headers, body);
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }

}
