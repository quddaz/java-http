package org.qupring.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpTomcatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qupring.mvc.handler.HandlerMapping;

class LoginControllerTest {

    private QupringMvc qupringMvc;

    @BeforeEach
    void setUp() {
        HandlerMapping handlerMapping = new HandlerMapping();
        handlerMapping.addMappings(List.of(LoginController.class));
        qupringMvc = new QupringMvc(handlerMapping, Map.of());
    }

    @Test
    void 로그인_요청_본문을_DTO로_변환해_로그인한다() {
        // given
        HttpRequest request = request(Map.of(
                "account", "gugu",
                "password", "password"
        ));
        HttpTomcatResponse response = HttpTomcatResponse.createDefault();

        // when
        qupringMvc.run(request, response);

        // then
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeader("Location")).isEqualTo("/index.html");
        assertThat(response.getHeader("Set-Cookie")).startsWith("JSESSIONID=");
        assertThat(request.getSession(false).getAttribute("user")).isNotNull();
    }

    @Test
    void 비밀번호가_다르면_인증_실패_페이지로_이동한다() {
        // given
        HttpRequest request = request(Map.of(
                "account", "gugu",
                "password", "wrong-password"
        ));
        HttpTomcatResponse response = HttpTomcatResponse.createDefault();

        // when
        qupringMvc.run(request, response);

        // then
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeader("Location")).isEqualTo("/401.html");
        assertThat(response.getHeader("Set-Cookie")).isNull();
    }

    private HttpRequest request(Map<String, String> body) {
        return new HttpTomcatRequest(
                HttpMethod.POST,
                "/login",
                "HTTP/1.1",
                null,
                Map.of(),
                Map.of(),
                Map.of(),
                body
        );
    }
}
