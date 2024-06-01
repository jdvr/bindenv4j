package dev.juanvega.bindenv4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnvBinder {

    private final EnvReader envReader;

    public EnvBinder(EnvReader envReader) {
        this.envReader = envReader;
    }

    /**
     * Binds environment variables to the fields of the given class.
     *
     * @param classType The class type to be inspected for fields.
     */
    public <T> T bind(Class<T> classType) {

        try {
            if (classType.isRecord()) {
                var constructors = classType.getDeclaredConstructors();
                var targetConstructor = constructors[0];
                var valuesByField = fieldValuesByName(classType);
                targetConstructor.setAccessible(true);
                Parameter[] parameters = targetConstructor.getParameters();
                Object[] parameterValues = new Object[parameters.length];
                for (var i = 0; i < parameters.length; i++) {
                    var parameter = parameters[i];
                    var targetParameterName = parameter.getName();
                    if (!valuesByField.containsKey(targetParameterName)) {
                        throw new IllegalStateException(
                                "Unable to find field for constructor parameter %s".formatted(targetParameterName)
                        );
                    }
                    parameterValues[i] = valuesByField.get(targetParameterName);
                }
                return (T) targetConstructor.newInstance(parameterValues);
            } else {
                T instance = classType.newInstance();
                var valuesByField = fieldValues(classType);
                for (Map.Entry<Field, Object> valueByField : valuesByField.entrySet()) {
                    valueByField.getKey().setAccessible(true);
                    valueByField.getKey().set(instance, valueByField.getValue());
                }
                return instance;
            }


        } catch (InstantiationException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private <T> Map<Field, Object> fieldValues(Class<T> classType) {
        var result = new HashMap<Field, Object>();
        for (Field field : classType.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            var envVarName = field.getName();
            if (field.isAnnotationPresent(Name.class)) {
                envVarName = field.getAnnotation(Name.class).value();
            }
            Optional<?> envData = envReader.read(envVarName, field.getType());
            var value = envData.orElseGet(() -> {
                if (field.getType().isPrimitive()) {
                    throw new IllegalStateException("No value for " + field.getName());
                }

                return null;
            });
            result.put(field, value);
        }
        return result;
    }

    private <T> Map<String, Object> fieldValuesByName(Class<T> classType) {
        var entries = fieldValues(classType).entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey().getName(), entry.getValue()))
                .toArray(Map.Entry[]::new);
        return Map.ofEntries(entries);

    }
}
