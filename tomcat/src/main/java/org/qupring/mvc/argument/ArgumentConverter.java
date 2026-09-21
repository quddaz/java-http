package org.qupring.mvc.argument;

final class ArgumentConverter {

    private ArgumentConverter() {
    }

    static boolean supports(Class<?> type) {
        return type == String.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class;
    }

    static Object convert(String value, Class<?> type, String name) {
        if (value == null) {
            throw new IllegalArgumentException("요청 값이 존재하지 않습니다: " + name);
        }

        if (type == String.class) {
            return value;
        }

        if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        }

        if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        }

        throw new IllegalArgumentException("지원하지 않는 인자 타입: " + type.getSimpleName());
    }
}
