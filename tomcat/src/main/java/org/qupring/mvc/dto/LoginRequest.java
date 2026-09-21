package org.qupring.mvc.dto;

public record LoginRequest(
        String account,
        String password
) {
}
