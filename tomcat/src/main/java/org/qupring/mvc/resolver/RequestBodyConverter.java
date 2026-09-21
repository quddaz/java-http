package org.qupring.mvc.resolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Map;

final class RequestBodyConverter {

    private RequestBodyConverter() {
    }

    static Object convert(Map<String, String> body, Class<?> targetType) {
        if (targetType == Map.class) {
            return Map.copyOf(body);
        }

        if (targetType.isRecord()) {
            return convertRecord(body, targetType);
        }

        throw new IllegalArgumentException(
                "@RequestBody는 record 또는 Map 타입만 지원합니다: "
                        + targetType.getSimpleName()
        );
    }

    private static Object convertRecord(
            Map<String, String> body,
            Class<?> targetType
    ) {
        try {
            RecordComponent[] components = targetType.getRecordComponents();
            Class<?>[] parameterTypes = new Class<?>[components.length];
            Object[] arguments = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                parameterTypes[i] = component.getType();
                arguments[i] = ArgumentConverter.convert(
                        body.get(component.getName()),
                        component.getType(),
                        component.getName()
                );
            }

            Constructor<?> constructor = targetType.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException(
                    "요청 본문을 " + targetType.getSimpleName() + "으로 변환할 수 없습니다.",
                    exception
            );
        }
    }
}
