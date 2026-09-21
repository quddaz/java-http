import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpRequestParser;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.response.HttpTomcatResponse;

public class ReflectionTest {
    public static void main(String[] args) {
        try {
            Class<?> clazz = Class.forName("org.qupring.mvc.LoginController");
            Method method = findLoginMethod(clazz);

            HttpTomcatRequest request = createRequest();
            Field[] fields = request.getClass().getDeclaredFields();
            System.out.println(fields[0].getName()  + " : " + fields[0].getType());
            HttpTomcatResponse response = HttpTomcatResponse.createDefault();

            invoke(clazz, method, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Method findLoginMethod(Class<?> clazz) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(
                "login",
                HttpRequest.class,
                HttpResponse.class
        );
    }

    private static HttpTomcatRequest createRequest() {
        String rawRequest = """
            POST /login?account=gugu&password=password HTTP/1.1\r
            Host: localhost:8080\r
            Connection: keep-alive\r
            Content-Length: 0\r
            \r
            """;

        return new HttpRequestParser()
                .parse(HttpTomcatRequest.class, rawRequest);
    }

    private static void invoke(
            Class<?> clazz,
            Method method,
            HttpTomcatRequest request,
            HttpTomcatResponse response
    ) throws Exception {
        Object controller = clazz.getDeclaredConstructor().newInstance();
        method.invoke(controller, request, response);
    }
}
