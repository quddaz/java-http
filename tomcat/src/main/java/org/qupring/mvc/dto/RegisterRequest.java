package org.qupring.mvc.dto;

public record RegisterRequest(
        String account,
        String password,
        String email
) {
}
