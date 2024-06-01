package dev.juanvega.bindenv4j;

import com.sun.jdi.Value;

import java.lang.reflect.Constructor;
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
        T instance = null;
        try {
            var constructors = classType.getDeclaredConstructors();
            var targetConstructor = constructors[0];
            for (Constructor<?> constructor : constructors) {
                if (constructor.equals(targetConstructor)) {
                    continue;
                }
                if (targetConstructor.getParameterCount() > constructor.getParameterCount()) {
                    targetConstructor = constructor;
                }
            }
            targetConstructor.setAccessible(true);
            if (targetConstructor.getParameterCount() > 0) {
                var valuesByField = fieldValues(classType).entrySet();
                Parameter[] parameters = targetConstructor.getParameters();
                Object[] parameterValues = new Object[parameters.length];
                for (var i = 0; i < parameters.length; i++) {
                    var parameter = parameters[i];
                    var targetParameterName = parameter.getName();
                    var field = valuesByField.stream().filter(
                            entry -> entry.getKey().getName().equals(targetParameterName)
                    ).findFirst().orElseThrow(() -> new IllegalStateException("Unable to find field for constructor parameter %s".formatted(targetParameterName)));
                    parameterValues[i] = field.getValue();
                }
                return (T) targetConstructor.newInstance(parameterValues);
            }
            instance = (T) targetConstructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        var valuesByField = fieldValues(classType);
        for (Map.Entry<Field, Object> valueByField : valuesByField.entrySet()) {
            valueByField.getKey().setAccessible(true);
            try {
                valueByField.getKey().set(instance, valueByField.getValue());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
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

                return  null;
            });
            result.put(field, value);
        }
        return result;
    }
}
