package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.service.LoginSessionService;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.qupring.annotation.RequestBody;
import org.qupring.annotation.Route;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.dto.LoginRequest;
import org.qupring.mvc.dto.RegisterRequest;
import org.qupring.mvc.response.ControllerResponse;

public class LoginController {

    private static final int FOUND = 302;
    private static final String HTML_CONTENT_TYPE =
            "text/html;charset=utf-8";

    private static final String LOGIN_SUCCESS_PATH = "/index";
    private static final String LOGIN_FAILURE_PATH = "/401";

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final LoginSessionService loginSessionService =
            new LoginSessionService();

    @Route(path = "/login", method = HttpMethod.GET)
    public ControllerResponse loginPage(HttpRequest request) {
        if (loginSessionService.isLoggedIn(request)) {
            return ControllerResponse.status(FOUND)
                    .header("Location", LOGIN_SUCCESS_PATH)
                    .body("");
        }

        return ControllerResponse.ok()
                .header("Content-Type", HTML_CONTENT_TYPE)
                .body(HtmlReader.read("static/login.html"));
    }

    @Route(path = "/login", method = HttpMethod.POST)
    public ControllerResponse login(
            HttpRequest request,
            @RequestBody LoginRequest loginRequest
    ) {
        if (loginSessionService.isLoggedIn(request)) {
            return ControllerResponse.status(FOUND)
                    .header("Location", LOGIN_SUCCESS_PATH)
                    .body("");
        }

        User user = InMemoryUserRepository.findByAccount(loginRequest.account())
                .filter(foundUser ->
                        foundUser.checkPassword(loginRequest.password()))
                .orElse(null);

        if (user == null) {
            return ControllerResponse.status(FOUND)
                    .header("Location", LOGIN_FAILURE_PATH)
                    .body("");
        }

        String sessionId = loginSessionService.login(request, user);
        return ControllerResponse.status(FOUND)
                .header("Location", LOGIN_SUCCESS_PATH)
                .header("Set-Cookie", SESSION_COOKIE_NAME + "=" + sessionId)
                .body("");
    }

    @Route(path = "/register", method = HttpMethod.POST)
    public ControllerResponse register(
            @RequestBody RegisterRequest registerRequest
    ) {
        InMemoryUserRepository.save(
                new User(
                        registerRequest.account(),
                        registerRequest.password(),
                        registerRequest.email()
                )
        );

        return ControllerResponse.status(FOUND)
                .header("Location", "/login.html")
                .body("");
    }

}
