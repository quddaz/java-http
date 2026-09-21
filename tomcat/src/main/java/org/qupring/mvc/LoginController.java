package org.qupring.mvc;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.annotation.RequestBody;
import org.qupring.annotation.Route;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.dto.LoginRequest;
import org.qupring.mvc.dto.RegisterRequest;
import org.qupring.mvc.runner.ControllerResponse;
import org.qupring.session.Session;
import org.qupring.session.SessionManager;

public class LoginController {

    private static final int FOUND = 302;
    private static final String HTML_CONTENT_TYPE =
            "text/html;charset=utf-8";

    private static final String LOGIN_SUCCESS_PATH = "/index.html";
    private static final String LOGIN_FAILURE_PATH = "/401.html";

    private static final String SESSION_USER_KEY = "user";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    @Route(path = "/login", method = HttpMethod.GET)
    public ControllerResponse loginPage(
            HttpRequest request,
            HttpResponse response
    ) {
        if (isLoggedIn(request)) {
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
            HttpResponse response,
            @RequestBody LoginRequest loginRequest
    ) {
        if (isLoggedIn(request)) {
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

        Session session = createSession(request, user);
        return ControllerResponse.status(FOUND)
                .header("Location", LOGIN_SUCCESS_PATH)
                .header("Set-Cookie", SESSION_COOKIE_NAME + "=" + session.getId())
                .body("");
    }

    @Route(path = "/register", method = HttpMethod.POST)
    public ControllerResponse register(
            HttpResponse response,
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

    private boolean isLoggedIn(HttpRequest request) {
        Session session = request.getSession(false);

        return session != null
                && session.getAttribute(SESSION_USER_KEY) != null;
    }

    private void redirect(
            HttpResponse response,
            String location
    ) {
        response.setStatus(FOUND);
        response.setLocation(location);
    }
}
