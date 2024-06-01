package dev.juanvega.bindenv4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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
            instance = classType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
            var value = envData.orElseThrow(() -> new IllegalStateException("No value for " + field.getName()));
            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
