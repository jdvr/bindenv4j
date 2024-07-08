package dev.juanvega.bindenv4j.reader;

import java.util.Optional;

public class SystemEnvReader implements EnvReader {
    public <T> Optional<T> read(String key, Class<T> targetType) {
        String value = System.getenv(key);
        if (value == null) {
            return Optional.empty();
        }
        return switch (targetType.getSimpleName()) {
            case "String" -> Optional.of(targetType.cast(value));
            case "Integer" -> Optional.of(targetType.cast(Integer.parseInt(value)));
            case "Short" -> Optional.of(targetType.cast(Short.parseShort(value)));
            case "Float" -> Optional.of(targetType.cast(Float.parseFloat(value)));
            case "Boolean" -> switch (value.toLowerCase()) {
                case "true" -> Optional.of(targetType.cast(true));
                case "false" -> Optional.of(targetType.cast(false));
                default -> value.isBlank()
                        ? Optional.of(targetType.cast(false))
                        :Optional.of(targetType.cast(true));
            };
            default ->
                    throw new IllegalArgumentException("Unsupported type: " + targetType.getSimpleName());
        };

    }
}
