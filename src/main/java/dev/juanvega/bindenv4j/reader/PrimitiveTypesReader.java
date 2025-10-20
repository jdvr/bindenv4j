package dev.juanvega.bindenv4j.reader;

import java.util.Optional;
import java.util.function.Function;


/**
 * An {@link EnvReader} that reads primitive types and their boxed equivalents from a raw string
 * source.
 *
 * <p>This class uses a {@link Function} to read the raw string value of an environment variable and
 * then parses it into the target type.</p>
 */
public class PrimitiveTypesReader implements EnvReader {

    private final Function<String, String> rawReader;

    /**
     * Creates a new {@link PrimitiveTypesReader} with the given raw string reader.
     *
     * @param rawReader a {@link Function} that takes the name of an environment variable and returns
     *                  its raw string value, or {@code null} if the variable is not set
     */
    public PrimitiveTypesReader(Function<String, String> rawReader) {
        this.rawReader = rawReader;
    }

    /**
     * Reads an environment variable and converts it to the specified type.
     *
     * <p>This method supports the following types:</p>
     *
     * <ul>
     *   <li>{@link String}
     *   <li>{@link Integer} and {@code int}
     *   <li>{@link Short} and {@code short}
     *   <li>{@link Long} and {@code long}
     *   <li>{@link Float} and {@code float}
     *   <li>{@link Double} and {@code double}
     *   <li>{@link Character} and {@code char}
     *   <li>{@link Boolean} and {@code boolean} (parses "true" and "false", case-insensitive)
     * </ul>
     *
     * @param key        the name of the environment variable
     * @param targetType the type to convert the value to
     * @return an {@link Optional} containing the value of the environment variable, or {@link
     * Optional#empty()} if the variable is not set
     * @throws IllegalArgumentException if the target type is not supported
     */
    @Override
    public <T> Optional<T> read(String key, Class<T> targetType) {

        var value = rawReader.apply(key);
        if (value == null) {
            return Optional.empty();
        }

        var parsed = switch (targetType.getSimpleName()) {
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
