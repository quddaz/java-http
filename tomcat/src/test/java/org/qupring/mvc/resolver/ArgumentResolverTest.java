package org.qupring.mvc.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpTomcatRequest;
import org.junit.jupiter.api.Test;
import org.qupring.annotation.RequestBody;
import org.qupring.annotation.RequestParam;
import org.qupring.annotation.RequestPath;

class ArgumentResolverTest {

    @Test
    void 쿼리_파라미터를_요청한_타입으로_변환한다() throws NoSuchMethodException {
        // given
        RequestParamResolver resolver = new RequestParamResolver();
        Parameter parameter = parameterAt("query", 0, String.class, int.class);
        HttpRequest request = request(
                "/users",
                Map.of("name", "gugu", "age", "20"),
                Map.of()
        );

        // when
        Object name = resolver.resolveArgument(request, parameter);
        Object age = resolver.resolveArgument(
                request,
                parameterAt("query", 1, String.class, int.class)
        );

        // then
        assertThat(name).isEqualTo("gugu");
        assertThat(age).isEqualTo(20);
    }

    @Test
    void 요청_본문과_경로를_해결한다() throws NoSuchMethodException {
        // given
        RequestBodyResolver bodyResolver = new RequestBodyResolver();
        RequestPathResolver pathResolver = new RequestPathResolver();
        HttpRequest request = request(
                "/login",
                Map.of(),
                Map.of("account", "gugu")
        );

        // when
        Object loginRequest = bodyResolver.resolveArgument(
                request,
                parameterAt("body", 0, LoginRequest.class)
        );
        Object path = pathResolver.resolveArgument(
                request,
                parameterAt("path", 0, String.class)
        );

        // then
        assertThat(loginRequest).isEqualTo(new LoginRequest("gugu"));
        assertThat(path).isEqualTo("/login");
    }

    @Test
    void 필수_요청_값이_없으면_예외가_발생한다() throws NoSuchMethodException {
        // given
        RequestParamResolver resolver = new RequestParamResolver();
        Parameter parameter = parameterAt("query", 0, String.class, int.class);
        HttpRequest request = request("/users", Map.of(), Map.of());

        // when & then
        assertThatThrownBy(() -> resolver.resolveArgument(request, parameter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    private Parameter parameterAt(
            String methodName,
            int index,
            Class<?>... parameterTypes
    ) throws NoSuchMethodException {
        Method method = TestController.class.getDeclaredMethod(methodName, parameterTypes);
        return method.getParameters()[index];
    }

    private HttpRequest request(
            String path,
            Map<String, String> queryParameters,
            Map<String, String> body
    ) {
        return new HttpTomcatRequest(
                HttpMethod.GET,
                path,
                "HTTP/1.1",
                null,
                Map.of(),
                queryParameters,
                Map.of(),
                body
        );
    }

    private static class TestController {

        void query(
                @RequestParam("name") String name,
                @RequestParam("age") int age
        ) {
        }

        void body(@RequestBody LoginRequest loginRequest) {
        }

        void path(@RequestPath String path) {
        }
    }

    private record LoginRequest(String account) {
    }
}
