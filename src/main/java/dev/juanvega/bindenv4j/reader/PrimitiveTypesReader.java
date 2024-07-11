package dev.juanvega.bindenv4j.reader;

import java.util.Optional;
import java.util.function.Function;

public class PrimitiveTypesReader implements EnvReader {

    private final Function<String, String> rawReader;

    public PrimitiveTypesReader(Function<String, String> rawReader) {
        this.rawReader = rawReader;
    }

    @Override
    public <T> Optional<T> read(String key, Class<T> targetType) {
        var value = rawReader.apply(key);
        if (value == null) {
            return Optional.empty();
        }

        var parsed =  switch (targetType.getSimpleName()) {
            case "String" -> value;
            case "Integer", "int" -> Integer.valueOf(value);
            case "Short", "short" -> Short.valueOf(value);
            case "Long", "long" -> Long.valueOf(value);
            case "Float", "float" -> Float.valueOf(value);
            case "Double", "double" -> Double.valueOf(value);
            case "Character", "char" -> Character.valueOf(value.charAt(0));
            case "Boolean", "boolean" -> Boolean.valueOf(switch (value.toLowerCase()) {
                case "true" -> true;
                case "false" -> false;
                default -> !value.isBlank();
            });
            default -> throw new IllegalArgumentException(
                    "Unsupported type: " + targetType.getSimpleName()
            );
        };

        return Optional.of((T) parsed);
    }
}
